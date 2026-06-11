package devOps.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devOps.dtos.FavoriDTO;
import devOps.dtos.NotationDTO;
import devOps.dtos.PublicationDTO;
import devOps.dtos.UserProfileDTO;
import devOps.models.UtilisateurEntity;
import devOps.repositories.UtilisateurRepository;
import devOps.services.FeedService;
import devOps.services.NotationService;
import devOps.util.SecurityUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UtilisateurRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotationService notationService;

    @Autowired
    private FeedService feedService;

    /**
     * GET /api/users/me
     * Retourne le profil complet de l'utilisateur connecté (avec stats, favoris, avis, posts).
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getProfile() {
        Long userId = getCurrentUserId();
        UtilisateurEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer les données liées
        List<FavoriDTO> favoris = notationService.getFavorisByUser(userId);
        List<NotationDTO> avis = notationService.getNotationsByUser(userId);
        List<PublicationDTO> posts = feedService.getAllPosts(userId).stream()
                .filter(p -> userId.equals(p.getAuteurId()))
                .toList();

        UserProfileDTO dto = new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getVille(),
                user.getDateNaissance()
        );
        dto.setNbPosts(posts.size());
        dto.setNbFavoris(favoris.size());
        dto.setNbAvis(avis.size());
        dto.setFavoris(favoris);
        dto.setAvis(avis);
        dto.setPosts(posts);

        return ResponseEntity.ok(dto);
    }

    /**
     * PUT /api/users/me
     * Met à jour username, email, bio, ville, dateNaissance.
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileDTO> updateProfile(@Valid @RequestBody UserProfileDTO dto) {
        Long userId = getCurrentUserId();
        UtilisateurEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier unicité du username si changé
        if (!user.getUsername().equals(dto.getUsername())
                && userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà utilisé");
        }

        // Vérifier unicité de l'email si changé
        if (!user.getEmail().equals(dto.getEmail())
                && userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setBio(dto.getBio());
        user.setVille(dto.getVille());
        user.setDateNaissance(dto.getDateNaissance());
        userRepository.save(user);

        return ResponseEntity.ok(new UserProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getVille(),
                user.getDateNaissance()
        ));
    }

    /**
     * PUT /api/users/me/password
     * Change le mot de passe.
     */
    @PutMapping("/me/password")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestBody Map<String, String> body) {
        Long userId = getCurrentUserId();
        UtilisateurEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String ancien = body.get("ancienMotDePasse");
        String nouveau = body.get("nouveauMotDePasse");

        if (ancien == null || nouveau == null) {
            throw new RuntimeException("Les champs ancienMotDePasse et nouveauMotDePasse sont requis");
        }

        if (!passwordEncoder.matches(ancien, user.getPassword())) {
            throw new RuntimeException("L'ancien mot de passe est incorrect");
        }

        if (nouveau.length() < 6) {
            throw new RuntimeException("Le nouveau mot de passe doit contenir au moins 6 caractères");
        }

        user.setPassword(passwordEncoder.encode(nouveau));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour avec succès"));
    }

    /**
     * DELETE /api/users/me
     * Supprime le compte de l'utilisateur connecté.
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount() {
        Long userId = getCurrentUserId();
        UtilisateurEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        return userId;
    }
}