/**
 * Script pour la page des favoris.
 * Affiche les bibliothèques favorites de l'utilisateur.
 */

document.addEventListener('DOMContentLoaded', async () => {
    // Vérifier l'authentification
    if (!isAuthenticated()) {
        alert('Vous devez être connecté pour accéder à vos favoris.');
        window.location.href = 'login.html';
        return;
    }

    // Charger les favoris
    await loadFavoris();
});

/**
 * Charge et affiche les favoris de l'utilisateur.
 */
async function loadFavoris() {
    const container = document.getElementById('favoris-container');
    
    try {
        const favoris = await NotationAPI.getMesFavoris();
        
        if (favoris.length === 0) {
            container.innerHTML = '<p class="info-message">Vous n\'avez aucune bibliothèque en favoris. Commencez à en ajouter !</p>';
            return;
        }

        container.innerHTML = '';
        container.classList.remove('loading');

        favoris.forEach(favori => {
            const card = createFavoriCard(favori);
            container.appendChild(card);
        });
    } catch (error) {
        container.innerHTML = `<p class="error-message">Erreur lors du chargement: ${error.message}</p>`;
    }
}

/**
 * Crée une carte pour un favori.
 */
function createFavoriCard(favori) {
    const card = document.createElement('div');
    card.className = 'card';
    
    const dateAjout = new Date(favori.dateAjout).toLocaleDateString('fr-FR', {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
    });
    
    card.innerHTML = `
        <div class="card-header">
            <h4 class="card-title">📚 ${favori.bibliothequeNom}</h4>
            <span class="badge badge-success">★ Favori</span>
        </div>
        <p><strong>Ajouté le:</strong> ${dateAjout}</p>
        <div class="card-actions">
            <button onclick="removeFavori(${favori.id}, ${favori.bibliothequeId})" class="btn btn-danger">Supprimer des favoris</button>
        </div>
    `;
    
    return card;
}

/**
 * Supprime un favori.
 */
async function removeFavori(favoriId, bibliothequeId) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce favori ?')) {
        try {
            await NotationAPI.supprimerFavori(bibliothequeId);
            await loadFavoris();
        } catch (error) {
            alert('Erreur lors de la suppression: ' + error.message);
        }
    }
}
