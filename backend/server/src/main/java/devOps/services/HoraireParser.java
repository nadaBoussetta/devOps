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

    /**
     * Parse une chaîne d'horaires non structurée et retourne une liste de HoraireDTO structurés par jour.
     * 
     * @param heuresOuverture texte brut des horaires
     * @return liste de HoraireDTO structurés
     */
    public List<HoraireDTO> parseHoraires(String heuresOuverture) {
        List<HoraireDTO> resultat = new ArrayList<>();
        
        if (heuresOuverture == null || heuresOuverture.trim().isEmpty()) {
            return resultat;
        }

        String texte = heuresOuverture.toLowerCase().trim();

        // Nettoyer le texte
        texte = texte.replaceAll("\\s+", " ");
        texte = texte.replaceAll("à", "à");
        texte = texte.replaceAll("-", "à");

        // Diviser par les séparateurs principaux (virgule, point-virgule)
        // On ne divise plus par "et" car il peut séparer des créneaux
        String[] segments = texte.split("[,;]");

        for (String segment : segments) {
            segment = segment.trim();
            if (segment.isEmpty()) continue;

            parseSegment(segment, resultat);
        }

        // Trier le résultat par jour de la semaine
        resultat.sort(Comparator.comparingInt(h -> Arrays.asList(JOURS_SEMAINE).indexOf(h.getJourSemaine())));

        return resultat;
    }

    /**
     * Parse un segment d'horaire (ex: "du lundi au vendredi de 14h à 17h30")
     */
    private void parseSegment(String segment, List<HoraireDTO> resultat) {
        // 1. Extraire les jours
        Pattern jourPattern = Pattern.compile("(?:du\\s+)?([a-z]+)(?:\\s+au\\s+([a-z]+)|(?:\\s*[à-]\\s*)?([a-z]+))?", Pattern.CASE_INSENSITIVE);
        Matcher jourMatcher = jourPattern.matcher(segment);
        
        if (jourMatcher.find()) {
            String jourDebutStr = jourMatcher.group(1);
            String jourFinStr = jourMatcher.group(2) != null ? jourMatcher.group(2) : jourMatcher.group(3);
            
            String jdDebut = JOUR_MAPPING.get(jourDebutStr);
            String jdFin = jourFinStr != null ? JOUR_MAPPING.get(jourFinStr) : jdDebut;
            
            if (jdDebut != null) {
                List<String> jours = getJoursBetween(jdDebut, jdFin != null ? jdFin : jdDebut);
                
                // 2. Extraire tous les créneaux horaires dans ce segment
                Pattern heurePattern = Pattern.compile("([0-9]{1,2})h(?::?([0-9]{2}))?\\s*(?:à|-)\\s*([0-9]{1,2})h(?::?([0-9]{2}))?", Pattern.CASE_INSENSITIVE);
                Matcher heureMatcher = heurePattern.matcher(segment);
                
                while (heureMatcher.find()) {
                    int hDebut = Integer.parseInt(heureMatcher.group(1));
                    int mDebut = heureMatcher.group(2) != null ? Integer.parseInt(heureMatcher.group(2)) : 0;
                    int hFin = Integer.parseInt(heureMatcher.group(3));
                    int mFin = heureMatcher.group(4) != null ? Integer.parseInt(heureMatcher.group(4)) : 0;
                    
                    LocalTime timeDebut = LocalTime.of(hDebut, mDebut);
                    LocalTime timeFin = LocalTime.of(hFin, mFin);
                    
                    for (String jour : jours) {
                        HoraireDTO horaire = new HoraireDTO();
                        horaire.setJourSemaine(jour);
                        horaire.setHeureOuverture(timeDebut.format(TIME_FORMATTER));
                        horaire.setHeureFermeture(timeFin.format(TIME_FORMATTER));
                        resultat.add(horaire);
                    }
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

                // Si la plage demandée est incluse dans l'un des créneaux, elle est ouverte
                if (!ouverture.isAfter(heureDebut) && !fermeture.isBefore(heureFin)) {
                    return true;
                }
            }
        }

        return false;
    }
}
