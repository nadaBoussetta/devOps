package devOps.repositories;

import devOps.models.PublicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<PublicationEntity, Long> {

    // Publications créées par un utilisateur donné
    List<PublicationEntity> findByUtilisateurId(Long utilisateurId);

    // Publications où un utilisateur est répondeur
    List<PublicationEntity> findByRepondeurId(Long repondeurId);

    // Toutes les publications triées de la plus récente à la plus ancienne
    List<PublicationEntity> findAllByOrderByDateDesc();

    // Publications d’un utilisateur triées par date décroissante
    List<PublicationEntity> findByUtilisateurIdOrderByDateDesc(Long utilisateurId);

    // Échanges directs entre deux utilisateurs
    List<PublicationEntity> findByUtilisateurIdAndRepondeurId(Long utilisateurId, Long repondeurId);
}
