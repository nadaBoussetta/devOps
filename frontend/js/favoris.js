document.addEventListener('DOMContentLoaded', async () => {
    if (!isAuthenticated()) {
        alert('Vous devez être connecté pour accéder à vos favoris.');
        window.location.href = 'login.html';
        return;
    }
    await loadFavoris();
});

// =============================================================================
// CHARGEMENT
// =============================================================================

async function loadFavoris() {
    const container = document.getElementById('favoris-container');
    try {
        const favoris = await fetchAPI('/favoris');

        if (!favoris || favoris.length === 0) {
            container.innerHTML = `
                <p class="info-message">
                    <i class="fas fa-heart-broken"></i>
                    Vous n'avez aucune bibliothèque en favoris.
                    <a href="recherche.html" style="color:rgb(2,71,100); margin-left:6px;">En ajouter ?</a>
                </p>`;
            return;
        }

        container.innerHTML = '';
        favoris.forEach(favori => container.appendChild(createFavoriCard(favori)));

    } catch (error) {
        container.innerHTML = `<p class="error-message"><i class="fas fa-exclamation-circle"></i> Erreur lors du chargement : ${error.message}</p>`;
    }
}

// =============================================================================
// CRÉATION DES CARTES
// =============================================================================

function createFavoriCard(favori) {
    const card = document.createElement('div');
    card.className = 'card';

    const typeIcon  = favori.bibliothequeType === 'UNIVERSITAIRE' ? '🎓' : '📚';
    const adresse   = favori.bibliothequeAdresse
        ? `<p><strong>📍 Adresse :</strong> <span>${favori.bibliothequeAdresse}</span></p>` : '';
    const typeLabel = favori.bibliothequeType
        ? `<p><strong>🏷️ Type :</strong> <span>${favori.bibliothequeType}</span></p>` : '';
    const dateAjout = favori.dateAjout
        ? new Date(favori.dateAjout).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })
        : '—';

    // Note personnelle stockée en localStorage par id
    const storageKey  = `favori-note-${favori.bibliothequeId}`;
    const noteActuelle = localStorage.getItem(storageKey) || '';

    card.innerHTML = `
        <div class="card-header">
            <h4 class="card-title">${typeIcon} ${favori.bibliothequeNom}</h4>
            <span class="badge badge-success">★ Favori</span>
        </div>
        ${adresse}
        ${typeLabel}
        <p><strong>📅 Ajouté le :</strong> <span>${dateAjout}</span></p>

        <textarea class="description-textarea" rows="2" placeholder="Ajouter une note personnelle...">${noteActuelle}</textarea>
        <button class="description-save-btn"><i class="fas fa-save"></i> Sauvegarder la note</button>

        <div class="card-actions">
            <button class="btn btn-danger">
                <i class="fas fa-trash"></i> Supprimer
            </button>
        </div>
    `;

    // Sauvegarder la note en localStorage
    const textarea = card.querySelector('.description-textarea');
    const saveBtn  = card.querySelector('.description-save-btn');
    saveBtn.addEventListener('click', () => {
        localStorage.setItem(storageKey, textarea.value.trim());
        saveBtn.innerHTML = '<i class="fas fa-check"></i> Note sauvegardée !';
        setTimeout(() => { saveBtn.innerHTML = '<i class="fas fa-save"></i> Sauvegarder la note'; }, 1800);
    });

    // Supprimer le favori
    const btnDelete = card.querySelector('.btn-danger');
    btnDelete.dataset.bibliothequeId = favori.bibliothequeId;
    btnDelete.addEventListener('click', () => removeFavori(btnDelete));

    return card;
}

// =============================================================================
// SUPPRESSION
// =============================================================================

async function removeFavori(btn) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer ce favori ?')) return;
    try {
        await fetchAPI(`/favoris/${btn.dataset.bibliothequeId}`, { method: 'DELETE' });
        localStorage.removeItem(`favori-note-${btn.dataset.bibliothequeId}`);
        await loadFavoris();
    } catch (error) {
        alert('Erreur lors de la suppression : ' + error.message);
    }
}