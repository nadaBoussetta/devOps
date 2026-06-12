package devOps.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import devOps.models.ReactionEntity;

@Repository
public interface ReactionRepository extends JpaRepository<ReactionEntity, Long> {

    Optional<ReactionEntity> findByUserIdAndPostId(Long userId, Long postId);

    List<ReactionEntity> findByPostId(Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);

    // Compte les réactions par type pour un post
    @Query("SELECT r.type, COUNT(r) FROM ReactionEntity r WHERE r.post.id = :postId GROUP BY r.type")
    List<Object[]> countByTypeForPost(@Param("postId") Long postId);
}