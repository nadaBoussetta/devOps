/**
 * Script pour la page de recherche de bibliothèques.
 * Affiche les résultats avec toutes les informations et bouton favoris.
 */

document.addEventListener('DOMContentLoaded', () => {
    const searchForm = document.getElementById('search-form');
    searchForm.addEventListener('submit', handleSearch);
});

/**
 * Gère la soumission du formulaire de recherche.
 */
async function handleSearch(e) {
    e.preventDefault();

    const adresse = document.getElementById('adresse').value;
    const heureDebut = document.getElementById('heureDebut').value;
    const heureFin = document.getElementById('heureFin').value;
    const rayon = parseFloat(document.getElementById('rayon').value);

    const resultsContainer = document.getElementById('results-container');
    resultsContainer.innerHTML = '<p class="loading">Recherche en cours...</p>';

    try {
        const resultats = await BibliothequeAPI.rechercher(adresse, heureDebut, heureFin, rayon);

        if (resultats.length === 0) {
            resultsContainer.innerHTML = '<p class="info-message">Aucune bibliothèque trouvée avec ces critères.</p>';
            return;
        }

        displayResults(resultats);
    } catch (error) {
        resultsContainer.innerHTML = `<p class="error-message">Erreur lors de la recherche: ${error.message}</p>`;
    }
}

/**
 * Affiche les résultats de la recherche.
 */
function displayResults(resultats) {
    const container = document.getElementById('results-container');
    container.innerHTML = '';

    resultats.forEach(biblio => {
        const card = createResultCard(biblio);
        container.appendChild(card);
    });
}

/**
 * Crée une carte détaillée pour une bibliothèque.
 */
function createResultCard(biblio) {
    const card = document.createElement('div');
    card.className = 'card';

    const typeIcon = biblio.type === 'UNIVERSITAIRE' ? '🎓' : '📚';
    const noteStars = '⭐'.repeat(Math.round(biblio.noteGlobale || 0));
    const ouvertBadge = biblio.ouvert
        ? '<span class="badge badge-success">✓ Ouvert</span>'
        : '<span class="badge badge-danger">✗ Fermé</span>';

    card.innerHTML = `
        <div class="card-header">
            <h4 class="card-title">${typeIcon} ${biblio.nom}</h4>
            ${ouvertBadge}
        </div>
        <p><strong>📍 Adresse:</strong> ${biblio.adresse}</p>
        <p><strong>📏 Distance:</strong> ${biblio.distance || '-'} km</p>
        <p><strong>⭐ Note:</strong> ${noteStars} ${biblio.noteGlobale ? biblio.noteGlobale.toFixed(1) : '-'} (${biblio.nombreNotations || 0} avis)</p>
        <p><strong>🏷️ Type:</strong> ${biblio.type || '-'}</p>
        <details>
            <summary style="cursor: pointer; font-weight: 600; margin-top: 1rem;">🕐 Voir les horaires</summary>
            <div style="margin-top: 0.5rem;">
                ${formatHorairesDetailles(biblio.horaires)}
            </div>
        </details>
        <button onclick="ajouterAuxFavoris(${biblio.id})" class="btn-favori">❤️ Ajouter aux favoris</button>
    `;

    return card;
}

/**
 * Formate les horaires détaillés pour l'affichage.
 */
function formatHorairesDetailles(horaires) {
    if (!horaires || horaires.length === 0) return '<p>Horaires non disponibles</p>';

    const joursOrdre = ['LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI', 'DIMANCHE'];
    const horairesTries = horaires.sort((a, b) =>
        joursOrdre.indexOf(a.jourSemaine) - joursOrdre.indexOf(b.jourSemaine)
    );

    let html = '<ul style="list-style: none; padding-left: 0;">';
    horairesTries.forEach(h => {
        html += `<li><strong>${h.jourSemaine}:</strong> ${h.heureOuverture} - ${h.heureFermeture}</li>`;
    });
    html += '</ul>';

    return html;
}

/**
 * Envoie une bibliothèque dans les favoris.
 */
async function ajouterAuxFavoris(bibliothequeId) {
    try {
        const response = await fetch('http://localhost:8080/api/favoris', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify({ bibliothequeId })
        });

        if (!response.ok) throw new Error("Impossible d'ajouter aux favoris");
        alert("Bibliothèque ajoutée aux favoris !");
    } catch (err) {
        console.error(err);
        alert(err.message);
    }
}
