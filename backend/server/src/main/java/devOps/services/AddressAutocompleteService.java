package devOps.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Service pour l'autocomplétion d'adresses via l'API française data.gouv.fr
 */
@Service
public class AddressAutocompleteService {

    private static final String API_ADRESSE_URL = "https://api-adresse.data.gouv.fr/search/";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AddressAutocompleteService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Récupère les suggestions d'adresses pour une requête donnée.
     *
     * @param query texte de recherche
     * @param limit nombre maximum de résultats
     * @return liste des adresses suggérées
     */
    public List<AddressSuggestion> getAddressSuggestions(String query, int limit) {
        List<AddressSuggestion> suggestions = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return suggestions;
        }

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_ADRESSE_URL)
                    .queryParam("q", query)
                    .queryParam("limit", Math.min(limit, 10)); // Limiter à 10 résultats max

            JsonNode response = restTemplate.getForObject(builder.toUriString(), JsonNode.class);

            if (response != null && response.has("features") && response.get("features").isArray()) {
                for (JsonNode feature : response.get("features")) {
                    try {
                        AddressSuggestion suggestion = parseFeature(feature);
                        if (suggestion != null) {
                            suggestions.add(suggestion);
                        }
                    } catch (Exception e) {
                        // Continuer si le parsing d'une adresse échoue
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching address suggestions: " + e.getMessage());
        }

        return suggestions;
    }

    /**
     * Parse une feature GeoJSON pour extraire les informations d'adresse.
     */
    private AddressSuggestion parseFeature(JsonNode feature) {
        try {
            JsonNode properties = feature.get("properties");
            JsonNode geometry = feature.get("geometry");

            if (properties == null || geometry == null) {
                return null;
            }

            String label = properties.has("label") ? properties.get("label").asText() : null;
            double score = properties.has("score") ? properties.get("score").asDouble() : 0;

            JsonNode coordinates = geometry.get("coordinates");
            if (coordinates == null || coordinates.size() < 2) {
                return null;
            }

            double longitude = coordinates.get(0).asDouble();
            double latitude = coordinates.get(1).asDouble();

            String city = properties.has("city") ? properties.get("city").asText() : "";
            String postcode = properties.has("postcode") ? properties.get("postcode").asText() : "";
            String name = properties.has("name") ? properties.get("name").asText() : "";

            return new AddressSuggestion(label, name, city, postcode, latitude, longitude, score);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * DTO pour les suggestions d'adresses.
     */
    public static class AddressSuggestion {
        private String label;
        private String name;
        private String city;
        private String postcode;
        private double latitude;
        private double longitude;
        private double score;

        public AddressSuggestion(String label, String name, String city, String postcode,
                                 double latitude, double longitude, double score) {
            this.label = label;
            this.name = name;
            this.city = city;
            this.postcode = postcode;
            this.latitude = latitude;
            this.longitude = longitude;
            this.score = score;
        }

        // Getters
        public String getLabel() { return label; }
        public String getName() { return name; }
        public String getCity() { return city; }
        public String getPostcode() { return postcode; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public double getScore() { return score; }
    }
}
