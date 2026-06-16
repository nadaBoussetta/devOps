package com.bibliotheque.idf.controllers;

import devOps.controllers.LibraryController;
import devOps.dtos.ItineraireResponseDTO;
import devOps.dtos.LibraryResponseDTO;
import devOps.dtos.RechercheDTO;
import devOps.services.ItineraireOptimisationService;
import devOps.services.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LibraryControllerTest {

    private LibraryController libraryController;
    private LibraryService libraryService;
    private ItineraireOptimisationService itineraireOptimisationService;

    @BeforeEach
    void setUp() throws Exception {
        libraryController = new LibraryController();

        libraryService = mock(LibraryService.class);
        itineraireOptimisationService = mock(ItineraireOptimisationService.class);

        inject("libraryService", libraryService);
        inject("itineraireOptimisationService", itineraireOptimisationService);
    }

    private void inject(String fieldName, Object value) throws Exception {
        Field field = LibraryController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(libraryController, value);
    }

    @Test
    void rechercherBibliotheques_shouldReturnResults() {
        RechercheDTO rechercheDTO = new RechercheDTO();

        LibraryResponseDTO bibliotheque = new LibraryResponseDTO();
        bibliotheque.setId(1L);
        bibliotheque.setNom("Bibliothèque Sorbonne");

        when(libraryService.rechercherBibliotheques(rechercheDTO))
                .thenReturn(List.of(bibliotheque));

        var response = libraryController.rechercherBibliotheques(rechercheDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Bibliothèque Sorbonne", response.getBody().get(0).getNom());

        verify(libraryService).rechercherBibliotheques(rechercheDTO);
    }

    @Test
    void calculerItineraire_shouldReturnItinerary() {
        RechercheDTO rechercheDTO = new RechercheDTO();

        ItineraireResponseDTO itineraire = new ItineraireResponseDTO();
        itineraire.setAdresseDepart("Paris");

        when(itineraireOptimisationService.calculerItineraire(rechercheDTO))
                .thenReturn(itineraire);

        var response = libraryController.calculerItineraire(rechercheDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Paris", response.getBody().getAdresseDepart());

        verify(itineraireOptimisationService).calculerItineraire(rechercheDTO);
    }

    @Test
    void getAllBibliotheques_shouldReturnAllLibraries() {
        LibraryResponseDTO bibliotheque = new LibraryResponseDTO();
        bibliotheque.setId(2L);
        bibliotheque.setNom("Bibliothèque Nanterre");

        when(libraryService.getAllBibliotheques())
                .thenReturn(List.of(bibliotheque));

        var response = libraryController.getAllBibliotheques();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals(2L, response.getBody().get(0).getId());

        verify(libraryService).getAllBibliotheques();
    }

    @Test
    void getBibliothequeById_shouldReturnLibrary() {
        LibraryResponseDTO bibliotheque = new LibraryResponseDTO();
        bibliotheque.setId(3L);
        bibliotheque.setNom("Bibliothèque Sainte-Geneviève");

        when(libraryService.getBibliothequeById(3L))
                .thenReturn(bibliotheque);

        var response = libraryController.getBibliothequeById(3L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(3L, response.getBody().getId());
        assertEquals("Bibliothèque Sainte-Geneviève", response.getBody().getNom());

        verify(libraryService).getBibliothequeById(3L);
    }
}