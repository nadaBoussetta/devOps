package devOps.services;

import devOps.adapter.*;
import devOps.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LivreService {

    @Autowired
    private BibliothequeAdapterFactory adapterFactory;

    public List<LivreResponseDTO> rechercherLivre(String titre) {
        List<LivreResponseDTO> resultats = new ArrayList<>();

        List<LibraryAdapter> adapters = adapterFactory.getAllAdapters();

        for (LibraryAdapter adapter : adapters) {
            try {
                List<LivreResponseDTO> livresAdapter = adapter.rechercherLivre(titre);
                resultats.addAll(livresAdapter);
            } catch (Exception e) {
                System.err.println("Erreur lors de la recherche dans " +
                        adapter.getNomBibliotheque() + ": " + e.getMessage());
            }
        }

        return resultats;
    }

    public List<LivreResponseDTO> rechercherLivreDansBibliotheque(String titre, String nomBibliotheque) {
        LibraryAdapter adapter = adapterFactory.getAdapter(nomBibliotheque);

        if (adapter == null) {
            throw new RuntimeException("Bibliothèque non trouvée: " + nomBibliotheque);
        }

        return adapter.rechercherLivre(titre);
    }
}

