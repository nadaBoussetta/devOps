package com.bibliotheque.idf.services;

import devOps.dtos.LibraryResponseDTO;
import devOps.dtos.RechercheDTO;
import devOps.enums.TypeBibliotheque;
import devOps.models.LibraryEntity;
import devOps.repositories.LibraryRepository;
import devOps.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LibraryServiceAdditionalTest {

    private LibraryService libraryService;
    private LibraryRepository libraryRepository;
    private GeolocationService geolocationService;
    private IleDeFranceLibraryApiService ileDeFranceLibraryApiService;
    private HoraireParser horaireParser;
    private HoraireSearchService horaireSearchService;

    @BeforeEach
    void setUp() throws Exception {
        libraryService = new LibraryService();

        libraryRepository = mock(LibraryRepository.class);
        geolocationService = mock(GeolocationService.class);
        ileDeFranceLibraryApiService = mock(IleDeFranceLibraryApiService.class);
        horaireParser = mock(HoraireParser.class);
        horaireSearchService = mock(HoraireSearchService.class);

        inject("libraryRepository", libraryRepository);
        inject("geolocationService", geolocationService);
        inject("ileDeFranceLibraryApiService", ileDeFranceLibraryApiService);
        inject("horaireParser", horaireParser);
        inject("horaireSearchService", horaireSearchService);
    }

    private void inject(String fieldName, Object value) throws Exception {
        Field field = LibraryService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(libraryService, value);
    }

    @Test
    void getAllBibliotheques_shouldReturnAllLibrariesAsDTO() {
        LibraryEntity library = createLibrary(1L, "Bibliothèque test");

        when(libraryRepository.findAll()).thenReturn(List.of(library));

        List<LibraryResponseDTO> resultats = libraryService.getAllBibliotheques();

        assertEquals(1, resultats.size());
        assertEquals(1L, resultats.get(0).getId());
        assertEquals("Bibliothèque test", resultats.get(0).getNom());
        assertEquals(TypeBibliotheque.UNIVERSITAIRE, resultats.get(0).getType());

        verify(libraryRepository).findAll();
    }

    @Test
    void getAllBibliotheques_shouldReturnEmptyListWhenRepositoryReturnsNoLibraries() {
        when(libraryRepository.findAll()).thenReturn(List.of());

        List<LibraryResponseDTO> resultats = libraryService.getAllBibliotheques();

        assertTrue(resultats.isEmpty());
        verify(libraryRepository).findAll();
    }

    @Test
    void getBibliothequeById_shouldReturnLibraryWhenItExists() {
        LibraryEntity library = createLibrary(2L, "Sorbonne");

        when(libraryRepository.findById(2L)).thenReturn(Optional.of(library));

        LibraryResponseDTO resultat = libraryService.getBibliothequeById(2L);

        assertEquals(2L, resultat.getId());
        assertEquals("Sorbonne", resultat.getNom());
        assertEquals("Adresse test", resultat.getAdresse());

        verify(libraryRepository).findById(2L);
    }

    @Test
    void getBibliothequeById_shouldThrowExceptionWhenLibraryDoesNotExist() {
        when(libraryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> libraryService.getBibliothequeById(99L)
        );

        assertTrue(exception.getMessage().contains("Bibliothèque non trouvée"));
    }

    @Test
    void rechercherBibliotheques_shouldReturnEmptyListWhenAddressIsNotFound() {
        RechercheDTO recherche = new RechercheDTO();
        recherche.setAdresse("Adresse inconnue");

        when(geolocationService.geocodeAdresse("Adresse inconnue")).thenReturn(null);

        List<LibraryResponseDTO> resultats = libraryService.rechercherBibliotheques(recherche);

        assertTrue(resultats.isEmpty());
        verify(geolocationService).geocodeAdresse("Adresse inconnue");
        verifyNoInteractions(ileDeFranceLibraryApiService);
    }

    private LibraryEntity createLibrary(Long id, String nom) {
        LibraryEntity library = new LibraryEntity();
        library.setId(id);
        library.setNom(nom);
        library.setAdresse("Adresse test");
        library.setLatitude(48.8566);
        library.setLongitude(2.3522);
        library.setType(TypeBibliotheque.UNIVERSITAIRE);
        library.setNoteGlobale(4.5);
        library.setNombreNotations(12);
        return library;
    }
}