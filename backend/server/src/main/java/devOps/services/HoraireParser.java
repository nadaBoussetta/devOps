package devOps.services;

import devOps.dtos.HoraireDTO;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service pour parser les horaires non structurés en format structuré par jour.
 * Gère les formats textuels comme "du lundi au vendredi de 14h à 17h30, le samedi de 14h à 19h"
 */
@Service
public class HoraireParser {

    private static final Map<String, String> JOUR_MAPPING = new HashMap<>();
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    static {
        JOUR_MAPPING.put("lundi", "LUNDI");
        JOUR_MAPPING.put("mardi", "MARDI");
        JOUR_MAPPING.put("mercredi", "MERCREDI");
        JOUR_MAPPING.put("jeudi", "JEUDI");
        JOUR_MAPPING.put("vendredi", "VENDREDI");
        JOUR_MAPPING.put("samedi", "SAMEDI");
        JOUR_MAPPING.put("dimanche", "DIMANCHE");
    }

    private static final String[] JOURS_SEMAINE = {"LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"};
    private static final int[] JOURS_INDEX = {0, 1, 2, 3, 4, 5, 6};

    /**
     * Parse une chaîne d'horaires non structurée et retourne une liste de HoraireDTO structurés par jour.
     * 
     * Exemples de formats supportés :
     * - "du lundi au vendredi de 14h à 17h30, le samedi de 14h à 19h"
     * - "lundi-vendredi 14h-17h30, samedi 14h-19h"
     * - "lundi 09:00-12:00 et 14:00-18:00"
     * 
     * @param heuresOuverture texte brut des horaires
     * @return liste de HoraireDTO structurés
     */
    public List<HoraireDTO> parseHoraires(String heuresOuverture) {
        Map<String, HoraireDTO> horaireMap = new LinkedHashMap<>();
        
        if (heuresOuverture == null || heuresOuverture.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String texte = heuresOuverture.toLowerCase().trim();

        // Nettoyer le texte
        texte = texte.replaceAll("\\s+", " ");
        texte = texte.replaceAll("à", "à");
        texte = texte.replaceAll("-", "à");

        // Diviser par les séparateurs principaux (virgule, point-virgule, "et")
        String[] segments = texte.split("[,;]|\\s+et\\s+");

        for (String segment : segments) {
            segment = segment.trim();
            if (segment.isEmpty()) continue;

            parseSegment(segment, horaireMap);
        }

        // Convertir la map en liste triée
        List<HoraireDTO> resultat = new ArrayList<>();
        for (String jour : JOURS_SEMAINE) {
            if (horaireMap.containsKey(jour)) {
                resultat.add(horaireMap.get(jour));
            }
        }

        return resultat;
    }

    /**
     * Parse un segment d'horaire (ex: "du lundi au vendredi de 14h à 17h30")
     */
    private void parseSegment(String segment, Map<String, HoraireDTO> horaireMap) {
        // Extraire les jours et les heures
        Pattern pattern = Pattern.compile(
            "(?:du\\s+)?([a-z]+)(?:\\s+au\\s+([a-z]+)|(?:\\s*[à-]\\s*)?([a-z]+))?.*?(?:de\\s+)?([0-9]{1,2})h(?::?([0-9]{2}))?\\s*(?:à|-)\\s*([0-9]{1,2})h(?::?([0-9]{2}))?",
            Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(segment);

        if (matcher.find()) {
            String jourDebut = matcher.group(1);
            String jourFin = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
            
            int heureDebut = Integer.parseInt(matcher.group(4));
            int minuteDebut = matcher.group(5) != null ? Integer.parseInt(matcher.group(5)) : 0;
            
            int heureFin = Integer.parseInt(matcher.group(6));
            int minuteFin = matcher.group(7) != null ? Integer.parseInt(matcher.group(7)) : 0;

            LocalTime timeDebut = LocalTime.of(heureDebut, minuteDebut);
            LocalTime timeFin = LocalTime.of(heureFin, minuteFin);

            String jdDebut = JOUR_MAPPING.get(jourDebut);
            String jdFin = jourFin != null ? JOUR_MAPPING.get(jourFin) : jdDebut;

            if (jdDebut != null && jdFin != null) {
                // Ajouter les horaires pour tous les jours de la plage
                List<String> jours = getJoursBetween(jdDebut, jdFin);
                for (String jour : jours) {
                    HoraireDTO horaire = new HoraireDTO();
                    horaire.setJourSemaine(jour);
                    horaire.setHeureOuverture(timeDebut.format(TIME_FORMATTER));
                    horaire.setHeureFermeture(timeFin.format(TIME_FORMATTER));
                    horaireMap.put(jour, horaire);
                }
            }
        } else {
            // Essayer un format plus simple : "lundi 14h-17h30"
            Pattern simplePattern = Pattern.compile(
                "([a-z]+)\\s+([0-9]{1,2})h(?::?([0-9]{2}))?\\s*(?:à|-)\\s*([0-9]{1,2})h(?::?([0-9]{2}))?",
                Pattern.CASE_INSENSITIVE
            );
            
            Matcher simpleMatcher = simplePattern.matcher(segment);
            if (simpleMatcher.find()) {
                String jour = simpleMatcher.group(1);
                int heureDebut = Integer.parseInt(simpleMatcher.group(2));
                int minuteDebut = simpleMatcher.group(3) != null ? Integer.parseInt(simpleMatcher.group(3)) : 0;
                int heureFin = Integer.parseInt(simpleMatcher.group(4));
                int minuteFin = simpleMatcher.group(5) != null ? Integer.parseInt(simpleMatcher.group(5)) : 0;

                String jd = JOUR_MAPPING.get(jour);
                if (jd != null) {
                    HoraireDTO horaire = new HoraireDTO();
                    horaire.setJourSemaine(jd);
                    horaire.setHeureOuverture(LocalTime.of(heureDebut, minuteDebut).format(TIME_FORMATTER));
                    horaire.setHeureFermeture(LocalTime.of(heureFin, minuteFin).format(TIME_FORMATTER));
                    horaireMap.put(jd, horaire);
                }
            }
        }
    }

    /**
     * Retourne la liste des jours entre deux jours (inclus).
     */
    private List<String> getJoursBetween(String jourDebut, String jourFin) {
        List<String> resultat = new ArrayList<>();
        int indexDebut = Arrays.asList(JOURS_SEMAINE).indexOf(jourDebut);
        int indexFin = Arrays.asList(JOURS_SEMAINE).indexOf(jourFin);

        if (indexDebut == -1 || indexFin == -1) {
            return resultat;
        }

        if (indexDebut <= indexFin) {
            for (int i = indexDebut; i <= indexFin; i++) {
                resultat.add(JOURS_SEMAINE[i]);
            }
        } else {
            // Cas où on passe par la fin de semaine
            for (int i = indexDebut; i < JOURS_SEMAINE.length; i++) {
                resultat.add(JOURS_SEMAINE[i]);
            }
            for (int i = 0; i <= indexFin; i++) {
                resultat.add(JOURS_SEMAINE[i]);
            }
        }

        return resultat;
    }

    /**
     * Vérifie si une bibliothèque est ouverte pendant une plage horaire donnée.
     */
    public boolean isOpenDuring(List<HoraireDTO> horaires, LocalTime heureDebut, LocalTime heureFin, String jourSemaine) {
        if (horaires == null || horaires.isEmpty()) {
            return false;
        }

        for (HoraireDTO horaire : horaires) {
            if (horaire.getJourSemaine().equals(jourSemaine)) {
                LocalTime ouverture = LocalTime.parse(horaire.getHeureOuverture(), TIME_FORMATTER);
                LocalTime fermeture = LocalTime.parse(horaire.getHeureFermeture(), TIME_FORMATTER);

                // Vérifier si la plage demandée est couverte par les horaires
                return !ouverture.isAfter(heureDebut) && !fermeture.isBefore(heureFin);
            }
        }

        return false;
    }

    /**
     * Détermine si une bibliothèque est actuellement ouverte.
     */
    public boolean isCurrentlyOpen(List<HoraireDTO> horaires) {
        LocalTime now = LocalTime.now();
        String jourAujourdhui = JOURS_SEMAINE[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];

        return isOpenDuring(horaires, now, now, jourAujourdhui);
    }
}
