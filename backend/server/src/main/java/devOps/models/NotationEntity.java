package devOps.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "notations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "bibliotheque_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UtilisateurEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bibliotheque_id", nullable = false)
    private LibraryEntity bibliotheque;

    @Column(nullable = false)
    private Integer note; // 1 à 5

    @Column(length = 500)
    private String commentaire;

    @Column(nullable = false)
    private LocalDate dateVisite;

    @Column(nullable = false)
    private LocalDate dateNotation;

    @PrePersist
    protected void onCreate() {
        dateNotation = LocalDate.now();
    }
}
