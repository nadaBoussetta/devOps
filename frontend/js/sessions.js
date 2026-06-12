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
    await loadDashboard();

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
// ── À ajouter à la fin de sessions.js (après loadStatistiques) ──────────────

/**
 * Met à jour le tableau de bord complet.
 */
async function loadDashboard() {
    try {
        const sessions = await SessionAPI.getSessionsCompleteees();
        updateCircle(sessions);
        updateWeekBars(sessions);
        updateLastSession(sessions);
    } catch (e) {
        console.error('Dashboard:', e);
    }
}

// ── Cercle de progression hebdo ───────────────────────────────────────────────
function updateCircle(sessions) {
    const OBJECTIF_MINUTES = 300; // 5h par semaine

    const now = new Date();
    const debutSemaine = new Date(now);
    debutSemaine.setDate(now.getDate() - now.getDay() + (now.getDay() === 0 ? -6 : 1));
    debutSemaine.setHours(0, 0, 0, 0);

    const minutesSemaine = sessions
        .filter(s => new Date(s.dateCreation) >= debutSemaine)
        .reduce((sum, s) => sum + (s.tempsEcoulesMinutes || 0), 0);

    const pct     = Math.min(100, Math.round((minutesSemaine / OBJECTIF_MINUTES) * 100));
    const circonf = 327; // 2 * PI * 52
    const offset  = circonf - (pct / 100) * circonf;

    const circle = document.getElementById('circle-fill');
    const pctEl  = document.getElementById('circle-pct');
    const noteEl = document.getElementById('dash-objectif-note');

    if (circle)  circle.style.strokeDashoffset = offset;
    if (pctEl)   pctEl.textContent = pct + '%';
    if (noteEl)  noteEl.textContent = minutesToLabel(minutesSemaine) + ' / 5h travaillées';
}

// ── Barres par jour de la semaine ─────────────────────────────────────────────
function updateWeekBars(sessions) {
    const container = document.getElementById('week-bars');
    if (!container) return;

    const now = new Date();
    const today = now.getDay() === 0 ? 6 : now.getDay() - 1; // 0=Lundi

    // Calculer les minutes par jour (7 derniers jours)
    const minutesParJour = Array(7).fill(0);
    sessions.forEach(s => {
        const d = new Date(s.dateCreation);
        const diff = Math.floor((now - d) / 86400000);
        if (diff >= 0 && diff < 7) {
            const idx = 6 - diff; // index dans la semaine
            minutesParJour[idx] += (s.tempsEcoulesMinutes || 0);
        }
    });

    const maxMin = Math.max(...minutesParJour, 1);

    container.innerHTML = minutesParJour.map((min, i) => {
        const h   = Math.max(4, Math.round((min / maxMin) * 76));
        const cls = i === today ? 'week-bar today' : 'week-bar';
        const tip = min > 0 ? minutesToLabel(min) : '0 min';
        return `<div class="week-bar-wrap" title="${tip}">
                    <div class="${cls}" style="height:${h}px"></div>
                </div>`;
    }).join('');
}

// ── Dernière session ──────────────────────────────────────────────────────────
function updateLastSession(sessions) {
    const el = document.getElementById('last-session-info');
    if (!el) return;

    if (!sessions.length) {
        el.innerHTML = `<i class="fas fa-history"></i><span style="color:var(--text-gray);font-size:0.82rem">Aucune session</span>`;
        return;
    }

    const last = sessions[0];
    const date = new Date(last.dateCreation).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' });

    el.innerHTML = `
        <div class="ls-objectif">${escSess(last.objectif)}</div>
        <div class="ls-meta">
            <span><i class="fas fa-clock"></i> ${minutesToLabel(last.tempsEcoulesMinutes)}</span>
            <span><i class="fas fa-calendar"></i> ${date}</span>
            <span><i class="fas fa-check-circle" style="color:var(--success)"></i> Complétée</span>
        </div>`;
}

// ── Utilitaires ───────────────────────────────────────────────────────────────
function minutesToLabel(min) {
    const m = Math.round(min || 0);
    if (m < 60) return m + ' min';
    const h = Math.floor(m / 60);
    const r = m % 60;
    return r > 0 ? `${h}h ${r}min` : `${h}h`;
}

function escSess(t) {
    if (!t) return '';
    const d = document.createElement('div');
    d.appendChild(document.createTextNode(t));
    return d.innerHTML;
}