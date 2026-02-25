package com.bibliotheque.idf.util;

import devOps.util.DistanceCalculator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class DistanceCalculatorTest {

    @Test
    void testCalculateDistance_SamePoint() {
        double distance = DistanceCalculator.calculateDistance(48.8566, 2.3522, 48.8566, 2.3522);
        assertEquals(0.0, distance, 0.01, "La distance entre deux points identiques doit être 0");
    }

    @Test
    void testCalculateDistance_ParisTourEiffel() {
        // Distance entre Notre-Dame et Tour Eiffel (environ 3.5 km)
        double lat1 = 48.8530; // Notre-Dame
        double lon1 = 2.3499;
        double lat2 = 48.8584; // Tour Eiffel
        double lon2 = 2.2945;

        double distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);
        
        assertTrue(distance > 3.0 && distance < 4.5, 
                "La distance entre Notre-Dame et Tour Eiffel devrait être environ 3.5 km, obtenu: " + distance);
    }

    @Test
    void testCalculateDistance_ParisLyon() {
        // Distance entre Paris et Lyon (environ 400 km)
        double lat1 = 48.8566; // Paris
        double lon1 = 2.3522;
        double lat2 = 45.7640; // Lyon
        double lon2 = 4.8357;

        double distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);
        
        assertTrue(distance > 350 && distance < 450, 
                "La distance entre Paris et Lyon devrait être environ 400 km, obtenu: " + distance);
    }

    @Test
    void testCalculateDistance_Symmetry() {
        double lat1 = 48.8566;
        double lon1 = 2.3522;
        double lat2 = 48.8584;
        double lon2 = 2.2945;

        double distance1 = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);
        double distance2 = DistanceCalculator.calculateDistance(lat2, lon2, lat1, lon1);
        
        assertEquals(distance1, distance2, 0.01, "La distance doit être symétrique");
    }
}
