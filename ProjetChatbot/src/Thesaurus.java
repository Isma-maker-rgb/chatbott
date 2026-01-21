import java.io.*;
import java.util.*;

public class Thesaurus {
    private class EntreeSortie implements Comparable<EntreeSortie> {
        private String entree;
        private String sortie;

        public EntreeSortie(String entree, String sortie) {
            this.entree = entree;
            this.sortie = sortie;
        }

        public int compareTo(EntreeSortie o) {
            return this.entree.compareTo(o.entree);
        }
    }

    private ArrayList<EntreeSortie> table = new ArrayList<>();

    public Thesaurus(String nomFichier) {
        try (Scanner sc = new Scanner(new File(nomFichier))) {
            while (sc.hasNextLine()) {
                String ligne = sc.nextLine();
                int sep = ligne.indexOf(":");
                if (sep != -1) {
                    table.add(new EntreeSortie(
                        ligne.substring(0, sep).trim().toLowerCase(),
                        ligne.substring(sep + 1).trim().toLowerCase()
                    ));
                }
            }
            // Tri optimisé pour la recherche dichotomique
            Collections.sort(table);
        } catch (FileNotFoundException e) {
            System.out.println("Attention : thesaurus.txt non trouvé.");
        }
    }

    public String rechercherSortiePourEntree(String entree) {
        int low = 0, high = table.size() - 1;
        String cible = entree.toLowerCase();
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = cible.compareTo(table.get(mid).entree);
            if (cmp == 0) return table.get(mid).sortie;
            if (cmp < 0) high = mid - 1; else low = mid + 1;
        }
        return entree;
    }
}