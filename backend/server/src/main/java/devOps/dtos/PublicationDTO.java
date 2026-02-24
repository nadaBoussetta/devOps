package devOps.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationDTO {

    private Long id;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    private Long auteurId;
    private String auteurUsername;
    private Long bibliothequeId;
    private String bibliothequeNom;
    private LocalDateTime dateCreation;
    private List<CommentDTO> comments;
}