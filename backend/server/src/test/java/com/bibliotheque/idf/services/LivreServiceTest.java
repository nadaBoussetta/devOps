package devOps.services;

import devOps.adapter.BibliothequeAdapterFactory;
import devOps.adapter.LibraryAdapter;
import devOps.dtos.LivreResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivreServiceTest {

    @Mock
    private BibliothequeAdapterFactory adapterFactory;

    @Mock
    private LibraryAdapter adapterA;

    @Mock
    private LibraryAdapter adapterB;

    @InjectMocks
    private LivreService livreService;

    @Test
    void rechercherLivre_shouldAggregateResultsAndIgnoreFailingAdapter() {
        String titre = "Les Miserables";
        LivreResponseDTO livre = new LivreResponseDTO("Les Miserables", "Victor Hugo", "Bibliotheque A", true, "A1", "ISBN-1");

        when(adapterFactory.getAllAdapters()).thenReturn(List.of(adapterA, adapterB));
        when(adapterA.rechercherLivre(titre)).thenReturn(List.of(livre));
        when(adapterB.rechercherLivre(titre)).thenThrow(new RuntimeException("API KO"));

        List<LivreResponseDTO> resultats = livreService.rechercherLivre(titre);

        assertEquals(1, resultats.size());
        assertEquals("Bibliotheque A", resultats.get(0).getBibliotheque());
        verify(adapterA).rechercherLivre(titre);
        verify(adapterB).rechercherLivre(titre);
    }

    @Test
    void rechercherLivreDansBibliotheque_shouldThrowWhenAdapterMissing() {
        when(adapterFactory.getAdapter("Inconnue")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> livreService.rechercherLivreDansBibliotheque("Titre", "Inconnue"));

        assertTrue(ex.getMessage().contains("non trouv"));
    }
}