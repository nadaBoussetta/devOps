package devOps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(
        name = "favori_bibliotheque",
        uniqueConstraints = @UniqueConstraint(columnNames = {"utilisateur_id", "library_id"})
)
public class FavoriBibliothequeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateAjout = new Date();

    @ManyToOne(optional = false)
    @JoinColumn(name = "utilisateur_id")
    private UtilisateurEntity utilisateur;

    @ManyToOne(optional = false)
    @JoinColumn(name = "library_id")
    private LibraryEntity library;
}
