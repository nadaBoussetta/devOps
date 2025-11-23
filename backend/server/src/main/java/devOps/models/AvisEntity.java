package devOps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AvisEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    private Integer note;

    private String description;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id") // colonne différente
    private UtilisateurEntity utilisateur;

    @ManyToOne
    @JoinColumn(name = "lieu_id") // colonne différente
    private LieuEntity lieu;
}
