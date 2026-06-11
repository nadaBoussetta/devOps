package com.bibliotheque.idf.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import devOps.enums.ReactionType;
import devOps.models.PublicationEntity;
import devOps.models.ReactionEntity;
import devOps.models.UtilisateurEntity;

class ReactionEntityTest {

    @Test
    void reactionEntity_shouldStoreAllFields() {
        UtilisateurEntity user = new UtilisateurEntity();
        user.setId(1L);
        user.setUsername("alice");

        PublicationEntity post = new PublicationEntity();
        post.setId(10L);

        ReactionEntity reaction = new ReactionEntity();
        reaction.setId(5L);
        reaction.setUser(user);
        reaction.setPost(post);
        reaction.setType(ReactionType.JAIME);

        assertEquals(5L, reaction.getId());
        assertEquals(user, reaction.getUser());
        assertEquals(post, reaction.getPost());
        assertEquals(ReactionType.JAIME, reaction.getType());
    }

    @Test
    void reactionType_shouldHaveFiveValues() {
        ReactionType[] types = ReactionType.values();
        assertEquals(5, types.length);
    }

    @Test
    void reactionType_shouldContainAllExpectedValues() {
        assertNotNull(ReactionType.valueOf("JAIME"));
        assertNotNull(ReactionType.valueOf("ADORER"));
        assertNotNull(ReactionType.valueOf("HAHA"));
        assertNotNull(ReactionType.valueOf("WOUAH"));
        assertNotNull(ReactionType.valueOf("TRISTE"));
    }
}