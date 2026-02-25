package devOps.services;

import devOps.dtos.LibraryResponseDTO;
import devOps.dtos.RechercheDTO;
import devOps.enums.TypeBibliotheque;
import devOps.models.HoraireEntity;
import devOps.models.LibraryEntity;
import devOps.repositories.LibraryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private LibraryRepository libraryRepository;

    @Mock
    private GeolocationService geolocationService;

    @InjectMocks
    private LibraryService libraryService;

    private LibraryEntity proche;
    private LibraryEntity loin;

    @BeforeEach
    void setUp() {
        proche = createLibrary(1L, "Bibliotheque Proche", 48.8566, 2.3522, TypeBibliotheque.PUBLIQUE, 4.6, 20,
                LocalTime.of(9, 0), LocalTime.of(20, 0));
        loin = createLibrary(2L, "Bibliotheque Loin", 48.9000, 2.4000, TypeBibliotheque.UNIVERSITAIRE, 4.0, 8,
                LocalTime.of(8, 0), LocalTime.of(17, 0));
    }

    @Test
    void rechercherBibliotheques_shouldFilterByDistanceAndSetOpenStatus() {
        RechercheDTO recherche = new RechercheDTO();
        recherche.setAdresse("Paris");
        recherche.setHeureDebut("10:00");
        recherche.setHeureFin("18:00");
        recherche.setRayon(2.0);

        when(geolocationService.geocodeAdresse("Paris")).thenReturn(new double[]{48.8566, 2.3522});
        when(libraryRepository.findAll()).thenReturn(List.of(proche, loin));

        List<LibraryResponseDTO> resultats = libraryService.rechercherBibliotheques(recherche);

        assertEquals(1, resultats.size());
        assertEquals("Bibliotheque Proche", resultats.get(0).getNom());
        assertTrue(resultats.get(0).getOuvert());
        verify(libraryRepository).findAll();
    }

    @Test
    void getBibliothequeById_shouldReturnDto() {
        when(libraryRepository.findById(1L)).thenReturn(Optional.of(proche));

        LibraryResponseDTO dto = libraryService.getBibliothequeById(1L);

        assertEquals(1L, dto.getId());
        assertEquals("Bibliotheque Proche", dto.getNom());
        assertEquals(1, dto.getHoraires().size());
    }

    @Test
    void getBibliothequeById_shouldThrowWhenMissing() {
        when(libraryRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> libraryService.getBibliothequeById(999L));

        assertTrue(ex.getMessage().contains("non trouv"));
    }

    private LibraryEntity createLibrary(Long id, String nom, double lat, double lon, TypeBibliotheque type,
                                        double note, int nbNotes, LocalTime ouverture, LocalTime fermeture) {
        LibraryEntity lib = new LibraryEntity();
        lib.setId(id);
        lib.setNom(nom);
        lib.setAdresse("Adresse " + nom);
        lib.setLatitude(lat);
        lib.setLongitude(lon);
        lib.setType(type);
        lib.setNoteGlobale(note);
        lib.setNombreNotations(nbNotes);

        HoraireEntity horaire = new HoraireEntity();
        horaire.setId(id);
        horaire.setBibliotheque(lib);
        horaire.setJourSemaine("LUNDI");
        horaire.setHeureOuverture(ouverture);
        horaire.setHeureFermeture(fermeture);
        lib.getHoraires().add(horaire);
        return lib;
    }
}