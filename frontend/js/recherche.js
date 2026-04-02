let map; // Variable globale pour la carte Leaflet
let markers = []; // Stockage des marqueurs

document.addEventListener('DOMContentLoaded', () => {
    const searchForm = document.getElementById('search-form');
    searchForm.addEventListener('submit', handleSearch);
    
    initializeMap();
});

function initializeMap() {
    map = L.map('map').setView([48.8566, 2.3522], 12); // Paris par défaut
    
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
        maxZoom: 19
    }).addTo(map);
}

function clearMarkers() {
    markers.forEach(marker => map.removeLayer(marker));
    markers = [];
}

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
            document.getElementById('map-section').style.display = 'none';
            return;
        }

        // Afficher la carte et les résultats
        document.getElementById('map-section').style.display = 'block';
        displayResults(resultats);
        displayMap(resultats);
    } catch (error) {
        resultsContainer.innerHTML = `<p class="error-message">Erreur lors de la recherche: ${error.message}</p>`;
        document.getElementById('map-section').style.display = 'none';
    }
}

function displayMap(resultats) {
    clearMarkers();

    if (resultats.length === 0) return;

    // Récupérer les coordonnées de l'adresse recherchée (depuis le premier résultat)
    const searchLat = resultats[0].searchLatitude;
    const searchLon = resultats[0].searchLongitude;

    // Ajouter un marqueur pour l'adresse recherchée (en rouge)
    if (searchLat && searchLon) {
        const searchMarker = L.marker([searchLat, searchLon], {
            icon: L.icon({
                iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
                shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
                iconSize: [25, 41],
                iconAnchor: [12, 41],
                popupAnchor: [1, -34],
                shadowSize: [41, 41]
            })
        }).addTo(map);

        searchMarker.bindPopup(`<b>Votre adresse</b><br/>${resultats[0].searchLatitude.toFixed(4)}, ${resultats[0].searchLongitude.toFixed(4)}`);
        markers.push(searchMarker);
    }

    // Ajouter les marqueurs pour les bibliothèques (en bleu)
    resultats.forEach((biblio, index) => {
        if (biblio.latitude && biblio.longitude) {
            const marker = L.marker([biblio.latitude, biblio.longitude], {
                icon: L.icon({
                    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',
                    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
                    iconSize: [25, 41],
                    iconAnchor: [12, 41],
                    popupAnchor: [1, -34],
                    shadowSize: [41, 41]
                })
            }).addTo(map);

            marker.bindPopup(`
                <b>${biblio.nom}</b><br/>
                ${biblio.adresse}<br/>
                Distance: ${biblio.distance} km<br/>
                Type: ${biblio.type || '-'}
            `);

            markers.push(marker);
        }
    });

   // 1. zoom immédiat sur l'adresse utilisateur (priorité)
   if (searchLat && searchLon) {
       map.setView([searchLat, searchLon], 14); // 👈 zoom plus précis (14 = ville/ quartier)
   }

   // 2. petit délai pour laisser Leaflet stabiliser
   setTimeout(() => {

       if (markers.length > 0) {
           const group = new L.featureGroup(markers);

           map.fitBounds(group.getBounds(), {
               padding: [50, 50],   // 👈 marge propre
               maxZoom: 15          // 👈 empêche zoom trop large
           });
       }

   }, 300);

   setTimeout(() => {
       map.invalidateSize();
   }, 200);
}

function displayResults(resultats) {
    const container = document.getElementById('results-container');
    container.innerHTML = '';

    resultats.forEach(biblio => {
        const card = createResultCard(biblio);
        container.appendChild(card);
    });
}

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
