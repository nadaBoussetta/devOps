package devOps.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import devOps.enums.TypeBibliotheque;
import devOps.models.LibraryEntity;

@Repository
public interface LibraryRepository extends JpaRepository<LibraryEntity, Long> {

    List<LibraryEntity> findByType(TypeBibliotheque type);

    List<LibraryEntity> findByNomContainingIgnoreCase(String nom);

    // Recherche par nom ET adresse pour éviter les doublons lors de l'upsert
    Optional<LibraryEntity> findByNomAndAdresse(String nom, String adresse);
}