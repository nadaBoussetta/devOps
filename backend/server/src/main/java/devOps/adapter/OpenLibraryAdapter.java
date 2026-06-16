package devOps.adapter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;

import devOps.dtos.LivreResponseDTO;

/**
 * Adapter réel vers l'API Open Library (openlibrary.org).
 * Remplace ParisDescarteAdapter et SorbonneAdapter qui utilisaient des mocks.
 * Open Library est gratuite, sans clé API, couvre des millions de livres.
 */
@Component
public class OpenLibraryAdapter implements LibraryAdapter {

    private static final String NOM_BIBLIOTHEQUE = "Open Library";
    private static final String API_URL = "https://openlibrary.org/search.json";
    private static final int MAX_RESULTS = 20;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<LivreResponseDTO> rechercherLivre(String titre) {
        List<LivreResponseDTO> resultats = new ArrayList<>();

        try {
            String url = UriComponentsBuilder.fromHttpUrl(API_URL)
                    .queryParam("q", titre)
                    .queryParam("limit", MAX_RESULTS)
                    .queryParam("lang", "fre")
                    .queryParam("fields", "title,author_name,isbn,key,first_publish_year,subject")
                    .toUriString();

            JsonNode root = restTemplate.getForObject(url, JsonNode.class);

            if (root == null || !root.has("docs")) return resultats;

            for (JsonNode doc : root.get("docs")) {
                String isbn = extraireISBN(doc);
                String auteur = extraireAuteur(doc);
                String cote = doc.path("key").asText(null); // ex: /works/OL123W

                LivreResponseDTO livre = new LivreResponseDTO(
                        doc.path("title").asText("Titre inconnu"),
                        auteur,
                        NOM_BIBLIOTHEQUE,
                        true, // Open Library = disponible en ligne
                        cote,
                        isbn
                );
                resultats.add(livre);
            }

        } catch (Exception e) {
            System.err.println("[OpenLibraryAdapter] Erreur appel API: " + e.getMessage());
        }

        return resultats;
    }

    private String extraireISBN(JsonNode doc) {
        if (!doc.has("isbn") || !doc.get("isbn").isArray()) return null;
        // Préférer ISBN-13 (13 chiffres), sinon prendre le premier
        for (JsonNode isbn : doc.get("isbn")) {
            String val = isbn.asText("");
            if (val.length() == 13) return val;
        }
        JsonNode first = doc.get("isbn").get(0);
        return first != null ? first.asText(null) : null;
    }

    private String extraireAuteur(JsonNode doc) {
        if (!doc.has("author_name") || !doc.get("author_name").isArray()) return "Auteur inconnu";
        return doc.get("author_name").get(0).asText("Auteur inconnu");
    }

    @Override
    public String getNomBibliotheque() {
        return NOM_BIBLIOTHEQUE;
    }
}