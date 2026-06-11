/**
 * Feed Social — réactions, commentaires, reposts
 */

// Définition des réactions avec emoji, label et couleur
const REACTIONS = {
    JAIME:  { emoji: '👍', label: "J'aime",  color: '#3b82f6' },
    ADORER: { emoji: '❤️', label: 'Adorer',  color: '#ef4444' },
    HAHA:   { emoji: '😂', label: 'Haha',    color: '#f59e0b' },
    WOUAH:  { emoji: '😮', label: 'Wouah',   color: '#8b5cf6' },
    TRISTE: { emoji: '😢', label: 'Triste',  color: '#6b7280' },
};

let repostTargetId = null;

// ─── Init ─────────────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', async () => {
    if (!isAuthenticated()) {
        alert('Vous devez être connecté pour accéder au feed social.');
        window.location.href = 'login.html';
        return;
    }

    // Initiale dans la zone de composition
    const username = localStorage.getItem('username') || '?';
    const composeAvatar = document.getElementById('compose-avatar');
    const welcomeAvatar = document.getElementById('welcome-avatar');
    const welcomeUsername = document.getElementById('welcome-username');
    if (welcomeAvatar) welcomeAvatar.textContent = username[0].toUpperCase();
    if (welcomeUsername) welcomeUsername.textContent = username;
    if (composeAvatar) composeAvatar.textContent = username[0].toUpperCase();

    await loadPosts();

    // Formulaire publication
    document.getElementById('create-post-form').addEventListener('submit', handleCreatePost);

    // Compteur caractères
    const textarea = document.getElementById('post-content');
    const counter  = document.getElementById('post-count');
    textarea.addEventListener('input', () => { counter.textContent = textarea.value.length; });

    // Modal repost
    document.getElementById('modal-repost-cancel').addEventListener('click', closeRepostModal);
    document.getElementById('modal-repost-confirm').addEventListener('click', handleRepost);
    document.getElementById('modal-repost').addEventListener('click', (e) => {
        if (e.target === document.getElementById('modal-repost')) closeRepostModal();
    });
});

// ─── Chargement ───────────────────────────────────────────────────────────────

async function loadPosts() {
    const container = document.getElementById('posts-list');
    try {
        const posts = await FeedAPI.getAllPosts();
        if (!posts.length) {
            container.innerHTML = '<p class="feed-empty"><i class="fas fa-dove"></i> Aucune publication pour le moment. Soyez le premier !</p>';
            return;
        }
        container.innerHTML = '';
        posts.forEach(post => container.appendChild(createPostElement(post)));
    } catch (err) {
        container.innerHTML = `<p class="feed-error"><i class="fas fa-exclamation-circle"></i> Erreur : ${err.message}</p>`;
    }
}

// ─── Création d'un post ───────────────────────────────────────────────────────

async function handleCreatePost(e) {
    e.preventDefault();
    const input = document.getElementById('post-content');
    const contenu = input.value.trim();
    if (!contenu) return;

    const btn = e.target.querySelector('.btn-post');
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Publication...';

    try {
        await FeedAPI.createPost(contenu);
        input.value = '';
        document.getElementById('post-count').textContent = '0';
        await loadPosts();
    } catch (err) {
        alert('Erreur : ' + err.message);
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-paper-plane"></i> Publier';
    }
}

// ─── Élément post ─────────────────────────────────────────────────────────────

function createPostElement(post) {
    const div = document.createElement('div');
    div.className = 'post-card' + (post.estRepost ? ' post-repost' : '');
    div.id = `post-${post.id}`;

    const date = formatDate(post.dateCreation);
    const initiale = (post.auteurUsername || '?')[0].toUpperCase();
    const totalReactions = post.reactions
        ? Object.values(post.reactions).reduce((a, b) => a + b, 0)
        : 0;

    div.innerHTML = `
        <!-- En-tête -->
        <div class="post-header">
            <div class="post-avatar">${initiale}</div>
            <div class="post-meta">
                <span class="post-author">${escapeHtml(post.auteurUsername || 'Anonyme')}</span>
                ${post.bibliothequeNom ? `<span class="post-biblio"><i class="fas fa-building-columns"></i> ${escapeHtml(post.bibliothequeNom)}</span>` : ''}
                <span class="post-date">${date}</span>
            </div>
        </div>

        <!-- Badge repost -->
        ${post.estRepost ? `<div class="repost-badge"><i class="fas fa-retweet"></i> A republié</div>` : ''}

        <!-- Post original embarqué -->
        ${post.estRepost && post.postOriginalContenu ? `
        <div class="post-original-embed">
            <div class="embed-header">
                <span class="embed-avatar">${(post.postOriginalAuteur || '?')[0].toUpperCase()}</span>
                <span class="embed-author">${escapeHtml(post.postOriginalAuteur || '')}</span>
                <span class="embed-date">${formatDate(post.postOriginalDate)}</span>
            </div>
            <p class="embed-contenu">${escapeHtml(post.postOriginalContenu)}</p>
        </div>
        ` : ''}

        <!-- Contenu -->
        ${post.contenu ? `<p class="post-body">${escapeHtml(post.contenu)}</p>` : ''}

        <!-- Compteur réactions -->
        <div class="reactions-summary" id="reactions-summary-${post.id}">
            ${buildReactionsSummary(post.reactions, post.maReaction, totalReactions)}
        </div>

        <!-- Barre d'actions -->
        <div class="post-actions-bar">

            <!-- Bouton réaction principal + popup -->
            <div class="reaction-wrapper">
                <button class="action-btn reaction-btn ${post.maReaction ? 'reacted' : ''}"
                    id="reaction-btn-${post.id}"
                    style="${post.maReaction ? `color:${REACTIONS[post.maReaction]?.color}` : ''}"
                    onclick="toggleReactionPicker(${post.id})">
                    ${post.maReaction
                        ? `${REACTIONS[post.maReaction].emoji} <span>${REACTIONS[post.maReaction].label}</span>`
                        : `<i class="fas fa-thumbs-up"></i> <span>J'aime</span>`}
                </button>
                <div class="reaction-picker" id="picker-${post.id}">
                    ${Object.entries(REACTIONS).map(([key, r]) => `
                        <button class="reaction-option ${post.maReaction === key ? 'active' : ''}"
                            title="${r.label}"
                            onclick="handleReaction(${post.id}, '${key}')">
                            <span class="reaction-emoji">${r.emoji}</span>
                            <span class="reaction-label">${r.label}</span>
                        </button>
                    `).join('')}
                </div>
            </div>

            <!-- Commentaires -->
            <button class="action-btn" onclick="toggleComments(${post.id})">
                <i class="fas fa-comment"></i>
                <span>Commenter ${(post.comments && post.comments.length) ? `(${post.comments.length})` : ''}</span>
            </button>

            <!-- Repost -->
            <button class="action-btn" onclick="openRepostModal(${post.id})">
                <i class="fas fa-retweet"></i>
                <span>Republier${post.nbReposts ? ` (${post.nbReposts})` : ''}</span>
            </button>
        </div>

        <!-- Section commentaires (masquée par défaut) -->
        <div class="comments-section" id="comments-${post.id}" style="display:none">
            <div class="comments-list" id="comments-list-${post.id}">
                ${renderComments(post.comments || [], post.id)}
            </div>
            <form class="comment-form" onsubmit="handleAddComment(event, ${post.id})">
                <input type="text" placeholder="Écrire un commentaire..." required maxlength="500">
                <button type="submit"><i class="fas fa-paper-plane"></i></button>
            </form>
        </div>
    `;

    // Fermer le picker si on clique ailleurs
    document.addEventListener('click', (e) => {
        const picker = document.getElementById(`picker-${post.id}`);
        const btn    = document.getElementById(`reaction-btn-${post.id}`);
        if (picker && !picker.contains(e.target) && btn && !btn.contains(e.target)) {
            picker.classList.remove('open');
        }
    });

    return div;
}

// ─── Résumé des réactions ─────────────────────────────────────────────────────

function buildReactionsSummary(reactions, maReaction, total) {
    if (!reactions || total === 0) return '';

    const topEmojis = Object.entries(reactions)
        .filter(([, v]) => v > 0)
        .sort(([, a], [, b]) => b - a)
        .slice(0, 3)
        .map(([k]) => REACTIONS[k].emoji)
        .join('');

    return `<span class="reactions-emojis">${topEmojis}</span> <span class="reactions-count">${total}</span>`;
}

// ─── Réactions ────────────────────────────────────────────────────────────────

function toggleReactionPicker(postId) {
    const picker = document.getElementById(`picker-${postId}`);
    if (picker) picker.classList.toggle('open');
}

async function handleReaction(postId, type) {
    // Fermer le picker
    const picker = document.getElementById(`picker-${postId}`);
    if (picker) picker.classList.remove('open');

    try {
        const result = await FeedAPI.reagir(postId, type);

        // Mettre à jour le bouton principal
        const btn = document.getElementById(`reaction-btn-${postId}`);
        if (btn) {
            if (result.maReaction) {
                const r = REACTIONS[result.maReaction];
                btn.innerHTML = `${r.emoji} <span>${r.label}</span>`;
                btn.style.color = r.color;
                btn.classList.add('reacted');
            } else {
                btn.innerHTML = `<i class="fas fa-thumbs-up"></i> <span>J'aime</span>`;
                btn.style.color = '';
                btn.classList.remove('reacted');
            }
        }

        // Mettre à jour le résumé
        const summary = document.getElementById(`reactions-summary-${postId}`);
        if (summary) {
            const total = Object.values(result.comptages).reduce((a, b) => a + b, 0);
            summary.innerHTML = buildReactionsSummary(result.comptages, result.maReaction, total);
        }

        // Mettre à jour les options actives dans le picker
        Object.keys(REACTIONS).forEach(key => {
            const opt = picker?.querySelector(`.reaction-option[onclick*="'${key}'"]`);
            if (opt) opt.classList.toggle('active', result.maReaction === key);
        });

    } catch (err) {
        alert('Erreur : ' + err.message);
    }
}

// ─── Commentaires ─────────────────────────────────────────────────────────────

function toggleComments(postId) {
    const section = document.getElementById(`comments-${postId}`);
    if (section) section.style.display = section.style.display === 'none' ? 'block' : 'none';
}

function renderComments(comments, postId) {
    if (!comments.length) return '<p class="no-comments">Aucun commentaire — soyez le premier !</p>';
    return comments.map(c => `
        <div class="comment-item" id="comment-${c.id}">
            <div class="comment-avatar">${(c.auteurUsername || '?')[0].toUpperCase()}</div>
            <div class="comment-body">
                <div class="comment-header">
                    <span class="comment-author">${escapeHtml(c.auteurUsername || 'Anonyme')}</span>
                    <span class="comment-date">${formatDate(c.dateCreation)}</span>
                </div>
                <p class="comment-text">${escapeHtml(c.contenu)}</p>
                <button class="reply-btn" onclick="toggleReplyForm(${c.id}, ${postId})">
                    <i class="fas fa-reply"></i> Répondre
                </button>
                <div id="reply-form-${c.id}" style="display:none">
                    <form class="comment-form reply-form" onsubmit="handleReply(event, ${c.id})">
                        <input type="text" placeholder="Votre réponse..." required maxlength="500">
                        <button type="submit"><i class="fas fa-paper-plane"></i></button>
                    </form>
                </div>
                ${c.reponses && c.reponses.length ? `
                    <div class="replies-list">
                        ${renderComments(c.reponses, postId)}
                    </div>
                ` : ''}
            </div>
        </div>
    `).join('');
}

async function handleAddComment(e, postId) {
    e.preventDefault();
    const input = e.target.querySelector('input');
    const contenu = input.value.trim();
    if (!contenu) return;

    const btn = e.target.querySelector('button');
    btn.disabled = true;

    try {
        await FeedAPI.addComment(postId, contenu);
        input.value = '';
        // Recharger uniquement ce post
        const updated = await FeedAPI.getPostById(postId);
        const list = document.getElementById(`comments-list-${postId}`);
        if (list) list.innerHTML = renderComments(updated.comments || [], postId);
    } catch (err) {
        alert('Erreur : ' + err.message);
    } finally {
        btn.disabled = false;
    }
}

function toggleReplyForm(commentId, postId) {
    const form = document.getElementById(`reply-form-${commentId}`);
    if (form) form.style.display = form.style.display === 'none' ? 'block' : 'none';
}

async function handleReply(e, commentId) {
    e.preventDefault();
    const input = e.target.querySelector('input');
    const contenu = input.value.trim();
    if (!contenu) return;

    try {
        const reply = await FeedAPI.addReply(commentId, contenu);
        input.value = '';
        document.getElementById(`reply-form-${commentId}`).style.display = 'none';
        // Recharger le post parent
        const postId = reply.postId;
        const updated = await FeedAPI.getPostById(postId);
        const list = document.getElementById(`comments-list-${postId}`);
        if (list) list.innerHTML = renderComments(updated.comments || [], postId);
    } catch (err) {
        alert('Erreur : ' + err.message);
    }
}

// ─── Repost ───────────────────────────────────────────────────────────────────

function openRepostModal(postId) {
    repostTargetId = postId;
    document.getElementById('repost-commentaire').value = '';
    document.getElementById('modal-repost').style.display = 'flex';
}

function closeRepostModal() {
    repostTargetId = null;
    document.getElementById('modal-repost').style.display = 'none';
}

async function handleRepost() {
    if (!repostTargetId) return;
    const commentaire = document.getElementById('repost-commentaire').value.trim();
    const btn = document.getElementById('modal-repost-confirm');
    btn.disabled = true;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Republication...';

    try {
        await FeedAPI.repost(repostTargetId, commentaire);
        closeRepostModal();
        await loadPosts();
    } catch (err) {
        alert('Erreur : ' + err.message);
    } finally {
        btn.disabled = false;
        btn.innerHTML = '<i class="fas fa-retweet"></i> Republier';
    }
}

// ─── Utilitaires ──────────────────────────────────────────────────────────────

function formatDate(dateStr) {
    if (!dateStr) return '';
    const d = new Date(dateStr);
    const now = new Date();
    const diff = Math.floor((now - d) / 1000);
    if (diff < 60)   return 'À l\'instant';
    if (diff < 3600) return `Il y a ${Math.floor(diff / 60)} min`;
    if (diff < 86400) return `Il y a ${Math.floor(diff / 3600)} h`;
    return d.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' });
}

function escapeHtml(text) {
    if (!text) return '';
    const d = document.createElement('div');
    d.appendChild(document.createTextNode(text));
    return d.innerHTML;
}