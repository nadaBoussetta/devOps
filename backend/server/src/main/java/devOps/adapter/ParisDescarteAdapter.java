package devOps.adapter;

import devOps.dtos.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ParisDescarteAdapter implements LibraryAdapter {

    private static final String NOM_BIBLIOTHEQUE = "Bibliothèque Paris Descartes";

    //Mock
    private final List<LivreResponseDTO> catalogueMock = List.of(
        new LivreResponseDTO("Les Misérables", "Victor Hugo", NOM_BIBLIOTHEQUE, false, "PD-LIT-HUG-001", "978-2-07-036736-1"),
        new LivreResponseDTO("1984", "George Orwell", NOM_BIBLIOTHEQUE, true, "PD-LIT-ORW-001", "978-2-07-036822-1"),
        new LivreResponseDTO("Le Meilleur des mondes", "Aldous Huxley", NOM_BIBLIOTHEQUE, true, "PD-LIT-HUX-001", "978-2-266-12889-2"),
        new LivreResponseDTO("L'Étranger", "Albert Camus", NOM_BIBLIOTHEQUE, true, "PD-LIT-CAM-001", "978-2-07-036002-7"),
        new LivreResponseDTO("La Peste", "Albert Camus", NOM_BIBLIOTHEQUE, false, "PD-LIT-CAM-002", "978-2-07-036021-8"),
        new LivreResponseDTO("Le Rouge et le Noir", "Stendhal", NOM_BIBLIOTHEQUE, true, "PD-LIT-STE-001", "978-2-253-00356-9"),
        new LivreResponseDTO("Les Contemplations", "Victor Hugo", NOM_BIBLIOTHEQUE, true, "PD-POE-HUG-001", "978-2-253-00357-6"),
        new LivreResponseDTO("Voyage au bout de la nuit", "Louis-Ferdinand Céline", NOM_BIBLIOTHEQUE, true, "PD-LIT-CEL-001", "978-2-07-036011-9")
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
