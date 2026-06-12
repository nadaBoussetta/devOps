/**
 * Script pour la page profil utilisateur.
 * Endpoints utilisés :
 *   GET    /api/users/me
 *   PUT    /api/users/me
 *   PUT    /api/users/me/password
 *   DELETE /api/users/me
 */

document.addEventListener('DOMContentLoaded', async () => {
    if (!isAuthenticated()) {
        alert('Vous devez être connecté pour accéder à votre profil.');
        window.location.href = 'login.html';
        return;
    }

    await chargerProfil();

    // ── Photos ────────────────────────────────────────────────────────────────
    const savedPhoto = localStorage.getItem('profilePhoto');
    if (savedPhoto) setAvatarPhoto(savedPhoto);

    const savedBanner = localStorage.getItem('profileBanner');
    if (savedBanner) setBannerPhoto(savedBanner);

    document.getElementById('photo-input').addEventListener('change', function () {
        const file = this.files[0];
        if (!file) return;
        const reader = new FileReader();
        reader.onload = (e) => {
            try { localStorage.setItem('profilePhoto', e.target.result); } catch { alert('Image trop lourde.'); }
            setAvatarPhoto(e.target.result);
        };
        reader.readAsDataURL(file);
    });

    document.getElementById('banner-input').addEventListener('change', function () {
        const file = this.files[0];
        if (!file) return;
        const reader = new FileReader();
        reader.onload = (e) => {
            try { localStorage.setItem('profileBanner', e.target.result); } catch { alert('Image trop lourde.'); }
            setBannerPhoto(e.target.result);
        };
        reader.readAsDataURL(file);
    });

    // ── Compteur bio ──────────────────────────────────────────────────────────
    const bioInput = document.getElementById('bio');
    const bioCount = document.getElementById('bio-count');
    bioInput.addEventListener('input', () => {
        bioCount.textContent = bioInput.value.length;
    });

    // ── Formulaires ───────────────────────────────────────────────────────────
    document.getElementById('form-profil').addEventListener('submit', handleUpdateProfil);
    document.getElementById('form-password').addEventListener('submit', handleUpdatePassword);

    // ── Suppression de compte ─────────────────────────────────────────────────
    document.getElementById('btn-delete-account').addEventListener('click', () => {
        document.getElementById('modal-delete').style.display = 'flex';
    });
    document.getElementById('modal-cancel').addEventListener('click', () => {
        document.getElementById('modal-delete').style.display = 'none';
    });
    document.getElementById('modal-confirm').addEventListener('click', handleDeleteAccount);
});

// ─── Avatar & Bannière ────────────────────────────────────────────────────────

function setAvatarPhoto(src) {
    const avatar = document.getElementById('avatar-initiale');
    avatar.style.backgroundImage = `url('${src}')`;
    avatar.style.backgroundSize = 'cover';
    avatar.style.backgroundPosition = 'center';
    avatar.style.backgroundRepeat = 'no-repeat';
    avatar.style.color = 'transparent';
    avatar.style.fontSize = '0';
}

function setAvatarInitiale(initiale) {
    const avatar = document.getElementById('avatar-initiale');
    avatar.style.backgroundImage = '';
    avatar.style.color = '';
    avatar.style.fontSize = '';
    avatar.innerHTML = `${initiale}<div class="avatar-overlay"><i class="fas fa-camera"></i></div>`;
}

function setBannerPhoto(src) {
    const banner = document.getElementById('banner-cover');
    banner.style.backgroundImage = `url('${src}')`;
    banner.style.backgroundSize = 'cover';
    banner.style.backgroundPosition = 'center';
}

// ─── Chargement du profil ─────────────────────────────────────────────────────

async function chargerProfil() {
    try {
        const user = await fetchAPI('/users/me');

        // Champs formulaire
        document.getElementById('username').value = user.username || '';
        document.getElementById('email').value    = user.email    || '';
        document.getElementById('ville').value    = user.ville    || '';
        document.getElementById('bio').value      = user.bio      || '';
        document.getElementById('bio-count').textContent = (user.bio || '').length;
        if (user.dateNaissance) {
            document.getElementById('dateNaissance').value = user.dateNaissance;
        }

        // Bannière profil
        document.getElementById('banner-username').textContent = user.username || '';
        document.getElementById('banner-ville-text').textContent = user.ville || '–';

        // Stats
        document.getElementById('stat-posts').textContent   = user.nbPosts   ?? 0;
        document.getElementById('stat-favoris').textContent = user.nbFavoris  ?? 0;
        document.getElementById('stat-avis').textContent    = user.nbAvis     ?? 0;

        // Avatar
        if (!localStorage.getItem('profilePhoto')) {
            setAvatarInitiale(user.username ? user.username[0].toUpperCase() : '?');
        }

        // Listes
        renderFavoris(user.favoris  || []);
        renderAvis(user.avis        || []);
        renderPosts(user.posts      || []);

    } catch (err) {
        showMessage('msg-profil-err', err.message || 'Impossible de charger le profil.');
    }
}

// ─── Rendu des listes ─────────────────────────────────────────────────────────

function renderFavoris(favoris) {
    const container = document.getElementById('favoris-list');
    if (!favoris.length) {
        container.innerHTML = '<p class="empty-msg"><i class="fas fa-heart-broken"></i> Aucune bibliothèque favorite pour l\'instant.</p>';
        return;
    }
    container.innerHTML = favoris.map(f => `
        <div class="mini-card">
            <div class="mini-card-icon"><i class="fas fa-building-columns"></i></div>
            <div class="mini-card-body">
                <strong>${f.bibliothequeNom || 'Bibliothèque'}</strong>
                <span class="mini-card-date"><i class="fas fa-calendar-alt"></i> ${formatDate(f.dateAjout)}</span>
            </div>
            <a href="recherche.html" class="mini-card-link"><i class="fas fa-arrow-right"></i></a>
        </div>
    `).join('');
}

function renderAvis(avis) {
    const container = document.getElementById('avis-list');
    if (!avis.length) {
        container.innerHTML = '<p class="empty-msg"><i class="fas fa-star"></i> Vous n\'avez encore laissé aucun avis.</p>';
        return;
    }
    container.innerHTML = avis.map(a => `
        <div class="avis-card">
            <div class="avis-header">
                <strong>${a.bibliothequeNom || 'Bibliothèque'}</strong>
                <div class="stars">${renderStars(a.note)}</div>
            </div>
            ${a.commentaire ? `<p class="avis-commentaire">"${a.commentaire}"</p>` : ''}
            <span class="avis-date"><i class="fas fa-calendar-alt"></i> Visité le ${formatDate(a.dateVisite)}</span>
        </div>
    `).join('');
}

function renderPosts(posts) {
    const container = document.getElementById('posts-list');
    if (!posts.length) {
        container.innerHTML = '<p class="empty-msg"><i class="fas fa-pen-nib"></i> Vous n\'avez encore rien publié.</p>';
        return;
    }
    container.innerHTML = posts.map(p => `
        <div class="post-card">
            <p class="post-contenu">${escapeHtml(p.contenu)}</p>
            <div class="post-footer">
                ${p.bibliothequeNom ? `<span><i class="fas fa-building-columns"></i> ${p.bibliothequeNom}</span>` : ''}
                <span class="post-date"><i class="fas fa-clock"></i> ${formatDate(p.dateCreation)}</span>
            </div>
        </div>
    `).join('');
}

function renderStars(note) {
    return Array.from({ length: 5 }, (_, i) =>
        `<i class="fas fa-star ${i < note ? 'star-on' : 'star-off'}"></i>`
    ).join('');
}

// ─── Mise à jour du profil ────────────────────────────────────────────────────

async function handleUpdateProfil(e) {
    e.preventDefault();
    hideMessages('profil');

    const username      = document.getElementById('username').value.trim();
    const email         = document.getElementById('email').value.trim();
    const bio           = document.getElementById('bio').value.trim();
    const ville         = document.getElementById('ville').value.trim();
    const dateNaissance = document.getElementById('dateNaissance').value || null;

    try {
        const updated = await fetchAPI('/users/me', {
            method: 'PUT',
            body: JSON.stringify({ username, email, bio, ville, dateNaissance })
        });

        localStorage.setItem('username', updated.username);

        // Mettre à jour la bannière
        document.getElementById('banner-username').textContent = updated.username;
        document.getElementById('banner-ville-text').textContent = updated.ville || '–';

        if (!localStorage.getItem('profilePhoto')) {
            setAvatarInitiale(updated.username[0].toUpperCase());
        }

        showMessage('msg-profil-ok', 'Profil mis à jour avec succès.', 'success');
    } catch (err) {
        showMessage('msg-profil-err', err.message || 'Erreur lors de la mise à jour.');
    }
}

// ─── Changement de mot de passe ───────────────────────────────────────────────

async function handleUpdatePassword(e) {
    e.preventDefault();
    hideMessages('pwd');

    const ancien  = document.getElementById('ancien-pwd').value;
    const nouveau = document.getElementById('nouveau-pwd').value;
    const confirm = document.getElementById('confirm-pwd').value;

    if (nouveau !== confirm) {
        showMessage('msg-pwd-err', 'Les nouveaux mots de passe ne correspondent pas.');
        return;
    }

    try {
        await fetchAPI('/users/me/password', {
            method: 'PUT',
            body: JSON.stringify({ ancienMotDePasse: ancien, nouveauMotDePasse: nouveau })
        });
        showMessage('msg-pwd-ok', 'Mot de passe changé avec succès.', 'success');
        document.getElementById('form-password').reset();
    } catch (err) {
        showMessage('msg-pwd-err', err.message || 'Erreur lors du changement de mot de passe.');
    }
}

// ─── Suppression de compte ────────────────────────────────────────────────────

async function handleDeleteAccount() {
    try {
        await fetchAPI('/users/me', { method: 'DELETE' });
        // Nettoyer le localStorage et rediriger
        localStorage.clear();
        window.location.href = 'login.html';
    } catch (err) {
        document.getElementById('modal-delete').style.display = 'none';
        showMessage('msg-profil-err', err.message || 'Erreur lors de la suppression du compte.');
    }
}

// ─── Utilitaires ──────────────────────────────────────────────────────────────

function showMessage(id, text, type = 'error') {
    const el = document.getElementById(id);
    if (!el) return;
    if (text !== null && text !== undefined) el.textContent = text;
    el.className = `message ${type}`;
    el.style.display = 'block';
    setTimeout(() => { el.style.display = 'none'; }, 4000);
}

function hideMessages(prefix) {
    [`msg-${prefix}-ok`, `msg-${prefix}-err`].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.style.display = 'none';
    });
}

function formatDate(dateStr) {
    if (!dateStr) return '–';
    const d = new Date(dateStr);
    if (isNaN(d)) return dateStr;
    return d.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
}

function escapeHtml(text) {
    const d = document.createElement('div');
    d.appendChild(document.createTextNode(text));
    return d.innerHTML;
}