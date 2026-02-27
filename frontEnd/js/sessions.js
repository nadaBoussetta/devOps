/**
 * Script pour la page des sessions de révision.
 * Feature 5 : Mode "session de révision".
 */

let sessionEnCours = null;
let timerInterval = null;
let isRunning = false;

document.addEventListener('DOMContentLoaded', async () => {
    // Vérifier l'authentification
    if (!isAuthenticated()) {
        alert('Vous devez être connecté pour accéder aux sessions.');
        window.location.href = 'login.html';
        return;
    }

    // Charger les données
    await loadSessionEnCours();
    await loadSessions();
    await loadStatistiques();

    // Gestion de la création de session
    const createSessionForm = document.getElementById('create-session-form');
    createSessionForm.addEventListener('submit', handleCreateSession);
});

/**
 * API pour les sessions
 */
const SessionAPI = {
    async creerSession(objectif, dureeMinutes) {
        return fetchAPI('/sessions', {
            method: 'POST',
            body: JSON.stringify({ objectif, dureeMinutes })
        });
    },

    async getSessionEnCours() {
        try {
            return await fetchAPI('/sessions/en-cours');
        } catch (error) {
            return null;
        }
    },

    async getSessions() {
        return fetchAPI('/sessions');
    },

    async getSessionsCompleteees() {
        return fetchAPI('/sessions/completees');
    },

    async updateTempsEcoule(sessionId, tempsEcoulesMinutes) {
        return fetchAPI(`/sessions/${sessionId}/temps?tempsEcoulesMinutes=${tempsEcoulesMinutes}`, {
            method: 'PUT'
        });
    },

    async completerSession(sessionId) {
        return fetchAPI(`/sessions/${sessionId}/completer`, {
            method: 'POST'
        });
    },

    async supprimerSession(sessionId) {
        return fetchAPI(`/sessions/${sessionId}`, {
            method: 'DELETE'
        });
    },

    async getStatistiques() {
        return fetchAPI('/sessions/statistiques/hebdo');
    }
};

/**
 * Charge la session en cours.
 */
async function loadSessionEnCours() {
    try {
        sessionEnCours = await SessionAPI.getSessionEnCours();
        displaySessionEnCours();
    } catch (error) {
        console.log('Aucune session en cours');
    }
}

/**
 * Affiche la session en cours.
 */
function displaySessionEnCours() {
    const container = document.getElementById('session-en-cours');
    
    if (sessionEnCours) {
        container.style.display = 'block';
        document.getElementById('session-objectif').textContent = sessionEnCours.objectif;
        updateTimerDisplay();
        updateProgressBar();
    } else {
        container.style.display = 'none';
    }
}

/**
 * Met à jour l'affichage du minuteur.
 */
function updateTimerDisplay() {
    if (!sessionEnCours) return;

    const totalSeconds = sessionEnCours.dureeMinutes * 60;
    const elapsedSeconds = sessionEnCours.tempsEcoulesMinutes * 60;
    const remainingSeconds = totalSeconds - elapsedSeconds;

    const hours = Math.floor(remainingSeconds / 3600);
    const minutes = Math.floor((remainingSeconds % 3600) / 60);
    const seconds = remainingSeconds % 60;

    const timerDisplay = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    document.getElementById('session-timer').textContent = timerDisplay;
    document.getElementById('temps-restant').textContent = sessionEnCours.tempsRestantMinutes;
}

/**
 * Met à jour la barre de progression.
 */
function updateProgressBar() {
    if (!sessionEnCours) return;

    const progression = sessionEnCours.progressionPourcentage || 0;
    const progressFill = document.getElementById('progress-fill');
    const progressText = document.getElementById('progress-text');

    progressFill.style.width = progression + '%';
    progressText.textContent = Math.round(progression) + '%';
}

/**
 * Démarre la session.
 */
function startSession() {
    if (!sessionEnCours || isRunning) return;

    isRunning = true;
    document.getElementById('btn-start').style.display = 'none';
    document.getElementById('btn-pause').style.display = 'inline-block';

    timerInterval = setInterval(() => {
        sessionEnCours.tempsEcoulesMinutes += 1/60; // Incrémenter par 1 seconde
        updateTimerDisplay();
        updateProgressBar();

        // Vérifier si la session est complétée
        if (sessionEnCours.tempsEcoulesMinutes >= sessionEnCours.dureeMinutes) {
            pauseSession();
            alert('Session complétée !');
        }
    }, 1000);
}

/**
 * Met en pause la session.
 */
function pauseSession() {
    if (!isRunning) return;

    isRunning = false;
    clearInterval(timerInterval);
    document.getElementById('btn-pause').style.display = 'none';
    document.getElementById('btn-resume').style.display = 'inline-block';

    // Sauvegarder le temps écoulé
    updateSessionTime();
}

/**
 * Reprend la session.
 */
function resumeSession() {
    startSession();
    document.getElementById('btn-resume').style.display = 'none';
}

/**
 * Complète la session.
 */
async function completerSession() {
    if (!sessionEnCours) return;

    if (confirm('Êtes-vous sûr de vouloir terminer cette session ?')) {
        try {
            pauseSession();
            await SessionAPI.completerSession(sessionEnCours.id);
            alert('Session complétée avec succès !');
            sessionEnCours = null;
            document.getElementById('session-en-cours').style.display = 'none';
            await loadSessions();
            await loadStatistiques();
        } catch (error) {
            alert('Erreur lors de la complétion de la session: ' + error.message);
        }
    }
}

/**
 * Met à jour le temps écoulé de la session.
 */
async function updateSessionTime() {
    if (!sessionEnCours) return;

    try {
        await SessionAPI.updateTempsEcoule(sessionEnCours.id, Math.floor(sessionEnCours.tempsEcoulesMinutes));
    } catch (error) {
        console.error('Erreur lors de la mise à jour du temps:', error);
    }
}

/**
 * Gère la création d'une nouvelle session.
 */
async function handleCreateSession(e) {
    e.preventDefault();

    const objectif = document.getElementById('objectif').value;
    const duree = parseInt(document.getElementById('duree').value);

    try {
        sessionEnCours = await SessionAPI.creerSession(objectif, duree);
        displaySessionEnCours();
        document.getElementById('create-session-form').reset();
        await loadSessions();
    } catch (error) {
        alert('Erreur lors de la création de la session: ' + error.message);
    }
}

/**
 * Charge et affiche toutes les sessions.
 */
async function loadSessions() {
    const container = document.getElementById('sessions-container');

    try {
        const sessions = await SessionAPI.getSessionsCompleteees();

        if (sessions.length === 0) {
            container.innerHTML = '<p class="info-message">Vous n\'avez aucune session complétée pour le moment.</p>';
            return;
        }

        container.innerHTML = '';
        container.classList.remove('loading');

        sessions.forEach(session => {
            const card = createSessionCard(session);
            container.appendChild(card);
        });
    } catch (error) {
        container.innerHTML = `<p class="error-message">Erreur lors du chargement: ${error.message}</p>`;
    }
}

/**
 * Crée une carte pour une session.
 */
function createSessionCard(session) {
    const card = document.createElement('div');
    card.className = 'card';

    const dateCreation = new Date(session.dateCreation).toLocaleDateString('fr-FR', {
        day: 'numeric',
        month: 'long',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });

    const dureeHeures = Math.floor(session.dureeMinutes / 60);
    const dureeMinutes = session.dureeMinutes % 60;
    const dureeFormatee = dureeHeures > 0 
        ? `${dureeHeures}h ${dureeMinutes}m`
        : `${dureeMinutes}m`;

    card.innerHTML = `
        <div class="card-header">
            <h4 class="card-title">✓ ${session.objectif}</h4>
            <span class="badge badge-success">Complétée</span>
        </div>
        <p><strong>Durée:</strong> ${dureeFormatee}</p>
        <p><strong>Date:</strong> ${dateCreation}</p>
        <div class="card-actions">
            <button onclick="supprimerSession(${session.id})" class="btn btn-danger">Supprimer</button>
        </div>
    `;

    return card;
}

/**
 * Supprime une session.
 */
async function supprimerSession(sessionId) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette session ?')) {
        try {
            await SessionAPI.supprimerSession(sessionId);
            await loadSessions();
        } catch (error) {
            alert('Erreur lors de la suppression: ' + error.message);
        }
    }
}

/**
 * Charge et affiche les statistiques.
 */
async function loadStatistiques() {
    try {
        const stats = await SessionAPI.getStatistiques();
        document.getElementById('stat-sessions').textContent = stats.nombreSessions;
        document.getElementById('stat-minutes').textContent = stats.totalMinutes;
        document.getElementById('stat-streak').textContent = stats.streak;
    } catch (error) {
        console.error('Erreur lors du chargement des statistiques:', error);
    }
}
