package devOps.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "horaires")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoraireEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bibliotheque_id", nullable = false)
    @JsonIgnore
    private LibraryEntity bibliotheque;

    @Column(nullable = false)
    private String jourSemaine; // LUNDI, MARDI, etc.

    @Column(nullable = false)
    private LocalTime heureOuverture;

    @Column(nullable = false)
    private LocalTime heureFermeture;

    public boolean estOuvertSur(LocalTime debut, LocalTime fin) {
        return !heureOuverture.isAfter(debut) && !heureFermeture.isBefore(fin);
    }
}
