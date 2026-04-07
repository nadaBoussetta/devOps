/**
 * Module API pour les appels REST vers le backend Spring Boot.
 */

const API_BASE_URL = 'http://localhost:8080/api';

/**
 * Récupère le token JWT du localStorage.
 */
function getToken() {
    return localStorage.getItem('token');
}

/**
 * Sauvegarde le token JWT dans le localStorage.
 */
function saveToken(token) {
    localStorage.setItem('token', token);
}

/**
 * Supprime le token JWT du localStorage.
 */
function removeToken() {
    localStorage.removeItem('token');
}

/**
 * Vérifie si l'utilisateur est connecté.
 */
function isAuthenticated() {
    return getToken() !== null;
}

/**
 * Effectue une requête HTTP avec gestion des erreurs.
 */
async function fetchAPI(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    // Ajouter le token JWT si disponible
    const token = getToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        const response = await fetch(url, {
            ...options,
            headers
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Erreur lors de la requête');
        }

        return await response.json();
    } catch (error) {
        console.error('Erreur API:', error);
        throw error;
    }
}

/**
 * API Auth
 */
const AuthAPI = {
    async register(username, email, password) {
        return fetchAPI('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ username, email, password })
        });
    },

    async login(username, password) {
        const response = await fetchAPI('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
        
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
        window.location.href = 'index.html';
    }
};

/**
 * API Bibliothèques
 */
const BibliothequeAPI = {
    async getAll() {
        return fetchAPI('/bibliotheques');
    },

    async getById(id) {
        return fetchAPI(`/bibliotheques/${id}`);
    },

    async rechercher(adresse, heureDebut, heureFin, rayon) {
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

/**
 * API Livres
 */
const LivreAPI = {
    async rechercher(titre) {
        return fetchAPI(`/livres/recherche?titre=${encodeURIComponent(titre)}`);
    },

    async rechercherDansBibliotheque(titre, bibliotheque) {
        return fetchAPI(`/livres/recherche/${encodeURIComponent(bibliotheque)}?titre=${encodeURIComponent(titre)}`);
    }
};

/**
 * API Feed
 */
const FeedAPI = {
    async getAllPosts() {
        return fetchAPI('/feed');
    },

    async getPostById(id) {
        return fetchAPI(`/feed/${id}`);
    },

    async createPost(contenu, bibliothequeId = null) {
        return fetchAPI('/feed', {
            method: 'POST',
            body: JSON.stringify({ contenu, bibliothequeId })
        });
    },

    async addComment(postId, contenu) {
        return fetchAPI(`/feed/${postId}/comments`, {
            method: 'POST',
            body: JSON.stringify({ contenu })
        });
    },

    async addReply(commentId, contenu) {
        return fetchAPI(`/feed/comments/${commentId}/replies`, {
            method: 'POST',
            body: JSON.stringify({ contenu })
        });
    }
};

/**
 * API Notations
 */
const NotationAPI = {
    async noter(bibliothequeId, note, commentaire, dateVisite) {
        return fetchAPI('/notations', {
            method: 'POST',
            body: JSON.stringify({ bibliothequeId, note, commentaire, dateVisite })
        });
    },

    async getMesNotations() {
        return fetchAPI('/notations/mes-notations');
    },

    async getNotationsByBibliotheque(bibliothequeId) {
        return fetchAPI(`/notations/bibliotheque/${bibliothequeId}`);
    },

    async ajouterFavori(bibliothequeId) {
        return fetchAPI(`/notations/favoris/${bibliothequeId}`, {
            method: 'POST'
        });
    },

    async supprimerFavori(bibliothequeId) {
        return fetchAPI(`/notations/favoris/${bibliothequeId}`, {
            method: 'DELETE'
        });
    },

    async getMesFavoris() {
        return fetchAPI('/notations/mes-favoris');
    }
};

/**
 * API Recommandations
 */
const RecommendationAPI = {
    async getRecommendations() {
        return fetchAPI('/recommandations');
    }
};

/**
 * Mise à jour du lien d'authentification dans la navigation.
 */
function updateAuthLink() {
    const authLink = document.getElementById('auth-link');
    if (authLink) {
        if (isAuthenticated()) {
            const username = localStorage.getItem('username');
            authLink.textContent = username || 'Mon compte';
            authLink.href = '#';
            authLink.addEventListener('click', (e) => {
                e.preventDefault();
                if (confirm('Voulez-vous vous déconnecter ?')) {
                    AuthAPI.logout();
                }
            });
        } else {
            authLink.textContent = 'Connexion';
            authLink.href = 'login.html';
        }
    }
}

// Initialisation
document.addEventListener('DOMContentLoaded', updateAuthLink);
