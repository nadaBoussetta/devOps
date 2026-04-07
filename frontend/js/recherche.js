let map; // Variable globale pour la carte Leaflet
let markers = []; // Stockage des marqueurs
let autocompleteTimeout; // Timeout pour l'autocomplétion

document.addEventListener('DOMContentLoaded', () => {
    const searchForm = document.getElementById('search-form');
    searchForm.addEventListener('submit', handleSearch);

    // Bouton de recherche avancée (itinéraire optimisé)
    const btnItineraire = document.getElementById('btn-itineraire');
    if (btnItineraire) {
        btnItineraire.addEventListener('click', handleItineraire);
    }

    // Initialiser la géolocalisation
    initializeGeolocation();

    // Initialiser l'autocomplétion
    initializeAutocomplete();

    initializeMap();
});

/**
 * Initialise la géolocalisation du navigateur
 */
function initializeGeolocation() {
    const geolocationBtn = document.getElementById('geolocation-btn');

    if (!navigator.geolocation) {
        geolocationBtn.style.display = 'none';
        return;
    }

    geolocationBtn.addEventListener('click', (e) => {
        e.preventDefault();
        useCurrentLocation();
    });
}

/**
 * Utilise la localisation actuelle de l'utilisateur
 */
function useCurrentLocation() {
    const geolocationBtn = document.getElementById('geolocation-btn');
    const addressInput = document.getElementById('adresse');

    geolocationBtn.classList.add('loading');
    geolocationBtn.disabled = true;

    navigator.geolocation.getCurrentPosition(
        (position) => {
            const lat = position.coords.latitude;
            const lon = position.coords.longitude;

            // Appeler l'API de géocodage inverse pour obtenir l'adresse
            reverseGeocode(lat, lon)
                .then(address => {
                    addressInput.value = address || `${lat.toFixed(4)}, ${lon.toFixed(4)}`;
                    geolocationBtn.classList.remove('loading');
                    geolocationBtn.disabled = false;
                    document.getElementById('autocomplete-suggestions').style.display = 'none';
                })
                .catch(error => {
                    console.error('Erreur lors du géocodage inverse:', error);
                    addressInput.value = `${lat.toFixed(4)}, ${lon.toFixed(4)}`;
                    geolocationBtn.classList.remove('loading');
                    geolocationBtn.disabled = false;
                });
        },
        (error) => {
            console.error('Erreur de géolocalisation:', error);
            alert('Impossible d\'accéder à votre localisation. Veuillez vérifier les permissions du navigateur.');
            geolocationBtn.classList.remove('loading');
            geolocationBtn.disabled = false;
        },
        {
            enableHighAccuracy: true,
            timeout: 10000,
            maximumAge: 0
        }
    );
}

/**
 * Effectue un géocodage inverse pour obtenir l'adresse à partir des coordonnées
 */
async function reverseGeocode(lat, lon) {
    try {
        const response = await fetch(`https://api-adresse.data.gouv.fr/reverse/?lon=${lon}&lat=${lat}`);
        const data = await response.json();

        if (data.features && data.features.length > 0) {
            return data.features[0].properties.label;
        }
        return null;
    } catch (error) {
        console.error('Erreur lors du géocodage inverse:', error);
        return null;
    }
}

/**
 * Initialise l'autocomplétion d'adresses
 */
function initializeAutocomplete() {
    const addressInput = document.getElementById('adresse');
    const suggestionsContainer = document.getElementById('autocomplete-suggestions');

    addressInput.addEventListener('input', (e) => {
        const query = e.target.value.trim();

        // Masquer les suggestions si le champ est vide
        if (query.length < 2) {
            suggestionsContainer.style.display = 'none';
            return;
        }

        // Effacer le timeout précédent
        clearTimeout(autocompleteTimeout);

        // Ajouter un délai avant de faire la requête
        autocompleteTimeout = setTimeout(() => {
            fetchAddressSuggestions(query);
        }, 300);
    });

    // Masquer les suggestions quand on clique ailleurs
    document.addEventListener('click', (e) => {
        if (e.target !== addressInput && e.target !== suggestionsContainer) {
            suggestionsContainer.style.display = 'none';
        }
    });
}

/**
 * Récupère les suggestions d'adresses depuis l'API
 */
async function fetchAddressSuggestions(query) {
    try {
        const response = await fetch(`http://localhost:8080/api/adresses/autocomplete?query=${encodeURIComponent(query)}&limit=5`);
        const suggestions = await response.json();

        displayAddressSuggestions(suggestions);
    } catch (error) {
        console.error('Erreur lors de la récupération des suggestions:', error);
        // Fallback: utiliser l'API directement
        fallbackAddressSuggestions(query);
    }
}

/**
 * Fallback si le backend n'est pas disponible
 */
async function fallbackAddressSuggestions(query) {
    try {
        const response = await fetch(`https://api-adresse.data.gouv.fr/search/?q=${encodeURIComponent(query)}&limit=5`);
        const data = await response.json();

        const suggestions = data.features.map(feature => ({
            label: feature.properties.label,
            name: feature.properties.name,
            city: feature.properties.city,
            postcode: feature.properties.postcode,
            latitude: feature.geometry.coordinates[1],
            longitude: feature.geometry.coordinates[0],
            score: feature.properties.score
        }));

        displayAddressSuggestions(suggestions);
    } catch (error) {
        console.error('Erreur lors du fallback:', error);
    }
}

/**
 * Affiche les suggestions d'adresses
 */
function displayAddressSuggestions(suggestions) {
    const suggestionsContainer = document.getElementById('autocomplete-suggestions');

    if (!suggestions || suggestions.length === 0) {
        suggestionsContainer.style.display = 'none';
        return;
    }

    suggestionsContainer.innerHTML = '';

    suggestions.forEach(suggestion => {
        const div = document.createElement('div');
        div.className = 'autocomplete-suggestion';

        const mainText = suggestion.label || suggestion.name;
        const subText = suggestion.postcode ? `${suggestion.postcode} ${suggestion.city}` : suggestion.city;

        div.innerHTML = `
            <div class="autocomplete-suggestion-main">${mainText}</div>
            <div class="autocomplete-suggestion-sub">${subText}</div>
        `;

        div.addEventListener('click', () => {
            selectAddressSuggestion(suggestion);
        });

        suggestionsContainer.appendChild(div);
    });

    suggestionsContainer.style.display = 'block';
}

/**
 * Sélectionne une suggestion d'adresse
 */
function selectAddressSuggestion(suggestion) {
    const addressInput = document.getElementById('adresse');
    const suggestionsContainer = document.getElementById('autocomplete-suggestions');

    addressInput.value = suggestion.label || suggestion.name;
    suggestionsContainer.style.display = 'none';
}

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

    const itineraireSection = document.getElementById('itineraire-section');
    itineraireSection.style.display = 'none';

    document.getElementById('itineraire-meta').innerHTML = '';
    document.getElementById('itineraire-steps').innerHTML = '';
    document.getElementById('itineraire-message').innerHTML = '';

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
        // Formater les horaires de manière lisible
        const debut = formatTime(h.heureOuverture);
        const fin = formatTime(h.heureFermeture);
        const jourFormate = formatJourSemaine(h.jourSemaine);
        html += `<li style="padding: 5px 0; border-bottom: 1px solid #eee;"><strong>${jourFormate}:</strong> ${debut} – ${fin}</li>`;
    });
    html += '</ul>';

    return html;
}

/**
 * Formate une heure au format HH:mm en format lisible
 */
function formatTime(timeString) {
    if (!timeString) return '-';
    const [hours, minutes] = timeString.split(':');
    return `${hours}h${minutes !== '00' ? minutes : ''}`;
}

/**
 * Formate le nom du jour de la semaine
 */
function formatJourSemaine(jour) {
    const jours = {
        'LUNDI': 'Lundi',
        'MARDI': 'Mardi',
        'MERCREDI': 'Mercredi',
        'JEUDI': 'Jeudi',
        'VENDREDI': 'Vendredi',
        'SAMEDI': 'Samedi',
        'DIMANCHE': 'Dimanche'
    };
    return jours[jour] || jour;
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

// =============================================================================
// RECHERCHE AVANCÉE - ITÍNÉRAIRE OPTIMISÉ
// =============================================================================

/**
 * Gestionnaire du bouton "Recherche Avancée".
 * Déclenche le calcul de l'itinéraire optimisé sans toucher à la recherche standard.
 */
async function handleItineraire() {

    const resultsContainer = document.getElementById('results-container');
    resultsContainer.innerHTML = '';

    document.getElementById('map-section').style.display = 'none';

    const adresse = document.getElementById('adresse').value.trim();
    const heureDebut = document.getElementById('heureDebut').value;
    const heureFin = document.getElementById('heureFin').value;
    const rayon = parseFloat(document.getElementById('rayon').value);

    if (!adresse) {
        alert('Veuillez saisir une adresse avant de lancer la recherche avancée.');
        return;
    }

    const btnItineraire = document.getElementById('btn-itineraire');
    btnItineraire.disabled = true;
    btnItineraire.textContent = 'Calcul en cours...';

    // Masquer les résultats précédents de l'itinéraire
    const itineraireSection = document.getElementById('itineraire-section');
    itineraireSection.style.display = 'none';

    try {
        const itineraire = await BibliothequeAPI.rechercherItineraire(adresse, heureDebut, heureFin, rayon);
        displayItineraire(itineraire);
    } catch (error) {
        const itineraireSection = document.getElementById('itineraire-section');
        itineraireSection.style.display = 'block';
        document.getElementById('itineraire-meta').innerHTML = '';
        document.getElementById('itineraire-steps').innerHTML = '';
        const msgEl = document.getElementById('itineraire-message');
        msgEl.className = 'itineraire-message error';
        msgEl.textContent = 'Erreur lors du calcul de l\'itinéraire : ' + error.message;
    } finally {
        btnItineraire.disabled = false;
        btnItineraire.innerHTML = '&#128205; Recherche Avancée (Itinéraire Optimisé)';
    }
}

/**
 * Affiche l'itinéraire optimisé dans la section dédiée.
 * @param {Object} itineraire - La réponse de l'API /bibliotheques/itineraire
 */
function displayItineraire(itineraire) {
    const itineraireSection = document.getElementById('itineraire-section');
    const metaContainer = document.getElementById('itineraire-meta');
    const stepsContainer = document.getElementById('itineraire-steps');
    const messageEl = document.getElementById('itineraire-message');

    itineraireSection.style.display = 'block';

    // --- Métadonnées de l'itinéraire ---
    const couvertBadgeClass = itineraire.creneauCompletementCouvert
        ? 'itineraire-badge-ok'
        : 'itineraire-badge-partial';
    const couvertLabel = itineraire.creneauCompletementCouvert
        ? '&#10003; Créneau complet couvert'
        : '&#9888; Créneau partiellement couvert';

    metaContainer.innerHTML = `
        <span>&#128205; Départ : <strong>${itineraire.adresseDepart}</strong></span>
        <span>&#128336; Créneau demandé : <strong>${itineraire.heureDebutDemandee} – ${itineraire.heureFinDemandee}</strong></span>
        <span>&#128218; ${itineraire.etapes ? itineraire.etapes.length : 0} étape(s)</span>
        <span>&#128663; Distance totale : <strong>${itineraire.distanceTotale || 0} km</strong></span>
        <span class="${couvertBadgeClass}">${couvertLabel}</span>
    `;

    // --- Étapes de l'itinéraire ---
    stepsContainer.innerHTML = '';

    if (!itineraire.etapes || itineraire.etapes.length === 0) {
        stepsContainer.innerHTML = '<p style="color:#555;">Aucune étape trouvée.</p>';
    } else {
        itineraire.etapes.forEach(etape => {
            const stepEl = createItineraireStep(etape);
            stepsContainer.appendChild(stepEl);
        });

        // Afficher l'itinéraire sur la carte
        displayItineraireOnMap(itineraire);
    }

    // --- Message de synthèse ---
    if (itineraire.message) {
        messageEl.className = 'itineraire-message ' +
            (itineraire.creneauCompletementCouvert ? 'success' : 'warning');
        messageEl.textContent = itineraire.message;
    } else {
        messageEl.textContent = '';
    }

    // Faire défiler jusqu'à la section
    itineraireSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

/**
 * Crée l'élément HTML pour une étape de l'itinéraire.
 * @param {Object} etape - Une étape de l'itinéraire
 * @returns {HTMLElement}
 */
function createItineraireStep(etape) {
    const biblio = etape.bibliotheque;
    const stepDiv = document.createElement('div');
    stepDiv.className = 'itineraire-step';

    const typeIcon = biblio.type === 'UNIVERSITAIRE' ? '&#127979;' : '&#128218;';

    stepDiv.innerHTML = `
        <div class="step-number">${etape.ordre}</div>
        <div class="step-content">
            <h4>${typeIcon} ${biblio.nom}</h4>
            <span class="step-creneau">&#128336; ${etape.creneauDebut} – ${etape.creneauFin}</span>
            <div class="step-details">
                <p><strong>&#128205; Adresse :</strong> ${biblio.adresse}</p>
                <p><strong>&#128663; Distance depuis étape précédente :</strong> ${etape.distanceDepuisPrecedent} km</p>
                <p><strong>&#128663; Distance cumulée :</strong> ${etape.distanceCumulee} km</p>
                <details>
                    <summary style="cursor:pointer;font-weight:600;margin-top:0.5rem;">&#128336; Voir les horaires complets</summary>
                    <div style="margin-top:0.4rem;">${formatHorairesDetailles(biblio.horaires)}</div>
                </details>
            </div>
        </div>
    `;

    return stepDiv;
}

/**
 * Affiche l'itinéraire sur la carte Leaflet avec des marqueurs numérotés
 * et une ligne en pointillé reliant les étapes.
 * @param {Object} itineraire - La réponse complète de l'itinéraire
 */
function displayItineraireOnMap(itineraire) {
    // Afficher la section carte
    document.getElementById('map-section').style.display = 'block';
    clearMarkers();

    const points = [];

    // Marqueur de départ (rouge)
    if (itineraire.latitudeDepart && itineraire.longitudeDepart) {
        const departMarker = L.marker(
            [itineraire.latitudeDepart, itineraire.longitudeDepart],
            {
                icon: L.icon({
                    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
                    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
                    iconSize: [25, 41],
                    iconAnchor: [12, 41],
                    popupAnchor: [1, -34],
                    shadowSize: [41, 41]
                })
            }
        ).addTo(map);
        departMarker.bindPopup(`<b>&#128205; Départ</b><br/>${itineraire.adresseDepart}`);
        markers.push(departMarker);
        points.push([itineraire.latitudeDepart, itineraire.longitudeDepart]);
    }

    // Marqueurs numérotés pour chaque étape (violet)
    const couleurs = [
        'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-violet.png',
        'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',
        'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
        'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-orange.png',
        'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-yellow.png'
    ];

    itineraire.etapes.forEach((etape, index) => {
        const biblio = etape.bibliotheque;
        if (!biblio.latitude || !biblio.longitude) return;

        const couleurUrl = couleurs[index % couleurs.length];
        const marker = L.marker([biblio.latitude, biblio.longitude], {
            icon: L.icon({
                iconUrl: couleurUrl,
                shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
                iconSize: [25, 41],
                iconAnchor: [12, 41],
                popupAnchor: [1, -34],
                shadowSize: [41, 41]
            })
        }).addTo(map);

        marker.bindPopup(`
            <b>Étape ${etape.ordre} : ${biblio.nom}</b><br/>
            ${biblio.adresse}<br/>
            <strong>Créneau :</strong> ${etape.creneauDebut} – ${etape.creneauFin}<br/>
            <strong>Distance cumulée :</strong> ${etape.distanceCumulee} km
        `);
        markers.push(marker);
        points.push([biblio.latitude, biblio.longitude]);
    });

    // Tracer la ligne de trajet en pointillés
    if (points.length > 1) {
        const polyline = L.polyline(points, {
            color: '#6f42c1',
            weight: 3,
            dashArray: '8, 6',
            opacity: 0.8
        }).addTo(map);
        markers.push(polyline);
    }

    // Ajuster le zoom pour voir tout l'itinéraire
    setTimeout(() => {
        if (markers.length > 0) {
            const group = new L.featureGroup(markers);
            map.fitBounds(group.getBounds(), { padding: [50, 50], maxZoom: 15 });
        }
        map.invalidateSize();
    }, 300);
}