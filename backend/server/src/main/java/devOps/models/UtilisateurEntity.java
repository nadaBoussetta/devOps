package devOps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
public class UtilisateurEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    private String nom;

    private String mail;

    private String localisation;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<FavoriEntity> favoris;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<RechercheEntity> recherches;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<PublicationEntity> publicationsPosees;

    @OneToMany(mappedBy = "repondeur", cascade = CascadeType.ALL)
    private List<PublicationEntity> publicationsRepondues;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<AvisEntity> avis;
}
