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
public class CommentDTO {

    private Long id;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    private Long auteurId;
    private String auteurUsername;
    private Long postId;
    private Long parentCommentId;
    private LocalDateTime dateCreation;
    private List<CommentDTO> reponses;
}