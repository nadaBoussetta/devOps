package devOps.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import devOps.dtos.DisponibiliteRequestDTO;
import devOps.dtos.DisponibiliteResultatDTO;
import devOps.dtos.LivreResponseDTO;
import devOps.services.LivreService;

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

    /**
     * POST /api/livres/disponibilite
     *
     * Reçoit un titre et une liste de bibliothèques IDF (issues de /api/bibliotheques/recherche).
     * Retourne pour chaque bibliothèque si le livre y est disponible.
     *
     * Body JSON :
     * {
     *   "titre": "Les Misérables",
     *   "bibliotheques": [
     *     { "nom": "Bibliothèque Sorbonne", "adresse": "...", "distance": 1.2 }
     *   ]
     * }
     */
    @PostMapping("/disponibilite")
    public ResponseEntity<List<DisponibiliteResultatDTO>> verifierDisponibilite(
            @RequestBody DisponibiliteRequestDTO request) {
        List<DisponibiliteResultatDTO> resultats =
                livreService.verifierDisponibiliteParBibliotheques(
                        request.getTitre(),
                        request.getBibliotheques()
                );
        return ResponseEntity.ok(resultats);
    }
}