package devOps.models;
import devOps.enums.TypeBibliotheque;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bibliotheques")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibraryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeBibliotheque type;

    @Column
    private Double noteGlobale = 0.0;

    @Column
    private Integer nombreNotations = 0;

    @OneToMany(mappedBy = "bibliotheque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HoraireEntity> horaires = new ArrayList<>();

    @OneToMany(mappedBy = "libraryEntity", cascade = CascadeType.ALL)
    private List<PublicationEntity> posts = new ArrayList<>();

    @OneToMany(mappedBy = "bibliotheque", cascade = CascadeType.ALL)
    private List<NotationEntity> notations = new ArrayList<>();

    @OneToMany(mappedBy = "libraryEntity", cascade = CascadeType.ALL)
    private List<FavoriEntity> favoris = new ArrayList<>();

    public void addHoraire(HoraireEntity horaire) {
        horaires.add(horaire);
        horaire.setBibliotheque(this);
    }

    public void updateNoteGlobale(Integer nouvelleNote) {
        double total = noteGlobale * nombreNotations + nouvelleNote;
        nombreNotations++;
        noteGlobale = total / nombreNotations;
    }
}