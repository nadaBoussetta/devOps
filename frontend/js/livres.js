/**
 * Recherche de livres via Google Books API + vérification disponibilité Sudoc IDF
 */

document.addEventListener('DOMContentLoaded', () => {
    const form     = document.getElementById('book-search-form');
    const input    = document.getElementById('titre_livre');
    const btnClear = document.getElementById('btn-clear');

    form.addEventListener('submit', (e) => {
        e.preventDefault();
        const q = input.value.trim();
        if (q) searchBooks(q);
    });

    input.addEventListener('input', () => {
        btnClear.style.display = input.value ? 'flex' : 'none';
    });

    btnClear.addEventListener('click', () => {
        input.value = '';
        btnClear.style.display = 'none';
        input.focus();
        resetResults();
    });
});

function searchQuick(query) {
    document.getElementById('titre_livre').value = query;
    document.getElementById('btn-clear').style.display = 'flex';
    searchBooks(query);
}

function resetResults() {
    document.getElementById('results-section').style.display = 'none';
    document.getElementById('empty-state').style.display     = 'flex';
    document.getElementById('book-results-container').innerHTML = '';
}

// ─── Recherche principale ─────────────────────────────────────────────────────

async function searchBooks(query) {
    const btn        = document.getElementById('btn-search');
    const container  = document.getElementById('book-results-container');
    const section    = document.getElementById('results-section');
    const emptyState = document.getElementById('empty-state');
    const aiSummary  = document.getElementById('ai-summary');
    const countEl    = document.getElementById('results-count');
    const titleEl    = document.getElementById('results-title');

    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Recherche en cours...';
    emptyState.style.display = 'none';
    section.style.display    = 'block';
    aiSummary.style.display  = 'none';
    container.innerHTML = `
        <div class="loading-grid">
            ${[1,2,3,4,5,6].map(() => `
                <div class="book-skeleton">
                    <div class="skel skel-cover"></div>
                    <div class="skel skel-title"></div>
                    <div class="skel skel-line"></div>
                    <div class="skel skel-line short"></div>
                </div>`).join('')}
        </div>`;
    countEl.textContent = '';
    titleEl.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Recherche en cours...`;

    try {
        const books = await searchGoogleBooks(query);

        titleEl.innerHTML   = `<i class="fas fa-list"></i> Résultats pour "${escapeHtml(query)}"`;
        countEl.textContent = `${books.length} livre${books.length > 1 ? 's' : ''} trouvé${books.length > 1 ? 's' : ''}`;

        if (!books.length) {
            container.innerHTML = `
                <div class="no-results">
                    <i class="fas fa-search"></i>
                    <p>Aucun livre trouvé pour "<strong>${escapeHtml(query)}</strong>".<br>Essayez un autre titre ou auteur.</p>
                </div>`;
            return;
        }

        // Afficher d'abord les cartes, puis vérifier dispo IDF en arrière-plan
        renderBooks(books);
        checkDisponibiliteIDF(books);

    } catch (err) {
        container.innerHTML = `
            <div class="no-results error">
                <i class="fas fa-exclamation-circle"></i>
                <p>${escapeHtml(err.message)}</p>
            </div>`;
        titleEl.innerHTML = `<i class="fas fa-exclamation-circle"></i> Erreur`;
        console.error('Erreur recherche livres:', err);
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-search"></i> Rechercher';
    }
}

// ─── Google Books API ─────────────────────────────────────────────────────────

async function searchGoogleBooks(query) {
    const url = `https://www.googleapis.com/books/v1/volumes?q=${encodeURIComponent(query)}&maxResults=40&langRestrict=fr&printType=books&key=AIzaSyBEs_clokNr7Js50BE3OxjtVCVIdr0eKpk`;

    const response = await fetch(url);
    if (!response.ok) throw new Error(`Erreur Google Books (${response.status})`);

    const data = await response.json();
    if (!data.items || data.items.length === 0) return [];

    return data.items.map(item => {
        const info = item.volumeInfo || {};
        return {
            id:         item.id,
            titre:      info.title                            || 'Titre inconnu',
            auteur:     (info.authors || []).join(', ')       || 'Auteur inconnu',
            annee:      info.publishedDate ? info.publishedDate.substring(0, 4) : null,
            genre:      (info.categories  || []).join(' / ') || null,
            editeur:    info.publisher                        || null,
            pages:      info.pageCount    ? String(info.pageCount) : null,
            note:       info.averageRating ? String(info.averageRating) : null,
            nbAvis:     info.ratingsCount  || 0,
            resume:     info.description   || null,
            isbn:       extractISBN(info.industryIdentifiers),
            langue:     langueLabel(info.language),
            couverture: info.imageLinks?.thumbnail || info.imageLinks?.smallThumbnail || null,
            lien:       info.previewLink  || info.infoLink || null,
            disponible: item.accessInfo?.viewability !== 'NO_PAGES',
            acces:      item.accessInfo?.epub?.isAvailable || item.accessInfo?.pdf?.isAvailable
        };
    });
}

// ─── Vérification disponibilité IDF via Sudoc ─────────────────────────────────

// Bibliothèques IDF référencées dans le Sudoc avec leurs codes RCR
// (RCR = Répertoire des Centres de Ressources — identifiant unique Sudoc)
const BIBLIOTHEQUES_IDF = [
    { nom: 'BnF — Bibliothèque nationale de France',    rcr: '751021301' },
    { nom: 'Bibliothèque Sainte-Geneviève',             rcr: '751052101' },
    { nom: 'Bibliothèque Mazarine',                     rcr: '751042101' },
    { nom: 'BU Paris-Sorbonne (Paris IV)',               rcr: '751031001' },
    { nom: 'BU Paris Cité (ex-Paris V)',                 rcr: '751052201' },
    { nom: 'BU Sorbonne Nouvelle (Paris III)',           rcr: '751032101' },
    { nom: 'BU Paris-Nanterre',                         rcr: '920502201' },
    { nom: 'BU Université Paris-Est Créteil',           rcr: '940112201' },
    { nom: 'BU CY Cergy Paris Université',              rcr: '950502101' },
    { nom: 'BU Paris-Saclay (UVSQ)',                    rcr: '780472201' },
];

async function checkDisponibiliteIDF(books) {
    // Pour chaque livre qui a un ISBN, on interroge le Sudoc
    // On fait les appels en parallèle pour ne pas bloquer l'UI
    const promises = books.map(async (book) => {
        if (!book.isbn) {
            updateBadgeIDF(book.id, 'inconnu', null);
            return;
        }

        try {
            // API Sudoc isbn2ppn : retourne les PPN (identifiants) des notices
            const url = `https://www.sudoc.fr/services/isbn2ppn/${book.isbn.replace(/-/g, '')}`;
            const res = await fetch(url, { signal: AbortSignal.timeout(5000) });

            if (!res.ok) {
                updateBadgeIDF(book.id, 'inconnu', null);
                return;
            }

            const text = await res.text();

            // Réponse XML — si on trouve un PPN c'est que le livre est dans le Sudoc
            if (text.includes('<ppn>') || text.includes('ppn=')) {
                // Extraire le premier PPN pour construire le lien de localisation
                const ppnMatch = text.match(/<ppn>(\d+)<\/ppn>/) || text.match(/ppn=(\d+)/);
                const ppn = ppnMatch ? ppnMatch[1] : null;

                // Lien vers les localisations dans les bibliothèques IDF
                const lienSudoc = ppn
                    ? `https://www.sudoc.fr/${ppn}`
                    : `https://www.sudoc.fr/cgi-bin/sru?version=1.1&operation=searchRetrieve&query=bath.isbn+all+%22${book.isbn}%22`;

                updateBadgeIDF(book.id, 'disponible', lienSudoc);
            } else {
                updateBadgeIDF(book.id, 'non-trouve', null);
            }

        } catch (err) {
            // Timeout ou erreur réseau — on affiche "non vérifié"
            updateBadgeIDF(book.id, 'inconnu', null);
        }
    });

    await Promise.allSettled(promises);
}

function updateBadgeIDF(bookId, statut, lienSudoc) {
    const badge = document.getElementById(`idf-badge-${bookId}`);
    if (!badge) return;

    if (statut === 'disponible') {
        badge.innerHTML = `
            <i class="fas fa-check-circle"></i>
            <span>Disponible en IDF</span>
            ${lienSudoc ? `<a href="${lienSudoc}" target="_blank" rel="noopener" class="badge-link">
                Voir les bibliothèques <i class="fas fa-external-link-alt"></i>
            </a>` : ''}`;
        badge.className = 'idf-badge idf-dispo';
    } else if (statut === 'non-trouve') {
        badge.innerHTML = `<i class="fas fa-times-circle"></i> <span>Non référencé en IDF</span>`;
        badge.className = 'idf-badge idf-indispo';
    } else {
        badge.innerHTML = `<i class="fas fa-question-circle"></i> <span>Disponibilité IDF non vérifiée</span>`;
        badge.className = 'idf-badge idf-inconnu';
    }
}

// ─── Rendu des livres ─────────────────────────────────────────────────────────

function renderBooks(books) {
    const container = document.getElementById('book-results-container');
    container.innerHTML = `<div class="books-grid">${books.map((b, i) => bookCard(b, i)).join('')}</div>`;
}

function bookCard(b, index) {
    const note   = parseFloat(b.note) || 0;
    const stars  = renderStars(note);
    const genres = b.genre
        ? b.genre.split('/').map(g => `<span class="genre-tag">${escapeHtml(g.trim())}</span>`).join('')
        : '';

    const coverHtml = b.couverture
        ? `<img src="${escapeHtml(b.couverture)}" alt="${escapeHtml(b.titre)}" class="cover-img"
               onerror="this.style.display='none';this.nextElementSibling.style.display='flex'">`
          + `<span class="cover-letter" style="display:none">${escapeHtml(b.titre[0].toUpperCase())}</span>`
        : `<span class="cover-letter">${escapeHtml(b.titre[0].toUpperCase())}</span>`;

    const coverBg = b.couverture ? 'background:#1a1a2e' : COVER_COLORS[index % COVER_COLORS.length];

    return `
    <div class="book-card" style="animation-delay:${index * 0.06}s">
        <div class="book-cover" style="background:${coverBg}">
            ${coverHtml}
            ${b.acces ? '<span class="online-badge"><i class="fas fa-book-open"></i> Aperçu</span>' : ''}
        </div>
        <div class="book-info">
            <div class="book-header">
                <h3 class="book-title">${escapeHtml(b.titre)}</h3>
                <div class="book-meta">
                    <span class="book-author"><i class="fas fa-user-pen"></i> ${escapeHtml(b.auteur)}</span>
                    ${b.annee ? `<span class="book-year"><i class="fas fa-calendar"></i> ${escapeHtml(b.annee)}</span>` : ''}
                </div>
            </div>

            ${genres ? `<div class="genre-tags">${genres}</div>` : ''}

            ${b.resume ? `<p class="book-resume">${escapeHtml(b.resume)}</p>` : ''}

            <!-- Badge disponibilité IDF — mis à jour en arrière-plan -->
            <div id="idf-badge-${b.id}" class="idf-badge idf-loading">
                <i class="fas fa-spinner fa-spin"></i>
                <span>Vérification disponibilité IDF...</span>
            </div>

            <div class="book-footer">
                <div class="book-rating">
                    ${stars}
                    ${note > 0 ? `<span class="rating-num">${note.toFixed(1)}</span>` : ''}
                    ${b.nbAvis > 0 ? `<span class="rating-count">(${b.nbAvis})</span>` : ''}
                </div>
                <div class="book-actions">
                    ${b.lien ? `
                        <a href="${escapeHtml(b.lien)}" target="_blank" rel="noopener" class="btn-read">
                            <i class="fas fa-external-link-alt"></i> Voir
                        </a>` : ''}
                </div>
            </div>

            ${b.editeur || b.pages || b.langue ? `
            <div class="book-details">
                ${b.editeur ? `<span><i class="fas fa-building"></i> ${escapeHtml(b.editeur)}</span>` : ''}
                ${b.pages   ? `<span><i class="fas fa-file-lines"></i> ${escapeHtml(b.pages)} pages</span>` : ''}
                ${b.langue  ? `<span><i class="fas fa-language"></i> ${escapeHtml(b.langue)}</span>` : ''}
                ${b.isbn    ? `<span><i class="fas fa-barcode"></i> ${escapeHtml(b.isbn)}</span>` : ''}
            </div>` : ''}
        </div>
    </div>`;
}

function renderStars(note) {
    const full  = Math.floor(note);
    const half  = note % 1 >= 0.5 ? 1 : 0;
    const empty = 5 - full - half;
    return [
        ...[...Array(full)].map(()  => '<i class="fas fa-star star-full"></i>'),
        ...(half ? ['<i class="fas fa-star-half-alt star-half"></i>'] : []),
        ...[...Array(empty)].map(() => '<i class="far fa-star star-empty"></i>'),
    ].join('');
}

function extractISBN(identifiers) {
    if (!identifiers) return null;
    const isbn13 = identifiers.find(i => i.type === 'ISBN_13');
    const isbn10 = identifiers.find(i => i.type === 'ISBN_10');
    return (isbn13 || isbn10)?.identifier || null;
}

function langueLabel(code) {
    const map = { fr: 'Français', en: 'Anglais', es: 'Espagnol', de: 'Allemand', it: 'Italien', pt: 'Portugais', ar: 'Arabe' };
    return map[code] || code || null;
}

const COVER_COLORS = [
    'linear-gradient(135deg,#024764,#012e41)',
    'linear-gradient(135deg,#1a237e,#283593)',
    'linear-gradient(135deg,#4a148c,#6a1b9a)',
    'linear-gradient(135deg,#880e4f,#ad1457)',
    'linear-gradient(135deg,#1b5e20,#2e7d32)',
    'linear-gradient(135deg,#bf360c,#d84315)',
    'linear-gradient(135deg,#37474f,#455a64)',
    'linear-gradient(135deg,#006064,#00838f)',
];

function escapeHtml(text) {
    if (text == null) return '';
    const d = document.createElement('div');
    d.appendChild(document.createTextNode(String(text)));
    return d.innerHTML;
}