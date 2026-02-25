package devOps.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class DistanceCalculatorTest {

    private static final double DELTA = 0.01; // tolérance raisonnable pour km

    @ParameterizedTest
    @CsvSource({
            "48.8566, 2.3522, 48.8566, 2.3522, 0.0",           // même point
            "48.8566, 2.3522, 48.8606, 2.3376, 1.15",          // Paris centre → Louvre ≈ 1.15 km
            "48.8566, 2.3522, 45.7578, 4.8320, 392.0",         // Paris → Lyon ≈ 392 km
            "51.5074, -0.1278, 48.8566, 2.3522, 344.0"         // Londres → Paris ≈ 344 km
    })
    void calculateDistance_shouldReturnCorrectHaversineDistance(
            double lat1, double lon1, double lat2, double lon2, double expected) {

        double distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);

        assertThat(distance).isCloseTo(expected, within(DELTA));
    }

    @Test
    void calculateDistance_shouldBeSymmetric() {
        double d1 = DistanceCalculator.calculateDistance(40.7128, -74.0060, 34.0522, -118.2437);
        double d2 = DistanceCalculator.calculateDistance(34.0522, -118.2437, 40.7128, -74.0060);

        assertThat(d1).isEqualTo(d2);
    }

    @Test
    void calculateDistance_shouldHandleSmallDistances() {
        // ~1 km vers l'est
        double distance = DistanceCalculator.calculateDistance(48.8566, 2.3522, 48.8566, 2.3700);

        assertThat(distance).isBetween(1.4, 1.6); // approx 1.5 km
    }
}