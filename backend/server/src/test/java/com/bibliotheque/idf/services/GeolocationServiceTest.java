package com.bibliotheque.idf.services;

import devOps.services.GeolocationService;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class GeolocationServiceTest {

    private final GeolocationService geolocationService = new GeolocationService(new RestTemplate());
    @Test
    void geocodeAdresse_shouldReturnKnownCoordinates() {
        double[] coords = geolocationService.geocodeAdresse("Paris");

        assertEquals(48.859, coords[0], 0.0001);
        assertEquals(2.347, coords[1], 0.0001);
    }


    @Test
    void geocodeAdresse_shouldFallbackToParisForUnknownAddress() {
        double[] coords = geolocationService.geocodeAdresse("Adresse Inconnue");

        assertEquals(44.756637, coords[0], 0.0001);
        assertEquals(4.791447, coords[1], 0.0001);
    }
}