import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Thesaurus {

    private class EntreeSortie implements Comparable<EntreeSortie> {
        private String entree; // un mot
        private String sortie; // sa forme canonique

        public EntreeSortie(String entree, String sortie) {
            this.entree = entree;
            this.sortie = sortie;
        }

        public int compareTo(EntreeSortie o) {
            return this.entree.compareTo(o.entree);
        }
    }

    private ArrayList<EntreeSortie> table;

    public Thesaurus(String nomFichier) {
        //{}=>{ constructeur créant et initialisant l'attribut table à partir du contenu du fichier dont le nom est passé en paramètre, puis triant la table
        // en utilisant la méthode compareTo d'EntreeSortie
        // remarque 1 : utilise ajouterEntreeSortie et trierEntreesSorties
        // remarque 2 : pour la lecture du fichier, inspirez-vous de lireMotsOutils de Utilitaire
        // remarque 3 : pour les traitements de la chaîne lue, utilisez les méthodes indexOf,substring de String
        table = new ArrayList<>();
        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                int index = ligne.indexOf(":");
                if (index != -1)
                {
                    String entree = ligne.substring(0, index);
                    String sortie = ligne.substring(index+1);
                    ajouterEntreeSortie(entree,sortie);
                }
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        trierEntreesSorties(table);
    }

    public void ajouterEntreeSortie(String entree, String sortie)
    {
        //{}=>{ajoute à la fin de la table une nouvelle EntreeSortie avec les attributs entree et sortie}
        EntreeSortie i = new EntreeSortie(entree,sortie);
        table.add(i);
    }


    public String rechercherSortiePourEntree(String entree) {
        // {l'attribut table du thesaurus est trié sur l'attribut entree des Entree-Sortie}=>
        // {résultat = la forme canonique associée à entree dans le thésaurus si l'entrée entree existe,
        // entree elle-même si elle n'existe pas. La recherche doit être dichotomique.
        // remarque : utilise compareTo de EntreeSortie }
        int inf = 0, sup = table.size()-1, m;
        while (inf < sup) {
            m = (inf + sup) / 2;
            if (entree.compareTo(table.get(m).entree) <= 0) {
                sup = m;
            } else {
                inf = m + 1;
            }
        }
        if (entree.compareTo(table.get(sup).entree) == 0) {
            return table.get(sup).sortie;
        } else {
            return entree;
        }

    }

    static void trierEntreesSorties(ArrayList<EntreeSortie> v)
    {
        //{} => {trie v sur la base de la méthode compareTo de EntreeSortie}
        int n = v.size();
        for (int i = 0; i < n-1; i++)
        {
            int minIndex = i;
            for (int j = i + 1; j < n; j++)
            {
                if (v.get(j).compareTo(v.get(minIndex)) < 0)
                {
                    minIndex = j;
                }
            }
            v.set(minIndex, v.get(i));
            v.set(i, v.get(minIndex));
        }
    }
}
