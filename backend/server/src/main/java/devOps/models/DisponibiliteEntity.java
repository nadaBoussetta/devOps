package devOps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DisponibiliteEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    private String bibliotheque;

    private Boolean livreDispo;

    @ManyToOne
    @JoinColumn(name = "lieu_id") // <-- colonne différente
    private LieuEntity lieu;

    @OneToOne
    @JoinColumn(name = "livre_id") // <-- colonne différente
    private LivreEntity livre;
}
