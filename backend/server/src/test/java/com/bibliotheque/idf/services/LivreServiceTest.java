package com.bibliotheque.idf.services;

import devOps.adapter.BibliothequeAdapterFactory;
import devOps.adapter.LibraryAdapter;
import devOps.dtos.LivreResponseDTO;
import devOps.services.LivreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LivreServiceTest {

    private LivreService livreService;
    private BibliothequeAdapterFactory adapterFactory;
    private LibraryAdapter adapterSorbonne;
    private LibraryAdapter adapterDescartes;

    @BeforeEach
    void setUp() throws Exception {
        livreService = new LivreService();

        adapterFactory = mock(BibliothequeAdapterFactory.class);
        adapterSorbonne = mock(LibraryAdapter.class);
        adapterDescartes = mock(LibraryAdapter.class);

        Field field = LivreService.class.getDeclaredField("adapterFactory");
        field.setAccessible(true);
        field.set(livreService, adapterFactory);
    }

    @Test
    void rechercherLivre_shouldReturnResultsFromAllLibraries() {
        LivreResponseDTO livre1 = new LivreResponseDTO();
        livre1.setTitre("Java pour débutants");
        livre1.setAuteur("Auteur A");
        livre1.setBibliotheque("Sorbonne");

        LivreResponseDTO livre2 = new LivreResponseDTO();
        livre2.setTitre("Java avancé");
        livre2.setAuteur("Auteur B");
        livre2.setBibliotheque("Paris Descartes");

        when(adapterFactory.getAllAdapters()).thenReturn(List.of(adapterSorbonne, adapterDescartes));
        when(adapterSorbonne.rechercherLivre("Java")).thenReturn(List.of(livre1));
        when(adapterDescartes.rechercherLivre("Java")).thenReturn(List.of(livre2));

        List<LivreResponseDTO> resultats = livreService.rechercherLivre("Java");

        assertEquals(2, resultats.size());
        assertEquals("Java pour débutants", resultats.get(0).getTitre());
        assertEquals("Java avancé", resultats.get(1).getTitre());

        verify(adapterSorbonne).rechercherLivre("Java");
        verify(adapterDescartes).rechercherLivre("Java");
    }

    @Test
    void rechercherLivre_shouldContinueIfOneLibraryFails() {
        LivreResponseDTO livre = new LivreResponseDTO();
        livre.setTitre("Python pratique");
        livre.setAuteur("Auteur C");
        livre.setBibliotheque("Paris Descartes");

        when(adapterFactory.getAllAdapters()).thenReturn(List.of(adapterSorbonne, adapterDescartes));
        when(adapterSorbonne.getNomBibliotheque()).thenReturn("Sorbonne");
        when(adapterSorbonne.rechercherLivre("Python")).thenThrow(new RuntimeException("Erreur API"));
        when(adapterDescartes.rechercherLivre("Python")).thenReturn(List.of(livre));

        List<LivreResponseDTO> resultats = livreService.rechercherLivre("Python");

        assertEquals(1, resultats.size());
        assertEquals("Python pratique", resultats.get(0).getTitre());

        verify(adapterSorbonne).rechercherLivre("Python");
        verify(adapterDescartes).rechercherLivre("Python");
    }

    @Test
    void rechercherLivreDansBibliotheque_shouldReturnBooksFromSelectedLibrary() {
        LivreResponseDTO livre = new LivreResponseDTO();
        livre.setTitre("SQL essentiel");
        livre.setAuteur("Auteur D");
        livre.setBibliotheque("Sorbonne");

        when(adapterFactory.getAdapter("Sorbonne")).thenReturn(adapterSorbonne);
        when(adapterSorbonne.rechercherLivre("SQL")).thenReturn(List.of(livre));

        List<LivreResponseDTO> resultats =
                livreService.rechercherLivreDansBibliotheque("SQL", "Sorbonne");

        assertEquals(1, resultats.size());
        assertEquals("SQL essentiel", resultats.get(0).getTitre());

        verify(adapterFactory).getAdapter("Sorbonne");
        verify(adapterSorbonne).rechercherLivre("SQL");
    }

    @Test
    void rechercherLivreDansBibliotheque_shouldThrowExceptionWhenLibraryNotFound() {
        when(adapterFactory.getAdapter("Bibliothèque inconnue")).thenReturn(null);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> livreService.rechercherLivreDansBibliotheque("Java", "Bibliothèque inconnue")
        );

        assertTrue(exception.getMessage().contains("Bibliothèque non trouvée"));
    }
}