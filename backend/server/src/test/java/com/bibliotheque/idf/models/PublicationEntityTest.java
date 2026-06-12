package com.bibliotheque.idf.models;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import devOps.models.CommentEntity;
import devOps.models.PublicationEntity;
import devOps.models.UtilisateurEntity;

class PublicationEntityTest {

    @Test
    void publicationEntity_shouldStoreBasicFields() {
        UtilisateurEntity user = new UtilisateurEntity();
        user.setId(1L);
        user.setUsername("alice");

        PublicationEntity post = new PublicationEntity();
        post.setId(1L);
        post.setContenu("Contenu du post");
        post.setAuteur(user);
        post.setEstRepost(false);
        post.setDateCreation(LocalDateTime.now());

        assertEquals(1L, post.getId());
        assertEquals("Contenu du post", post.getContenu());
        assertEquals(user, post.getAuteur());
        assertFalse(post.getEstRepost());
        assertNull(post.getPostOriginal());
    }

    @Test
    void publicationEntity_shouldSupportRepostReference() {
        UtilisateurEntity alice = new UtilisateurEntity();
        alice.setId(1L);
        alice.setUsername("alice");

        UtilisateurEntity bob = new UtilisateurEntity();
        bob.setId(2L);
        bob.setUsername("bob");

        PublicationEntity original = new PublicationEntity();
        original.setId(1L);
        original.setContenu("Post original");
        original.setAuteur(alice);
        original.setEstRepost(false);

        PublicationEntity repost = new PublicationEntity();
        repost.setId(2L);
        repost.setContenu("Commentaire du repost");
        repost.setAuteur(bob);
        repost.setEstRepost(true);
        repost.setPostOriginal(original);

        assertTrue(repost.getEstRepost());
        assertEquals(original, repost.getPostOriginal());
        assertEquals(1L, repost.getPostOriginal().getId());
        assertEquals("alice", repost.getPostOriginal().getAuteur().getUsername());
    }

    @Test
    void addComment_shouldLinkCommentToPost() {
        UtilisateurEntity user = new UtilisateurEntity();
        user.setId(1L);

        PublicationEntity post = new PublicationEntity();
        post.setId(5L);
        post.setContenu("Un post");
        post.setAuteur(user);
        post.setEstRepost(false);

        CommentEntity comment = new CommentEntity();
        comment.setId(10L);
        comment.setContenu("Un commentaire");
        comment.setAuteur(user);

        post.addComment(comment);

        assertEquals(1, post.getComments().size());
        assertEquals(post, comment.getPost());
    }
}