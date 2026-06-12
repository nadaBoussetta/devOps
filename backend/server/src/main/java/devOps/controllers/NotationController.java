package devOps.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devOps.dtos.NotificationDTO;
import devOps.services.NotificationService;
import devOps.util.SecurityUtil;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(uid()));
    }

    @GetMapping("/non-lues")
    public ResponseEntity<List<NotificationDTO>> getNonLues() {
        return ResponseEntity.ok(notificationService.getNotificationsNonLuesByUser(uid()));
    }

    @GetMapping("/count-non-lues")
    public ResponseEntity<Map<String, Integer>> countNonLues() {
        Map<String, Integer> r = new HashMap<>();
        r.put("count", notificationService.countNotificationsNonLues(uid()));
        return ResponseEntity.ok(r);
    }

    @PutMapping("/{id}/lire")
    public ResponseEntity<NotificationDTO> marquerLue(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.marquerCommeLue(id));
    }

    @PutMapping("/lire-tout")
    public ResponseEntity<Void> marquerToutesLues() {
        notificationService.marquerToutesCommeLues(uid());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        notificationService.supprimerNotification(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lues")
    public ResponseEntity<Void> supprimerLues() {
        notificationService.supprimerNotificationsLues(uid());
        return ResponseEntity.noContent().build();
    }

    // ✅ Endpoints de déclenchement manuel (utile pour tests / démo)
    @PostMapping("/generer/rappel-lecture")
    public ResponseEntity<Void> genererRappelLecture(@RequestBody Map<String, String> body) {
        notificationService.genererRappelLecture(uid(), body.get("titreLivre"));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generer/nouvelle-publication")
    public ResponseEntity<Void> genererNouvellePublication(@RequestBody Map<String, String> body) {
        notificationService.genererNotificationNouvellePublication(uid(),
                body.get("auteurUsername"), body.get("extrait"));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generer/suggestion-livre")
    public ResponseEntity<Void> genererSuggestionLivre(@RequestBody Map<String, String> body) {
        notificationService.genererSuggestionLivre(uid(),
                body.get("titreLivre"), body.get("auteur"), body.get("raison"));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generer/rappel-session")
    public ResponseEntity<Void> genererRappelSession(@RequestBody Map<String, String> body) {
        notificationService.genererRappelSession(uid(), body.get("objectif"));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generer/objectif-atteint")
    public ResponseEntity<Void> genererObjectifAtteint(@RequestBody Map<String, Object> body) {
        notificationService.genererNotificationObjectifAtteint(uid(),
                (String) body.get("objectif"),
                ((Number) body.get("minutes")).intValue());
        return ResponseEntity.noContent().build();
    }

    private Long uid() {
        Long id = SecurityUtil.getCurrentUserId();
        if (id == null) throw new RuntimeException("Non authentifié");
        return id;
    }
}