package devOps.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service de vérification de disponibilité d'un livre dans les bibliothèques
 * d'Île-de-France via le Sudoc (Système Universitaire de Documentation).
 *
 * L'API Sudoc isbn2ppn est publique et sans clé. Elle retourne un PPN
 * (identifiant de notice) si le livre est référencé dans le réseau Sudoc.
 *
 * Usage : injecté dans LivreService pour enrichir LivreResponseDTO
 * avec la disponibilité réelle en IDF.
 */
@Service
public class SudocService {

    private static final String SUDOC_ISBN2PPN_URL = "https://www.sudoc.fr/services/isbn2ppn/";
    private static final String SUDOC_FICHE_URL    = "https://www.sudoc.fr/";

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Résultat de la vérification Sudoc pour un ISBN.
     */
    public static class SudocResultat {
        public final boolean disponibleEnIDF;
        public final String  ppn;           // identifiant de notice Sudoc
        public final String  lienSudoc;     // URL vers la fiche et les localisations

        public SudocResultat(boolean disponibleEnIDF, String ppn, String lienSudoc) {
            this.disponibleEnIDF = disponibleEnIDF;
            this.ppn             = ppn;
            this.lienSudoc       = lienSudoc;
        }
    }

    /**
     * Vérifie si un livre (identifié par ISBN) est disponible dans le réseau
     * Sudoc IDF. Retourne null si l'ISBN est absent ou si l'appel échoue.
     */
    public SudocResultat verifierDisponibilite(String isbn) {
        if (isbn == null || isbn.isBlank()) return null;

        // Nettoyer les tirets éventuels
        String isbnPropre = isbn.replace("-", "").trim();

        try {
            String url      = SUDOC_ISBN2PPN_URL + isbnPropre;
            String xmlReponse = restTemplate.getForObject(url, String.class);

            if (xmlReponse == null) return new SudocResultat(false, null, null);

            // Extraire le premier PPN depuis le XML : <ppn>123456789</ppn>
            String ppn = extrairePPN(xmlReponse);

            if (ppn != null) {
                String lien = SUDOC_FICHE_URL + ppn;
                return new SudocResultat(true, ppn, lien);
            } else {
                return new SudocResultat(false, null, null);
            }

        } catch (Exception e) {
            System.err.println("[SudocService] Erreur ISBN " + isbnPropre + ": " + e.getMessage());
            return null; // null = vérification impossible (timeout, réseau…)
        }
    }

    /**
     * Vérifie la disponibilité pour une liste d'ISBNs.
     * Utile quand un livre a plusieurs éditions (ISBN-10 et ISBN-13).
     */
    public SudocResultat verifierDisponibiliteMultipleISBN(List<String> isbns) {
        for (String isbn : isbns) {
            SudocResultat resultat = verifierDisponibilite(isbn);
            if (resultat != null && resultat.disponibleEnIDF) return resultat;
        }
        return new SudocResultat(false, null, null);
    }

    private String extrairePPN(String xml) {
        // Gère <ppn>123456789</ppn> et ppn="123456789"
        Pattern pattern = Pattern.compile("<ppn>(\\d+)</ppn>");
        Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) return matcher.group(1);

        Pattern pattern2 = Pattern.compile("ppn=(\\d+)");
        Matcher matcher2 = pattern2.matcher(xml);
        if (matcher2.find()) return matcher2.group(1);

        return null;
    }
}