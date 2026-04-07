package devOps.controllers;

import devOps.dtos.ItineraireResponseDTO;
import devOps.dtos.LibraryResponseDTO;
import devOps.dtos.RechercheDTO;
import devOps.services.ItineraireOptimisationService;
import devOps.services.LibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LibraryControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private LibraryService libraryService;

    @MockBean
    private ItineraireOptimisationService itineraireOptimisationService;

    @Test
    public void testRechercheBibliotheques() {
        RechercheDTO recherche = new RechercheDTO();
        recherche.setAdresse("Paris");
        recherche.setRayon(5.0);
        recherche.setHeureDebut("10:00");
        recherche.setHeureFin("12:00");

        LibraryResponseDTO lib = new LibraryResponseDTO();
        lib.setNom("Biblio Test");
        lib.setOuvert(true);

        when(libraryService.rechercherBibliotheques(any(RechercheDTO.class)))
                .thenReturn(Arrays.asList(lib));

        ResponseEntity<LibraryResponseDTO[]> response = restTemplate.postForEntity(
                "/api/bibliotheques/recherche", recherche, LibraryResponseDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertEquals("Biblio Test", response.getBody()[0].getNom());
        assertTrue(response.getBody()[0].getOuvert());
    }

    @Test
    public void testItineraireBibliotheques() {
        RechercheDTO recherche = new RechercheDTO();
        recherche.setAdresse("Paris");
        recherche.setRayon(5.0);
        recherche.setHeureDebut("10:00");
        recherche.setHeureFin("17:00");

        ItineraireResponseDTO itineraire = new ItineraireResponseDTO();
        itineraire.setCreneauCompletementCouvert(true);
        itineraire.setDistanceTotale(2.5);

        when(itineraireOptimisationService.calculerItineraire(any(RechercheDTO.class)))
                .thenReturn(itineraire);

        ResponseEntity<ItineraireResponseDTO> response = restTemplate.postForEntity(
                "/api/bibliotheques/itineraire", recherche, ItineraireResponseDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getCreneauCompletementCouvert());
        assertEquals(2.5, response.getBody().getDistanceTotale());
    }
}
