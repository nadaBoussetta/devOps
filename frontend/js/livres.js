/**
 * Script pour la page de recherche de livres.
 * Implémente la Feature 2 : Recherche de livres via le pattern Adapter.
 */

document.addEventListener('DOMContentLoaded', () => {
    const bookSearchForm = document.getElementById('book-search-form');
    bookSearchForm.addEventListener('submit', handleBookSearch);
});

/**
 * Gère la soumission du formulaire de recherche de livres.
 */
async function handleBookSearch(e) {
    e.preventDefault();
    
    const titre = document.getElementById('titre').value;
    const resultsContainer = document.getElementById('book-results-container');
    
    resultsContainer.innerHTML = '<p class="loading">Recherche en cours dans les bibliothèques universitaires...</p>';

    try {
        const resultats = await LivreAPI.rechercher(titre);
        
        if (resultats.length === 0) {
            resultsContainer.innerHTML = '<p class="info-message">Aucun livre trouvé avec ce titre.</p>';
            return;
        }

        displayBookResults(resultats);
    } catch (error) {
        resultsContainer.innerHTML = `<p class="error-message">Erreur lors de la recherche: ${error.message}</p>`;
    }
}

/**
 * Affiche les résultats de la recherche de livres.
 */
function displayBookResults(resultats) {
    const container = document.getElementById('book-results-container');
    container.innerHTML = '';

    // Grouper les résultats par bibliothèque
    const parBibliotheque = {};
    resultats.forEach(livre => {
        if (!parBibliotheque[livre.bibliotheque]) {
            parBibliotheque[livre.bibliotheque] = [];
        }
        parBibliotheque[livre.bibliotheque].push(livre);
    });

    // Afficher les résultats groupés
    for (const [bibliotheque, livres] of Object.entries(parBibliotheque)) {
        const section = document.createElement('div');
        section.className = 'card';
        
        section.innerHTML = `
            <h4 style="margin-bottom: 1rem; color: var(--primary-color);">
                🎓 ${bibliotheque} (${livres.length} résultat${livres.length > 1 ? 's' : ''})
            </h4>
            <div class="books-list">
                ${livres.map(livre => createBookCard(livre)).join('')}
            </div>
        `;
        
        container.appendChild(section);
    }
}

/**
 * Crée une carte pour un livre.
 */
function createBookCard(livre) {
    const disponibleBadge = livre.disponible
        ? '<span class="badge badge-success">✓ Disponible</span>'
        : '<span class="badge badge-danger">✗ Indisponible</span>';
    
    return `
        <div style="padding: 1rem; border: 1px solid var(--border); border-radius: 0.5rem; margin-bottom: 1rem;">
            <div style="display: flex; justify-content: space-between; align-items: start; margin-bottom: 0.5rem;">
                <div>
                    <h5 style="margin: 0; color: var(--dark);">📖 ${livre.titre}</h5>
                    <p style="margin: 0.25rem 0; color: #6b7280;">par ${livre.auteur}</p>
                </div>
                ${disponibleBadge}
            </div>
            <p style="margin: 0.5rem 0;"><strong>Cote:</strong> ${livre.cote || 'N/A'}</p>
            ${livre.isbn ? `<p style="margin: 0.5rem 0;"><strong>ISBN:</strong> ${livre.isbn}</p>` : ''}
        </div>
    `;
}
