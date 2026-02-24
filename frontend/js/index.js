/**
 * Script pour la page d'accueil.
 */

document.addEventListener('DOMContentLoaded', async () => {
    await loadBibliotheques();
});

/**
 * Charge et affiche toutes les bibliothèques.
 */
async function loadBibliotheques() {
    const container = document.getElementById('bibliotheques-container');
    
    try {
        const bibliotheques = await BibliothequeAPI.getAll();
        
        if (bibliotheques.length === 0) {
            container.innerHTML = '<p class="info-message">Aucune bibliothèque disponible.</p>';
            return;
        }

        container.innerHTML = '';
        container.classList.remove('loading');

        bibliotheques.forEach(biblio => {
            const card = createBibliothequeCard(biblio);
            container.appendChild(card);
        });
    } catch (error) {
        container.innerHTML = `<p class="error-message">Erreur lors du chargement: ${error.message}</p>`;
    }
}

/**
 * Crée une carte pour une bibliothèque.
 */
function createBibliothequeCard(biblio) {
    const card = document.createElement('div');
    card.className = 'card';
    
    const typeIcon = biblio.type === 'UNIVERSITAIRE' ? '🎓' : '📚';
    const noteStars = '⭐'.repeat(Math.round(biblio.noteGlobale));
    
    card.innerHTML = `
        <div class="card-header">
            <h4 class="card-title">${typeIcon} ${biblio.nom}</h4>
            <span class="badge badge-info">${biblio.type}</span>
        </div>
        <p><strong>📍 Adresse:</strong> ${biblio.adresse}</p>
        <p><strong>⭐ Note:</strong> ${noteStars} ${biblio.noteGlobale.toFixed(1)} (${biblio.nombreNotations} avis)</p>
        <p><strong>🕐 Horaires:</strong> ${formatHoraires(biblio.horaires)}</p>
    `;
    
    return card;
}

/**
 * Formate les horaires pour l'affichage.
 */
function formatHoraires(horaires) {
    if (!horaires || horaires.length === 0) {
        return 'Non disponibles';
    }
    
    const premier = horaires[0];
    return `${premier.heureOuverture} - ${premier.heureFermeture}`;
}
