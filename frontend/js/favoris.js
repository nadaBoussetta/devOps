document.addEventListener('DOMContentLoaded', () => {
    loadFavoris();
});

async function loadFavoris() {
    const container = document.getElementById('favoris-container');
    container.innerHTML = '<p class="loading">Chargement de vos favoris...</p>';

    try {
        const response = await fetch('http://localhost:8080/api/favoris', {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (!response.ok) throw new Error("Impossible de récupérer les favoris");
        const favoris = await response.json();

        if (favoris.length === 0) {
            container.innerHTML = '<p class="info-message">Vous n’avez aucun favori pour le moment.</p>';
            return;
        }

        container.innerHTML = '';
        favoris.forEach(f => {
            const card = createFavoriCard(f);
            container.appendChild(card);
        });

    } catch (err) {
        container.innerHTML = `<p class="error-message">${err.message}</p>`;
        console.error(err);
    }
}

function createFavoriCard(favori) {
    const card = document.createElement('div');
    card.className = 'card';

    const typeIcon = favori.type === 'UNIVERSITAIRE' ? '🎓' : '📚'; // si tu as type
    const noteStars = '⭐'.repeat(Math.round(favori.noteGlobale || 0));

    const ouvertBadge = favori.ouvert 
        ? '<span class="badge badge-success">✓ Ouvert</span>'
        : '<span class="badge badge-danger">✗ Fermé</span>';

    card.innerHTML = `
        <div class="card-header">
            <h4 class="card-title">${typeIcon} ${favori.bibliothequeNom}</h4>
            ${ouvertBadge}
        </div>
        <button onclick="supprimerFavori(${favori.id})">❌ Retirer des favoris</button>
    `;

    return card;
}

async function supprimerFavori(favoriId) {
    if (!confirm("Voulez-vous vraiment supprimer ce favori ?")) return;

    try {
        const response = await fetch(`http://localhost:8080/api/favoris/${favoriId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (!response.ok) throw new Error("Impossible de supprimer le favori");
        alert("Favori supprimé !");
        loadFavoris(); // recharge la liste
    } catch (err) {
        console.error(err);
        alert(err.message);
    }
}