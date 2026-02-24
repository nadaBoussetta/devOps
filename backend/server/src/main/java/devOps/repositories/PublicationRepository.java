package devOps.repositories;

import devOps.models.PublicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<PublicationEntity, Long> {

    List<PublicationEntity> findByAuteurId(Long auteurId);

    List<PublicationEntity> findByAuteurIdOrderByDateCreationDesc(Long auteurId);

    List<PublicationEntity> findAllByOrderByDateCreationDesc();
}
