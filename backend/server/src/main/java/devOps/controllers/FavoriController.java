package devOps.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devOps.dtos.FavoriDTO;
import devOps.enums.TypeBibliotheque;
import devOps.models.FavoriEntity;
import devOps.models.LibraryEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.FavoriRepository;
import devOps.repositories.LibraryRepository;
import devOps.util.SecurityUtil;

@RestController
@RequestMapping("/api/favoris")
@CrossOrigin(origins = "*")
public class FavoriController {

    @Autowired
    private FavoriRepository favoriRepository;

    @Autowired
    private LibraryRepository libraryRepository;

    @PostMapping
    public ResponseEntity<FavoriDTO> ajouterAuxFavoris(@RequestBody FavoriDTO favoriDTO) {
        Long userId = SecurityUtil.getCurrentUserId();

        // Upsert : cherche par nom+adresse si fournis (bibliothèque externe API IDF),
        // sinon cherche par id classique
        LibraryEntity library;

        if (favoriDTO.getBibliothequeNom() != null && favoriDTO.getBibliothequeAdresse() != null) {
            library = libraryRepository
                    .findByNomAndAdresse(favoriDTO.getBibliothequeNom(), favoriDTO.getBibliothequeAdresse())
                    .orElseGet(() -> {
                        LibraryEntity newLib = new LibraryEntity();
                        newLib.setNom(favoriDTO.getBibliothequeNom());
                        newLib.setAdresse(favoriDTO.getBibliothequeAdresse());
                        newLib.setLatitude(favoriDTO.getBibliothequeLatitude() != null ? favoriDTO.getBibliothequeLatitude() : 0.0);
                        newLib.setLongitude(favoriDTO.getBibliothequeLongitude() != null ? favoriDTO.getBibliothequeLongitude() : 0.0);
                        newLib.setType(favoriDTO.getBibliothequeType() != null ? favoriDTO.getBibliothequeType() : TypeBibliotheque.PUBLIQUE);
                        newLib.setNoteGlobale(0.0);
                        newLib.setNombreNotations(0);
                        return libraryRepository.save(newLib);
                    });
        } else {
            library = libraryRepository.findById(favoriDTO.getBibliothequeId())
                    .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));
        }

        if (favoriRepository.existsByUser_IdAndLibraryEntity_Id(userId, library.getId())) {
            return ResponseEntity.badRequest().body(null);
        }

        FavoriEntity favori = new FavoriEntity();
        UtilisateurEntity user = new UtilisateurEntity();
        user.setId(userId);
        favori.setUser(user);
        favori.setLibraryEntity(library);

        FavoriEntity saved = favoriRepository.save(favori);

        return ResponseEntity.ok(toDTO(saved));
    }

    @GetMapping
    public ResponseEntity<List<FavoriDTO>> getFavoris() {
        Long userId = SecurityUtil.getCurrentUserId();

        List<FavoriDTO> favoris = favoriRepository.findByUser_IdOrderByDateAjoutDesc(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(favoris);
    }

    @DeleteMapping("/{bibliothequeId}")
    public ResponseEntity<Void> supprimerFavori(@PathVariable Long bibliothequeId) {
        Long userId = SecurityUtil.getCurrentUserId();
        favoriRepository.deleteByUser_IdAndLibraryEntity_Id(userId, bibliothequeId);
        return ResponseEntity.ok().build();
    }

    // ── Convertit un FavoriEntity en FavoriDTO (8 champs) ──
    private FavoriDTO toDTO(FavoriEntity f) {
        return new FavoriDTO(
                f.getId(),
                f.getLibraryEntity().getId(),
                f.getLibraryEntity().getNom(),
                f.getDateAjout(),
                f.getLibraryEntity().getAdresse(),
                f.getLibraryEntity().getLatitude(),
                f.getLibraryEntity().getLongitude(),
                f.getLibraryEntity().getType()
        );
    }
}