package devOps.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<PublicationEntity> posts = new ArrayList<>();

    @OneToMany(mappedBy = "auteur", cascade = CascadeType.ALL)
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<NotationEntity> notations = new ArrayList<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<FavoriEntity> favoris = new ArrayList<>();

    public UtilisateurEntity(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles.add("ROLE_USER");
    }
}
