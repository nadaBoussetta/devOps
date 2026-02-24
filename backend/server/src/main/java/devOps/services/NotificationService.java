package devOps.services;

import devOps.dtos.*;
import devOps.enums.TypeNotification;
import devOps.models.*;
import devOps.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UtilisateurRepository userRepository;

    @Autowired
    private LibraryRepository bibliothequeRepository;

    @Transactional
    public NotificationDTO creerNotification(Long userId, TypeNotification type, String titre,
                                             String message, Long bibliothequeId) {
        UtilisateurEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        NotificationEntity notification = new NotificationEntity();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitre(titre);
        notification.setMessage(message);
        notification.setLue(false);

        if (bibliothequeId != null) {
            LibraryEntity bibliotheque = bibliothequeRepository.findById(bibliothequeId)
                    .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));
            notification.setBibliotheque(bibliotheque);
        }

        NotificationEntity savedNotification = notificationRepository.save(notification);
        return convertNotificationToDTO(savedNotification);
    }

    public List<NotificationDTO> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByDateCreationDesc(userId).stream()
                .map(this::convertNotificationToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getNotificationsNonLuesByUser(Long userId) {
        return notificationRepository.findByUserIdAndLueIsFalseOrderByDateCreationDesc(userId).stream()
                .map(this::convertNotificationToDTO)
                .collect(Collectors.toList());
    }

    public Integer countNotificationsNonLues(Long userId) {
        return notificationRepository.countByUserIdAndLueIsFalse(userId);
    }

    @Transactional
    public NotificationDTO marquerCommeLue(Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));

        notification.marquerCommeLue();
        NotificationEntity updatedNotification = notificationRepository.save(notification);
        return convertNotificationToDTO(updatedNotification);
    }

    @Transactional
    public void marquerToutesCommeLues(Long userId) {
        List<NotificationEntity> notifications = notificationRepository
                .findByUserIdAndLueIsFalseOrderByDateCreationDesc(userId);

        notifications.forEach(NotificationEntity::marquerCommeLue);
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void supprimerNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void supprimerNotificationsLues(Long userId) {
        notificationRepository.deleteByUserIdAndLueIsTrue(userId);
    }

    @Transactional
    public void genererNotificationFermeture(Long userId, Long bibliothequeId) {
        LibraryEntity bibliotheque = bibliothequeRepository.findById(bibliothequeId)
                .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));

        String titre = "Fermeture imminente";
        String message = "La bibliothèque " + bibliotheque.getNom() + " ferme dans 30 minutes.";

        creerNotification(userId, TypeNotification.FERMETURE_BIBLIOTHEQUE, titre, message, bibliothequeId);
    }

    @Transactional
    public void genererNotificationAfflluenceFaible(Long userId, Long bibliothequeId) {
        LibraryEntity bibliotheque = bibliothequeRepository.findById(bibliothequeId)
                .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));

        String titre = "Affluence faible";
        String message = "La bibliothèque " + bibliotheque.getNom() + " a une affluence plus faible que d'habitude. C'est le moment idéal pour réviser !";

        creerNotification(userId, TypeNotification.AFFLUENCE_FAIBLE, titre, message, bibliothequeId);
    }

    @Transactional
    public void genererNotificationLivreDisponible(Long userId, String titreLivre, Long bibliothequeId) {
        LibraryEntity bibliotheque = bibliothequeRepository.findById(bibliothequeId)
                .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));

        String titre = "Livre disponible";
        String message = "Le livre \"" + titreLivre + "\" est maintenant disponible à la bibliothèque " + bibliotheque.getNom() + ".";

        creerNotification(userId, TypeNotification.LIVRE_DISPONIBLE, titre, message, bibliothequeId);
    }

    @Transactional
    public void genererNotificationRecommandation(Long userId, Long bibliothequeId) {
        LibraryEntity bibliotheque = bibliothequeRepository.findById(bibliothequeId)
                .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));

        String titre = "Recommandation personnalisée";
        String message = "Nous vous recommandons la bibliothèque " + bibliotheque.getNom() + " basée sur vos préférences.";

        creerNotification(userId, TypeNotification.RECOMMANDATION, titre, message, bibliothequeId);
    }

    private NotificationDTO convertNotificationToDTO(NotificationEntity notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setTitre(notification.getTitre());
        dto.setMessage(notification.getMessage());
        dto.setLue(notification.getLue());
        dto.setDateCreation(notification.getDateCreation());
        dto.setDateConsultation(notification.getDateConsultation());

        if (notification.getBibliotheque() != null) {
            dto.setBibliothequeId(notification.getBibliotheque().getId());
            dto.setBibliothequeNom(notification.getBibliotheque().getNom());
        }

        return dto;
    }
}