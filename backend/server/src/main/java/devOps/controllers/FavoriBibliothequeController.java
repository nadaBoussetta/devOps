package devOps.controllers;

import devOps.models.FavoriBibliothequeEntity;
import devOps.models.LibraryEntity;
import devOps.services.FavoriBibliothequeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs/{utilisateurId}")
public class FavoriBibliothequeController {

    private final FavoriBibliothequeService service;

    public FavoriBibliothequeController(FavoriBibliothequeService service) {
        this.service = service;
    }

    @PostMapping("/bibliotheques-favoris/{libraryId}")
    public void add(@PathVariable Long utilisateurId, @PathVariable String libraryId) {
        service.addFavori(utilisateurId, libraryId);
    }

    @DeleteMapping("/bibliotheques-favoris/{libraryId}")
    public void remove(@PathVariable Long utilisateurId, @PathVariable String libraryId) {
        service.removeFavori(utilisateurId, libraryId);
    }

    @GetMapping("/bibliotheques-favoris")
    public List<FavoriBibliothequeEntity> list(@PathVariable Long utilisateurId) {
        return service.listFavoris(utilisateurId);
    }

    @GetMapping("/recommandations")
    public List<LibraryEntity> recommandations(@PathVariable Long utilisateurId) {
        return service.recommendations(utilisateurId);
    }
}
