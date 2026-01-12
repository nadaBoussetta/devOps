package devOps.controllers;

import devOps.endpoints.LibraryEndpoint;
import devOps.dtos.LibraryResponseDTO;
import devOps.services.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/libraries")
public abstract class LibraryController implements LibraryEndpoint {

    private final LibraryService libraryService;

    @Override
    @GetMapping
    public List<LibraryResponseDTO> getAllLibraries() {
        return libraryService.getAllLibraries();
    }

    @GetMapping("/recommend")
    @Override
    public LibraryResponseDTO getBestLibrary(@RequestParam double latitude,
                                             @RequestParam double longitude) {
        return libraryService.getBestLibrary(latitude, longitude);
    }
}
