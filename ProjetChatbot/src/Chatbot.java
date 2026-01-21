import java.util.ArrayList;
import java.util.Scanner;

public class Chatbot {
    private static final String MESSAGE_IGNORANCE = "Je ne sais pas.";
    private static Index indexThemes;
    private static Index indexFormes;
    private static ArrayList<String> motsOutils;
    private static ArrayList<String> reponses;
    private static ArrayList<String> formesReponses;
    private static Thesaurus thesaurus;

    private static String derniereQuestionPosee = "";
    private static ArrayList<String> contexteMotsCles = new ArrayList<>();

    public static void main(String[] args) {
        initialiser();
        Scanner sc = new Scanner(System.in);
        System.out.println("J'attends tes questions de culture générale.");

        while (true) {
            System.out.print("> ");
            String entree = sc.nextLine().trim();
            if (entree.equalsIgnoreCase("Au revoir")) break;

            if (entree.equalsIgnoreCase("Je vais te l'apprendre.")) {
                if (derniereQuestionPosee.isEmpty()) {
                    System.out.println("> Pose-moi une question d'abord !");
                } else {
                    System.out.println("> Je t'écoute.");
                    System.out.print("> ");
                    String nouvelleRep = sc.nextLine().trim();
                    apprendre(derniereQuestionPosee, nouvelleRep);
                    System.out.println("> Très bien, c'est noté.");
                }
            } else {
                derniereQuestionPosee = entree;
                System.out.println("> " + repondreEnContexte(entree));
            }
        }
    }

    private static void initialiser() {
        motsOutils = Utilitaire.lireMotsOutils("mots-outils.txt");
        Utilitaire.trierChaines(motsOutils);
        thesaurus = new Thesaurus("thesaurus.txt");
        reponses = Utilitaire.lireReponses("reponses.txt");
        
        indexThemes = new Index();
        for (int i = 0; i < reponses.size(); i++) {
            ArrayList<String> cles = Utilitaire.extraireMotsCles(reponses.get(i), motsOutils, thesaurus);
            for (String cle : cles) indexThemes.ajouterSortieAEntree(cle, i);
        }

        formesReponses = Utilitaire.constructionTableFormes(reponses, motsOutils);
        ArrayList<String> qr = Utilitaire.lireQuestionsReponses("questions-reponses.txt");
        indexFormes = Utilitaire.constructionIndexFormes(qr, formesReponses, motsOutils);
    }

    private static void apprendre(String q, String r) {
        Utilitaire.sauvegarderDansFichier(r, "reponses.txt");
        Utilitaire.sauvegarderDansFichier(q + " ? " + r, "questions-reponses.txt");
        
        reponses.add(r);
        int id = reponses.size() - 1;
        ArrayList<String> cles = Utilitaire.extraireMotsCles(r, motsOutils, thesaurus);
        for (String cle : cles) indexThemes.ajouterSortieAEntree(cle, id);
    }

    private static String repondreEnContexte(String question) {
        ArrayList<String> motsSignificatifs = Utilitaire.extraireMotsCles(question, motsOutils, thesaurus);
        
        // Gestion du contexte
        if (motsSignificatifs.size() <= 1 && !contexteMotsCles.isEmpty()) {
            for (String m : contexteMotsCles) {
                if (!motsSignificatifs.contains(m)) motsSignificatifs.add(m);
            }
        } else {
            contexteMotsCles = new ArrayList<>(motsSignificatifs);
        }

        ArrayList<Integer> candidats = new ArrayList<>();
        for (String m : motsSignificatifs) {
            candidats = Utilitaire.fusion(candidats, indexThemes.rechercherSorties(m));
        }
        candidats = Utilitaire.maxOccurences(candidats, motsSignificatifs.size());

        if (candidats.isEmpty()) return MESSAGE_IGNORANCE;
        
        ArrayList<Integer> selection = Utilitaire.selectionReponsesCandidates(question, candidats, indexFormes, reponses, formesReponses, motsOutils);
        
        return reponses.get(selection.isEmpty() ? candidats.get(0) : selection.get(0));
    }
}