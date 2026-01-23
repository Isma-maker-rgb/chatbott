import java.util.ArrayList;
import java.util.Scanner;

public class Chatbot {

    // Messages constants utilisés par le chatbot
    private static final String MESSAGE_IGNORANCE = "Je ne sais pas.";
    private static final String MESSAGE_APPRENTISSAGE = "Je vais te l'apprendre.";
    private static final String MESSAGE_BIENVENUE = "J'attends tes questions de culture générale.";
    private static final String MESSAGE_QUITTER = "Au revoir.";
    private static final String MESSAGE_INVITATION = "Je t'écoute.";
    private static final String MESSAGE_CONFIRMATION = "Très bien, c'est noté.";

    // Index pour trouver rapidement les réponses à partir des mots NON outils de la question (Étape 1)
    private static Index indexThemes;
    
    // Index pour trouver rapidement les formes de réponse à partir des mots-outils de la question (Étape 2)
    private static Index indexFormes;
    
    // Vecteur trié des mots outils (qui, que, quoi, où, quand, comment, etc.)
    static private ArrayList<String> motsOutils;
    
    // Vecteur de toutes les réponses possibles du chatbot
    static private ArrayList<String> reponses;
    
    // Vecteur des formes de réponses (ex: "... a ... le ...")
    private static ArrayList<String> formesReponses;
    
    // Thésaurus pour gérer les synonymes et variantes (Partie 2.1)
    private static Thesaurus thesaurus;
    
    // Variable pour stocker la dernière question contenant des mots non-outils (Partie 2.2)
    // Permet de répondre à des questions en contexte comme "Et quand ?" après "Où est né Léonard de Vinci ?"
    private static String derniereQuestionAvecContexte = "";

    public static void main(String[] args) {

        // === INITIALISATION ===
        
        // 1. Charger et trier les mots-outils
        motsOutils = Utilitaire.lireMotsOutils("mots-outils.txt");
        Utilitaire.trierChaines(motsOutils);

        // 2. Charger toutes les réponses possibles
        reponses = Utilitaire.lireReponses("reponses.txt");

        // 3. Charger le thésaurus (Partie 2.1)
        thesaurus = new Thesaurus("thesaurus.txt");
        
        // DEBUG: Décommenter pour voir le contenu du thésaurus
        // thesaurus.afficher();

        // 4. Construire l'index des thèmes (pour l'Étape 1 - recherche par contenu)
        // Permet de trouver rapidement quelles réponses contiennent un mot donné
        indexThemes = Utilitaire.constructionIndexReponses(reponses, motsOutils, thesaurus);
        
        // DEBUG: Décommenter pour voir l'index des thèmes
        // indexThemes.afficher();

        // 5. Construire la table des formes de réponses
        // Ex: "... a ... le ... en ..." est une forme de réponse
        formesReponses = Utilitaire.constructionTableFormes(reponses, motsOutils, thesaurus);
        
        // DEBUG: Décommenter pour voir les formes
        // System.out.println(formesReponses);

        // 6. Charger les paires question/réponse idéales
        ArrayList<String> questionsReponses = Utilitaire.lireQuestionsReponses("questions-reponses.txt");

        // 7. Construire l'index des formes (pour l'Étape 2 - recherche par forme)
        // Permet de savoir quelle forme de réponse correspond à quelle forme de question
        indexFormes = Utilitaire.constructionIndexFormes(questionsReponses, formesReponses, motsOutils, thesaurus);
        
        // DEBUG: Décommenter pour voir l'index des formes
        // indexFormes.afficher();

        // === BOUCLE PRINCIPALE ===
        
        String reponse = "";
        String entreeUtilisateur = "";

        Scanner lecteur = new Scanner(System.in);
        System.out.println();
        System.out.print("> ");
        System.out.println(MESSAGE_BIENVENUE);

        do {
            System.out.print("> ");
            entreeUtilisateur = lecteur.nextLine();
            
            if (entreeUtilisateur.compareToIgnoreCase(MESSAGE_QUITTER) != 0) {
                
                // === PARTIE 2.2.c: DÉCIDER COMMENT RÉPONDRE ===
                
                // Vérifier si la question ne contient QUE des mots-outils
                // Ex: "Et quand ?" après "Où est né Léonard de Vinci ?"
                if (Utilitaire.entierementInclus(motsOutils, entreeUtilisateur)) {
                    
                    // Répondre en utilisant le contexte de la question précédente
                    reponse = repondreEnContexte(entreeUtilisateur, derniereQuestionAvecContexte);
                    
                } else {
                    
                    // === RÉPONSE NORMALE (avec mots non-outils) ===
                    
                    reponse = repondre(entreeUtilisateur);
                    
                    // Enregistrer cette question comme contexte pour la prochaine fois
                    derniereQuestionAvecContexte = entreeUtilisateur;
                    
                    // === PARTIE 2.4: APPRENTISSAGE INTERACTIF ===
                    
                    // Si le chatbot ne connaît pas la réponse, proposer d'apprendre
                    if (reponse.equals(MESSAGE_IGNORANCE)) {
                        System.out.println("> " + reponse);
                        System.out.println("> " + MESSAGE_APPRENTISSAGE);
                        System.out.print("> ");
                        String nouvelleReponse = lecteur.nextLine();
                        
                        // Vérifier si cette réponse existe déjà dans la base
                        boolean repExiste = Utilitaire.reponseExiste(nouvelleReponse, indexThemes, reponses, motsOutils, thesaurus);
                        
                        if (!repExiste) {
                            // Cas 1 ou 3: La réponse n'existe pas encore
                            
                            // Ajouter la réponse au vecteur et à l'index
                            Utilitaire.IntegrerNouvelleReponse(nouvelleReponse, reponses, indexThemes, motsOutils);
                            
                            // Sauvegarder dans le fichier pour la persistance
                            Utilitaire.ecrireFichier("reponses.txt", nouvelleReponse);
                            
                            // Mettre à jour la table des formes si cette forme n'existe pas
                            String forme = Utilitaire.calculForme(nouvelleReponse, motsOutils, thesaurus);
                            boolean formeExiste = false;
                            for (String f : formesReponses) {
                                if (f.equals(forme)) {
                                    formeExiste = true;
                                    break;
                                }
                            }
                            if (!formeExiste) {
                                formesReponses.add(forme);
                            }
                        }
                        
                        // Vérifier si la forme question/réponse existe déjà
                        boolean qrExiste = Utilitaire.formeQuestionReponseExiste(entreeUtilisateur, nouvelleReponse, 
                                                                                  indexFormes, formesReponses, motsOutils, thesaurus);
                        
                        if (!qrExiste) {
                            // Cas 1 ou 2: La forme question/réponse n'existe pas
                            
                            // Ajouter la nouvelle question/réponse à l'index des formes
                            Utilitaire.integrerNouvelleQuestionReponse(entreeUtilisateur, nouvelleReponse, 
                                                                       formesReponses, indexFormes, motsOutils, thesaurus);
                            
                            // Sauvegarder dans le fichier pour la persistance
                            String qrPaire = entreeUtilisateur + "?" + nouvelleReponse;
                            Utilitaire.ecrireFichier("questions-reponses.txt", qrPaire);
                        }
                        
                        reponse = MESSAGE_CONFIRMATION;
                    }
                }
                
                System.out.println("> " + reponse);
            }
        } while (entreeUtilisateur.compareToIgnoreCase(MESSAGE_QUITTER) != 0);
    }

    static private String repondre(String question) {
        // === ÉTAPE 1: TROUVER LES RÉPONSES CANDIDATES (par contenu) ===
        // On cherche toutes les réponses qui contiennent TOUS les mots non-outils de la question
        ArrayList<Integer> reponsesCandidates = Utilitaire.constructionReponsesCandidates(
            question, indexThemes, motsOutils, thesaurus);

        if (reponsesCandidates.isEmpty()) {
            // Aucune réponse ne contient tous les mots de la question
            return MESSAGE_IGNORANCE;
        }

        // === ÉTAPE 2: SÉLECTIONNER LA MEILLEURE RÉPONSE (par forme) ===
        // Parmi les candidates, on garde celles dont la forme correspond à la question
        ArrayList<Integer> reponsesSelectionnees = Utilitaire.selectionReponsesCandidates(
                question, reponsesCandidates, indexFormes, reponses, formesReponses, motsOutils, thesaurus);

        if (reponsesSelectionnees.isEmpty()) {
            // Aucune réponse candidate n'a la bonne forme
            return MESSAGE_IGNORANCE;
        }

        // Choisir une réponse aléatoire parmi les réponses sélectionnées
        int choix = (int) (Math.random() * reponsesSelectionnees.size());
        return reponses.get(reponsesSelectionnees.get(choix));
    }

    // === PARTIE 2.2.b: RÉPONDRE EN CONTEXTE ===
    static private String repondreEnContexte(String question, String questionPrecedente) {
        // Cette méthode permet de répondre à des questions comme "Et quand ?" 
        // en utilisant le contexte de la question précédente
        
        // Si pas de contexte précédent, impossible de répondre
        if (questionPrecedente.isEmpty()) {
            return MESSAGE_IGNORANCE;
        }
        
        // ÉTAPE 1: Construire les réponses candidates à partir de la QUESTION PRÉCÉDENTE
        // Cela nous donne toutes les réponses qui parlent du même sujet
        // Ex: si la question précédente était "Où est né Léonard de Vinci ?",
        // on trouve toutes les réponses parlant de Léonard de Vinci
        ArrayList<Integer> reponsesCandidates = Utilitaire.constructionReponsesCandidates(
            questionPrecedente, indexThemes, motsOutils, thesaurus);
        
        if (reponsesCandidates.isEmpty()) {
            return MESSAGE_IGNORANCE;
        }
        
        // ÉTAPE 2: Sélectionner parmi ces candidats ceux dont la forme correspond à la NOUVELLE QUESTION
        // Ex: si la nouvelle question est "Et quand ?", on garde seulement les réponses
        // qui ont une forme compatible avec "quand" (contenant une date)
        ArrayList<Integer> reponsesSelectionnees = Utilitaire.selectionReponsesCandidates(
            question, reponsesCandidates, indexFormes, reponses, formesReponses, motsOutils, thesaurus);
        
        if (reponsesSelectionnees.isEmpty()) {
            return MESSAGE_IGNORANCE;
        }
        
        // Choisir une réponse aléatoire parmi les réponses sélectionnées
        int choix = (int) (Math.random() * reponsesSelectionnees.size());
        return reponses.get(reponsesSelectionnees.get(choix));
    }
}