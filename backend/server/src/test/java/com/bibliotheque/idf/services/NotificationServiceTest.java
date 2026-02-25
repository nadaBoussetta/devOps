package devOps.services;

import devOps.dtos.NotificationDTO;
import devOps.enums.TypeNotification;
import devOps.models.LibraryEntity;
import devOps.models.NotificationEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.LibraryRepository;
import devOps.repositories.NotificationRepository;
import devOps.repositories.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UtilisateurRepository userRepository;

    @Mock
    private LibraryRepository bibliothequeRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void creerNotification_shouldCreateAndMapDto() {
        UtilisateurEntity user = new UtilisateurEntity();
        user.setId(1L);

        LibraryEntity biblio = new LibraryEntity();
        biblio.setId(2L);
        biblio.setNom("Biblio A");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bibliothequeRepository.findById(2L)).thenReturn(Optional.of(biblio));
        when(notificationRepository.save(any(NotificationEntity.class))).thenAnswer(invocation -> {
            NotificationEntity n = invocation.getArgument(0);
            n.setId(50L);
            n.setDateCreation(LocalDateTime.now());
            return n;
        });

        NotificationDTO dto = notificationService.creerNotification(
                1L, TypeNotification.RECOMMANDATION, "Titre", "Message", 2L
        );

        assertEquals(50L, dto.getId());
        assertEquals(TypeNotification.RECOMMANDATION, dto.getType());
        assertEquals(2L, dto.getBibliothequeId());
        assertFalse(dto.getLue());
    }

    @Test
    void marquerCommeLue_shouldSetReadFlag() {
        NotificationEntity notification = new NotificationEntity();
        notification.setId(7L);
        notification.setLue(false);

        when(notificationRepository.findById(7L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);

        NotificationDTO dto = notificationService.marquerCommeLue(7L);

        assertTrue(dto.getLue());
        verify(notificationRepository).save(notification);
    }

    @Test
    void countNotificationsNonLues_shouldReturnRepositoryValue() {
        when(notificationRepository.countByUserIdAndLueIsFalse(1L)).thenReturn(3);

        Integer count = notificationService.countNotificationsNonLues(1L);

        assertEquals(3, count);
    }
}