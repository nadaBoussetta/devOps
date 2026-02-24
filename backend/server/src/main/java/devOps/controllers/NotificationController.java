package devOps.controllers;

import devOps.dtos.NotificationDTO;
import devOps.services.NotificationService;
import devOps.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<NotificationDTO> notifications = notificationService.getNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/non-lues")
    public ResponseEntity<List<NotificationDTO>> getNotificationsNonLues(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<NotificationDTO> notifications = notificationService.getNotificationsNonLuesByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/count-non-lues")
    public ResponseEntity<Map<String, Integer>> countNotificationsNonLues(Authentication authentication) {
        Long userId = extractUserId(authentication);
        Integer count = notificationService.countNotificationsNonLues(userId);
        Map<String, Integer> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{notificationId}/lire")
    public ResponseEntity<NotificationDTO> marquerCommeLue(
            @PathVariable Long notificationId,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        NotificationDTO notification = notificationService.marquerCommeLue(notificationId);
        return ResponseEntity.ok(notification);
    }

    @PutMapping("/lire-tout")
    public ResponseEntity<Void> marquerToutesCommeLues(Authentication authentication) {
        Long userId = extractUserId(authentication);
        notificationService.marquerToutesCommeLues(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> supprimerNotification(
            @PathVariable Long notificationId,
            Authentication authentication) {
        Long userId = extractUserId(authentication);
        notificationService.supprimerNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/lues")
    public ResponseEntity<Void> supprimerNotificationsLues(Authentication authentication) {
        Long userId = extractUserId(authentication);
        notificationService.supprimerNotificationsLues(userId);
        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(Authentication authentication) {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        return userId;
    }
}