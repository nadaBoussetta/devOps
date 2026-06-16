package com.bibliotheque.idf.controllers;

import devOps.controllers.FeedController;
import devOps.dtos.CommentDTO;
import devOps.dtos.PublicationDTO;
import devOps.dtos.ReactionDTO;
import devOps.services.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedControllerTest {

    private FeedController feedController;
    private FeedService feedService;

    @BeforeEach
    void setUp() throws Exception {
        feedController = new FeedController();
        feedService = mock(FeedService.class);

        Field field = FeedController.class.getDeclaredField("feedService");
        field.setAccessible(true);
        field.set(feedController, feedService);
    }

    @Test
    void getPostById_shouldReturnPost() {
        PublicationDTO post = new PublicationDTO();

        when(feedService.getPostById(1L)).thenReturn(post);

        var response = feedController.getPostById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertSame(post, response.getBody());

        verify(feedService).getPostById(1L);
    }

    @Test
    void getCommentsByPost_shouldReturnComments() {
        CommentDTO comment = new CommentDTO();

        when(feedService.getCommentsByPost(1L)).thenReturn(List.of(comment));

        var response = feedController.getCommentsByPost(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());

        verify(feedService).getCommentsByPost(1L);
    }

    @Test
    void createPost_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> feedController.createPost(new PublicationDTO(), null));
    }

    @Test
    void repost_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> feedController.repost(1L, Map.of("commentaire", "Test")));
    }

    @Test
    void reagir_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> feedController.reagir(1L, Map.of("type", "JAIME")));
    }

    @Test
    void addCommentToPost_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> feedController.addCommentToPost(1L, new CommentDTO(), null));
    }

    @Test
    void addReplyToComment_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> feedController.addReplyToComment(1L, new CommentDTO(), null));
    }
}