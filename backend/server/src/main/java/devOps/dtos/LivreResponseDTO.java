package devOps.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivreResponseDTO {

    private String titre;
    private String auteur;
    private String bibliotheque;
    private Boolean disponible;
    private String cote;
    private String isbn;
}
