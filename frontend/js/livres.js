/**
 * Recherche de livres via Google Books API
 * + disponibilité dans les bibliothèques IDF (géolocalisation automatique)
 *
 * Au chargement de la page :
 *   1. Géolocalisation silencieuse de l'utilisateur
 *   2. Reverse geocode → adresse lisible
 *   3. Recherche automatique des bibliothèques IDF dans un rayon de 5 km
 *   4. Résultats stockés en mémoire → utilisés pour chaque recherche de livre
 *
 * Lors d'une recherche de livre :
 *   1. Google Books → cartes visuelles
 *   2. POST /api/livres/disponibilite avec le titre + les bibliothèques proches
 *   3. Badge mis à jour : liste des bibliothèques qui ont le livre
 */

// Bibliothèques IDF chargées automatiquement au démarrage
let bibliothequesCourantes = [];
let geolocChargement = null; // Promise de chargement (évite double appel)

// ─── Initialisation ───────────────────────────────────────────────────────────

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

    // Lancer la géolocalisation + chargement des bibliothèques en arrière-plan
    // dès l'ouverture de la page, sans attendre que l'utilisateur cherche
    geolocChargement = chargerBibliothequesCourantes();
});

/**
 * Géolocalise l'utilisateur silencieusement, puis charge les bibliothèques IDF proches.
 * Stocke le résultat dans bibliothequesCourantes.
 */
async function chargerBibliothequesCourantes() {
    try {
        // 1. Obtenir la position GPS
        const position = await new Promise((resolve, reject) => {
            if (!navigator.geolocation) {
                reject(new Error('Géolocalisation non supportée'));
                return;
            }
            navigator.geolocation.getCurrentPosition(resolve, reject, {
                enableHighAccuracy: false,
                timeout: 8000,
                maximumAge: 60000 // accepter une position en cache jusqu'à 1 min
            });
        });

        const lat = position.coords.latitude;
        const lon = position.coords.longitude;

        // 2. Reverse geocode → adresse lisible pour l'API backend
        const adresse = await reverseGeocode(lat, lon)
                     || `${lat.toFixed(5)}, ${lon.toFixed(5)}`;

        // 3. Appeler /api/bibliotheques/recherche avec rayon 5 km, horaires larges
        const resultats = await BibliothequeAPI.rechercher(adresse, '00:00', '23:59', 5);
        bibliothequesCourantes = resultats || [];

    } catch (err) {
        // Géolocalisation refusée ou timeout → tableau vide, badges neutres
        bibliothequesCourantes = [];
        console.warn('[IDF] Géolocalisation impossible:', err.message);
    }
}

async function reverseGeocode(lat, lon) {
    try {
        const res  = await fetch(
            `https://api-adresse.data.gouv.fr/reverse/?lon=${lon}&lat=${lat}`
        );
        const data = await res.json();
        if (data.features && data.features.length > 0) {
            return data.features[0].properties.label;
        }
        return null;
    } catch {
        return null;
    }
}

// ─── Recherche principale ─────────────────────────────────────────────────────

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
        // Attendre que le chargement des bibliothèques soit terminé
        // (s'il est encore en cours au moment où l'utilisateur cherche)
        if (geolocChargement) await geolocChargement;

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

        renderBooks(books);

        // Vérifier la dispo dans les bibliothèques IDF en arrière-plan
        checkDisponibiliteIDF(books, query);

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

// ─── Disponibilité dans les bibliothèques IDF ─────────────────────────────────

async function checkDisponibiliteIDF(books, query) {
    // Si aucune bibliothèque n'a été chargée (géoloc refusée), badge neutre
    if (!bibliothequesCourantes || bibliothequesCourantes.length === 0) {
        books.forEach(b => updateBadgeIDF(b.id, 'geo-indisponible', null));
        return;
    }

    // Appels en parallèle pour tous les livres
    const promises = books.map(async (book) => {
        try {
            const response = await fetch(`${API_BASE_URL}/livres/disponibilite`, {
                method:  'POST',
                headers: {
                    'Content-Type':  'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
                },
                body: JSON.stringify({
                    titre:         book.titre,
                    bibliotheques: bibliothequesCourantes.map(b => ({
                        nom:      b.nom,
                        adresse:  b.adresse,
                        distance: b.distance,
                        ouvert:   b.ouvert
                    }))
                })
            });

            if (!response.ok) {
                updateBadgeIDF(book.id, 'erreur', null);
                return;
            }

            const resultats    = await response.json();
            const disponibles  = resultats.filter(r => r.livreDisponible);

            if (disponibles.length > 0) {
                updateBadgeIDF(book.id, 'disponible', disponibles);
            } else {
                updateBadgeIDF(book.id, 'non-disponible', null);
            }

        } catch (err) {
            updateBadgeIDF(book.id, 'erreur', null);
        }
    });

    await Promise.allSettled(promises);
}

function updateBadgeIDF(bookId, statut, bibliotheques) {
    const badge = document.getElementById(`idf-badge-${bookId}`);
    if (!badge) return;

    if (statut === 'disponible') {
        const liste = bibliotheques.map(b => `
            <li class="badge-biblio-item ${b.ouvert ? 'ouvert' : 'ferme'}">
                <i class="fas fa-${b.ouvert ? 'door-open' : 'door-closed'}"></i>
                <span class="badge-biblio-nom">${escapeHtml(b.nomBibliotheque)}</span>
                <span class="badge-biblio-dist">${b.distance != null ? b.distance.toFixed(1) + ' km' : ''}</span>
                <span class="badge-biblio-statut">${b.ouvert ? 'Ouvert' : 'Fermé'}</span>
            </li>`).join('');

        badge.innerHTML = `
            <div class="badge-dispo-header">
                <i class="fas fa-check-circle"></i>
                <span>Disponible dans ${bibliotheques.length} bibliothèque${bibliotheques.length > 1 ? 's' : ''} près de vous</span>
            </div>
            <ul class="badge-biblio-list">${liste}</ul>`;
        badge.className = 'idf-badge idf-dispo';

    } else if (statut === 'non-disponible') {
        badge.innerHTML = `<i class="fas fa-times-circle"></i> <span>Non disponible dans les bibliothèques proches</span>`;
        badge.className = 'idf-badge idf-indispo';

    } else if (statut === 'geo-indisponible') {
        badge.innerHTML = `<i class="fas fa-location-slash"></i> <span>Activez la géolocalisation pour voir la disponibilité locale</span>`;
        badge.className = 'idf-badge idf-inconnu';

    } else {
        badge.innerHTML = `<i class="fas fa-question-circle"></i> <span>Disponibilité non vérifiée</span>`;
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
            ${index < 3 ? `
                <div class="recommended-badge">
                    <i class="fas fa-award"></i>
                    Top ${index + 1}
                </div>
            ` : ''}

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

            <div id="idf-badge-${b.id}" class="idf-badge idf-loading">
                <i class="fas fa-spinner fa-spin"></i>
                <span>Vérification dans vos bibliothèques...</span>
            </div>

            ${index < 3 ? `
                <div class="recommendation-info">
                    <i class="fas fa-star"></i>
                    Livre recommandé par l'algorithme d'optimisation
                </div>
            ` : ''}

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