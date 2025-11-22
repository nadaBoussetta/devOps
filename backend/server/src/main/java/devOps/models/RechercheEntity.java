package devOps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class RechercheEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    private String depart;

    private String coordonnees;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private UtilisateurEntity utilisateur;
}
