package devOps.controllers;

import devOps.endpoints.LivreEndpoint;
import devOps.responses.LivreResponseDTO;
import devOps.services.LivreService;
import org.springframework.web.bind.annotation.*;
import devOps.responses.DisponibiliteResponseDTO;


import java.util.List;

@RestController
public class LivreController implements LivreEndpoint {

    private final LivreService livreService;

    public LivreController(LivreService livreService) {
        this.livreService = livreService;
    }

    @Override
    public List<LivreResponseDTO> rechercherLivres(
            @PathVariable String lieuId,
            @RequestParam String titre
    ) {
        return livreService.rechercherLivres(lieuId, titre);
    }

    @Override
    public DisponibiliteResponseDTO verifierDisponibilite(String lieuId, String livreId) {
        return livreService.verifierDisponibilite(lieuId, livreId);
    }

    @Override
    public void emprunterLivre(String lieuId, String livreId) {
        livreService.emprunterLivre(lieuId, livreId);
    }

}
