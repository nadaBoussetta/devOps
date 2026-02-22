package devOps.controllers;

import devOps.dtos.*;
import devOps.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/livres")
@CrossOrigin(origins = "*")
public class LivreController {

    @Autowired
    private LivreService livreService;

    @GetMapping("/recherche")
    public ResponseEntity<List<LivreResponseDTO>> rechercherLivre(@RequestParam String titre) {
        List<LivreResponseDTO> resultats = livreService.rechercherLivre(titre);
        return ResponseEntity.ok(resultats);
    }

    @GetMapping("/recherche/{bibliotheque}")
    public ResponseEntity<List<LivreResponseDTO>> rechercherLivreDansBibliotheque(
            @PathVariable String bibliotheque,
            @RequestParam String titre) {
        List<LivreResponseDTO> resultats = livreService.rechercherLivreDansBibliotheque(titre, bibliotheque);
        return ResponseEntity.ok(resultats);
    }
}
