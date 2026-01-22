import java.util.ArrayList;

public class Index {

    // Classe interne : Une entrée de l'index (un mot) associée à une liste d'identifiants (sorties)
    private class EntreeIndex implements Comparable<EntreeIndex> {
        private String entree;
        private ArrayList<Integer> sorties;

        public EntreeIndex(String entree) {
            this.entree = entree;
            this.sorties = new ArrayList<>();
        }

        public ArrayList<Integer> getSorties() {
            return sorties;
        }

        // Recherche dichotomique d'un entier dans la liste triée 'sorties'
        // Retourne l'index si trouvé, sinon -(point d'insertion) - 1
        public int rechercherSortie(Integer valeur) {
            int inf = 0;
            int sup = sorties.size() - 1;

            while (inf <= sup) {
                int m = (inf + sup) / 2;
                int valM = sorties.get(m);

                if (valM == valeur) return m;
                if (valM < valeur) inf = m + 1;
                else sup = m - 1;
            }
            return -(inf + 1); // Code pour dire "pas trouvé, mais à insérer ici"
        }

        // Ajoute un identifiant de manière triée
        public void ajouterSortie(Integer identifiant) {
            int pos = rechercherSortie(identifiant);
            // Si pos < 0, l'élément n'existe pas, on l'ajoute au bon endroit
            if (pos < 0) {
                int indexInsertion = -(pos + 1);
                sorties.add(indexInsertion, identifiant);
            }
        }

        @Override
        public int compareTo(EntreeIndex o) {
            return this.entree.compareTo(o.entree);
        }

        @Override
        public String toString() {
            return entree + " => " + sorties;
        }
    }

    // L'index est une liste d'EntreeIndex, triée par ordre alphabétique des mots
    private ArrayList<EntreeIndex> table;

    public Index() {
        this.table = new ArrayList<>();
    }

    // Recherche dichotomique de l'entrée (le mot) dans la table
    public int rechercherEntree(String mot) {
        int inf = 0;
        int sup = table.size() - 1;

        while (inf <= sup) {
            int m = (inf + sup) / 2;
            EntreeIndex element = table.get(m);
            int comp = mot.compareTo(element.entree);

            if (comp == 0) return m; // Trouvé
            if (comp > 0) inf = m + 1;
            else sup = m - 1;
        }
        return -(inf + 1); // Pas trouvé
    }

    // Ajoute une association mot -> identifiant
    public void ajouterSortieAEntree(String mot, Integer id) {
        int pos = rechercherEntree(mot);
        EntreeIndex entreeIndex;

        if (pos >= 0) {
            // Le mot existe déjà
            entreeIndex = table.get(pos);
        } else {
            // Le mot n'existe pas, on le crée et on l'insère au bon endroit
            entreeIndex = new EntreeIndex(mot);
            int indexInsertion = -(pos + 1);
            table.add(indexInsertion, entreeIndex);
        }
        // On ajoute l'identifiant à ce mot
        entreeIndex.ajouterSortie(id);
    }

    // Récupère la liste des IDs associés à un mot
    public ArrayList<Integer> rechercherSorties(String mot) {
        int pos = rechercherEntree(mot);
        if (pos >= 0) {
            return table.get(pos).getSorties();
        } else {
            return new ArrayList<>(); // Retourne liste vide si mot inconnu
        }
    }

    public void afficher() {
        for (EntreeIndex e : table) {
            System.out.println(e);
        }
    }
}
