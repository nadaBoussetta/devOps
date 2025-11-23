package devOps.services;

import devOps.component.DisponibiliteComponent;
import devOps.mappers.LivreMapper;
import devOps.responses.LivreResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

}
