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
    @JoinColumn(name = "id")
    private LieuEntity lieu;

    @OneToOne
    @JoinColumn(name = "id")
    private LivreEntity livre;
}
