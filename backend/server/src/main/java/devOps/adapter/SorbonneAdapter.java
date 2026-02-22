package devOps.adapter;

import devOps.dtos.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SorbonneAdapter implements LibraryAdapter {

    private static final String NOM_BIBLIOTHEQUE = "Bibliothèque Sorbonne";

    private final List<LivreResponseDTO> catalogueMock = List.of(
        new LivreResponseDTO("Les Misérables", "Victor Hugo", NOM_BIBLIOTHEQUE, true, "FR-840-HUG-001", "978-2-07-036736-1"),
        new LivreResponseDTO("Le Comte de Monte-Cristo", "Alexandre Dumas", NOM_BIBLIOTHEQUE, true, "FR-840-DUM-001", "978-2-253-09811-7"),
        new LivreResponseDTO("Madame Bovary", "Gustave Flaubert", NOM_BIBLIOTHEQUE, false, "FR-840-FLA-001", "978-2-253-00349-1"),
        new LivreResponseDTO("L'Étranger", "Albert Camus", NOM_BIBLIOTHEQUE, true, "FR-840-CAM-001", "978-2-07-036002-7"),
        new LivreResponseDTO("Le Petit Prince", "Antoine de Saint-Exupéry", NOM_BIBLIOTHEQUE, true, "FR-840-SAI-001", "978-2-07-061275-8"),
        new LivreResponseDTO("Germinal", "Émile Zola", NOM_BIBLIOTHEQUE, false, "FR-840-ZOL-001", "978-2-253-00431-3"),
        new LivreResponseDTO("Les Fleurs du mal", "Charles Baudelaire", NOM_BIBLIOTHEQUE, true, "FR-841-BAU-001", "978-2-253-00510-5"),
        new LivreResponseDTO("Candide", "Voltaire", NOM_BIBLIOTHEQUE, true, "FR-840-VOL-001", "978-2-253-00350-7")
    );

    @Override
    public List<LivreResponseDTO> rechercherLivre(String titre) {
        List<LivreResponseDTO> resultats = new ArrayList<>();
        
        String titreRecherche = titre.toLowerCase();
        
        for (LivreResponseDTO livre : catalogueMock) {
            if (livre.getTitre().toLowerCase().contains(titreRecherche)) {
                resultats.add(livre);
            }
        }
        
        return resultats;
    }

    @Override
    public String getNomBibliotheque() {
        return NOM_BIBLIOTHEQUE;
    }
}
