import java.util.ArrayList;

/**
 * ===========================
 * CLASSE Index
 * ===========================
 *
 * Cette classe implémente une structure d’index générique.
 *
 * Un index permet d’associer :
 *  - une clé (String)
 *  - à une liste d’identifiants (entiers)
 *
 * Exemple :
 *  "telephone" → [0, 2, 5]
 *
 * Dans ce projet, on utilise cette classe pour :
 *  - l’index sur le contenu (thème)
 *  - l’index sur la forme des questions
 */
public class Index {

    /**
     * ===========================
     * CLASSE INTERNE EntreeIndex
     * ===========================
     *
     * Représente UNE entrée de l’index :
     *  - une clé (mot ou forme)
     *  - une liste d’IDs associés
     *
     * Exemple :
     *  entree = "telephone"
     *  sorties = [0, 2, 5]
     */
    private class EntreeIndex implements Comparable<EntreeIndex> {

        /** La clé de l’entrée (mot, forme, etc.) */
        private String entree;

        /**
         * Liste des identifiants associés à cette entrée.
         * Cette liste est TOUJOURS triée.
         */
        private ArrayList<Integer> sorties;

        /**
         * Constructeur :
         * initialise une entrée avec une liste vide.
         */
        public EntreeIndex(String entree) {
            this.entree = entree;
            this.sorties = new ArrayList<>();
        }

        /**
         * Getter sur les sorties.
         */
        public ArrayList<Integer> getSorties() {
            return sorties;
        }

        /**
         * Recherche dichotomique d’un identifiant dans la liste des sorties.
         *
         * Pourquoi une recherche dichotomique ?
         * → car la liste est triée
         * → plus rapide qu’une recherche linéaire
         *
         * @return
         *  - index >= 0 si trouvé
         *  - -(position d’insertion + 1) sinon
         */
        public int rechercherSortie(Integer id) {
            int inf = 0;
            int sup = sorties.size() - 1;

            while (inf <= sup) {
                int milieu = (inf + sup) / 2;
                int valeurMilieu = sorties.get(milieu);

                if (valeurMilieu.equals(id)) {
                    return milieu;
                }

                if (valeurMilieu < id) {
                    inf = milieu + 1;
                } else {
                    sup = milieu - 1;
                }
            }

            // Convention Java pour indiquer la position d’insertion
            return -(inf + 1);
        }

        /**
         * Ajoute un identifiant à la liste des sorties.
         *
         * - conserve le tri
         * - évite les doublons
         */
        public void ajouterSortie(Integer id) {
            int position = rechercherSortie(id);

            // Si l'identifiant n'est pas déjà présent
            if (position < 0) {
                int indexInsertion = -(position + 1);
                sorties.add(indexInsertion, id);
            }
        }

        /**
         * Méthode obligatoire car EntreeIndex implémente Comparable.
         *
         * Elle permet de comparer deux entrées lexicographiquement,
         * ce qui rend possible le tri de la table principale.
         */
        @Override
        public int compareTo(EntreeIndex autre) {
            return this.entree.compareTo(autre.entree);
        }

        /**
         * Méthode utilitaire pour l’affichage (debug).
         */
        @Override
        public String toString() {
            return entree + " → " + sorties;
        }
    }

    /**
     * ===========================
     * TABLE PRINCIPALE DE L’INDEX
     * ===========================
     *
     * Liste triée d’EntreeIndex.
     */
    private ArrayList<EntreeIndex> table;

    /**
     * Constructeur :
     * crée un index vide.
     */
    public Index() {
        table = new ArrayList<>();
    }

    /**
     * Recherche dichotomique d’une entrée dans la table.
     *
     * @param mot clé recherchée
     * @return index de l’entrée ou position d’insertion négative
     */
    public int rechercherEntree(String mot) {
        int inf = 0;
        int sup = table.size() - 1;

        while (inf <= sup) {
            int milieu = (inf + sup) / 2;
            EntreeIndex e = table.get(milieu);

            int comparaison = mot.compareTo(e.entree);

            if (comparaison == 0) return milieu;
            if (comparaison > 0) inf = milieu + 1;
            else sup = milieu - 1;
        }

        return -(inf + 1);
    }

    /**
     * Associe un mot à un identifiant.
     *
     * - crée l’entrée si elle n’existe pas
     * - ajoute l’identifiant à la liste correspondante
     */
    public void ajouterSortieAEntree(String mot, Integer id) {
        int position = rechercherEntree(mot);
        EntreeIndex entreeIndex;

        if (position >= 0) {
            // L’entrée existe déjà
            entreeIndex = table.get(position);
        } else {
            // Nouvelle entrée à insérer au bon endroit
            entreeIndex = new EntreeIndex(mot);
            table.add(-(position + 1), entreeIndex);
        }

        entreeIndex.ajouterSortie(id);
    }

    /**
     * Retourne la liste des identifiants associés à un mot.
     * Si le mot est absent, retourne une liste vide.
     */
    public ArrayList<Integer> rechercherSorties(String mot) {
        int pos = rechercherEntree(mot);
        return (pos >= 0) ? table.get(pos).getSorties() : new ArrayList<>();
    }

    /**
     * Affiche l’index (utile pour vérifier son contenu).
     */
    public void afficher() {
        for (EntreeIndex e : table) {
            System.out.println(e);
        }
    }
}
