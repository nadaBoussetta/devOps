package devOps.controllers;

import devOps.dtos.FavoriDTO;
import devOps.models.FavoriEntity;
import devOps.models.LibraryEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.FavoriRepository;
import devOps.repositories.LibraryRepository;
import devOps.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
        Long userId = SecurityUtil.getCurrentUserId(); // <-- appel direct au static

        LibraryEntity library = libraryRepository.findById(favoriDTO.getBibliothequeId())
                .orElseThrow(() -> new RuntimeException("Bibliothèque non trouvée"));

        if (favoriRepository.existsByUser_IdAndLibraryEntity_Id(userId, library.getId())) {
            return ResponseEntity.badRequest().body(null);
        }

        FavoriEntity favori = new FavoriEntity();
        UtilisateurEntity user = new UtilisateurEntity();
        user.setId(userId);

        favori.setUser(user);
        favori.setLibraryEntity(library);

        FavoriEntity saved = favoriRepository.save(favori);

        FavoriDTO dto = new FavoriDTO(
                saved.getId(),
                saved.getLibraryEntity().getId(),
                saved.getLibraryEntity().getNom(),
                saved.getDateAjout()
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<FavoriDTO>> getFavoris() {
        Long userId = SecurityUtil.getCurrentUserId();

        List<FavoriDTO> favoris = favoriRepository.findByUser_IdOrderByDateAjoutDesc(userId)
                .stream()
                .map(f -> new FavoriDTO(
                        f.getId(),
                        f.getLibraryEntity().getId(),
                        f.getLibraryEntity().getNom(),
                        f.getDateAjout()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(favoris);
    }

    @DeleteMapping("/{bibliothequeId}")
    public ResponseEntity<Void> supprimerFavori(@PathVariable Long bibliothequeId) {
        Long userId = SecurityUtil.getCurrentUserId();
        favoriRepository.deleteByUser_IdAndLibraryEntity_Id(userId, bibliothequeId);
        return ResponseEntity.ok().build();
    }
}