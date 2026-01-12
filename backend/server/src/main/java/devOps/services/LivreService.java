package devOps.services;

import devOps.component.DisponibiliteComponent;
import devOps.mappers.LivreMapper;
import devOps.responses.LivreResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import devOps.responses.DisponibiliteResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;



@Service
public class LivreService {

    private final DisponibiliteComponent disponibiliteComponent;
    private final LivreMapper livreMapper;

    public LivreService(DisponibiliteComponent disponibiliteComponent, LivreMapper livreMapper) {
        this.disponibiliteComponent = disponibiliteComponent;
        this.livreMapper = livreMapper;
    }

    public List<LivreResponseDTO> rechercherLivres(String lieuId, String titre) {
        return disponibiliteComponent.searchBooks(lieuId, titre)
                .stream()
                .map(d -> livreMapper.toResponse(d.getLivre()))
                .toList();
    }

    public DisponibiliteResponseDTO verifierDisponibilite(String lieuId, String livreId) {
        boolean dispo = disponibiliteComponent.isLivreDisponible(lieuId, livreId);
        return new DisponibiliteResponseDTO(lieuId, livreId, dispo);
    }

    public void emprunterLivre(String lieuId, String livreId) {
        disponibiliteComponent.emprunterLivre(lieuId, livreId);
    }

}
