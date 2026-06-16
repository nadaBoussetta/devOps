package com.bibliotheque.idf.controllers;

import devOps.controllers.NotationController;
import devOps.dtos.NotationDTO;
import devOps.services.NotationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotationControllerTest {

    private NotationController notationController;
    private NotationService notationService;

    @BeforeEach
    void setUp() throws Exception {
        notationController = new NotationController();
        notationService = mock(NotationService.class);

        Field field = NotationController.class.getDeclaredField("notationService");
        field.setAccessible(true);
        field.set(notationController, notationService);
    }

    @Test
    void getAvisBibliotheque_shouldReturnAvis() {
        NotationDTO avis = new NotationDTO();

        when(notationService.getNotationsByBibliotheque(1L))
                .thenReturn(List.of(avis));

        var response = notationController.getAvisBibliotheque(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());

        verify(notationService).getNotationsByBibliotheque(1L);
    }

    @Test
    void getMesAvis_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class, () -> notationController.getMesAvis());
    }

    @Test
    void noter_shouldThrowWhenUserNotAuthenticated() {
        NotationDTO dto = new NotationDTO();

        assertThrows(RuntimeException.class, () -> notationController.noter(dto));
    }

    @Test
    void supprimer_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class, () -> notationController.supprimer(1L));
    }

    @Test
    void getMesFavoris_shouldThrowWhenUserNotAuthenticated() {
        assertThrows(RuntimeException.class, () -> notationController.getMesFavoris());
    }
}