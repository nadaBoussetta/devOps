package devOps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@Entity
public class PublicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private UtilisateurEntity utilisateur;

    @ManyToOne
    @JoinColumn(name = "repondeur_id")
    private UtilisateurEntity repondeur;
}
