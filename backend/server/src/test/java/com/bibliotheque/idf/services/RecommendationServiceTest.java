package com.bibliotheque.idf.services;

import devOps.dtos.LibraryResponseDTO;
import devOps.enums.TypeBibliotheque;
import devOps.models.FavoriEntity;
import devOps.models.LibraryEntity;
import devOps.models.NotationEntity;
import devOps.repositories.FavoriRepository;
import devOps.repositories.LibraryRepository;
import devOps.repositories.NotationRepository;
import devOps.services.LibraryService;
import devOps.services.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {

    private RecommendationService recommendationService;
    private NotationRepository notationRepository;
    private FavoriRepository favoriRepository;
    private LibraryRepository libraryRepository;
    private LibraryService libraryService;

    @BeforeEach
    void setUp() throws Exception {
        recommendationService = new RecommendationService();

        notationRepository = mock(NotationRepository.class);
        favoriRepository = mock(FavoriRepository.class);
        libraryRepository = mock(LibraryRepository.class);
        libraryService = mock(LibraryService.class);

        inject("notationRepository", notationRepository);
        inject("favoriRepository", favoriRepository);
        inject("bibliothequeRepository", libraryRepository);
        inject("bibliothequeService", libraryService);
    }

    private void inject(String fieldName, Object value) throws Exception {
        Field field = RecommendationService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(recommendationService, value);
    }

    @Test
    void getRecommendations_shouldReturnEmptyListWhenNoLibraryExists() {
        when(notationRepository.findByUserIdOrderByDateNotationDesc(1L)).thenReturn(List.of());
        when(favoriRepository.findByUser_IdOrderByDateAjoutDesc(1L)).thenReturn(List.of());
        when(libraryRepository.findAll()).thenReturn(List.of());

        List<LibraryResponseDTO> resultats = recommendationService.getRecommendations(1L);

        assertTrue(resultats.isEmpty());
        verify(libraryRepository).findAll();
    }

    @Test
    void getRecommendations_shouldLimitResultsToTenLibraries() {
        when(notationRepository.findByUserIdOrderByDateNotationDesc(1L)).thenReturn(List.of());
        when(favoriRepository.findByUser_IdOrderByDateAjoutDesc(1L)).thenReturn(List.of());

        List<LibraryEntity> bibliotheques = new ArrayList<>();

        for (long i = 1; i <= 15; i++) {
            LibraryEntity biblio = createLibrary(
                    i,
                    "Bibliothèque " + i,
                    TypeBibliotheque.UNIVERSITAIRE,
                    4.0,
                    20
            );

            bibliotheques.add(biblio);

            LibraryResponseDTO dto = new LibraryResponseDTO();
            dto.setId(i);
            dto.setNom("Bibliothèque " + i);

            when(libraryService.getBibliothequeById(i)).thenReturn(dto);
        }

        when(libraryRepository.findAll()).thenReturn(bibliotheques);

        List<LibraryResponseDTO> resultats = recommendationService.getRecommendations(1L);

        assertEquals(10, resultats.size());
    }

    @Test
    void getRecommendations_shouldExcludeFavoriteLibraries() {
        LibraryEntity favoriteLibrary = createLibrary(
                1L,
                "Bibliothèque déjà en favori",
                TypeBibliotheque.UNIVERSITAIRE,
                5.0,
                30
        );

        LibraryEntity recommendedLibrary = createLibrary(
                2L,
                "Bibliothèque recommandée",
                TypeBibliotheque.UNIVERSITAIRE,
                4.0,
                20
        );

        FavoriEntity favori = new FavoriEntity();
        favori.setLibraryEntity(favoriteLibrary);

        LibraryResponseDTO recommendedDto = new LibraryResponseDTO();
        recommendedDto.setId(2L);
        recommendedDto.setNom("Bibliothèque recommandée");

        when(notationRepository.findByUserIdOrderByDateNotationDesc(1L)).thenReturn(List.of());
        when(favoriRepository.findByUser_IdOrderByDateAjoutDesc(1L)).thenReturn(List.of(favori));
        when(libraryRepository.findAll()).thenReturn(List.of(favoriteLibrary, recommendedLibrary));
        when(libraryService.getBibliothequeById(2L)).thenReturn(recommendedDto);

        List<LibraryResponseDTO> resultats = recommendationService.getRecommendations(1L);

        assertEquals(1, resultats.size());
        assertEquals(2L, resultats.get(0).getId());

        verify(libraryService, never()).getBibliothequeById(1L);
        verify(libraryService).getBibliothequeById(2L);
    }

    @Test
    void getRecommendations_shouldExcludeHighlyRatedLibraries() {
        LibraryEntity alreadyRatedLibrary = createLibrary(
                1L,
                "Bibliothèque déjà bien notée",
                TypeBibliotheque.UNIVERSITAIRE,
                5.0,
                30
        );

        LibraryEntity newLibrary = createLibrary(
                2L,
                "Nouvelle bibliothèque",
                TypeBibliotheque.UNIVERSITAIRE,
                4.0,
                20
        );

        NotationEntity notation = new NotationEntity();
        notation.setBibliotheque(alreadyRatedLibrary);
        notation.setNote(5);

        LibraryResponseDTO newLibraryDto = new LibraryResponseDTO();
        newLibraryDto.setId(2L);
        newLibraryDto.setNom("Nouvelle bibliothèque");

        when(notationRepository.findByUserIdOrderByDateNotationDesc(1L)).thenReturn(List.of(notation));
        when(favoriRepository.findByUser_IdOrderByDateAjoutDesc(1L)).thenReturn(List.of());
        when(libraryRepository.findAll()).thenReturn(List.of(alreadyRatedLibrary, newLibrary));
        when(libraryService.getBibliothequeById(2L)).thenReturn(newLibraryDto);

        List<LibraryResponseDTO> resultats = recommendationService.getRecommendations(1L);

        assertEquals(1, resultats.size());
        assertEquals(2L, resultats.get(0).getId());

        verify(libraryService, never()).getBibliothequeById(1L);
        verify(libraryService).getBibliothequeById(2L);
    }

    private LibraryEntity createLibrary(
            Long id,
            String nom,
            TypeBibliotheque type,
            Double noteGlobale,
            Integer nombreNotations
    ) {
        LibraryEntity library = new LibraryEntity();
        library.setId(id);
        library.setNom(nom);
        library.setAdresse("Adresse test");
        library.setLatitude(48.8566);
        library.setLongitude(2.3522);
        library.setType(type);
        library.setNoteGlobale(noteGlobale);
        library.setNombreNotations(nombreNotations);
        return library;
    }
}