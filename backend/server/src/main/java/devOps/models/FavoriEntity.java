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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateAjout;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")   // colonne propre
    private UtilisateurEntity utilisateur;

    @ManyToOne
    @JoinColumn(name = "livre_id")         // colonne propre
    private LivreEntity livre;
}
