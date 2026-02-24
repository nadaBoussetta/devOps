package devOps.models;

import devOps.enums.TypeNotification;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UtilisateurEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeNotification type;

    @Column(nullable = false, length = 500)
    private String titre;

    @Column(nullable = false, length = 1000)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bibliotheque_id")
    private LibraryEntity bibliotheque;

    @Column(nullable = false)
    private Boolean lue = false;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime dateConsultation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }

    /**
     * Marque la notification comme lue.
     */
    public void marquerCommeLue() {
        this.lue = true;
        this.dateConsultation = LocalDateTime.now();
    }
}