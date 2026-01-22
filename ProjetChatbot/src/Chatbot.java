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
        motsOutils = Utilitaire.lireMotsOutils("mots-outils.txt");
        Utilitaire.trierChaines(motsOutils);

        reponses = Utilitaire.lireReponses("reponses.txt");

        // 2. Construction Index Contenu (Etape 1)
        indexThemes = Utilitaire.constructionIndexReponses(reponses, motsOutils);

        // 3. Construction Index Forme (Etape 2)
        // D'abord on liste toutes les formes possibles existant dans reponses.txt
        formesReponses = Utilitaire.constructionTableFormes(reponses, motsOutils);

        // Ensuite on apprend quel type de question mène à quel type de forme de réponse
        ArrayList<String> questionsReponses = Utilitaire.lireQuestionsReponses("questions-reponses.txt");
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

    static private String repondre(String questionUtilisateur) {
        // --- NOUVELLE FONCTIONNALITÉ : APPRENTISSAGE ---
        if (questionUtilisateur.equalsIgnoreCase("je vais te l'apprendre.")) {
            Scanner sc = new Scanner(System.in);

            System.out.println("> Quelle est la question ?");
            System.out.print("> ");
            String q = sc.nextLine();

            System.out.println("> Quelle est la réponse ?");
            System.out.print("> ");
            String r = sc.nextLine();

            // 1. Sauvegarde physique dans les fichiers .txt
            Utilitaire.ecrireFichier("reponses.txt", r);
            Utilitaire.ecrireFichier("questions-reponses.txt", q + "?" + r);

            // 2. Mise à jour des structures de données en mémoire
            // Ajout de la réponse et mise à jour de l'index thématique
            Utilitaire.IntegrerNouvelleReponse(r, reponses, indexThemes, motsOutils);
            // Ajout de la forme de la réponse et mise à jour de l'index des formes
            Utilitaire.integrerNouvelleQuestionReponse(q, r, formesReponses, indexFormes, motsOutils);

            return "Merci ! J'ai bien enregistré cette nouvelle connaissance.";
        }

        // --- ETAPE 1 : Recherche sur le thème ---
        ArrayList<Integer> reponsesCandidates = Utilitaire.constructionReponsesCandidates(questionUtilisateur, indexThemes, motsOutils);

        if (reponsesCandidates.isEmpty()) {
            return MESSAGE_IGNORANCE;
        }

        // --- ETAPE 2 : Filtrage sur la forme ---
        ArrayList<Integer> reponsesSelectionnees = Utilitaire.selectionReponsesCandidates(questionUtilisateur, reponsesCandidates, indexFormes, reponses, formesReponses, motsOutils);

        if (reponsesSelectionnees.isEmpty()) {
            // Optionnel : on peut retourner une réponse de l'étape 1 si le filtrage de forme est trop strict
            return MESSAGE_IGNORANCE;
        }

        // Choix aléatoire parmi les réponses sélectionnées
        int choix = (int) (Math.random() * reponsesSelectionnees.size());
        return reponses.get(reponsesSelectionnees.get(choix));
    }
}
