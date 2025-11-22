package devOps.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class LivreEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String id;

    private String titre;

    private String auteur;

    private String isbn;
}
