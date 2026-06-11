package devOps.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // ✅ Réactions : { "JAIME": 3, "HAHA": 1, ... }
    private Map<String, Long> reactions;

    // La réaction de l'utilisateur courant (null si aucune)
    private String maReaction;

    // ✅ Repost
    private Boolean estRepost;
    private Long postOriginalId;
    private String postOriginalAuteur;
    private String postOriginalContenu;
    private LocalDateTime postOriginalDate;
    private Long nbReposts;
}