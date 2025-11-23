package devOps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DisponibiliteEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    private String bibliotheque;

    private Boolean livreDispo;

    @ManyToOne
    @JoinColumn(name = "lieu_id")
    private LieuEntity lieu;

    @ManyToOne
    @JoinColumn(name = "livre_id")
    private LivreEntity livre;

    public LivreEntity getLivre() {
        return this.livre;
    }


}
