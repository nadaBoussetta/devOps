package devOps.services;

import devOps.dtos.HoraireDTO;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HoraireParserTest {

    private final HoraireParser parser = new HoraireParser();

    @Test
    public void testParseHorairesMultiples() {
        String input = "MARDI: 10h-12h \t 14h-17h";
        List<HoraireDTO> result = parser.parseHoraires(input);
        
        System.out.println("Result for MARDI multiples: " + result.size());
        for(HoraireDTO h : result) {
            System.out.println(h.getJourSemaine() + ": " + h.getHeureOuverture() + "-" + h.getHeureFermeture());
        }

        assertEquals(2, result.size());
        assertEquals("MARDI", result.get(0).getJourSemaine());
        assertEquals("10:00", result.get(0).getHeureOuverture());
        assertEquals("12:00", result.get(0).getHeureFermeture());
        
        assertEquals("MARDI", result.get(1).getJourSemaine());
        assertEquals("14:00", result.get(1).getHeureOuverture());
        assertEquals("17:00", result.get(1).getHeureFermeture());
    }

    @Test
    public void testParsePlageJoursEtHorairesMultiples() {
        String input = "du lundi au vendredi de 10h à 12h et 14h à 18h";
        List<HoraireDTO> result = parser.parseHoraires(input);
        
        System.out.println("Result for range multiples: " + result.size());
        for(HoraireDTO h : result) {
            System.out.println(h.getJourSemaine() + ": " + h.getHeureOuverture() + "-" + h.getHeureFermeture());
        }

        // 5 jours * 2 créneaux = 10
        assertEquals(10, result.size());
        
        // Vérifier pour LUNDI
        long lundiCount = result.stream().filter(h -> h.getJourSemaine().equals("LUNDI")).count();
        assertEquals(2, lundiCount);
    }

    @Test
    public void testIsOpenDuringWithMultipleSlots() {
        String input = "MARDI: 10h-12h \t 14h-17h";
        List<HoraireDTO> horaires = parser.parseHoraires(input);
        
        // Test dans le premier créneau
        assertTrue(parser.isOpenDuring(horaires, LocalTime.of(10, 30), LocalTime.of(11, 30), "MARDI"));
        
        // Test dans le second créneau
        assertTrue(parser.isOpenDuring(horaires, LocalTime.of(15, 0), LocalTime.of(16, 0), "MARDI"));
        
        // Test entre les deux créneaux (fermé)
        assertFalse(parser.isOpenDuring(horaires, LocalTime.of(12, 30), LocalTime.of(13, 30), "MARDI"));
        
        // Test chevauchement (fermé car pas entièrement couvert par UN créneau)
        assertFalse(parser.isOpenDuring(horaires, LocalTime.of(11, 0), LocalTime.of(15, 0), "MARDI"));
    }
}
