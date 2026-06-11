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
    const setupContainer = document.getElementById('setup-container');
    const activeContainer = document.getElementById('session-active-container');

    if (!setupContainer || !activeContainer) return;

    if (sessionEnCours) {

        setupContainer.style.display = 'none';
        activeContainer.style.display = 'block';

        document.getElementById('session-objectif-display').textContent =
            sessionEnCours.objectif;

        updateTimerDisplay();
        updateProgressBar();

    } else {

        setupContainer.style.display = 'block';
        activeContainer.style.display = 'none';

    }
}

/**
 * Met à jour l'affichage du minuteur.
 */

function updateTimerDisplay() {
    if (!sessionEnCours) return;

    const totalSeconds = Math.floor(sessionEnCours.dureeMinutes * 60);
    const elapsedSeconds = Math.floor(sessionEnCours.tempsEcoulesMinutes * 60);

    const remainingSeconds = Math.max(
        totalSeconds - elapsedSeconds,
        0
    );

    const hours = Math.floor(remainingSeconds / 3600);
    const minutes = Math.floor((remainingSeconds % 3600) / 60);
    const seconds = remainingSeconds % 60;

    document.getElementById('session-timer').textContent =
        `${String(hours).padStart(2, '0')}:` +
        `${String(minutes).padStart(2, '0')}:` +
        `${String(seconds).padStart(2, '0')}`;
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

    const btnPause = document.getElementById('btn-pause');
    const btnResume = document.getElementById('btn-resume');

    if (btnPause) btnPause.style.display = 'inline-flex';
    if (btnResume) btnResume.style.display = 'none';

    timerInterval = setInterval(() => {

        sessionEnCours.tempsEcoulesMinutes += (1 / 60);

        updateTimerDisplay();
        updateProgressBar();

        if (
            sessionEnCours.tempsEcoulesMinutes >=
            sessionEnCours.dureeMinutes
        ) {

            clearInterval(timerInterval);
            isRunning = false;

            alert('Session terminée !');
            completerSession();
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

    const btnPause = document.getElementById('btn-pause');
    const btnResume = document.getElementById('btn-resume');

    if (btnPause) btnPause.style.display = 'none';
    if (btnResume) btnResume.style.display = 'inline-flex';

    updateSessionTime();
}

/**
 * Reprend la session.
 */
function resumeSession() {

    const btnResume = document.getElementById('btn-resume');

    if (btnResume) {
        btnResume.style.display = 'none';
    }

    startSession();
}

/**
 * Complète la session.
 */
async function completerSession() {

    if (!sessionEnCours) return;

    try {

        clearInterval(timerInterval);

        await SessionAPI.completerSession(
            sessionEnCours.id
        );

        sessionEnCours = null;
        isRunning = false;

        displaySessionEnCours();

        await loadSessions();
        await loadStatistiques();

    } catch (error) {

        alert(
            'Erreur lors de la complétion : ' +
            error.message
        );
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

    const objectif = document.getElementById('objectif').value.trim();
    const duree = parseInt(
        document.getElementById('duree').value,
        10
    );

    try {

        const nouvelleSession =
            await SessionAPI.creerSession(objectif, duree);

        sessionEnCours = {
            ...nouvelleSession,
            tempsEcoulesMinutes: 0
        };

        document.getElementById(
            'create-session-form'
        ).reset();

        displaySessionEnCours();

        startSession();

        await loadSessions();

    } catch (error) {

        console.error(error);

        alert(
            'Erreur lors de la création de la session : ' +
            error.message
        );
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
