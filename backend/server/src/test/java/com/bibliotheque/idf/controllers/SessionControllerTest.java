package com.bibliotheque.idf.controllers;

import devOps.controllers.SessionController;
import devOps.dtos.SessionDTO;
import devOps.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionControllerTest {

    private SessionController sessionController;
    private SessionService sessionService;

    @BeforeEach
    void setUp() throws Exception {
        sessionController = new SessionController();
        sessionService = mock(SessionService.class);

        Field field = SessionController.class.getDeclaredField("sessionService");
        field.setAccessible(true);
        field.set(sessionController, sessionService);
    }

    @Test
    void creerSession_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> sessionController.creerSession(new SessionDTO(), null));
    }

    @Test
    void getSessionEnCours_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> sessionController.getSessionEnCours(null));
    }

    @Test
    void getSessionsByUser_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> sessionController.getSessionsByUser(null));
    }

    @Test
    void getSessionsCompleteesbyUser_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> sessionController.getSessionsCompleteesbyUser(null));
    }

    @Test
    void updateTempsEcoule_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> sessionController.updateTempsEcoule(1L, 30, null));
    }

    @Test
    void completerSession_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> sessionController.completerSession(1L, null));
    }

    @Test
    void supprimerSession_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> sessionController.supprimerSession(1L, null));
    }

    @Test
    void getStatistiquesHebdomadaires_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class,
                () -> sessionController.getStatistiquesHebdomadaires(null));
    }
}