package com.bibliotheque.idf.controllers;

import devOps.controllers.LivreController;
import devOps.dtos.DisponibiliteRequestDTO;
import devOps.dtos.DisponibiliteResultatDTO;
import devOps.dtos.LivreResponseDTO;
import devOps.services.LivreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LivreControllerTest {

    private LivreController livreController;
    private LivreService livreService;

    @BeforeEach
    void setUp() throws Exception {
        livreController = new LivreController();
        livreService = mock(LivreService.class);

        Field field = LivreController.class.getDeclaredField("livreService");
        field.setAccessible(true);
        field.set(livreController, livreService);
    }

    @Test
    void rechercherLivre_shouldReturnResults() {

        LivreResponseDTO livre = new LivreResponseDTO();
        livre.setTitre("Java");

        when(livreService.rechercherLivre("Java"))
                .thenReturn(List.of(livre));

        var response = livreController.rechercherLivre("Java");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Java", response.getBody().get(0).getTitre());

        verify(livreService).rechercherLivre("Java");
    }

    @Test
    void rechercherLivreDansBibliotheque_shouldReturnResults() {

        LivreResponseDTO livre = new LivreResponseDTO();
        livre.setTitre("SQL");

        when(livreService.rechercherLivreDansBibliotheque(
                "SQL",
                "Sorbonne"))
                .thenReturn(List.of(livre));

        var response =
                livreController.rechercherLivreDansBibliotheque(
                        "Sorbonne",
                        "SQL"
                );

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("SQL", response.getBody().get(0).getTitre());

        verify(livreService)
                .rechercherLivreDansBibliotheque("SQL", "Sorbonne");
    }

    @Test
    void verifierDisponibilite_shouldReturnResults() {

        DisponibiliteRequestDTO request =
                new DisponibiliteRequestDTO();

        request.setTitre("Les Misérables");
        request.setBibliotheques(List.of());

        DisponibiliteResultatDTO resultat =
                new DisponibiliteResultatDTO();

        when(livreService.verifierDisponibiliteParBibliotheques(
                anyString(),
                anyList()))
                .thenReturn(List.of(resultat));

        var response =
                livreController.verifierDisponibilite(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());

        verify(livreService)
                .verifierDisponibiliteParBibliotheques(
                        eq("Les Misérables"),
                        anyList()
                );
    }
}