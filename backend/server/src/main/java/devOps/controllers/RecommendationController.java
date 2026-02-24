package devOps.controllers;


import devOps.dtos.LibraryResponseDTO;
import devOps.services.RecommendationService;
import devOps.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/recommandations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<LibraryResponseDTO>> getRecommendations(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<LibraryResponseDTO> recommandations = recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(recommandations);
    }

    private Long extractUserId(Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        return userId;
    }
}