package com.bibliotheque.idf.models;

import devOps.models.CommentEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentEntityTest {
    
    @Test
    void addReponse_shouldLinkParentAndChild() {
        CommentEntity parent = new CommentEntity();
        CommentEntity child = new CommentEntity();

        parent.addReponse(child);

        assertEquals(1, parent.getReponses().size());
        assertTrue(parent.getReponses().contains(child));
        assertEquals(parent, child.getParentComment());
    }
}