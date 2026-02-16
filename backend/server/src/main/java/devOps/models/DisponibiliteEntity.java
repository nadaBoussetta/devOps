package devOps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DisponibiliteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bibliotheque;

    private Boolean livreDispo;

    @ManyToOne
    @JoinColumn(name = "lieu_id")   // ← colonne différente
    private LieuEntity lieu;

    @OneToOne
    @JoinColumn(name = "livre_id")  // ← colonne différente
    private LivreEntity livre;

    public LivreEntity getLivre() {
        return this.livre;
    }


}
