package devOps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@Entity
public class FavoriEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    private Date dateAjout;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private UtilisateurEntity utilisateur;
}
