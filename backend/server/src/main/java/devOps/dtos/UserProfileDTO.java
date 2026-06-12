package devOps.dtos;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    private Long id;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String username;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    // ✅ Nouveaux champs
    private String bio;
    private String ville;
    private LocalDate dateNaissance;

    // ✅ Statistiques (lecture seule, non modifiables)
    private Integer nbPosts;
    private Integer nbFavoris;
    private Integer nbAvis;

    // ✅ Données affichées sur le profil
    private List<FavoriDTO> favoris;
    private List<NotationDTO> avis;
    private List<PublicationDTO> posts;

    // Constructeur minimal pour la création/mise à jour
    public UserProfileDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    // Constructeur avec champs optionnels
    public UserProfileDTO(Long id, String username, String email, String bio, String ville, LocalDate dateNaissance) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.ville = ville;
        this.dateNaissance = dateNaissance;
    }
}