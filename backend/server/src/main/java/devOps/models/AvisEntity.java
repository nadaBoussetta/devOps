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
    @JoinColumn(name = "id")
    private UtilisateurEntity utilisateur;

    @ManyToOne
    @JoinColumn(name = "id")
    private LieuEntity lieu;
}
