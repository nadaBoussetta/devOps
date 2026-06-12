/**
 * Notifications — livres, social, sessions, bibliothèques
 */

let allNotifications = [];
let currentFilter    = 'tous';

// ── Config des types ──────────────────────────────────────────────────────────
const TYPE_CONFIG = {
    FERMETURE_BIBLIOTHEQUE: { icon: 'fa-door-closed',    color: '#f87171', label: 'Fermeture',       cat: 'bibliotheque' },
    AFFLUENCE_FAIBLE:       { icon: 'fa-chart-bar',      color: '#fbbf24', label: 'Affluence faible', cat: 'bibliotheque' },
    LIVRE_DISPONIBLE:       { icon: 'fa-book-open',      color: '#34d399', label: 'Livre dispo',      cat: 'livre'        },
    NOUVELLE_BIBLIOTHEQUE:  { icon: 'fa-building-columns',color: '#60a5fa', label: 'Nouveauté',       cat: 'bibliotheque' },
    RECOMMANDATION:         { icon: 'fa-star',            color: '#a78bfa', label: 'Recommandation',   cat: 'bibliotheque' },
    RAPPEL_LECTURE:         { icon: 'fa-bookmark',        color: '#34d399', label: 'Rappel lecture',   cat: 'livre'        },
    NOUVELLE_PUBLICATION:   { icon: 'fa-comment-dots',   color: '#60a5fa', label: 'Nouveau post',     cat: 'social'       },
    RECHERCHE_LIVRE:        { icon: 'fa-magnifying-glass',color: '#a78bfa', label: 'Suggestion livre', cat: 'livre'        },
    SESSION_REMINDER:       { icon: 'fa-stopwatch',       color: '#fbbf24', label: 'Rappel session',   cat: 'session'      },
    OBJECTIF_ATTEINT:       { icon: 'fa-trophy',          color: '#34d399', label: 'Objectif atteint', cat: 'session'      },
};

// ── Init ──────────────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async () => {
    if (!isAuthenticated()) {
        window.location.href = 'login.html';
        return;
    }
    await loadNotifications();

    // Filtres
    document.getElementById('filter-tabs').addEventListener('click', e => {
        const tab = e.target.closest('.filter-tab');
        if (!tab) return;
        document.querySelectorAll('.filter-tab').forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        currentFilter = tab.dataset.filter;
        displayNotifications();
    });
});

// ── Chargement ────────────────────────────────────────────────────────────────
async function loadNotifications() {
    try {
        allNotifications = await NotificationAPI.getNotifications();
        updateCounts();
        buildCategorySummary();
        displayNotifications();
    } catch (err) {
        document.getElementById('notifications-container').innerHTML =
            `<div class="notif-empty error"><i class="fas fa-exclamation-circle"></i><p>${err.message}</p></div>`;
    }
}

function updateCounts() {
    const nonLues = allNotifications.filter(n => !n.lue).length;
    document.getElementById('count-tous').textContent     = allNotifications.length;
    document.getElementById('count-non-lues').textContent = nonLues;

    const badge = document.getElementById('header-badge');
    const bc    = document.getElementById('badge-count');
    if (nonLues > 0) { bc.textContent = nonLues; badge.style.display = 'flex'; }
    else             { badge.style.display = 'none'; }
}

// ── Résumé catégories ─────────────────────────────────────────────────────────
function buildCategorySummary() {
    const cats = {
        livre:       { label: 'Livres',         icon: 'fa-book',             count: 0, color: '#34d399' },
        social:      { label: 'Social',          icon: 'fa-comment-dots',     count: 0, color: '#60a5fa' },
        session:     { label: 'Sessions',        icon: 'fa-stopwatch',        count: 0, color: '#fbbf24' },
        bibliotheque:{ label: 'Bibliothèques',   icon: 'fa-building-columns', count: 0, color: '#a78bfa' },
    };

    allNotifications.forEach(n => {
        const cfg = TYPE_CONFIG[n.type];
        if (cfg && cats[cfg.cat]) cats[cfg.cat].count++;
    });

    const html = Object.values(cats).map(c => `
        <div class="cat-pill" style="--cat-color:${c.color}">
            <i class="fas ${c.icon}"></i>
            <span class="cat-count">${c.count}</span>
            <span class="cat-label">${c.label}</span>
        </div>`).join('');

    document.getElementById('category-summary').innerHTML = html;
}

// ── Affichage ─────────────────────────────────────────────────────────────────
function displayNotifications() {
    const container = document.getElementById('notifications-container');
    let list = allNotifications;

    if (currentFilter === 'non-lues') {
        list = list.filter(n => !n.lue);
    } else if (currentFilter !== 'tous') {
        const types = currentFilter.split(',');
        list = list.filter(n => types.includes(n.type));
    }

    if (!list.length) {
        container.innerHTML = `
            <div class="notif-empty">
                <i class="fas fa-bell-slash"></i>
                <p>Aucune notification dans cette catégorie.</p>
            </div>`;
        return;
    }

    // Grouper par date
    const grouped = groupByDate(list);
    container.innerHTML = '';

    grouped.forEach(({ label, items }) => {
        const group = document.createElement('div');
        group.className = 'notif-group';
        group.innerHTML = `<div class="notif-group-label">${label}</div>`;
        items.forEach((n, i) => {
            const el = buildNotifCard(n, i);
            group.appendChild(el);
        });
        container.appendChild(group);
    });
}

function groupByDate(notifs) {
    const today     = new Date(); today.setHours(0,0,0,0);
    const yesterday = new Date(today); yesterday.setDate(today.getDate() - 1);
    const groups    = {};

    notifs.forEach(n => {
        const d = new Date(n.dateCreation); d.setHours(0,0,0,0);
        let label;
        if (d >= today)          label = "Aujourd'hui";
        else if (d >= yesterday) label = 'Hier';
        else                     label = d.toLocaleDateString('fr-FR', { weekday:'long', day:'numeric', month:'long' });

        if (!groups[label]) groups[label] = [];
        groups[label].push(n);
    });

    return Object.entries(groups).map(([label, items]) => ({ label, items }));
}

// ── Carte notification ────────────────────────────────────────────────────────
function buildNotifCard(n, index) {
    const div  = document.createElement('div');
    const cfg  = TYPE_CONFIG[n.type] || { icon:'fa-bell', color:'#a0a0a5', label:'Notification' };
    const time = relativeTime(n.dateCreation);

    div.className = `notif-card${n.lue ? ' lue' : ' non-lue'}`;
    div.style.animationDelay = `${index * 0.05}s`;

    div.innerHTML = `
        <div class="notif-icon-wrap" style="--notif-color:${cfg.color}">
            <i class="fas ${cfg.icon}"></i>
        </div>
        <div class="notif-body">
            <div class="notif-meta">
                <span class="notif-type-badge" style="--notif-color:${cfg.color}">${cfg.label}</span>
                ${n.bibliothequeNom ? `<span class="notif-bib"><i class="fas fa-location-dot"></i> ${escH(n.bibliothequeNom)}</span>` : ''}
                <span class="notif-time"><i class="fas fa-clock"></i> ${time}</span>
            </div>
            <h4 class="notif-titre">${escH(n.titre)}</h4>
            <p class="notif-message">${escH(n.message)}</p>
            ${buildActionLink(n)}
        </div>
        <div class="notif-right">
            ${!n.lue ? `<div class="unread-dot"></div>` : ''}
            <div class="notif-btns">
                ${!n.lue ? `
                <button class="notif-btn" title="Marquer comme lu" onclick="marquerCommeLue(${n.id})">
                    <i class="fas fa-check"></i>
                </button>` : ''}
                <button class="notif-btn notif-btn-del" title="Supprimer" onclick="supprimerNotification(${n.id})">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        </div>`;

    return div;
}

function buildActionLink(n) {
    const links = {
        LIVRE_DISPONIBLE:    { label: 'Chercher ce livre',      href: 'livres.html' },
        RECHERCHE_LIVRE:     { label: 'Voir le livre',          href: 'livres.html' },
        RAPPEL_LECTURE:      { label: 'Rechercher dans livres', href: 'livres.html' },
        NOUVELLE_PUBLICATION:{ label: 'Voir le feed',           href: 'feed.html'   },
        SESSION_REMINDER:    { label: 'Lancer une session',     href: 'sessions.html' },
        OBJECTIF_ATTEINT:    { label: 'Voir mes sessions',      href: 'sessions.html' },
        RECOMMANDATION:      { label: 'Voir la bibliothèque',   href: 'recherche.html' },
        AFFLUENCE_FAIBLE:    { label: 'Réserver ma place',      href: 'recherche.html' },
    };
    const link = links[n.type];
    if (!link) return '';
    return `<a href="${link.href}" class="notif-action-link">${link.label} <i class="fas fa-arrow-right"></i></a>`;
}

// ── Actions ───────────────────────────────────────────────────────────────────
async function marquerCommeLue(id) {
    try {
        await NotificationAPI.marquerCommeLue(id);
        await loadNotifications();
    } catch (e) { alert(e.message); }
}

async function marquerToutesCommeLues() {
    try {
        await NotificationAPI.marquerToutesCommeLues();
        await loadNotifications();
    } catch (e) { alert(e.message); }
}

async function supprimerNotification(id) {
    try {
        await NotificationAPI.supprimerNotification(id);
        await loadNotifications();
    } catch (e) { alert(e.message); }
}

async function supprimerNotificationsLues() {
    try {
        await NotificationAPI.supprimerNotificationsLues();
        await loadNotifications();
    } catch (e) { alert(e.message); }
}

// ── API ───────────────────────────────────────────────────────────────────────
const NotificationAPI = {
    getNotifications:           () => fetchAPI('/notifications'),
    getNotificationsNonLues:    () => fetchAPI('/notifications/non-lues'),
    countNotificationsNonLues:  () => fetchAPI('/notifications/count-non-lues'),
    marquerCommeLue:     (id)   => fetchAPI(`/notifications/${id}/lire`, { method: 'PUT' }),
    marquerToutesCommeLues:     () => fetchAPI('/notifications/lire-tout', { method: 'PUT' }),
    supprimerNotification: (id) => fetchAPI(`/notifications/${id}`, { method: 'DELETE' }),
    supprimerNotificationsLues: () => fetchAPI('/notifications/lues', { method: 'DELETE' }),
};

// Badge nav — mis à jour toutes les 30s
async function updateNotificationBadge() {
    try {
        const r = await NotificationAPI.countNotificationsNonLues();
        let badge = document.getElementById('notification-badge');
        if (r.count > 0) {
            if (!badge) {
                badge = document.createElement('span');
                badge.id = 'notification-badge';
                badge.className = 'nav-notif-badge';
                const a = document.querySelector('a[href="notifications.html"]');
                if (a) a.parentElement.style.position = 'relative', a.parentElement.appendChild(badge);
            }
            badge.textContent = r.count > 9 ? '9+' : r.count;
            badge.style.display = 'flex';
        } else if (badge) {
            badge.style.display = 'none';
        }
    } catch {}
}
setInterval(updateNotificationBadge, 30000);
document.addEventListener('DOMContentLoaded', updateNotificationBadge);

// ── Utilitaires ───────────────────────────────────────────────────────────────
function relativeTime(dateStr) {
    const diff = Math.floor((Date.now() - new Date(dateStr)) / 1000);
    if (diff < 60)    return 'À l\'instant';
    if (diff < 3600)  return `Il y a ${Math.floor(diff/60)} min`;
    if (diff < 86400) return `Il y a ${Math.floor(diff/3600)} h`;
    if (diff < 604800)return `Il y a ${Math.floor(diff/86400)} j`;
    return new Date(dateStr).toLocaleDateString('fr-FR', { day:'numeric', month:'short' });
}

function escH(t) {
    if (!t) return '';
    const d = document.createElement('div');
    d.appendChild(document.createTextNode(t));
    return d.innerHTML;
}