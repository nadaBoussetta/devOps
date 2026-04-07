package devOps.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        // 🔥 1️⃣ normalisation intelligente
        String normalized = normalizeAddress(adresse);

        // 2️⃣ tentative normale (optimisée)
        double[] result = callApi(normalized);
        if (result != null) return result;

        // 3️⃣ fallback : simplification (sans numéro)
        String simplified = simplifyAddress(normalized);
        result = callApi(simplified);
        if (result != null) return result;

        // 4️⃣ fallback final : ville seule
        String city = extractCity(normalized);
        return callApi(city);
    }

    /**
     * NORMALISATION INTELLIGENTE
     * 👉 remet la ville en premier
     */
    private String normalizeAddress(String adresse) {

        adresse = adresse.trim().replaceAll("\\s+", " ");

        // détecter code postal
        Pattern cpPattern = Pattern.compile("\\b\\d{5}\\b");
        Matcher matcher = cpPattern.matcher(adresse);

        String codePostal = "";
        if (matcher.find()) {
            codePostal = matcher.group();
            adresse = adresse.replace(codePostal, "").trim();
        }

        // détecter ville (dernier mot souvent)
        String[] parts = adresse.split(" ");
        if (parts.length > 1) {
            String ville = parts[parts.length - 1];

            // reconstruire : ville + reste
            String reste = adresse.substring(0, adresse.lastIndexOf(ville)).trim();

            return ville + " " + reste + (codePostal.isEmpty() ? "" : " " + codePostal);
        }

        return adresse;
    }

    /**
     * APPEL API
     */
    private double[] callApi(String query) {

        try {
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(API_URL)
                    .queryParam("q", query)
                    .queryParam("limit", 5)
                    .queryParam("autocomplete", 1); // 🔥 améliore pertinence

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
     * SIMPLIFICATION
     */
    private String simplifyAddress(String adresse) {
        return adresse
                .replaceAll("\\d+", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /**
     * EXTRAIRE VILLE
     */
    private String extractCity(String adresse) {

        // essayer avec code postal
        Pattern cpPattern = Pattern.compile("(\\d{5})\\s*(\\w+)");
        Matcher matcher = cpPattern.matcher(adresse);

        if (matcher.find()) {
            return matcher.group(2); // ville après CP
        }

        // fallback : dernier mot
        String[] parts = adresse.split(" ");
        return parts[parts.length - 1];
    }
}