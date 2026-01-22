/**
 * ============================================================
 * CLASSE Thesaurus
 * ============================================================
 *
 * Cette classe implémente un thésaurus très simple.
 *
 * Son rôle est de :
 *  - regrouper des mots ayant le même sens
 *  - ramener ces mots à une forme canonique
 *
 * Cela permet au chatbot de :
 *  - reconnaître des variantes d’un même mot
 *  - améliorer la pertinence des réponses
 *
 * ⚠️ IMPORTANT :
 * Cette classe respecte volontairement un modèle SIMPLE,
 * conforme à l’énoncé de la SAE (pas de NLP avancé).
 */
public class Thesaurus {

    /* ============================================================
     *  STRUCTURE DU THÉSAURUS
     * ============================================================
     */

    /**
     * Le thésaurus est représenté par un tableau 2D de chaînes.
     *
     * Chaque sous-tableau correspond à un groupe de synonymes.
     *
     * Convention :
     *  - le PREMIER mot du groupe est la forme canonique
     *  - les suivants sont des variantes acceptées
     *
     * Exemple :
     *  {"inventer", "inventé", "invention"}
     *
     * → tous ces mots seront normalisés en "inventer"
     */
    private static final String[][] thesaurus = {

        {"inventer", "inventé", "invention"},
        {"decouvrir", "découvrir", "découvert"},
        {"ecrire", "écrire", "écrit"},
        {"fonder", "fondé", "fondation"},
        {"naître", "né", "naissance"}
    };

    /* ============================================================
     *  MÉTHODE PRINCIPALE DU THÉSAURUS
     * ============================================================
     */

    /**
     * Retourne la forme canonique d’un mot.
     *
     * Fonctionnement :
     *  1) on met le mot en minuscules
     *  2) on parcourt tous les groupes du thésaurus
     *  3) si le mot est trouvé dans un groupe :
     *     → on retourne le premier mot du groupe
     *  4) sinon, on retourne le mot tel quel
     *
     * @param mot mot à normaliser
     * @return forme canonique du mot
     */
    public static String getFormeCanonique(String mot) {

        // Sécurité : si le mot est null, on retourne null
        if (mot == null) {
            return null;
        }

        // Normalisation de base
        mot = mot.toLowerCase().trim();

        // Parcours des groupes de synonymes
        for (String[] groupe : thesaurus) {

            // Parcours des mots du groupe
            for (String synonyme : groupe) {

                // Si le mot correspond à une variante
                if (mot.equals(synonyme)) {

                    // Retour de la forme canonique
                    return groupe[0];
                }
            }
        }

        // Si le mot n’appartient à aucun groupe
        return mot;
    }
}
