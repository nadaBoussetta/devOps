package devOps.controllers;

import devOps.endpoints.LibraryEndpoint;
import devOps.responses.LibraryResponseDTO;
import devOps.services.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/libraries")
public class LibraryController implements LibraryEndpoint {

    private final LibraryService libraryService;

    @Override
    @GetMapping
    public List<LibraryResponseDTO> getAllLibraries() {
        return libraryService.getAllLibraries();
    }

    @Override
    @GetMapping("/recommend")
    public LibraryResponseDTO getBestLibrary(@RequestParam double latitude,
                                             @RequestParam double longitude) {
        return libraryService.getBestLibrary(latitude, longitude);
    }

    @Override
    @GetMapping("/{id}")
    public LibraryResponseDTO getLibraryById(@PathVariable String id) {
        return libraryService.getLibrary(id);
    }

    @GetMapping("/nearby")
    public List<LibraryResponseDTO> getLibrariesNearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "20") double radiusKm) {
        return libraryService.findLibrariesNearDTO(lat, lon, radiusKm);
    }

    // Récupérer les noms des bibliothèques proches
    @GetMapping("/nearby-names")
    public List<String> getLibraryNamesNearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "20") double radiusKm) {
        return libraryService.findLibraryNamesNear(lat, lon, radiusKm);
    }
}
