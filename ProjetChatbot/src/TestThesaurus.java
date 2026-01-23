import java.util.ArrayList;

/**
 * Classe de test pour vérifier que le thésaurus fonctionne correctement
 * Utiliser cette classe pour déboguer avant de lancer le chatbot complet
 */
public class TestThesaurus {
    
    public static void main(String[] args) {
        System.out.println("=== TEST COMPLET DU SYSTÈME ===\n");
        
        // 1. Charger tous les composants
        System.out.println("1. Chargement des composants...");
        ArrayList<String> motsOutils = Utilitaire.lireMotsOutils("../mots-outils.txt");
        Utilitaire.trierChaines(motsOutils);
        System.out.println("   ✓ Mots-outils chargés: " + motsOutils.size() + " mots");
        
        ArrayList<String> reponses = Utilitaire.lireReponses("../reponses.txt");
        System.out.println("   ✓ Réponses chargées: " + reponses.size() + " réponses");
        
        Thesaurus thesaurus = new Thesaurus("../thesaurus.txt");
        System.out.println("   ✓ Thésaurus chargé\n");
        
        // 2. Test du thésaurus avec les mots de la question problématique
        System.out.println("2. Test du thésaurus avec la question: \"Comment se nomment les habitants de Grenoble ?\"\n");
        
        String[] motsQuestion = {"comment", "se", "nomment", "les", "habitants", "de", "grenoble"};
        
        for (String mot : motsQuestion) {
            String forme = thesaurus.rechercherSortiePourEntree(mot);
            if (!forme.equals(mot)) {
                System.out.println("   \"" + mot + "\" -> \"" + forme + "\" (trouvé dans thésaurus)");
            } else {
                System.out.println("   \"" + mot + "\" -> \"" + forme + "\" (pas dans thésaurus)");
            }
        }
        
        System.out.println();
        
        // 3. Vérifier si la réponse existe
        System.out.println("3. Recherche de la réponse dans la base...\n");
        
        String reponseAttendue = "Les habitants de Grenoble s'appellent les Grenoblois.";
        boolean trouve = false;
        int indexReponse = -1;
        
        for (int i = 0; i < reponses.size(); i++) {
            if (reponses.get(i).equalsIgnoreCase(reponseAttendue)) {
                trouve = true;
                indexReponse = i;
                break;
            }
        }
        
        if (trouve) {
            System.out.println("   ✓ Réponse trouvée à l'index " + indexReponse + ": \"" + reponses.get(indexReponse) + "\"");
        } else {
            System.out.println("   ✗ ERREUR: Réponse NON trouvée dans reponses.txt!");
            System.out.println("   Vérifiez que cette ligne existe dans reponses.txt:");
            System.out.println("   " + reponseAttendue);
        }
        
        System.out.println();
        
        // 4. Test de l'index des thèmes
        System.out.println("4. Construction de l'index des thèmes...\n");
        Index indexThemes = Utilitaire.constructionIndexReponses(reponses, motsOutils, thesaurus);
        
        // Vérifier quels mots de la question sont indexés
        String[] motsNonOutils = {"nomment", "habitants", "grenoble"};
        
        for (String mot : motsNonOutils) {
            String motCanonique = thesaurus.rechercherSortiePourEntree(mot);
            ArrayList<Integer> reponsesAvecCeMot = indexThemes.rechercherSorties(motCanonique);
            
            System.out.println("   Mot \"" + mot + "\" (canonique: \"" + motCanonique + "\"):");
            System.out.println("   -> Trouvé dans " + reponsesAvecCeMot.size() + " réponse(s)");
            
            if (reponsesAvecCeMot.size() > 0 && reponsesAvecCeMot.size() <= 5) {
                for (Integer idx : reponsesAvecCeMot) {
                    System.out.println("      [" + idx + "] " + reponses.get(idx));
                }
            }
        }
        
        System.out.println();
        
        // 5. Test complet: construire les réponses candidates
        System.out.println("5. Test de constructionReponsesCandidates...\n");
        
        String question = "Comment se nomment les habitants de Grenoble ?";
        ArrayList<Integer> candidates = Utilitaire.constructionReponsesCandidates(
            question, indexThemes, motsOutils, thesaurus);
        
        System.out.println("   Question: \"" + question + "\"");
        System.out.println("   Réponses candidates trouvées: " + candidates.size());
        
        if (candidates.size() > 0) {
            System.out.println("   Liste des candidates:");
            for (Integer idx : candidates) {
                System.out.println("      [" + idx + "] " + reponses.get(idx));
            }
        } else {
            System.out.println("   ✗ AUCUNE réponse candidate!");
            System.out.println("   Cela signifie qu'aucune réponse ne contient TOUS les mots non-outils de la question.");
        }
        
        System.out.println();
        
        // 6. Vérifier les mots de la réponse attendue
        if (trouve) {
            System.out.println("6. Analyse de la réponse attendue...\n");
            
            ArrayList<String> motsReponse = decoupeEnMotsPublic(reponseAttendue);
            System.out.println("   Mots de la réponse: " + motsReponse);
            
            System.out.println("\n   Mots non-outils (avec leur forme canonique):");
            for (String mot : motsReponse) {
                String motCanonique = thesaurus.rechercherSortiePourEntree(mot);
                boolean estMotOutil = existeChaineDichoPublic(motsOutils, motCanonique);
                
                if (!estMotOutil) {
                    System.out.println("      \"" + mot + "\" -> \"" + motCanonique + "\"");
                }
            }
            
            System.out.println("\n   Vérification: la réponse contient-elle les mots de la question?");
            for (String mot : motsNonOutils) {
                String motCanonique = thesaurus.rechercherSortiePourEntree(mot);
                
                boolean dansReponse = false;
                for (String motRep : motsReponse) {
                    String motRepCanonique = thesaurus.rechercherSortiePourEntree(motRep);
                    if (motRepCanonique.equals(motCanonique)) {
                        dansReponse = true;
                        break;
                    }
                }
                
                if (dansReponse) {
                    System.out.println("      ✓ \"" + mot + "\" (canonique: \"" + motCanonique + "\") est dans la réponse");
                } else {
                    System.out.println("      ✗ \"" + mot + "\" (canonique: \"" + motCanonique + "\") N'EST PAS dans la réponse!");
                }
            }
        }
        
        System.out.println("\n=== FIN DES TESTS ===");
        System.out.println("\nRECOMMANDATIONS:");
        System.out.println("- Si la réponse n'est pas trouvée, ajoutez-la à reponses.txt");
        System.out.println("- Si les mots ne matchent pas, ajoutez les synonymes dans thesaurus.txt:");
        System.out.println("  Exemple: appellent:nomme");
        System.out.println("           nomment:nomme");
        System.out.println("           s:se");
    }
    
    // Méthodes utilitaires publiques pour les tests
    private static ArrayList<String> decoupeEnMotsPublic(String contenu) {
        String chaine = contenu.toLowerCase();
        chaine = chaine.replace('\n', ' ');
        chaine = chaine.replace('?', ' ');
        chaine = chaine.replace('-', ' ');
        chaine = chaine.replace('\'', ' ');
        chaine = chaine.replace('.', ' ');
        chaine = chaine.replace(',', ' ');
        chaine = chaine.replace(':', ' ');
        chaine = chaine.replace(';', ' ');
        chaine = chaine.replace('(', ' ');
        chaine = chaine.replace(')', ' ');
        
        String[] tabchaine = chaine.split(" ");
        ArrayList<String> resultat = new ArrayList<>();
        
        for (int i = 0; i < tabchaine.length; ++i) {
            if (!tabchaine[i].equals("")) {
                resultat.add(tabchaine[i]);
            }
        }
        return resultat;
    }
    
    private static boolean existeChaineDichoPublic(ArrayList<String> lesChaines, String chaine) {
        int inf = 0, sup = lesChaines.size() - 1;
        while (inf <= sup) {
            int m = (inf + sup) / 2;
            int cmp = chaine.compareTo(lesChaines.get(m));
            if (cmp == 0) return true;
            if (cmp < 0) sup = m - 1;
            else inf = m + 1;
        }
        return false;
    }
}