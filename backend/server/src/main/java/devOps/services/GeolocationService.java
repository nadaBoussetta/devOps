package devOps.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class GeolocationService {

    private static final String API_ADRESSE_URL = "https://api-adresse.data.gouv.fr/search/";
    private final RestTemplate restTemplate;

    public GeolocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double[] geocodeAdresse(String adresse) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_ADRESSE_URL)
                .queryParam("q", adresse)
                .queryParam("limit", 1);

        JsonNode response = restTemplate.getForObject(builder.toUriString(), JsonNode.class);

        if (response != null && response.has("features") && response.get("features").isArray() && response.get("features").size() > 0) {
            JsonNode geometry = response.get("features").get(0).get("geometry");
            if (geometry != null && geometry.has("coordinates") && geometry.get("coordinates").isArray() && geometry.get("coordinates").size() == 2) {
                double longitude = geometry.get("coordinates").get(0).asDouble();
                double latitude = geometry.get("coordinates").get(1).asDouble();
                return new double[]{latitude, longitude};
            }
        }
        // Fallback or error handling
        return new double[]{0.0, 0.0}; // Retourne des coordonnées par défaut en cas d'échec
    }
}