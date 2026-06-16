package devOps.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devOps.adapter.BibliothequeAdapterFactory;
import devOps.adapter.LibraryAdapter;
import devOps.dtos.DisponibiliteRequestDTO;
import devOps.dtos.DisponibiliteResultatDTO;
import devOps.dtos.LivreResponseDTO;

@Service
public class LivreService {

    @Autowired
    private BibliothequeAdapterFactory adapterFactory;

    /**
     * Recherche un livre par titre dans tous les adapters.
     */
    public List<LivreResponseDTO> rechercherLivre(String titre) {
        List<LivreResponseDTO> resultats = new ArrayList<>();
        for (LibraryAdapter adapter : adapterFactory.getAllAdapters()) {
            try {
                resultats.addAll(adapter.rechercherLivre(titre));
            } catch (Exception e) {
                System.err.println("[LivreService] Erreur adapter "
                        + adapter.getNomBibliotheque() + ": " + e.getMessage());
            }
        }
        return resultats;
    }

    /**
     * Recherche dans un adapter spécifique par nom de bibliothèque.
     */
    public List<LivreResponseDTO> rechercherLivreDansBibliotheque(String titre, String nomBibliotheque) {
        LibraryAdapter adapter = adapterFactory.getAdapter(nomBibliotheque);
        if (adapter == null) {
            throw new RuntimeException("Bibliothèque non trouvée: " + nomBibliotheque);
        }
        return adapter.rechercherLivre(titre);
    }

    /**
     * Pour chaque bibliothèque IDF reçue (issue de /api/bibliotheques/recherche),
     * cherche si le livre y est disponible via l'adapter correspondant.
     *
     * Si aucun adapter ne correspond exactement au nom de la bibliothèque IDF,
     * on utilise tous les adapters disponibles et on associe les résultats
     * par correspondance partielle de nom.
     */
    public List<DisponibiliteResultatDTO> verifierDisponibiliteParBibliotheques(
            String titre,
            List<DisponibiliteRequestDTO.BibliothequeSimpleDTO> bibliotheques) {

        List<DisponibiliteResultatDTO> resultats = new ArrayList<>();

        for (DisponibiliteRequestDTO.BibliothequeSimpleDTO biblio : bibliotheques) {

            // Chercher l'adapter dont le nom correspond à la bibliothèque IDF
            LibraryAdapter adapter = trouverAdapterParNom(biblio.getNom());

            List<LivreResponseDTO> exemplaires = new ArrayList<>();
            boolean livreDisponible = false;

            if (adapter != null) {
                try {
                    exemplaires = adapter.rechercherLivre(titre);
                    livreDisponible = !exemplaires.isEmpty();
                } catch (Exception e) {
                    System.err.println("[LivreService] Erreur recherche dans "
                            + biblio.getNom() + ": " + e.getMessage());
                }
            } else {
                // Pas d'adapter spécifique : chercher dans tous et filtrer
                // par nom de bibliothèque si le champ bibliotheque du livre correspond
                for (LibraryAdapter a : adapterFactory.getAllAdapters()) {
                    try {
                        List<LivreResponseDTO> found = a.rechercherLivre(titre);
                        if (!found.isEmpty()) {
                            exemplaires.addAll(found);
                            livreDisponible = true;
                        }
                    } catch (Exception e) {
                        System.err.println("[LivreService] Erreur adapter: " + e.getMessage());
                    }
                }
            }

            resultats.add(new DisponibiliteResultatDTO(
                    biblio.getNom(),
                    biblio.getAdresse(),
                    biblio.getDistance(),
                    biblio.getOuvert(),
                    livreDisponible,
                    exemplaires
            ));
        }

        return resultats;
    }

    /**
     * Cherche un adapter dont le nom contient le nom de la bibliothèque IDF
     * (correspondance partielle insensible à la casse).
     */
    private LibraryAdapter trouverAdapterParNom(String nomBibliothequeIDF) {
        if (nomBibliothequeIDF == null) return null;
        String nomLower = nomBibliothequeIDF.toLowerCase();

        return adapterFactory.getAllAdapters().stream()
                .filter(a -> nomLower.contains(a.getNomBibliotheque().toLowerCase())
                          || a.getNomBibliotheque().toLowerCase().contains(nomLower))
                .findFirst()
                .orElse(null);
    }
}