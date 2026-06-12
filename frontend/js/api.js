/**
 * Module API pour les appels REST vers le backend Spring Boot.
 */

const API_BASE_URL = window.API_URL || 'http://localhost:8080/api';

function getToken()          { return localStorage.getItem('token'); }
function saveToken(token)    { localStorage.setItem('token', token); }
function removeToken()       { localStorage.removeItem('token'); }
function isAuthenticated()   { return getToken() !== null; }

async function fetchAPI(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    const headers = { 'Content-Type': 'application/json', ...options.headers };
    const token = getToken();
    if (token) headers['Authorization'] = `Bearer ${token}`;

    try {
        const response = await fetch(url, { ...options, headers });

        if (response.status === 401) {
            removeToken();
            window.location.href = 'login.html';
            return;
        }
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Erreur lors de la requête');
        }
        const text = await response.text();
        return text ? JSON.parse(text) : {};
    } catch (error) {
        if (error instanceof TypeError) throw new Error('Serveur inaccessible. Vérifiez votre connexion.');
        console.error('Erreur API:', error);
        throw error;
    }
}

const AuthAPI = {
    async register(username, email, password) {
        return fetchAPI('/auth/register', { method: 'POST', body: JSON.stringify({ username, email, password }) });
    },
    async login(username, password) {
        const response = await fetchAPI('/auth/login', { method: 'POST', body: JSON.stringify({ username, password }) });
        if (response.token) {
            saveToken(response.token);
            localStorage.setItem('userId', response.userId);
            localStorage.setItem('username', response.username);
        }
        return response;
    },
    logout() {
        removeToken();
        localStorage.removeItem('userId');
        localStorage.removeItem('username');
        window.location.href = 'MyFeed.html';
    }
};

const BibliothequeAPI = {
    async getAll()                                  { return fetchAPI('/bibliotheques'); },
    async getById(id)                               { return fetchAPI(`/bibliotheques/${id}`); },
    async rechercher(adresse, heureDebut, heureFin, rayon) {
        return fetchAPI('/bibliotheques/recherche', { method: 'POST', body: JSON.stringify({ adresse, heureDebut, heureFin, rayon }) });
        return fetchAPI('/bibliotheques/recherche', {
            method: 'POST',
            body: JSON.stringify({ adresse, heureDebut, heureFin, rayon })
        });
    },

    /**
     * Calcule un itinéraire optimisé couvrant le créneau horaire complet.
     * Appelle le nouvel endpoint POST /api/bibliotheques/itineraire.
     */
    async rechercherItineraire(adresse, heureDebut, heureFin, rayon) {
        return fetchAPI('/bibliotheques/itineraire', {
            method: 'POST',
            body: JSON.stringify({ adresse, heureDebut, heureFin, rayon })
        });
    }
};

const LivreAPI = {
    async rechercher(titre) {
        return fetchAPI(`/livres/recherche?titre=${encodeURIComponent(titre)}`);
    },
    async rechercherDansBibliotheque(titre, bibliotheque) {
        return fetchAPI(`/livres/recherche/${encodeURIComponent(bibliotheque)}?titre=${encodeURIComponent(titre)}`);
    }
};

const FeedAPI = {
    async getAllPosts() {
        return fetchAPI('/feed');
    },
    async getPostById(id) {
        return fetchAPI(`/feed/${id}`);
    },
    async createPost(contenu, bibliothequeId = null) {
        return fetchAPI('/feed', { method: 'POST', body: JSON.stringify({ contenu, bibliothequeId }) });
    },
    // ✅ Nouveau : repost
    async repost(postId, commentaire) {
        return fetchAPI(`/feed/${postId}/repost`, {
            method: 'POST',
            body: JSON.stringify({ commentaire: commentaire || '' })
        });
    },
    // ✅ Nouveau : réaction
    async reagir(postId, type) {
        return fetchAPI(`/feed/${postId}/reactions`, {
            method: 'POST',
            body: JSON.stringify({ type })
        });
    },
    async addComment(postId, contenu) {
        return fetchAPI(`/feed/${postId}/comments`, { method: 'POST', body: JSON.stringify({ contenu }) });
    },
    async addReply(commentId, contenu) {
        return fetchAPI(`/feed/comments/${commentId}/replies`, { method: 'POST', body: JSON.stringify({ contenu }) });
    }
};

const NotationAPI = {
    async noter(bibliothequeId, note, commentaire, dateVisite) {
        return fetchAPI('/notations', { method: 'POST', body: JSON.stringify({ bibliothequeId, note, commentaire, dateVisite }) });
    },
    async getMesNotations()                         { return fetchAPI('/notations/mes-notations'); },
    async getNotationsByBibliotheque(id)            { return fetchAPI(`/notations/bibliotheque/${id}`); },
    async ajouterFavori(bibliothequeId)             { return fetchAPI(`/notations/favoris/${bibliothequeId}`, { method: 'POST' }); },
    async supprimerFavori(bibliothequeId)           { return fetchAPI(`/notations/favoris/${bibliothequeId}`, { method: 'DELETE' }); },
    async getMesFavoris()                           { return fetchAPI('/notations/mes-favoris'); }
};

const RecommendationAPI = {
    async getRecommendations() { return fetchAPI('/recommandations'); }
};

function updateAuthLink() {
    const authLink = document.getElementById('auth-link');
    if (authLink) {
        if (isAuthenticated()) {
            const username = localStorage.getItem('username');
            authLink.textContent = username || 'Mon compte';
            authLink.href = '#';
            authLink.addEventListener('click', (e) => {
                e.preventDefault();
                if (confirm('Voulez-vous vous déconnecter ?')) AuthAPI.logout();
            });
        } else {
            authLink.textContent = 'Connexion';
            authLink.href = 'login.html';
        }
    }
}

document.addEventListener('DOMContentLoaded', updateAuthLink);