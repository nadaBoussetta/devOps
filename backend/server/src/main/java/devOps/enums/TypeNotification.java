package devOps.enums;

public enum TypeNotification {
    FERMETURE_BIBLIOTHEQUE("Fermeture imminente"),
    AFFLUENCE_FAIBLE("Affluence faible"),
    LIVRE_DISPONIBLE("Livre disponible"),
    NOUVELLE_BIBLIOTHEQUE("Nouvelle bibliothèque"),
    RECOMMANDATION("Recommandation personnalisée");

    private final String description;

    TypeNotification(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}