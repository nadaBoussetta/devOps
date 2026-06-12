package devOps.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String contenu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id", nullable = false)
    private UtilisateurEntity auteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bibliotheque_id")
    private LibraryEntity libraryEntity;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    // ✅ Repost : référence au post original
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_original_id")
    private PublicationEntity postOriginal;

    @Column(nullable = false)
    private Boolean estRepost = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>();

    // ✅ Réactions
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReactionEntity> reactions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (estRepost == null) estRepost = false;
    }

    public void addComment(CommentEntity comment) {
        comments.add(comment);
        comment.setPost(this);
    }
}