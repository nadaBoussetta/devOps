package com.bibliotheque.idf.service;

import devOps.dtos.LibraryResponseDTO;
import devOps.dtos.RechercheDTO;
import devOps.enums.TypeBibliotheque;
import devOps.models.*;
import devOps.repositories.LibraryRepository;
import devOps.services.GeolocationService;
import devOps.services.LibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BibliothequeServiceTest {

    @Mock
    private LibraryRepository bibliothequeRepository;

    @Mock
    private GeolocationService geolocationService;

    @InjectMocks
    private LibraryService bibliothequeService;

    private List<LibraryEntity> bibliotheques;

    @BeforeEach
    void setUp() {
        bibliotheques = new ArrayList<>();

        // Bibliothèque 1 - Proche et ouverte
        LibraryEntity biblio1 = new LibraryEntity();
        biblio1.setId(1L);
        biblio1.setNom("Bibliothèque Proche");
        biblio1.setAdresse("10 Rue de Test, Paris");
        biblio1.setLatitude(48.8566);
        biblio1.setLongitude(2.3522);
        biblio1.setType(TypeBibliotheque.PUBLIQUE);
        biblio1.setNoteGlobale(4.5);
        biblio1.setNombreNotations(100);

        HoraireEntity horaire1 = new HoraireEntity();
        horaire1.setId(1L);
        horaire1.setBibliotheque(biblio1);
        horaire1.setJourSemaine("LUNDI");
        horaire1.setHeureOuverture(LocalTime.of(9, 0));
        horaire1.setHeureFermeture(LocalTime.of(20, 0));
        biblio1.getHoraires().add(horaire1);

        // Bibliothèque 2 - Loin
        LibraryEntity biblio2 = new LibraryEntity();
        biblio2.setId(2L);
        biblio2.setNom("Bibliothèque Loin");
        biblio2.setAdresse("100 Rue de Loin, Paris");
        biblio2.setLatitude(48.9000);
        biblio2.setLongitude(2.4000);
        biblio2.setType(TypeBibliotheque.UNIVERSITAIRE);
        biblio2.setNoteGlobale(4.0);
        biblio2.setNombreNotations(50);

        HoraireEntity horaire2 = new HoraireEntity();
        horaire2.setId(2L);
        horaire2.setBibliotheque(biblio2);
        horaire2.setJourSemaine("LUNDI");
        horaire2.setHeureOuverture(LocalTime.of(8, 0));
        horaire2.setHeureFermeture(LocalTime.of(18, 0));
        biblio2.getHoraires().add(horaire2);

        bibliotheques.add(biblio1);
        bibliotheques.add(biblio2);
    }

    @Test
    void testRechercherBibliotheques_Success() {
        // Arrange
        RechercheDTO recherche = new RechercheDTO();
        recherche.setAdresse("Paris");
        recherche.setHeureDebut("10:00");
        recherche.setHeureFin("18:00");
        recherche.setRayon(10.0);

        when(geolocationService.geocodeAdresse("Paris")).thenReturn(new double[]{48.8566, 2.3522});
        when(bibliothequeRepository.findAll()).thenReturn(bibliotheques);

        // Act
        List<LibraryResponseDTO> resultats = bibliothequeService.rechercherBibliotheques(recherche);

        // Assert
        assertNotNull(resultats);
        assertTrue(resultats.size() > 0);
        verify(geolocationService, times(1)).geocodeAdresse("Paris");
        verify(bibliothequeRepository, times(1)).findAll();
    }

    @Test
    void testRechercherBibliotheques_FiltrageParRayon() {
        // Arrange
        RechercheDTO recherche = new RechercheDTO();
        recherche.setAdresse("Paris");
        recherche.setHeureDebut("10:00");
        recherche.setHeureFin("18:00");
        recherche.setRayon(1.0); // Rayon très petit

        when(geolocationService.geocodeAdresse("Paris")).thenReturn(new double[]{48.8566, 2.3522});
        when(bibliothequeRepository.findAll()).thenReturn(bibliotheques);

        // Act
        List<LibraryResponseDTO> resultats = bibliothequeService.rechercherBibliotheques(recherche);

        // Assert
        assertNotNull(resultats);
        // Seule la bibliothèque proche devrait être retournée
        assertTrue(resultats.size() <= 1);
    }

    @Test
    void testGetAllBibliotheques() {
        // Arrange
        when(bibliothequeRepository.findAll()).thenReturn(bibliotheques);

        // Act
        List<LibraryResponseDTO> resultats = bibliothequeService.getAllBibliotheques();

        // Assert
        assertNotNull(resultats);
        assertEquals(2, resultats.size());
        verify(bibliothequeRepository, times(1)).findAll();
    }
}
