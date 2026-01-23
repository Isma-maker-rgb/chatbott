import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Thesaurus {

    private class EntreeSortie implements Comparable<EntreeSortie> {
        private String entree; // un mot (ex: "peinte", "appellent")
        private String sortie; // sa forme canonique (ex: "peint", "nomme")

        public EntreeSortie(String entree, String sortie) {
            this.entree = entree;
            this.sortie = sortie;
        }

        // Compare deux EntreeSortie sur la base de leur attribut "entree"
        // Permet de trier la table du thésaurus alphabétiquement
        public int compareTo(EntreeSortie o) {
            return this.entree.compareTo(o.entree);
        }
    }

    // Table triée contenant toutes les paires (mot -> forme canonique)
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

            // Lire chaque ligne du fichier thesaurus.txt
            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                
                // Trouver la position du ":"
                int index = ligne.indexOf(":");
                
                if (index != -1) {
                    // Extraire la partie avant ":" (le mot)
                    String entree = ligne.substring(0, index);
                    // Extraire la partie après ":" (sa forme canonique)
                    String sortie = ligne.substring(index + 1);
                    
                    // Ajouter cette paire à la table
                    ajouterEntreeSortie(entree, sortie);
                }
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // IMPORTANT: Trier la table pour permettre la recherche dichotomique
        trierEntreesSorties(table);
    }

    public void ajouterEntreeSortie(String entree, String sortie) {
        //{}=>{ajoute à la fin de la table une nouvelle EntreeSortie avec les attributs entree et sortie}
        EntreeSortie nouvelleEntree = new EntreeSortie(entree, sortie);
        table.add(nouvelleEntree);
    }

    public String rechercherSortiePourEntree(String entree) {
        // {l'attribut table du thesaurus est trié sur l'attribut entree des Entree-Sortie}=>
        // {résultat = la forme canonique associée à entree dans le thésaurus si l'entrée entree existe,
        // entree elle-même si elle n'existe pas. La recherche doit être dichotomique.
        // remarque : utilise compareTo de EntreeSortie }
        
        // Si la table est vide, retourner le mot tel quel
        if (table.isEmpty()) {
            return entree;
        }
        
        // Recherche dichotomique dans la table triée
        int inf = 0;
        int sup = table.size() - 1;
        
        while (inf <= sup) {
            int m = (inf + sup) / 2;
            int comparaison = entree.compareTo(table.get(m).entree);
            
            if (comparaison == 0) {
                // Trouvé! Retourner la forme canonique
                return table.get(m).sortie;
            } else if (comparaison < 0) {
                // Le mot cherché est avant m
                sup = m - 1;
            } else {
                // Le mot cherché est après m
                inf = m + 1;
            }
        }
        
        // Non trouvé dans le thésaurus, retourner le mot original
        return entree;
    }

    static void trierEntreesSorties(ArrayList<EntreeSortie> v) {
        //{} => {trie v sur la base de la méthode compareTo de EntreeSortie}
        // Utilise un tri par sélection
        
        int n = v.size();
        
        // Pour chaque position i
        for (int i = 0; i < n - 1; i++) {
            // Trouver l'indice du minimum dans la partie non triée
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (v.get(j).compareTo(v.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }
            
            // CORRECTION DU BUG: Échanger correctement les éléments
            // Sauvegarder l'élément minimum
            EntreeSortie temp = v.get(minIndex);
            // Mettre l'élément i à la place du minimum
            v.set(minIndex, v.get(i));
            // Mettre le minimum à la position i
            v.set(i, temp);
        }
    }
    
    // Méthode utile pour le débogage
    public void afficher() {
        System.out.println("=== Contenu du thésaurus ===");
        for (EntreeSortie es : table) {
            System.out.println(es.entree + " -> " + es.sortie);
        }
        System.out.println("=== Total: " + table.size() + " entrées ===");
    }
}