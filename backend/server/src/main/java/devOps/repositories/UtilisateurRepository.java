package devOps.repositories;

import devOps.models.*;
import devOps.models.UtilisateurEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<UtilisateurEntity, Long> {

    Optional<UtilisateurEntity> findByUsername(String username);

    Optional<UtilisateurEntity> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
