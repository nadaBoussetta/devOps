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

    // Récupérer toutes les bibliothèques
    @Override
    @GetMapping
    public List<LibraryResponseDTO> getAllLibraries() {
        return libraryService.getAllLibraries();
    }

    // Récupérer la meilleure bibliothèque pour l'utilisateur
    @Override
    @GetMapping("/recommend")
    public LibraryResponseDTO getBestLibrary(@RequestParam double latitude,
                                             @RequestParam double longitude) {
        return libraryService.getBestLibrary(latitude, longitude);
    }

    // Récupérer une bibliothèque par son ID
    @Override
    @GetMapping("/{id}")
    public LibraryResponseDTO getLibraryById(@PathVariable String id) {
        return libraryService.getLibrary(id);
    }

    // Créer une nouvelle bibliothèque
    @Override
    @PostMapping
    public LibraryResponseDTO createLibrary(@RequestBody LibraryResponseDTO libraryResponseDTO) {
        return libraryService.createLibrary(libraryResponseDTO);
    }

    // Récupérer les bibliothèques proches de l'utilisateur
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
