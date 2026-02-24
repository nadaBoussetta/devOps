package devOps.repositories;

import devOps.models.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    List<CommentEntity> findByPostIdOrderByDateCreationAsc(Long postId);

    List<CommentEntity> findByAuteurIdOrderByDateCreationDesc(Long auteurId);

    List<CommentEntity> findByParentCommentIdOrderByDateCreationAsc(Long parentCommentId);
}