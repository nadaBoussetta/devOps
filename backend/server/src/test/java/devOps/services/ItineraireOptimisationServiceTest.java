package devOps.services;

import devOps.dtos.ItineraireResponseDTO;
import devOps.dtos.RechercheDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

@SpringBootTest
public class ItineraireOptimisationServiceTest {

    @Autowired
    private ItineraireOptimisationService itineraireService;

    @MockBean
    private GeolocationService geolocationService;

    @Test
    void testCalculerItineraireAdresseIntrouvable() {
        when(geolocationService.geocodeAdresse(anyString())).thenReturn(null);

        RechercheDTO recherche = new RechercheDTO();
        recherche.setAdresse("Adresse Inexistante");
        recherche.setHeureDebut("10:00");
        recherche.setHeureFin("18:00");
        recherche.setRayon(5.0);

        ItineraireResponseDTO response = itineraireService.calculerItineraire(recherche);

        assertNotNull(response);
        assertTrue(response.getEtapes().isEmpty());
        assertEquals("Adresse introuvable. Veuillez vérifier l'adresse saisie.", response.getMessage());
    }
}
