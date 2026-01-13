package devOps.controllers;

import devOps.responses.LibraryResponseDTO;
import devOps.services.LibraryService;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/libraries")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/search")
    public List<LibraryResponseDTO> searchLibraries(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double radiusKm,
            @RequestParam String jour,        // ex: "Mardi"
            @RequestParam String heureDebut,  // ex: "09:00"
            @RequestParam String heureFin) {  // ex: "18:00"

        // Conversion jour français -> DayOfWeek
        DayOfWeek jourEnum = switch (jour.toLowerCase()) {
            case "lundi" -> DayOfWeek.MONDAY;
            case "mardi" -> DayOfWeek.TUESDAY;
            case "mercredi" -> DayOfWeek.WEDNESDAY;
            case "jeudi" -> DayOfWeek.THURSDAY;
            case "vendredi" -> DayOfWeek.FRIDAY;
            case "samedi" -> DayOfWeek.SATURDAY;
            case "dimanche" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Jour invalide : " + jour);
        };

        LocalTime debut = LocalTime.parse(heureDebut);
        LocalTime fin = LocalTime.parse(heureFin);

        return libraryService.searchLibraries(lat, lon, radiusKm, jourEnum, debut, fin);
    }
}
