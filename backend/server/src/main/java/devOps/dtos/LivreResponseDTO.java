package devOps.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse pour un livre.
 *
 * Champs ajoutés par rapport à l'original :
 *   - disponibleIDF  : true si le livre est référencé dans le Sudoc IDF
 *   - lienSudoc      : URL vers la fiche Sudoc (localisations en bibliothèques)
 *   - ppn            : identifiant de notice Sudoc
 *   - sudocVerifie   : false si la vérification n'a pas pu être faite (timeout…)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LivreResponseDTO {

    // ── Champs originaux ────────────────────────────────────────
    private String  titre;
    private String  auteur;
    private String  bibliotheque;
    private Boolean disponible;
    private String  cote;
    private String  isbn;

    // ── Nouveaux champs IDF / Sudoc ─────────────────────────────
    /** Le livre est-il référencé dans le réseau Sudoc (bibliothèques IDF) ? */
    private Boolean disponibleIDF = false;

    /** URL vers la fiche Sudoc avec la liste des bibliothèques qui le détiennent */
    private String lienSudoc;

    /** Identifiant de notice Sudoc (PPN), utile pour des appels ultérieurs */
    private String ppn;

    /**
     * false si la vérification Sudoc n'a pas pu aboutir
     * (ISBN manquant, timeout, erreur réseau).
     * Permet au frontend de distinguer "non disponible" de "non vérifié".
     */
    private Boolean sudocVerifie = false;

    // ── Constructeur de compatibilité (identique à l'original) ──
    public LivreResponseDTO(String titre, String auteur, String bibliotheque,
                            Boolean disponible, String cote, String isbn) {
        this.titre       = titre;
        this.auteur      = auteur;
        this.bibliotheque = bibliotheque;
        this.disponible  = disponible;
        this.cote        = cote;
        this.isbn        = isbn;
        this.disponibleIDF = false;
        this.sudocVerifie  = false;
    }
}