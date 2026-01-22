import java.util.ArrayList;

public class Index {

    // un index est un vecteur d'EntreeIndex
    private class EntreeIndex {

        // une EntreeIndex associe un vecteur trié d'entiers (sorties) à une String (entree)
        private String entree;
        private ArrayList<Integer> sorties;

        public ArrayList<Integer> getSorties() {
            return sorties;
        }


        //constructeur
        public EntreeIndex(String entree) {
            this.entree = entree;
            sorties = new ArrayList<>();
        }


        public int rechercherSortie(Integer sortie) {
            //{}=>{recherche dichotomique de sortie dans sorties (triée dans l'ordre croissant)
            // résultat = l'indice de sortie dans sorties si trouvé, - l'indice d'insertion si non trouvé }
            int inf = 0;
            int sup = sorties.size() - 1;

            while (inf <= sup) {
                int m = (inf + sup) / 2;
                int valM = sorties.get(m);

                if (valM == sortie) return m;
                if (valM < sortie) inf = m + 1;
                else sup = m - 1;
            }
            return -(inf + 1);
        }


        public void ajouterSortie(Integer sortie) {
            //{}=>{insère sortie à la bonne place dans sorties (triée dans l'ordre croissant)
            // remarque : utilise rechercherSortie de EntreeIndex }
            int pos = rechercherSortie(sortie);
            // Si pos < 0, l'élément n'existe pas, on l'ajoute au bon endroit
            if (pos < 0) {
                int indexInsertion = -(pos + 1);
                sorties.add(indexInsertion, sortie);
            }
        }


        @Override
        public String toString() {
            return entree + "=>" + sorties;
        }
    }

    //Un vecteur d'EntreeIndex trié sur l'attribut entree (String) des EntreeIndex
    private ArrayList<EntreeIndex> table;

    //constructeur
    public Index() {
        table = new ArrayList<>();
    }


    public int rechercherEntree(String entree) {
        //{}=>  {recherche dichotomique de entree dans table (triée dans l'ordre lexicographique des attributs entree des EntreeIndex) }
        //résultat =  l'indice de entree dans table si trouvé et -l'indice d'insertion sinon }
        int inf = 0;
        int sup = table.size() - 1;

        while (inf <= sup) {
            int m = (inf + sup) / 2;
            EntreeIndex element = table.get(m);
            int comp = entree.compareTo(element.entree);

            if (comp == 0) return m; // Trouvé
            if (comp > 0) inf = m + 1;
            else sup = m - 1;
        }
        return -(inf + 1);
    }


    public void ajouterSortieAEntree(String entree, Integer sortie) {
        // {}=>{ajoute l'entier sortie dans les sorties associées à l'entrée entree
        // si l'entrée entree n'existe pas elle est créée.
        // ne fait rien si sortie était déjà présente dans ses sorties.
        // remarque : utilise la fonction rechercherEntree de Index et la procedure ajouterSortie de EntreeIndex}
        int pos = rechercherEntree(entree);
        EntreeIndex entreeIndex;

        if (pos >= 0) {
            entreeIndex = table.get(pos);
        } else {
            entreeIndex = new EntreeIndex(entree);
            int indexInsertion = -(pos + 1);
            table.add(indexInsertion, entreeIndex);
        }
        entreeIndex.ajouterSortie(sortie);
    }


    public ArrayList<Integer> rechercherSorties(String entree) {
        // {}=>{résultat = les sorties associées à l'entrée entree
        // si l'entrée entree n'existe pas, une ArrayList vide est retournée.
        // remarque : utilise la fonction rechercherEntree de Index}
        int pos = rechercherEntree(entree);
        if (pos >= 0) {
            return table.get(pos).getSorties();
        } else {
            return new ArrayList<>();
        }
    }

    public void afficher() {
        // {}=>{affiche la table de l'index}
        for (int i = 0; i < table.size(); i++) {
            System.out.println(this.table.get(i));
        }
    }


}
