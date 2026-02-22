package devOps.adapter;

import devOps.dtos.*;

import java.util.List;

public interface LibraryAdapter {

    List<LivreResponseDTO> rechercherLivre(String titre);

    String getNomBibliotheque();
}
