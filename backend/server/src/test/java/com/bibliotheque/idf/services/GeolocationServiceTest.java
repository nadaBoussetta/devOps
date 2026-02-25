package devOps.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeolocationServiceTest {

    private final GeolocationService geolocationService = new GeolocationService();

    @Test
    void geocodeAdresse_shouldReturnKnownCoordinates() {
        double[] coords = geolocationService.geocodeAdresse("Paris");

        assertEquals(48.8566, coords[0], 0.0001);
        assertEquals(2.3522, coords[1], 0.0001);
    }

    @Test
    void geocodeAdresse_shouldNormalizeInput() {
        double[] coords = geolocationService.geocodeAdresse("  Tour Eiffel, Paris ");

        assertEquals(48.8584, coords[0], 0.0001);
        assertEquals(2.2945, coords[1], 0.0001);
    }

    @Test
    void geocodeAdresse_shouldFallbackToParisForUnknownAddress() {
        double[] coords = geolocationService.geocodeAdresse("Adresse Inconnue");

        assertEquals(48.8566, coords[0], 0.0001);
        assertEquals(2.3522, coords[1], 0.0001);
    }
}