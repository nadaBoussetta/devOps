package devOps.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import devOps.dtos.NotificationDTO;
import devOps.enums.TypeNotification;
import devOps.models.LibraryEntity;
import devOps.models.NotificationEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.LibraryRepository;
import devOps.repositories.NotificationRepository;
import devOps.repositories.UtilisateurRepository;

@Service
public class NotificationService {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UtilisateurRepository  userRepository;
    @Autowired private LibraryRepository      bibliothequeRepository;

    // ─── CRUD de base ─────────────────────────────────────────────────────────

    @Transactional
    public NotificationDTO creerNotification(Long userId, TypeNotification type,
                                             String titre, String message, Long bibliothequeId) {
        UtilisateurEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        NotificationEntity n = new NotificationEntity();
        n.setUser(user);
        n.setType(type);
        n.setTitre(titre);
        n.setMessage(message);
        n.setLue(false);

        if (bibliothequeId != null) {
            n.setBibliotheque(bibliothequeRepository.findById(bibliothequeId)
                    .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée")));
        }

        return convertToDTO(notificationRepository.save(n));
    }

    public List<NotificationDTO> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByDateCreationDesc(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<NotificationDTO> getNotificationsNonLuesByUser(Long userId) {
        return notificationRepository.findByUserIdAndLueIsFalseOrderByDateCreationDesc(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public Integer countNotificationsNonLues(Long userId) {
        return notificationRepository.countByUserIdAndLueIsFalse(userId);
    }

    @Transactional
    public NotificationDTO marquerCommeLue(Long notificationId) {
        NotificationEntity n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        n.marquerCommeLue();
        return convertToDTO(notificationRepository.save(n));
    }

    @Transactional
    public void marquerToutesCommeLues(Long userId) {
        List<NotificationEntity> notifs = notificationRepository
                .findByUserIdAndLueIsFalseOrderByDateCreationDesc(userId);
        notifs.forEach(NotificationEntity::marquerCommeLue);
        notificationRepository.saveAll(notifs);
    }

    @Transactional
    public void supprimerNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void supprimerNotificationsLues(Long userId) {
        notificationRepository.deleteByUserIdAndLueIsTrue(userId);
    }

    // ─── Générateurs de notifications enrichis ────────────────────────────────

    @Transactional
    public void genererNotificationFermeture(Long userId, Long bibliothequeId) {
        LibraryEntity b = getBib(bibliothequeId);
        creerNotification(userId, TypeNotification.FERMETURE_BIBLIOTHEQUE,
                "Fermeture imminente — " + b.getNom(),
                "La bibliothèque " + b.getNom() + " ferme dans 30 minutes. Pensez à sauvegarder vos affaires !",
                bibliothequeId);
    }

    @Transactional
    public void genererNotificationAffluenceFaible(Long userId, Long bibliothequeId) {
        LibraryEntity b = getBib(bibliothequeId);
        creerNotification(userId, TypeNotification.AFFLUENCE_FAIBLE,
                "Moment idéal pour réviser 📚",
                "La bibliothèque " + b.getNom() + " est calme en ce moment. C'est le moment parfait pour une session de révision !",
                bibliothequeId);
    }

    @Transactional
    public void genererNotificationLivreDisponible(Long userId, String titreLivre, Long bibliothequeId) {
        LibraryEntity b = getBib(bibliothequeId);
        creerNotification(userId, TypeNotification.LIVRE_DISPONIBLE,
                "\"" + titreLivre + "\" est disponible !",
                "Bonne nouvelle ! Le livre \"" + titreLivre + "\" est maintenant disponible à la bibliothèque " + b.getNom() + ". Dépêchez-vous avant qu'il ne soit emprunté.",
                bibliothequeId);
    }

    @Transactional
    public void genererNotificationRecommandation(Long userId, Long bibliothequeId) {
        LibraryEntity b = getBib(bibliothequeId);
        creerNotification(userId, TypeNotification.RECOMMANDATION,
                "Bibliothèque recommandée pour vous ⭐",
                "Basé sur vos habitudes, nous pensons que vous adoreriez " + b.getNom() + ". Elle correspond parfaitement à vos centres d'intérêt !",
                bibliothequeId);
    }

    // ✅ Nouveaux générateurs

    @Transactional
    public void genererRappelLecture(Long userId, String titreLivre) {
        String[] messages = {
            "Vous n'avez pas lu \"" + titreLivre + "\" depuis quelques jours. Reprenez votre lecture, vous étiez sur une bonne lancée !",
            "Rappel doux : \"" + titreLivre + "\" vous attend. Même 15 minutes de lecture font la différence !",
            "\"" + titreLivre + "\" s'impatiente 📖 Accordez-lui un peu de temps aujourd'hui ?"
        };
        int idx = (int)(Math.random() * messages.length);
        creerNotification(userId, TypeNotification.RAPPEL_LECTURE,
                "Continuez \"" + titreLivre + "\" 📖",
                messages[idx], null);
    }

    @Transactional
    public void genererNotificationNouvellePublication(Long userId, String auteurUsername, String extrait) {
        creerNotification(userId, TypeNotification.NOUVELLE_PUBLICATION,
                auteurUsername + " a publié quelque chose 💬",
                "\"" + extrait + "\" — Réagissez ou commentez dans le Feed Social !",
                null);
    }

    @Transactional
    public void genererSuggestionLivre(Long userId, String titreLivre, String auteur, String raison) {
        creerNotification(userId, TypeNotification.RECHERCHE_LIVRE,
                "Vous aimerez peut-être : " + titreLivre,
                "Notre IA recommande \"" + titreLivre + "\" de " + auteur + ". " + raison,
                null);
    }

    @Transactional
    public void genererRappelSession(Long userId, String objectif) {
        creerNotification(userId, TypeNotification.SESSION_REMINDER,
                "Il est temps de réviser ! ⏱️",
                "Vous avez planifié une session sur \"" + objectif + "\". Lancez le minuteur et restez concentré — vous êtes capable !",
                null);
    }

    @Transactional
    public void genererNotificationObjectifAtteint(Long userId, String objectif, int minutes) {
        creerNotification(userId, TypeNotification.OBJECTIF_ATTEINT,
                "Félicitations ! Objectif atteint 🎉",
                "Vous avez complété \"" + objectif + "\" en " + minutes + " minutes. Excellent travail — chaque session vous rapproche de votre but !",
                null);
    }

    // ─── Utilitaires ──────────────────────────────────────────────────────────

    private LibraryEntity getBib(Long id) {
        return bibliothequeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));
    }

    private NotificationDTO convertToDTO(NotificationEntity n) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setTitre(n.getTitre());
        dto.setMessage(n.getMessage());
        dto.setLue(n.getLue());
        dto.setDateCreation(n.getDateCreation());
        dto.setDateConsultation(n.getDateConsultation());
        if (n.getBibliotheque() != null) {
            dto.setBibliothequeId(n.getBibliotheque().getId());
            dto.setBibliothequeNom(n.getBibliotheque().getNom());
        }
        return dto;
    }
}