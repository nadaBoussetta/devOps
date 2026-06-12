package devOps.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/livres")
@CrossOrigin(origins = "*")
public class LivreIAController {

    @Value("${anthropic.api.key:}")
    private String anthropicApiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * POST /api/livres/ia
     * Proxy vers l'API Anthropic. Reçoit { "query": "..." },
     * appelle Claude avec web_search, extrait le JSON de livres et le renvoie directement.
     */
    @PostMapping("/ia")
    public ResponseEntity<String> rechercherAvecIA(@RequestBody Map<String, String> body) {

        String query = body.get("query");
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("{\"error\":\"Le champ query est requis\"}");
        }

        if (anthropicApiKey == null || anthropicApiKey.isBlank()
                || anthropicApiKey.equals("VOTRE_CLE_API_ICI")) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("{\"error\":\"Clé API Anthropic non configurée dans application.properties\"}");
        }

        try {
            // ── Construire le body Anthropic avec Jackson ─────────────────────
            ObjectNode requestBody = mapper.createObjectNode();
            requestBody.put("model", "claude-sonnet-4-20250514");
            requestBody.put("max_tokens", 2000);

            // System prompt
            requestBody.put("system",
                "Tu es un agent expert en littérature. Utilise la recherche web pour trouver des informations actuelles. " +
                "Retourne UNIQUEMENT un objet JSON valide (sans balises markdown, sans texte avant/après) avec ce format EXACT : " +
                "{\"summary\":\"phrase de contexte\",\"books\":[{\"titre\":\"...\",\"auteur\":\"...\",\"annee\":\"..\"," +
                "\"genre\":\"...\",\"editeur\":\"...\",\"pages\":\"...\",\"note\":\"4.2\",\"resume\":\"..\"," +
                "\"isbn\":\"...\",\"langue\":\"Français\",\"disponible_en_ligne\":true,\"lien\":\"https://...\"}]}. " +
                "Retourne entre 4 et 8 livres pertinents.");

            // Web search tool
            ArrayNode tools = mapper.createArrayNode();
            ObjectNode searchTool = mapper.createObjectNode();
            searchTool.put("type", "web_search_20250305");
            searchTool.put("name", "web_search");
            tools.add(searchTool);
            requestBody.set("tools", tools);

            // Message utilisateur
            ArrayNode messages = mapper.createArrayNode();
            ObjectNode userMsg = mapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", "Recherche des livres pour : \"" + query + "\". " +
                    "Utilise la recherche web pour avoir des informations à jour (notes, résumés, disponibilité).");
            messages.add(userMsg);
            requestBody.set("messages", messages);

            // ── Appel HTTP vers Anthropic ─────────────────────────────────────
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", anthropicApiKey);
            headers.set("anthropic-version", "2023-06-01");
            headers.set("anthropic-beta", "web-search-2025-03-05");

            HttpEntity<String> request = new HttpEntity<>(
                    mapper.writeValueAsString(requestBody), headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> anthropicResponse = restTemplate.exchange(
                    "https://api.anthropic.com/v1/messages",
                    HttpMethod.POST, request, String.class);

            // ── Extraire le JSON de livres depuis la réponse ──────────────────
            String extractedJson = extractBooksJson(anthropicResponse.getBody());
            return ResponseEntity.ok(extractedJson);

        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage().replace("\"", "'") : "Erreur inconnue";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + msg + "\"}");
        }
    }

    /**
     * Extrait le JSON de livres depuis la réponse brute d'Anthropic.
     * Gère les blocs : text, tool_use, tool_result.
     */
    private String extractBooksJson(String anthropicBody) throws Exception {
        JsonNode root = mapper.readTree(anthropicBody);
        JsonNode content = root.get("content");

        if (content == null || !content.isArray()) {
            throw new RuntimeException("Réponse Anthropic invalide : pas de contenu");
        }

        // Chercher le dernier bloc de type "text"
        String lastText = null;
        for (JsonNode block : content) {
            String type = block.has("type") ? block.get("type").asText() : "";
            if ("text".equals(type) && block.has("text")) {
                lastText = block.get("text").asText().trim();
            }
        }

        if (lastText == null || lastText.isBlank()) {
            throw new RuntimeException("Aucun bloc texte dans la réponse de Claude");
        }

        // Nettoyer les balises markdown éventuelles
        lastText = lastText.replaceAll("(?i)^```json\\s*", "")
                           .replaceAll("(?i)^```\\s*", "")
                           .replaceAll("```\\s*$", "")
                           .trim();

        // Tenter de parser directement
        try {
            mapper.readTree(lastText); // validation
            return lastText;
        } catch (Exception e) {
            // Extraire le premier bloc JSON avec regex
            int start = lastText.indexOf('{');
            int end   = lastText.lastIndexOf('}');
            if (start >= 0 && end > start) {
                String extracted = lastText.substring(start, end + 1);
                mapper.readTree(extracted); // validation
                return extracted;
            }
            throw new RuntimeException("Impossible d'extraire un JSON valide de la réponse : " + lastText.substring(0, Math.min(200, lastText.length())));
        }
    }
}