package devOps.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeolocationService {

    private static final String API_URL = "https://api-adresse.data.gouv.fr/search/";
    private final RestTemplate restTemplate;

    public GeolocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * POINT D'ENTRÉE PRINCIPAL
     */
    public double[] geocodeAdresse(String adresse) {

        if (adresse == null || adresse.trim().isEmpty()) {
            return null;
        }

        // 1️⃣ tentative normale
        double[] result = callApi(adresse);
        if (result != null) return result;

        // 2️⃣ fallback : simplification (adresse + ville)
        String simplified = simplifyAddress(adresse);
        result = callApi(simplified);
        if (result != null) return result;

        // 3️⃣ dernier fallback : ville seule
        String city = extractCity(adresse);
        return callApi(city);
    }

    /**
     * APPEL API PRINCIPAL
     */
    private double[] callApi(String query) {

        try {
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(API_URL)
                    .queryParam("q", query)
                    .queryParam("limit", 5);

            JsonNode response = restTemplate.getForObject(builder.toUriString(), JsonNode.class);

            if (response == null || !response.has("features")) {
                return null;
            }

            JsonNode features = response.get("features");

            double bestScore = -1;
            double bestLat = 0;
            double bestLon = 0;

            for (JsonNode feature : features) {

                JsonNode props = feature.get("properties");
                JsonNode geometry = feature.get("geometry");

                if (geometry == null || !geometry.has("coordinates")) continue;

                double score = props.has("score") ? props.get("score").asDouble() : 0;

                JsonNode coords = geometry.get("coordinates");

                double lon = coords.get(0).asDouble();
                double lat = coords.get(1).asDouble();

                if (score > bestScore) {
                    bestScore = score;
                    bestLat = lat;
                    bestLon = lon;
                }
            }

            if (bestScore > 0) {
                return new double[]{bestLat, bestLon};
            }

        } catch (Exception e) {
            System.err.println("Erreur géocodage API: " + e.getMessage());
        }

        return null;
    }

    /**
     * SUPPRESSION DU NUMÉRO POUR AMÉLIORER MATCHING
     */
    private String simplifyAddress(String adresse) {
        return adresse
                .replaceAll("\\d+", "")     // enlève numéro
                .replaceAll("\\s+", " ")    // clean espaces
                .trim();
    }

    /**
     * EXTRAIRE VILLE EN DERNIER RECOURS
     */
    private String extractCity(String adresse) {
        String[] parts = adresse.split(",");

        if (parts.length > 1) {
            return parts[parts.length - 1].trim();
        }

        return adresse;
    }
}