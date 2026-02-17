package devOps.repositories;

import devOps.models.FavoriBibliothequeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriBibliothequeRepository extends JpaRepository<FavoriBibliothequeEntity, Long> {

    List<FavoriBibliothequeEntity> findByUtilisateurId(Long utilisateurId);

    boolean existsByUtilisateurIdAndLibraryId(Long utilisateurId, String libraryId);

    void deleteByUtilisateurIdAndLibraryId(Long utilisateurId, String libraryId);
}
