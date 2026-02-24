package devOps.controllers;
import devOps.dtos.LibraryResponseDTO;
import devOps.dtos.RechercheDTO;
import devOps.services.LibraryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bibliotheques")
@CrossOrigin(origins = "*")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @PostMapping("/recherche")
    public ResponseEntity<List<LibraryResponseDTO>> rechercherBibliotheques(
            @Valid @RequestBody RechercheDTO rechercheDTO) {
        List<LibraryResponseDTO> resultats =
                libraryService.rechercherBibliotheques(rechercheDTO);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping
    public ResponseEntity<List<LibraryResponseDTO>> getAllBibliotheques() {
        List<LibraryResponseDTO> bibliotheques =
                libraryService.getAllBibliotheques();
        return ResponseEntity.ok(bibliotheques);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibraryResponseDTO> getBibliothequeById(@PathVariable Long id) {
        LibraryResponseDTO bibliotheque =
                libraryService.getBibliothequeById(id);
        return ResponseEntity.ok(bibliotheque);
    }
}