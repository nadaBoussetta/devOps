package devOps.enums;

public enum TypeNotification {
    FERMETURE_BIBLIOTHEQUE("Fermeture imminente"),
    AFFLUENCE_FAIBLE("Affluence faible"),
    LIVRE_DISPONIBLE("Livre disponible"),
    NOUVELLE_BIBLIOTHEQUE("Nouvelle bibliothèque"),
    RECOMMANDATION("Recommandation personnalisée"),
    RAPPEL_LECTURE("Rappel de lecture"),
    NOUVELLE_PUBLICATION("Nouvelle publication"),
    RECHERCHE_LIVRE("Suggestion de livre"),
    SESSION_REMINDER("Rappel de session"),
    OBJECTIF_ATTEINT("Objectif atteint");

    private final String description;

    TypeNotification(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
