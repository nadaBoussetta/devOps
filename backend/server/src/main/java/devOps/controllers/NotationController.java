package devOps.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devOps.dtos.FavoriDTO;
import devOps.dtos.NotationDTO;
import devOps.services.NotationService;
import devOps.util.SecurityUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notations")
@CrossOrigin(origins = "*")
public class NotationController {

    @Autowired
    private NotationService notationService;

    @PostMapping
    public ResponseEntity<NotationDTO> noter(@Valid @RequestBody NotationDTO dto) {
        return ResponseEntity.ok(notationService.noterBibliotheque(dto, uid()));
    }

    @GetMapping("/mes-avis")
    public ResponseEntity<List<NotationDTO>> getMesAvis() {
        return ResponseEntity.ok(notationService.getNotationsByUser(uid()));
    }

    @GetMapping("/bibliotheque/{bibliothequeId}")
    public ResponseEntity<List<NotationDTO>> getAvisBibliotheque(@PathVariable Long bibliothequeId) {
        return ResponseEntity.ok(notationService.getNotationsByBibliotheque(bibliothequeId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        notationService.supprimerNotation(id, uid());
        return ResponseEntity.noContent().build();
    }

    // ─── Favoris ──────────────────────────────────────────────────────────────

    @PostMapping("/favoris/{bibliothequeId}")
    public ResponseEntity<FavoriDTO> ajouterFavori(@PathVariable Long bibliothequeId) {
        return ResponseEntity.ok(notationService.ajouterFavori(bibliothequeId, uid()));
    }

    @DeleteMapping("/favoris/{bibliothequeId}")
    public ResponseEntity<Void> supprimerFavori(@PathVariable Long bibliothequeId) {
        notationService.supprimerFavori(bibliothequeId, uid());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favoris")
    public ResponseEntity<List<FavoriDTO>> getMesFavoris() {
        return ResponseEntity.ok(notationService.getFavorisByUser(uid()));
    }

    private Long uid() {
        Long id = SecurityUtil.getCurrentUserId();
        if (id == null) throw new RuntimeException("Non authentifié");
        return id;
    }
}
