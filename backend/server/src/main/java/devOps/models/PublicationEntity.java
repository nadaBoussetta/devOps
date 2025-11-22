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
    @Column(unique = true, nullable = false)
    private String id;

    private String message;

    private Date date;

    @ManyToOne
    @JoinColumn(name = "id")
    private UtilisateurEntity utilisateur;

    @ManyToOne
    @JoinColumn(name = "id")
    private UtilisateurEntity repondeur;
}
