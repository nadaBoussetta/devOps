package devOps.models;

import devOps.enums.TypeLieu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
public class LieuEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    private String nom;

    @Enumerated(EnumType.STRING)
    private TypeLieu type;

    private String adresse;

    private String coordonnees;

    private Integer nivCalme;

    private Boolean wifi;

    @OneToMany(mappedBy = "lieu", cascade = CascadeType.ALL)
    private List<DisponibiliteEntity> disponibilites;

    @OneToMany(mappedBy = "lieu", cascade = CascadeType.ALL)
    private List<AvisEntity> avis;
}
