import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * ============================================================
 * CLASSE Utilitaire
 * ============================================================
 *
 * Cette classe regroupe toutes les méthodes "outils" du projet.
 *
 * ➜ Elle ne stocke AUCUN état :
 *    - toutes les méthodes sont statiques
 *    - elle sert uniquement à effectuer des calculs
 *
 * Rôles principaux :
 *  1) Manipulation de chaînes (tri, recherche)
 *  2) Construction de l’index sur le contenu (étape 1)
 *  3) Construction des réponses candidates
 *  4) Calcul et gestion des formes (étape 2)
 */
public class Utilitaire {

    /* ============================================================
     *  PARTIE 1 — OUTILS GÉNÉRAUX SUR LES CHAÎNES
     * ============================================================
     */

    /**
     * Trie un tableau de chaînes par ordre lexicographique croissant.
     *
     * Pourquoi trier ?
     * → pour permettre ensuite une recherche dichotomique
     * → plus rapide qu’une recherche linéaire
     *
     * Algorithme utilisé : TRI PAR INSERTION
     * - simple à comprendre
     * - suffisant pour des tableaux de taille modérée
     */
    public static void trierChaines(String[] tab) {

        // On commence au second élément
        for (int i = 1; i < tab.length; i++) {

            // Élément à insérer au bon endroit
            String cle = tab[i];

            // j va parcourir la partie déjà triée
            int j = i - 1;

            // Tant que j est valide et que l’élément est plus grand que la clé
            while (j >= 0 && tab[j].compareTo(cle) > 0) {
                tab[j + 1] = tab[j]; // décalage vers la droite
                j--;
            }

            // Insertion de la clé à la bonne position
            tab[j + 1] = cle;
        }
    }

    /**
     * Recherche dichotomique d’une chaîne dans un tableau trié.
     *
     * ⚠️ PRÉCONDITION :
     * → le tableau doit être trié avant l’appel
     *
     * @return true si la chaîne est présente, false sinon
     */
    public static boolean existeChaineDicho(String[] tab, String s) {

        int inf = 0;
        int sup = tab.length - 1;

        // Normalisation pour éviter les problèmes de casse
        s = s.toLowerCase();

        while (inf <= sup) {
            int milieu = (inf + sup) / 2;

            int comparaison = s.compareTo(tab[milieu]);

            if (comparaison == 0) {
                return true; // trouvé
            }

            if (comparaison > 0) {
                inf = milieu + 1;
            } else {
                sup = milieu - 1;
            }
        }
        return false; // non trouvé
    }

    /* ============================================================
     *  PARTIE 2 — ÉTAPE 1 : INDEX SUR LE CONTENU (THÈME)
     * ============================================================
     */

    /**
     * Construit l’index sur le contenu.
     *
     * Principe :
     *  - on parcourt toutes les réponses
     *  - on découpe chaque réponse en mots
     *  - on ignore les mots-outils
     *  - on associe chaque mot significatif à l’identifiant
     *    de la réponse dans laquelle il apparaît
     *
     * Exemple :
     *  "Le téléphone a été inventé en 1876"
     *  → "telephone" → [id]
     *  → "inventer"  → [id]
     */
    public static Index constructionIndexReponses(
            String[] reponses,
            String[] motsOutils) {

        // Création de l’index vide
        Index index = new Index();

        // Parcours de toutes les réponses
        for (int idReponse = 0; idReponse < reponses.length; idReponse++) {

            // Découpage de la réponse en mots
            StringTokenizer tokenizer =
                    new StringTokenizer(
                            reponses[idReponse].toLowerCase(),
                            " .,;:!?"
                    );

            // Parcours des mots de la réponse
            while (tokenizer.hasMoreTokens()) {

                // Récupération du mot
                String mot = tokenizer.nextToken();

                // Normalisation via le thésaurus
                mot = Thesaurus.getFormeCanonique(mot);

                // Si le mot n’est PAS un mot-outil
                if (!existeChaineDicho(motsOutils, mot)) {

                    // Association mot → id de la réponse
                    index.ajouterSortieAEntree(mot, idReponse);
                }
            }
        }

        return index;
    }

    /**
     * Construit la liste des réponses candidates (ÉTAPE 1).
     *
     * Une réponse est candidate si :
     *  → elle contient TOUS les mots significatifs de la question
     *
     * Méthode :
     *  1) On récupère les listes d’IDs pour chaque mot
     *  2) On fusionne toutes les listes
     *  3) On garde les IDs apparaissant autant de fois
     *     qu’il y a de mots significatifs
     */
    public static ArrayList<Integer> constructionReponsesCandidates(
            String question,
            Index indexThemes,
            String[] motsOutils) {

        // Liste de listes d’identifiants
        ArrayList<ArrayList<Integer>> listes = new ArrayList<>();

        // Découpage de la question
        StringTokenizer tokenizer =
                new StringTokenizer(question.toLowerCase(), " .,;:!?");

        int nbMotsSignificatifs = 0;

        while (tokenizer.hasMoreTokens()) {
            String mot = tokenizer.nextToken();
            mot = Thesaurus.getFormeCanonique(mot);

            // On ignore les mots-outils
            if (!existeChaineDicho(motsOutils, mot)) {

                // On récupère les réponses contenant ce mot
                listes.add(indexThemes.rechercherSorties(mot));
                nbMotsSignificatifs++;
            }
        }

        // Fusion de toutes les listes
        ArrayList<Integer> fusion = new ArrayList<>();
        for (ArrayList<Integer> l : listes) {
            fusion.addAll(l);
        }

        // Sélection finale
        ArrayList<Integer> resultat = new ArrayList<>();

        for (Integer id : fusion) {

            int compteur = 0;
            for (Integer x : fusion) {
                if (id.equals(x)) compteur++;
            }

            // Le bon ID apparaît autant de fois que de mots significatifs
            if (compteur == nbMotsSignificatifs && !resultat.contains(id)) {
                resultat.add(id);
            }
        }

        return resultat;
    }

    /* ============================================================
     *  PARTIE 3 — ÉTAPE 2 : GESTION DES FORMES
     * ============================================================
     */

    /**
     * Calcule la forme grammaticale d’une phrase.
     *
     * La forme est définie comme :
     *  → la liste des mots-outils
     *  → accompagnés de leur position
     *
     * Exemple :
     *  "Qui a inventé le téléphone ?"
     *  → ["qui_0", "a_1"]
     */
    public static ArrayList<String> calculForme(
            String phrase,
            String[] motsOutils) {

        ArrayList<String> forme = new ArrayList<>();

        String[] mots = phrase.toLowerCase().split(" ");

        for (int i = 0; i < mots.length; i++) {

            // Nettoyage du mot (ponctuation)
            String mot = mots[i].replaceAll("[^a-zàâéèêîôûç]", "");

            // Si c’est un mot-outil, on l’ajoute à la forme
            if (existeChaineDicho(motsOutils, mot)) {
                forme.add(mot + "_" + i);
            }
        }
        return forme;
    }

    /**
     * Construit la table des formes à partir du fichier
     * questions-réponses idéales.
     *
     * Chaque ligne du fichier fournit UNE forme possible.
     */
    public static ArrayList<ArrayList<String>> constructionTableFormes(
            String[] questionsReponses,
            String[] motsOutils) {

        ArrayList<ArrayList<String>> formes = new ArrayList<>();

        for (String ligne : questionsReponses) {

            // Séparation question / réponse
            String question = ligne.split("\\?")[0];

            // Calcul de la forme de la question
            formes.add(calculForme(question, motsOutils));
        }

        return formes;
    }

    /**
     * Construit l’index des formes.
     *
     * Entrée :
     *  mot-outil_position
     *
     * Sortie :
     *  identifiant de la forme
     */
    public static Index constructionIndexForme(
            ArrayList<ArrayList<String>> formes) {

        Index index = new Index();

        for (int idForme = 0; idForme < formes.size(); idForme++) {
            for (String elementForme : formes.get(idForme)) {
                index.ajouterSortieAEntree(elementForme, idForme);
            }
        }
        return index;
    }
}
