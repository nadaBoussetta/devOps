/**
 * Script pour la page du feed social.
 * Implémente la Feature 3 : Feed social.
 */

document.addEventListener('DOMContentLoaded', async () => {
    // Vérifier l'authentification
    if (!isAuthenticated()) {
        alert('Vous devez être connecté pour accéder au feed social.');
        window.location.href = 'login.html';
        return;
    }

    // Charger les posts
    await loadPosts();

    // Gestion de la création de post
    const createPostForm = document.getElementById('create-post-form');
    createPostForm.addEventListener('submit', handleCreatePost);
});

/**
 * Charge et affiche tous les posts.
 */
async function loadPosts() {
    const container = document.getElementById('posts-list');
    
    try {
        const posts = await FeedAPI.getAllPosts();
        
        if (posts.length === 0) {
            container.innerHTML = '<p class="info-message">Aucune publication pour le moment. Soyez le premier à publier !</p>';
            return;
        }

        container.innerHTML = '';
        container.classList.remove('loading');

        posts.forEach(post => {
            const postElement = createPostElement(post);
            container.appendChild(postElement);
        });
    } catch (error) {
        container.innerHTML = `<p class="error-message">Erreur lors du chargement: ${error.message}</p>`;
    }
}

/**
 * Gère la création d'un nouveau post.
 */
async function handleCreatePost(e) {
    e.preventDefault();
    
    const contenu = document.getElementById('post-content').value;
    
    try {
        await FeedAPI.createPost(contenu);
        document.getElementById('post-content').value = '';
        await loadPosts();
    } catch (error) {
        alert('Erreur lors de la création du post: ' + error.message);
    }
}

/**
 * Crée un élément DOM pour un post.
 */
function createPostElement(post) {
    const postDiv = document.createElement('div');
    postDiv.className = 'post';
    
    const date = new Date(post.dateCreation).toLocaleDateString('fr-FR', {
        day: 'numeric',
        month: 'long',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
    
    postDiv.innerHTML = `
        <div class="post-header">
            <div>
                <span class="post-author">👤 ${post.auteurUsername}</span>
                ${post.bibliothequeNom ? `<span style="color: #6b7280;"> • 📚 ${post.bibliothequeNom}</span>` : ''}
            </div>
            <span class="post-date">${date}</span>
        </div>
        <div class="post-content">${post.contenu}</div>
        <div class="post-actions">
            <button onclick="showComments(${post.id})">💬 Commentaires (${post.comments ? post.comments.length : 0})</button>
        </div>
        <div id="comments-${post.id}" class="comments-section" style="display: none; margin-top: 1rem; padding-top: 1rem; border-top: 1px solid var(--border);">
            ${renderComments(post.comments || [])}
            <form onsubmit="handleAddComment(event, ${post.id})" style="margin-top: 1rem;">
                <textarea placeholder="Ajouter un commentaire..." rows="2" required style="width: 100%; padding: 0.5rem; border: 1px solid var(--border); border-radius: 0.5rem;"></textarea>
                <button type="submit" class="btn btn-primary" style="margin-top: 0.5rem;">Commenter</button>
            </form>
        </div>
    `;
    
    return postDiv;
}

/**
 * Affiche/masque les commentaires d'un post.
 */
function showComments(postId) {
    const commentsSection = document.getElementById(`comments-${postId}`);
    commentsSection.style.display = commentsSection.style.display === 'none' ? 'block' : 'none';
}

/**
 * Rend les commentaires d'un post.
 */
function renderComments(comments) {
    if (comments.length === 0) {
        return '<p style="color: #6b7280;">Aucun commentaire pour le moment.</p>';
    }
    
    return comments.map(comment => `
        <div style="padding: 0.75rem; background: var(--light); border-radius: 0.5rem; margin-bottom: 0.5rem;">
            <div style="display: flex; justify-content: space-between; margin-bottom: 0.5rem;">
                <strong style="color: var(--primary-color);">${comment.auteurUsername}</strong>
                <span style="color: #6b7280; font-size: 0.875rem;">
                    ${new Date(comment.dateCreation).toLocaleDateString('fr-FR')}
                </span>
            </div>
            <p style="margin: 0;">${comment.contenu}</p>
            ${comment.reponses && comment.reponses.length > 0 ? `
                <div style="margin-left: 1rem; margin-top: 0.5rem;">
                    ${renderComments(comment.reponses)}
                </div>
            ` : ''}
        </div>
    `).join('');
}

/**
 * Gère l'ajout d'un commentaire.
 */
async function handleAddComment(e, postId) {
    e.preventDefault();
    
    const form = e.target;
    const textarea = form.querySelector('textarea');
    const contenu = textarea.value;
    
    try {
        await FeedAPI.addComment(postId, contenu);
        textarea.value = '';
        await loadPosts();
    } catch (error) {
        alert('Erreur lors de l\'ajout du commentaire: ' + error.message);
    }
}
