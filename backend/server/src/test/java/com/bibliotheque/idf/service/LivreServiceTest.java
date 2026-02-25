package com.bibliotheque.idf.service;

import devOps.adapter.LibraryAdapter;
import devOps.adapter.BibliothequeAdapterFactory;
import devOps.dtos.LivreResponseDTO;
import devOps.services.LivreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LivreServiceTest {

    @Mock
    private BibliothequeAdapterFactory adapterFactory;

    @Mock
    private LibraryAdapter adapter1;

    @Mock
    private LibraryAdapter adapter2;

    @InjectMocks
    private LivreService livreService;


    @Test
    void testRechercherLivre_Success() {
        // Arrange
        String titre = "Les Misérables";
        
        LivreResponseDTO livre1 = new LivreResponseDTO("Les Misérables", "Victor Hugo", "Bibliothèque A", true, "FR-001", "123456");
        LivreResponseDTO livre2 = new LivreResponseDTO("Les Misérables", "Victor Hugo", "Bibliothèque B", false, "FR-002", "123456");
        
        when(adapter1.rechercherLivre(titre)).thenReturn(Arrays.asList(livre1));
        when(adapter2.rechercherLivre(titre)).thenReturn(Arrays.asList(livre2));
        when(adapterFactory.getAllAdapters()).thenReturn(Arrays.asList(adapter1, adapter2));

        // Act
        List<LivreResponseDTO> resultats = livreService.rechercherLivre(titre);

        // Assert
        assertNotNull(resultats);
        assertEquals(2, resultats.size());
        verify(adapter1, times(1)).rechercherLivre(titre);
        verify(adapter2, times(1)).rechercherLivre(titre);
    }

    @Test
    void testRechercherLivre_AucunResultat() {
        // Arrange
        String titre = "Livre Inexistant";
        
        when(adapter1.rechercherLivre(titre)).thenReturn(Arrays.asList());
        when(adapter2.rechercherLivre(titre)).thenReturn(Arrays.asList());
        when(adapterFactory.getAllAdapters()).thenReturn(Arrays.asList(adapter1, adapter2));

        // Act
        List<LivreResponseDTO> resultats = livreService.rechercherLivre(titre);

        // Assert
        assertNotNull(resultats);
        assertEquals(0, resultats.size());
    }

    @Test
    void testRechercherLivreDansBibliotheque_Success() {
        // Arrange
        String titre = "Les Misérables";
        String bibliotheque = "Bibliothèque A";

        LivreResponseDTO livre = new LivreResponseDTO("Les Misérables", "Victor Hugo", "Bibliothèque A", true, "FR-001", "123456");
        
        when(adapterFactory.getAdapter(bibliotheque)).thenReturn(adapter1);
        when(adapter1.rechercherLivre(titre)).thenReturn(Arrays.asList(livre));

        // Act
        List<LivreResponseDTO> resultats = livreService.rechercherLivreDansBibliotheque(titre, bibliotheque);

        // Assert
        assertNotNull(resultats);
        assertEquals(1, resultats.size());
        assertEquals("Bibliothèque A", resultats.get(0).getBibliotheque());
    }

    @Test
    void testRechercherLivreDansBibliotheque_BibliothequeInexistante() {
        // Arrange
        String titre = "Les Misérables";
        String bibliotheque = "Bibliothèque Inexistante";
        
        when(adapterFactory.getAdapter(bibliotheque)).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            livreService.rechercherLivreDansBibliotheque(titre, bibliotheque);
        });
    }
}
