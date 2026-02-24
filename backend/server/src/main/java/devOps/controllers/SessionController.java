package devOps.controllers;

import devOps.dtos.SessionDTO;
import devOps.services.SessionService;
import devOps.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostMapping
    public ResponseEntity<SessionDTO> creerSession(
            @Valid @RequestBody SessionDTO sessionDTO,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        SessionDTO createdSession = sessionService.creerSession(sessionDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSession);
    }

    @GetMapping("/en-cours")
    public ResponseEntity<SessionDTO> getSessionEnCours(Authentication authentication) {
        Long userId = extractUserId(authentication);
        SessionDTO session = sessionService.getSessionEnCours(userId);
        return ResponseEntity.ok(session);
    }

    @GetMapping
    public ResponseEntity<List<SessionDTO>> getSessionsByUser(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<SessionDTO> sessions = sessionService.getSessionsByUser(userId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/completees")
    public ResponseEntity<List<SessionDTO>> getSessionsCompleteesbyUser(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<SessionDTO> sessions = sessionService.getSessionsCompleteesByUser(userId);
        return ResponseEntity.ok(sessions);
    }

    @PutMapping("/{sessionId}/temps")
    public ResponseEntity<SessionDTO> updateTempsEcoule(
            @PathVariable Long sessionId,
            @RequestParam Integer tempsEcoulesMinutes,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        SessionDTO updatedSession = sessionService.updateTempsEcoule(sessionId, tempsEcoulesMinutes);
        return ResponseEntity.ok(updatedSession);
    }

    @PostMapping("/{sessionId}/completer")
    public ResponseEntity<SessionDTO> completerSession(
            @PathVariable Long sessionId,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        SessionDTO completedSession = sessionService.completerSession(sessionId);
        return ResponseEntity.ok(completedSession);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> supprimerSession(
            @PathVariable Long sessionId,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        sessionService.supprimerSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistiques/hebdo")
    public ResponseEntity<SessionService.SessionStatistiquesDTO> getStatistiquesHebdomadaires(
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        SessionService.SessionStatistiquesDTO stats = sessionService.getStatistiquesHebdomadaires(userId);
        return ResponseEntity.ok(stats);
    }

    private Long extractUserId(Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        return userId;
    }
}
