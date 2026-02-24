package devOps.repositories;

import devOps.enums.TypeBibliotheque;
import devOps.models.LibraryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryRepository extends JpaRepository<LibraryEntity, Long> {

    List<LibraryEntity> findByType(TypeBibliotheque type);

    List<LibraryEntity> findByNomContainingIgnoreCase(String nom);
}