package devOps.dtos;

import devOps.enums.TypeBibliotheque;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibraryResponseDTO {

    private Double searchLatitude;
    private Double searchLongitude;

    private Long id;
    private String nom;
    private String adresse;
    private Double latitude;
    private Double longitude;
    private TypeBibliotheque type;
    private Double noteGlobale;
    private Integer nombreNotations;
    private Double distance; // Distance calculée lors de la recherche
    private Boolean ouvert; // Statut d'ouverture pour le créneau recherché
    private List<HoraireDTO> horaires;
}