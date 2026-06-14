package devOps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "cached_libraries")
@Getter
@Setter
public class CachedLibraryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomEtablissement;
    private String nomRue;
    private String codePostal;
    private String commune;
    private String typeInst;

    @Column(columnDefinition = "TEXT")
    private String heuresOuverture;

    private Double latitude;
    private Double longitude;

    private LocalDateTime lastUpdated;
}
