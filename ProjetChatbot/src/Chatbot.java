import java.util.ArrayList;
import java.util.Scanner;

public class Chatbot {

    private static final String MESSAGE_IGNORANCE = "Je ne sais pas.";
    private static final String MESSAGE_BIENVENUE = "J'attends tes questions de culture générale.";
    private static final String MESSAGE_QUITTER = "Au revoir.";

    private static Index indexThemes;
    private static Index indexFormes;

    static private ArrayList<String> motsOutils;
    static private ArrayList<String> reponses;
    private static ArrayList<String> formesReponses;

    public static void main(String[] args) {

        System.out.println("Chargement des données...");

        // 1. Initialisation
        motsOutils = Utilitaire.lireMotsOutils("../mots-outils.txt");
        Utilitaire.trierChaines(motsOutils);

        reponses = Utilitaire.lireReponses("../reponses.txt");

        // 2. Construction Index Contenu (Etape 1)
        indexThemes = Utilitaire.constructionIndexReponses(reponses, motsOutils);

        // 3. Construction Index Forme (Etape 2)
        // D'abord on liste toutes les formes possibles existant dans reponses.txt
        formesReponses = Utilitaire.constructionTableFormes(reponses, motsOutils);
        
        // Ensuite on apprend quel type de question mène à quel type de forme de réponse
        ArrayList<String> questionsReponses = Utilitaire.lireQuestionsReponses("../questions-reponses.txt");
        indexFormes = Utilitaire.constructionIndexFormes(questionsReponses, formesReponses, motsOutils);

        // 4. Boucle principale
        Scanner lecteur = new Scanner(System.in);
        System.out.println(MESSAGE_BIENVENUE);

        String entreeUtilisateur = "";
        do {
            System.out.print("> ");
            if (lecteur.hasNextLine()) {
                entreeUtilisateur = lecteur.nextLine();
                
                if (!entreeUtilisateur.equalsIgnoreCase(MESSAGE_QUITTER)) {
                    String reponse = repondre(entreeUtilisateur);
                    System.out.println("> " + reponse);
                }
            } else {
                break; // Fin du flux
            }
        } while (!entreeUtilisateur.equalsIgnoreCase(MESSAGE_QUITTER));
        
        System.out.println(MESSAGE_QUITTER);
        lecteur.close();
    }

    static private String repondre(String question) {
        // --- ETAPE 1 : Recherche sur le thème ---
        ArrayList<Integer> reponsesCandidates = Utilitaire.constructionReponsesCandidates(question, indexThemes, motsOutils);

        if (reponsesCandidates.isEmpty()) {
            return MESSAGE_IGNORANCE;
        }

        // --- ETAPE 2 : Filtrage sur la forme ---
        ArrayList<Integer> reponsesSelectionnees = Utilitaire.selectionReponsesCandidates(
                question, reponsesCandidates, indexFormes, reponses, formesReponses, motsOutils);

        // Si l'étape 2 filtre tout, on renvoie "Je ne sais pas" (ou on pourrait renvoyer une réponse de l'étape 1 par défaut)
        if (reponsesSelectionnees.isEmpty()) {
             // Optionnel : décommenter ligne suivante pour être plus souple et répondre même si la forme est bizarre
             // return reponses.get(reponsesCandidates.get(0)); 
            return MESSAGE_IGNORANCE;
        }

        // Choix aléatoire parmi les réponses restantes
        int choix = (int) (Math.random() * reponsesSelectionnees.size());
        return reponses.get(reponsesSelectionnees.get(choix));
    }
}