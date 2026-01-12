package devOps.component;

import devOps.models.DisponibiliteEntity;
import devOps.repositories.DisponibiliteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;



import devOps.models.DisponibiliteEntity;
import devOps.repositories.DisponibiliteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class DisponibiliteComponent {

    private final DisponibiliteRepository disponibiliteRepository;

    public DisponibiliteComponent(DisponibiliteRepository disponibiliteRepository) {
        this.disponibiliteRepository = disponibiliteRepository;
    }

    public List<DisponibiliteEntity> searchBooks(String lieuId, String titre) {
        return disponibiliteRepository
                .findByLieuIdAndLivreDispoTrueAndLivreTitreContainingIgnoreCase(lieuId, titre);
    }

    public boolean isLivreDisponible(String lieuId, String livreId) {
        return disponibiliteRepository.existsByLieuIdAndLivreIdAndLivreDispoTrue(lieuId, livreId);
    }

    public void emprunterLivre(String lieuId, String livreId) {
        DisponibiliteEntity dispo = disponibiliteRepository
                .findByLieuIdAndLivreId(lieuId, livreId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Aucune disponibilité trouvée pour ce livre dans ce lieu"));

        if (Boolean.FALSE.equals(dispo.getLivreDispo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Livre indisponible (déjà emprunté)");
        }

        dispo.setLivreDispo(false);
        disponibiliteRepository.save(dispo);
    }
}
