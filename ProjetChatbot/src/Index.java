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


        public int rechercherSortie(Integer sortie)
        {
            //{}=>{recherche dichotomique de sortie dans sorties (triée dans l'ordre croissant)
            // résultat = l'indice de sortie dans sorties si trouvé, - l'indice d'insertion si non trouvé }
            if (sorties.size() == 0){
                return 0;
            }

            if (sorties.get(sorties.size()-1) < sortie)
            {
                return -sorties.size();
            }
            else
            {
                int inf = 0;
                int sup = sorties.size() - 1;
                int m;
                while (inf < sup)
                {
                    m = (inf + sup) / 2;
                    if (sorties.get(m) >= sortie) {sup = m;}
                    else {inf = m + 1;}
                }
                if (sorties.get(sup) == sortie) {return sup;}
                else {return -sup;}
            }
        }


        public void ajouterSortie(Integer sortie) {
            //{}=>{insère sortie à la bonne place dans sorties (triée dans l'ordre croissant)
            // remarque : utilise rechercherSortie de EntreeIndex }
            int pos = rechercherSortie(sortie);
            if (pos < 0) {
                int indexInsertion = -pos;
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


    public int rechercherEntree(String entree)
    {
        //{}=>  {recherche dichotomique de entree dans table (triée dans l'ordre lexicographique des attributs entree des EntreeIndex) }
        //résultat =  l'indice de entree dans table si trouvé et -l'indice d'insertion sinon }
        if (table.isEmpty()) {
            return -1;
        }

        if (entree.compareTo(table.get(table.size()-1).entree) > 0) {
            return -(table.size()+1);
        }

        int inf = 0;
        int sup = table.size() - 1;
        int m;

        while (inf < sup) {
            m = (inf + sup) / 2;
            if (entree.compareTo(table.get(m).entree) <= 0) {
                sup = m;
            } else {
                inf = m + 1;
            }
        }

        if (entree.compareTo(table.get(sup).entree) == 0) {
            return sup;
        } else {
            return -(sup + 1);
        }
    }


    public void ajouterSortieAEntree(String entree, Integer sortie) {
        // {}=>{ajoute l'entier sortie dans les sorties associées à l'entrée entree
        // si l'entrée entree n'existe pas elle est créée.
        // ne fait rien si sortie était déjà présente dans ses sorties.
        // remarque : utilise la fonction rechercherEntree de Index et la procedure ajouterSortie de EntreeIndex}
        int pos = rechercherEntree(entree);
        EntreeIndex ei;
//        if (pos == 0 && table.size() == 0){
//            ei = new EntreeIndex(entree);
//            table.add(0, ei);
//        }

        if (pos >= 0) {
            ei = table.get(pos);
        } else {
            ei = new EntreeIndex(entree);
            int indexInsertion = -(pos+1);
            table.add(indexInsertion, ei);
        }
        ei.ajouterSortie(sortie);
    }


    public ArrayList<Integer> rechercherSorties(String entree) {
        // {}=>{résultat = les sorties associées à l'entrée entree
        // si l'entrée entree n'existe pas, une ArrayList vide est retournée.
        // remarque : utilise la fonction rechercherEntree de Index}

        System.out.println("DEBUG : entree = " + entree);

        int i = rechercherEntree(entree);

        System.out.println("DEBUG : i = " + i);

        if (i >= 0){
            System.out.println("DEBUG : i is positive : table.get(i).getSorties() = " + table.get(i).getSorties());
            return table.get(i).getSorties();
        } else {
            System.out.println("DEBUG : i is NOT positive : return empty list = " + ArrayList<Integer>());
            return new ArrayList<Integer>();
        }
    }

    public void afficher() {
        // {}=>{affiche la table de l'index}
        for (int i = 0; i < table.size(); i++) {
            System.out.println(this.table.get(i));
        }
    }


}
