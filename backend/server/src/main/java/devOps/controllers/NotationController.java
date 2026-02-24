package devOps.controllers;

import devOps.dtos.FavoriDTO;
import devOps.dtos.NotationDTO;
import devOps.services.NotationService;
import devOps.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notations")
@CrossOrigin(origins = "*")
public class NotationController {

    @Autowired
    private NotationService notationService;

    @PostMapping
    public ResponseEntity<NotationDTO> noterBibliotheque(
            @Valid @RequestBody NotationDTO notationDTO,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        NotationDTO notation = notationService.noterBibliotheque(notationDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(notation);
    }

    @GetMapping("/mes-notations")
    public ResponseEntity<List<NotationDTO>> getMesNotations(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<NotationDTO> notations = notationService.getNotationsByUser(userId);
        return ResponseEntity.ok(notations);
    }

    @GetMapping("/bibliotheque/{bibliothequeId}")
    public ResponseEntity<List<NotationDTO>> getNotationsByBibliotheque(@PathVariable Long bibliothequeId) {
        List<NotationDTO> notations = notationService.getNotationsByBibliotheque(bibliothequeId);
        return ResponseEntity.ok(notations);
    }

    @PostMapping("/favoris/{bibliothequeId}")
    public ResponseEntity<FavoriDTO> ajouterFavori(
            @PathVariable Long bibliothequeId,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        FavoriDTO favori = notationService.ajouterFavori(bibliothequeId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(favori);
    }

    @DeleteMapping("/favoris/{bibliothequeId}")
    public ResponseEntity<Void> supprimerFavori(
            @PathVariable Long bibliothequeId,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        notationService.supprimerFavori(bibliothequeId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mes-favoris")
    public ResponseEntity<List<FavoriDTO>> getMesFavoris(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<FavoriDTO> favoris = notationService.getFavorisByUser(userId);
        return ResponseEntity.ok(favoris);
    }

    private Long extractUserId(Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        return userId;
    }
}