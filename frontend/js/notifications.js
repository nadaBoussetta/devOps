/**
 * Script pour la page des notifications.
 * Feature 6 : Notifications intelligentes.
 */

let allNotifications = [];
let currentFilter = 'tous';

document.addEventListener('DOMContentLoaded', async () => {
    // Vérifier l'authentification
    if (!isAuthenticated()) {
        alert('Vous devez être connecté pour accéder aux notifications.');
        window.location.href = 'login.html';
        return;
    }

    // Charger les notifications
    await loadNotifications();
});

/**
 * API pour les notifications
 */
const NotificationAPI = {
    async getNotifications() {
        return fetchAPI('/notifications');
    },

    async getNotificationsNonLues() {
        return fetchAPI('/notifications/non-lues');
    },

    async countNotificationsNonLues() {
        return fetchAPI('/notifications/count-non-lues');
    },

    async marquerCommeLue(notificationId) {
        return fetchAPI(`/notifications/${notificationId}/lire`, {
            method: 'PUT'
        });
    },

    async marquerToutesCommeLues() {
        return fetchAPI('/notifications/lire-tout', {
            method: 'PUT'
        });
    },

    async supprimerNotification(notificationId) {
        return fetchAPI(`/notifications/${notificationId}`, {
            method: 'DELETE'
        });
    },

    async supprimerNotificationsLues() {
        return fetchAPI('/notifications/lues', {
            method: 'DELETE'
        });
    }
};

/**
 * Charge et affiche toutes les notifications.
 */
async function loadNotifications() {
    const container = document.getElementById('notifications-container');

    try {
        allNotifications = await NotificationAPI.getNotifications();
        displayNotifications();
    } catch (error) {
        container.innerHTML = `<p class="error-message">Erreur lors du chargement: ${error.message}</p>`;
    }
}

/**
 * Affiche les notifications en fonction du filtre.
 */
function displayNotifications() {
    const container = document.getElementById('notifications-container');

    let filteredNotifications = allNotifications;

    if (currentFilter === 'non-lues') {
        filteredNotifications = allNotifications.filter(n => !n.lue);
    } else if (currentFilter === 'lues') {
        filteredNotifications = allNotifications.filter(n => n.lue);
    }

    if (filteredNotifications.length === 0) {
        container.innerHTML = '<p class="info-message">Aucune notification à afficher.</p>';
        return;
    }

    container.innerHTML = '';
    container.classList.remove('loading');

    filteredNotifications.forEach(notification => {
        const element = createNotificationElement(notification);
        container.appendChild(element);
    });
}

/**
 * Crée un élément DOM pour une notification.
 */
function createNotificationElement(notification) {
    const div = document.createElement('div');
    div.className = `notification-item ${notification.lue ? 'lue' : 'non-lue'}`;

    const date = new Date(notification.dateCreation).toLocaleDateString('fr-FR', {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });

    const typeEmoji = getTypeEmoji(notification.type);
    const typeLabel = getTypeLabel(notification.type);

    div.innerHTML = `
        <div class="notification-header">
            <div>
                <span class="notification-type">${typeEmoji} ${typeLabel}</span>
                ${notification.bibliothequeNom ? `<span style="color: #6b7280; margin-left: 0.5rem;">📚 ${notification.bibliothequeNom}</span>` : ''}
            </div>
            <span class="notification-date">${date}</span>
        </div>
        <div class="notification-titre">${notification.titre}</div>
        <div class="notification-message">${notification.message}</div>
        <div class="notification-actions">
            ${!notification.lue ? `<button class="btn-small btn-mark-read" onclick="marquerCommeLue(${notification.id})">Marquer comme lu</button>` : ''}
            <button class="btn-small btn-delete" onclick="supprimerNotification(${notification.id})">Supprimer</button>
        </div>
    `;

    return div;
}

/**
 * Retourne l'emoji correspondant au type de notification.
 */
function getTypeEmoji(type) {
    const emojis = {
        'FERMETURE_BIBLIOTHEQUE': '🔔',
        'AFFLUENCE_FAIBLE': '📊',
        'LIVRE_DISPONIBLE': '📖',
        'NOUVELLE_BIBLIOTHEQUE': '✨',
        'RECOMMANDATION': '⭐'
    };
    return emojis[type] || '📬';
}

/**
 * Retourne le label correspondant au type de notification.
 */
function getTypeLabel(type) {
    const labels = {
        'FERMETURE_BIBLIOTHEQUE': 'Fermeture',
        'AFFLUENCE_FAIBLE': 'Affluence',
        'LIVRE_DISPONIBLE': 'Livre',
        'NOUVELLE_BIBLIOTHEQUE': 'Nouvelle',
        'RECOMMANDATION': 'Recommandation'
    };
    return labels[type] || 'Notification';
}

/**
 * Filtre les notifications.
 */
function filterNotifications(filter) {
    currentFilter = filter;
    
    // Mettre à jour les boutons actifs
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');

    displayNotifications();
}

/**
 * Marque une notification comme lue.
 */
async function marquerCommeLue(notificationId) {
    try {
        await NotificationAPI.marquerCommeLue(notificationId);
        await loadNotifications();
    } catch (error) {
        alert('Erreur: ' + error.message);
    }
}

/**
 * Marque toutes les notifications comme lues.
 */
async function marquerToutesCommeLues() {
    if (confirm('Marquer toutes les notifications comme lues ?')) {
        try {
            await NotificationAPI.marquerToutesCommeLues();
            await loadNotifications();
        } catch (error) {
            alert('Erreur: ' + error.message);
        }
    }
}

/**
 * Supprime une notification.
 */
async function supprimerNotification(notificationId) {
    if (confirm('Êtes-vous sûr de vouloir supprimer cette notification ?')) {
        try {
            await NotificationAPI.supprimerNotification(notificationId);
            await loadNotifications();
        } catch (error) {
            alert('Erreur: ' + error.message);
        }
    }
}

/**
 * Supprime toutes les notifications lues.
 */
async function supprimerNotificationsLues() {
    if (confirm('Supprimer toutes les notifications lues ?')) {
        try {
            await NotificationAPI.supprimerNotificationsLues();
            await loadNotifications();
        } catch (error) {
            alert('Erreur: ' + error.message);
        }
    }
}

/**
 * Met à jour le badge de notifications non lues dans la navigation.
 */
async function updateNotificationBadge() {
    try {
        const result = await NotificationAPI.countNotificationsNonLues();
        const count = result.count;

        let badge = document.getElementById('notification-badge');
        if (count > 0) {
            if (!badge) {
                badge = document.createElement('span');
                badge.id = 'notification-badge';
                badge.className = 'notification-badge';
                const notifLink = document.querySelector('a[href="notifications.html"]');
                if (notifLink) {
                    notifLink.parentElement.appendChild(badge);
                }
            }
            badge.textContent = count;
            badge.style.display = 'inline-block';
        } else if (badge) {
            badge.style.display = 'none';
        }
    } catch (error) {
        console.error('Erreur lors de la mise à jour du badge:', error);
    }
}

// Mettre à jour le badge toutes les 30 secondes
setInterval(updateNotificationBadge, 30000);
document.addEventListener('DOMContentLoaded', updateNotificationBadge);
