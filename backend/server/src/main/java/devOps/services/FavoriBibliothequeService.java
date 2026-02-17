package devOps.services;

import devOps.models.FavoriBibliothequeEntity;
import devOps.models.LibraryEntity;
import devOps.models.UtilisateurEntity;
import devOps.repositories.FavoriBibliothequeRepository;
import devOps.repositories.LibraryRepository;
import devOps.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class FavoriBibliothequeService {

    private final FavoriBibliothequeRepository favoriRepo;
    private final UtilisateurRepository utilisateurRepo;
    private final LibraryRepository libraryRepo;

    public FavoriBibliothequeService(FavoriBibliothequeRepository favoriRepo,
                                     UtilisateurRepository utilisateurRepo,
                                     LibraryRepository libraryRepo) {
        this.favoriRepo = favoriRepo;
        this.utilisateurRepo = utilisateurRepo;
        this.libraryRepo = libraryRepo;
    }

    public void addFavori(Long utilisateurId, String libraryId) {

        if (favoriRepo.existsByUtilisateurIdAndLibraryId(utilisateurId, libraryId)) {
            return; // éviter les doublons
        }

        UtilisateurEntity user = utilisateurRepo.findById(utilisateurId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Utilisateur introuvable: " + utilisateurId));

        LibraryEntity library = libraryRepo.findById(libraryId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Bibliothèque introuvable: " + libraryId));

        FavoriBibliothequeEntity favori = new FavoriBibliothequeEntity();
        favori.setUtilisateur(user);
        favori.setLibrary(library);
        favori.setDateAjout(new Date());

        favoriRepo.save(favori);
    }

    public void removeFavori(Long utilisateurId, String libraryId) {
        favoriRepo.deleteByUtilisateurIdAndLibraryId(utilisateurId, libraryId);
    }

    public List<FavoriBibliothequeEntity> listFavoris(Long utilisateurId) {
        return favoriRepo.findByUtilisateurId(utilisateurId);
    }

    public List<LibraryEntity> recommendations(Long utilisateurId) {
        return libraryRepo.findAll(); // MVP simple
    }
}
