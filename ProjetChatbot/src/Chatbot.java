import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * ============================================================
 * CLASSE Chatbot
 * ============================================================
 *
 * Cette classe représente le programme principal du projet.
 *
 * Rôles :
 *  - lancer l’application
 *  - charger les données (réponses, mots-outils)
 *  - gérer le dialogue avec l’utilisateur
 *  - utiliser les index pour produire une réponse pertinente
 *  - apprendre dynamiquement quand il ne sait pas répondre
 *
 * C’est le point d’entrée du programme (méthode main).
 */
public class Chatbot {

    /* ============================================================
     *  DONNÉES GLOBALES DU CHATBOT
     * ============================================================
     */

    /**
     * Tableau contenant toutes les réponses connues du chatbot.
     * Chaque réponse est associée implicitement à son indice.
     */
    private static String[] reponses;

    /**
     * Tableau des mots-outils (articles, prépositions, etc.).
     * Ces mots n’ont pas de valeur sémantique.
     */
    private static String[] motsOutils;

    /**
     * Index sur le contenu (thèmes).
     * Associe un mot significatif aux réponses qui le contiennent.
     */
    private static Index indexThemes;

    /**
     * Dernière question posée par l’utilisateur.
     * Sert à gérer les questions dépendantes du contexte.
     */
    private static String derniereQuestion = null;

    /**
     * Générateur aléatoire utilisé pour varier les réponses.
     */
    private static final Random random = new Random();

    /* ============================================================
     *  MÉTHODE MAIN — POINT D’ENTRÉE DU PROGRAMME
     * ============================================================
     */

    public static void main(String[] args) {

        /* ===== Chargement des données ===== */

        // Lecture du fichier contenant les réponses
        reponses = LectureFichier.lireFichier("reponses.txt");

        // Lecture du fichier contenant les mots-outils
        motsOutils = LectureFichier.lireFichier("mots-outils.txt");

        // Tri indispensable pour la recherche dichotomique
        Utilitaire.trierChaines(motsOutils);

        // Construction de l’index sur le contenu
        indexThemes =
                Utilitaire.constructionIndexReponses(reponses, motsOutils);

        /* ===== Initialisation du dialogue ===== */

        Scanner sc = new Scanner(System.in);

        System.out.println("Bonjour !");
        System.out.println("Pose-moi une question de culture générale.");
        System.out.println("(Tape 'exit' pour quitter)");

        /* ===== Boucle principale du dialogue ===== */

        while (true) {

            System.out.print("> ");

            // Lecture de la question utilisateur
            String question = sc.nextLine();

            // Condition de sortie du programme
            if (question.equalsIgnoreCase("exit")) {
                break;
            }

            // Traitement de la question
            repondre(question, sc);
        }

        sc.close();
        System.out.println("Au revoir !");
    }

    /* ============================================================
     *  TRAITEMENT D’UNE QUESTION
     * ============================================================
     */

    /**
     * Gère la réponse du chatbot à une question.
     *
     * Cette méthode orchestre :
     *  - la gestion du contexte
     *  - la recherche des réponses candidates
     *  - l’apprentissage si nécessaire
     */
    private static void repondre(String question, Scanner sc) {

        /* ===== Gestion du contexte ===== */

        // Si la question est incomplète mais dépend du contexte
        if (estQuestionDeContexte(question) && derniereQuestion != null) {

            // On enrichit la question avec la précédente
            question = question + " " + derniereQuestion;
        }

        /* ===== Recherche des réponses candidates ===== */

        ArrayList<Integer> candidates =
                Utilitaire.constructionReponsesCandidates(
                        question,
                        indexThemes,
                        motsOutils
                );

        /* ===== Cas où aucune réponse n’est trouvée ===== */

        if (candidates.isEmpty()) {

            // Lancement de l’apprentissage dynamique
            apprentissage(sc);
            return;
        }

        /* ===== Sélection et affichage de la réponse ===== */

        // Choix aléatoire parmi les réponses candidates
        int indiceChoisi =
                candidates.get(random.nextInt(candidates.size()));

        System.out.println(reponses[indiceChoisi]);

        // Mise à jour du contexte
        derniereQuestion = question;
    }

    /* ============================================================
     *  GESTION DU CONTEXTE
     * ============================================================
     */

    /**
     * Détermine si une question dépend du contexte précédent.
     *
     * Exemple :
     *  - "Quand ?"
     *  - "Qui ?"
     *  - "En quelle année ?"
     *
     * @return true si la question est contextuelle
     */
    private static boolean estQuestionDeContexte(String question) {

        question = question.toLowerCase().trim();

        return question.startsWith("quand")
                || question.startsWith("qui")
                || question.startsWith("en quelle")
                || question.equals("qui ?")
                || question.equals("quand ?");
    }

    /* ============================================================
     *  APPRENTISSAGE DYNAMIQUE
     * ============================================================
     */

    /**
     * Permet au chatbot d’apprendre une nouvelle réponse.
     *
     * Déclenchée lorsque le chatbot ne sait pas répondre.
     *
     * Étapes :
     *  1) demander la réponse à l’utilisateur
     *  2) l’ajouter au tableau des réponses
     *  3) reconstruire l’index
     */
    private static void apprentissage(Scanner sc) {

        System.out.println("Je ne sais pas répondre à cette question.");
        System.out.println("Peux-tu m’indiquer la bonne réponse ?");
        System.out.print("> ");

        // Lecture de la réponse fournie par l’utilisateur
        String nouvelleReponse = sc.nextLine();

        /* ===== Ajout dynamique de la réponse ===== */

        // Création d’un nouveau tableau plus grand
        String[] nouvellesReponses = new String[reponses.length + 1];

        // Copie des anciennes réponses
        System.arraycopy(
                reponses,
                0,
                nouvellesReponses,
                0,
                reponses.length
        );

        // Ajout de la nouvelle réponse à la fin
        nouvellesReponses[reponses.length] = nouvelleReponse;

        // Mise à jour de la référence
        reponses = nouvellesReponses;

        /* ===== Reconstruction de l’index ===== */

        indexThemes =
                Utilitaire.constructionIndexReponses(reponses, motsOutils);

        System.out.println("Merci ! J’ai appris quelque chose 😊");
    }
}
