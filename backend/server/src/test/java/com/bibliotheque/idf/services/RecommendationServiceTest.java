package devOps.services;

import devOps.dtos.LibraryResponseDTO;
import devOps.enums.TypeBibliotheque;
import devOps.models.FavoriEntity;
import devOps.models.LibraryEntity;
import devOps.models.NotationEntity;
import devOps.repositories.FavoriRepository;
import devOps.repositories.LibraryRepository;
import devOps.repositories.NotationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private NotationRepository notationRepository;

    @Mock
    private FavoriRepository favoriRepository;

    @Mock
    private LibraryRepository bibliothequeRepository;

    @Mock
    private LibraryService bibliothequeService;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void getRecommendations_shouldExcludeRatedAndFavoriteLibraries() {
        LibraryEntity b1 = createLibrary(1L, TypeBibliotheque.PUBLIQUE, 4.5, 20);
        LibraryEntity b2 = createLibrary(2L, TypeBibliotheque.UNIVERSITAIRE, 4.9, 50);
        LibraryEntity b3 = createLibrary(3L, TypeBibliotheque.PUBLIQUE, 4.0, 8);

        NotationEntity notation = new NotationEntity();
        notation.setNote(5);
        notation.setBibliotheque(b1);

        FavoriEntity favori = new FavoriEntity();
        favori.setLibraryEntity(b2);

        when(notationRepository.findByUserIdOrderByDateNotationDesc(1L)).thenReturn(List.of(notation));
        when(favoriRepository.findByUser_IdOrderByDateAjoutDesc(1L)).thenReturn(List.of(favori));
        when(bibliothequeRepository.findAll()).thenReturn(List.of(b1, b2, b3));

        LibraryResponseDTO dto3 = new LibraryResponseDTO();
        dto3.setId(3L);
        dto3.setNom("B3");

        when(bibliothequeService.getBibliothequeById(3L)).thenReturn(dto3);

        List<LibraryResponseDTO> resultats = recommendationService.getRecommendations(1L);

        assertEquals(1, resultats.size());
        assertEquals(3L, resultats.get(0).getId());
        verify(bibliothequeService, never()).getBibliothequeById(1L);
        verify(bibliothequeService, never()).getBibliothequeById(2L);
    }

    private LibraryEntity createLibrary(Long id, TypeBibliotheque type, double note, int nbNotes) {
        LibraryEntity b = new LibraryEntity();
        b.setId(id);
        b.setType(type);
        b.setNom("B" + id);
        b.setNoteGlobale(note);
        b.setNombreNotations(nbNotes);
        return b;
    }
}