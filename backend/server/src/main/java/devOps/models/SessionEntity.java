package devOps.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UtilisateurEntity user;

    @Column(nullable = false)
    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    @Column(nullable = false)
    private String objectif;

    @Column(nullable = false)
    private Integer dureeMinutes;

    @Column(nullable = false)
    private Integer tempsEcoulesMinutes = 0;

    @Column(nullable = false)
    private Boolean completee = false;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateDebut = LocalDateTime.now();
    }

    public void completer() {
        this.completee = true;
        this.dateFin = LocalDateTime.now();
    }

    public Integer getTempsRestantMinutes() {
        return dureeMinutes - tempsEcoulesMinutes;
    }

    public Double getProgressionPourcentage() {
        if (dureeMinutes == 0) {
            return 0.0;
        }
        return (tempsEcoulesMinutes * 100.0) / dureeMinutes;
    }
}