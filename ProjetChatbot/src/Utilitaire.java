import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Utilitaire {

    private static final int NBMOTS_FORME = 5; // nombre maximal de mots-outils pris en compte pour les formes dans l'étape 2

    static public ArrayList<String> lireMotsOutils(String nomFichier) {
        //{}=>{résultat = le vecteur des mots outils construit à partir du fichier nomFichier}
        ArrayList<String> motsOutils = new ArrayList<>();
        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                motsOutils.add(ligne);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return motsOutils;
    }

    static public ArrayList<String> lireReponses(String nomFichier) {
        //{}=>{résultat = le vecteur des réponses construit à partir du fichier nomFichier}
        ArrayList<String> reponses = new ArrayList<>();

        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                reponses.add(ligne);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reponses;
    }

    static public ArrayList<String> lireQuestionsReponses(String nomFichier) {
        //{}=>{résultat = le vecteur des questions/réponses construit à partir du fichier nomFichier}
        ArrayList<String> questionsReponses = new ArrayList<>();

        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                questionsReponses.add(ligne);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionsReponses;


    }

    public static void ecrireFichier(String nomFichier, String chaineAEcrire) {
        //{}=>{la chaîne  chaineAEcrire est écrite après saut de ligne à la suite du fichier nomFichier}
        // true = mode append ? écrit à la suite sans effacer ce qui existe
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier, true))) {
            writer.newLine();
            writer.write(chaineAEcrire);// ajoute un retour à la ligne
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static private ArrayList<String> decoupeEnMots(String contenu) {
        //{}=>{résultat = le vecteur des mots de la chaîne contenu après pré-traitements divers}
        String chaine = contenu.toLowerCase();
        chaine = chaine.replace('\n', ' ');
        chaine = chaine.replace('?', ' ');
        chaine = chaine.replace('-', ' ');
        chaine = chaine.replace('\'', ' ');
        chaine = chaine.replace('.', ' ');
        chaine = chaine.replace(',', ' ');
        chaine = chaine.replace(':', ' ');
        chaine = chaine.replace(';', ' ');
        chaine = chaine.replace('\'', ' ');
        chaine = chaine.replace('"', ' ');
        chaine = chaine.replace('’', ' ');
        chaine = chaine.replace('’', ' ');
        chaine = chaine.replace("'", " ");
        chaine = chaine.replace('(', ' ');
        chaine = chaine.replace(')', ' ');
        chaine = chaine.replace('«', ' ');
        chaine = chaine.replace('-', ' ');


        String[] tabchaine = chaine.split(" ");
        ArrayList<String> resultat = new ArrayList<>();

        for (int i = 0; i < tabchaine.length; ++i) {
            if (!tabchaine[i].equals("")) {
                resultat.add(tabchaine[i]);
            }
        }

        return resultat;
    }


    static private boolean existeChaine(ArrayList<String> mots, String mot) {
        //{}=>  {recherche séquentielle de mot dans mots
        // résultat =  true si trouvé et false sinon }
        return false;
    }


    static private boolean existeChaineDicho(ArrayList<String> lesChaines, String chaine) {
        //{lesChaines (triée dans l'ordre lexicographique)}=>  {recherche dichotomique de chaine dans lesChaines
        // résultat =  true si trouvé et false sinon }
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

    static public boolean entierementInclus(ArrayList<String> mots, String question) {
        //{mots est trié dans l'ordre lexicographique}=>
        // résultat = true si tous les mots de questions sont dans mots, false sinon
        // remarque : utilise decoupeEnMots et existeChaineDicho}
        return false;
    }


    static private int rechercherChaine(ArrayList<String> lesChaines, String chaine) {
        // {}=>{résultat = l'indice de chaine dans lesChaines si trouvé et -1 sinon }
        for (int i = 0; i < lesChaines.size(); i++) {
            if (lesChaines.get(i).equals(chaine)) {
                return i;
            }
        }
        return -1;
    }


    static public void integrerNouvelleQuestionReponse(String question, String reponse, ArrayList<String> formes, Index indexFormes, ArrayList<String> motsOutils) {
        //{la forme de reponse n'existe pas ou n'est pas associée à question dans indexFormes}=>{la forme de reponse est ajoutée à la fin de formes si elle n'y est pas déjà
        // et indexFormes est mis à jour pour tenir compte de cette nouvelle question-réponse
        // remarque 1 : utilise calculForme, rechercherChaine, decoupeEnMots, existeChaineDicho, ajouterSortieAEntree, rechercherSortiePourEntree
        // remarque 2 : seuls les NBMOTS_FORME premiers mots-outils de la question sont pris en compte}
        String forme = calculForme(reponse, motsOutils);

        int indiceForme = rechercherChaine(formes, forme);
        if (indiceForme == -1) {
            formes.add(forme);
            indiceForme = formes.size() - 1;
        }

        ArrayList<String> motsQuestion = decoupeEnMots(question);

        int nbPris = 0;
        for (int i = 0; i < motsQuestion.size() && nbPris < NBMOTS_FORME; i++) {
            String mot = motsQuestion.get(i);

            if (existeChaineDicho(motsOutils, mot)) {
                String entreeIndex = mot + "_" + nbPris;
                indexFormes.ajouterSortieAEntree(entreeIndex, indiceForme);
                nbPris++;
            }
        }

    }


    static public void IntegrerNouvelleReponse(String reponse, ArrayList<String> reponses, Index indexContenu, ArrayList<String> motsOutils) {
        //{reponse n'est pas présent dans reponses}=>{reponse est ajoutée à la fin de reponses et indexContenu est mis à jour pour tenir compte de cette nouvelle réponse
        // remarque : utilise decoupeEnMots, existeChaineDicho, ajouterSortieAEntree, rechercherSortiePourEntree
        if (rechercherChaine(reponses, reponse) != -1) {
            return;
        }

        reponses.add(reponse);
        int indiceReponse = reponses.size() - 1;

        ArrayList<String> mots = decoupeEnMots(reponse);

        for (String mot : mots) {
            if (!existeChaineDicho(motsOutils, mot)) {
                indexContenu.ajouterSortieAEntree(mot, indiceReponse);
            }
        }
    }

    static public Index constructionIndexReponses(ArrayList<String> reponses, ArrayList<String> motsOutils) {
        //{}=>{résultat = un index dont les entrées sont les mots des réponses (reponses) absents de motsOutils.
        // et les sorties sont les indices (dans reponses) des réponses les contenant.
        // remarque : utilise existeChaineDicho, decoupeEnMots et ajouterSortieAEntree }
        Index index = new Index();
        for (int i = 0; i < reponses.size(); i++) {
            ArrayList<String> mots = decoupeEnMots(reponses.get(i));
            for (String mot : mots) {
                // Si ce n'est pas un mot outil, on l'indexe
                if (!existeChaineDicho(motsOutils, mot)) {
                    index.ajouterSortieAEntree(mot, i);
                }
            }
        }
        return index;
    }


    static void trierChaines(ArrayList<String> v) {
        //{}=>{v est trié dans l'ordre lexicographique }
        for (int i = 1; i < v.size(); i++) {
            String cle = v.get(i);
            int j = i - 1;
            while (j >= 0 && v.get(j).compareTo(cle) > 0) {
                v.set(j + 1, v.get(j));
                j--;
            }
            v.set(j + 1, cle);
        }

    }


    static ArrayList<Integer> maxOccurences(ArrayList<Integer> v, int seuil) {
        //{v trié} => {résultat = vecteur des entiers dont le nombre d'occurences
        // est maximal et au moins égal au seuil. Si le nombre d'occurences maximal est inférieur au seuil , un vecteur vide est retourné.
        // Par exemple, si V est [3,4,5,5,5,6,6,8,8,8,12,16,16,20]
        // si seuil<=3 alors le résultat est [5,8].
        // si le seuil>3 alors le résultat est []}
        ArrayList<Integer> res = new ArrayList<>();
        if (v.isEmpty()) return res;

        // Si le seuil est 0 (question vide ou que des mots outils), on ne retourne rien par sécurité
        if (seuil <= 0) return res;

        int currentVal = v.get(0);
        int count = 1;

        for (int i = 1; i < v.size(); i++) {
            if (v.get(i).equals(currentVal)) {
                count++;
            } else {
                if (count >= seuil) res.add(currentVal);
                currentVal = v.get(i);
                count = 1;
            }
        }
        // Vérifier le dernier élément
        if (count >= seuil) res.add(currentVal);

        return res;
    }

    static ArrayList<Integer> fusion(ArrayList<Integer> v1, ArrayList<Integer> v2) {
        //{v1 et v2 triés}=>{résultat = vecteur trié fusionnant v1 et v2 sans supprimer les répétitions
        // par exemple si v1 est [4,8,8,10,25] et v2 est [5,8,9,25]
        // le résultat est [4,5,8,8,8,9,10,25,25]}
        ArrayList<Integer> res = new ArrayList<>();
        int i = 0, j = 0;
        while (i < v1.size() && j < v2.size()) {
            if (v1.get(i) < v2.get(j)) {
                res.add(v1.get(i++));
            } else {
                res.add(v2.get(j++));
            }
        }
        while (i < v1.size()) res.add(v1.get(i++));
        while (j < v2.size()) res.add(v2.get(j++));
        return res;
    }


    static String calculForme(String chaine, ArrayList<String> motsOutils) {
        //{}=>{résultat = la concaténation des NBMOTS_FORME premiers mots-outils de chaine séparés par des blancs
        // remarque 1 : utilise decoupeMots et existeChaineDicho
        // remarque 2 : la limitation de la taille des formes permet d'accepter des réponses terminant par des précisions }
        return "";
    }

    static public ArrayList<String> constructionTableFormes(ArrayList<String> reponses, ArrayList<String> motsOutils) {
        //{}=>{résultat = le vecteur de toutes les formes de réponses dans reponses.
        // remarque : utilise calculForme et existeChaine }
        ArrayList<String> table = new ArrayList<>();
        for (String rep : reponses) {
            String forme = calculForme(rep, motsOutils);
            // Recherche séquentielle simple (la table n'est pas forcément triée ici selon l'énoncé)
            boolean existe = false;
            for (String f : table) {
                if (f.equals(forme)) {
                    existe = true;
                    break;
                }
            }
            if (!existe) table.add(forme);
        }
        return table;
    }

    static public Index constructionIndexFormes(ArrayList<String> questionsReponses, ArrayList<String> formes, ArrayList<String> motsOutils) {
        //{}=>{résultat = un index dont les entrées sont les "mots-outils positionnés" des questions (par exemple l'entrée pour un "Qui" en première position sera "qui_0")
        // et les sorties sont les indices (dans formes) des formes de réponses répondant aux questions contenant le mot-outil à cette position.
        // remarque 1 : utilise calculForme, rechercherChaine, decoupeEnMots, rechercherEntree, existeChaineDicho et ajouterSortieAEntree
        // remarque 2 : utilisez les méthodes indexOf et substring de String pour décomposer la question-réponse en question et réponse
        // remarque 3 : seuls les NBMOTS_FORME premiers mots-outils de la question sont pris en compte}
        Index index = new Index();

        for (String qr : questionsReponses) {
            int posSep = qr.indexOf("?");
            if (posSep == -1) continue;

            String question = qr.substring(0, posSep);
            String reponse = qr.substring(posSep + 1);

            // 1. Quelle est la forme de la réponse idéale ?
            String formeReponse = calculForme(reponse, motsOutils);
            int idForme = rechercherChaine(formes, formeReponse);

            if (idForme != -1) {
                // 2. Indexer cette forme via les mots-outils de la question
                ArrayList<String> motsQ = decoupeEnMots(question);
                int posOutil = 0;
                for (String m : motsQ) {
                    if (posOutil >= NBMOTS_FORME) break;

                    if (existeChaineDicho(motsOutils, m)) {
                        String cle = m + "_" + posOutil; // Ex: "qui_0", "a_1"
                        index.ajouterSortieAEntree(cle, idForme);
                        posOutil++;
                    }
                }
            }
        }
        return index;
    }

    static public ArrayList<Integer> constructionReponsesCandidates(String question, Index IndexReponses, ArrayList<String> motsOutils) {
        //{}=>{résultat = vecteur des identifiants de réponses contenant l'ensemble des mots non outils de la question.
        // remarque 1 : utilise decoupeEnMots, existeChaineDicho, rechercherSorties, fusion et maxOccurences
        // remarque 2 : maxOccurences est appelé en passant le nombre de mots non outils de la question comme valeur de seuil.
        // remarque 3 : on aurait pu calculer directement une intersection au lieu d'une fusion et se passer de maxOccurences mais on
        // souhaite pouvoir garder la possibilité d'assouplir par la suite la contrainte sur la présence de l'intégralité
        // des mots de la question dans la réponse }
        ArrayList<String> motsQuestion = decoupeEnMots(question);
        ArrayList<Integer> listeFusionnee = new ArrayList<>();
        int nbMotsNonOutils = 0;

        for (String mot : motsQuestion) {
            if (!existeChaineDicho(motsOutils, mot)) {
                nbMotsNonOutils++;
                ArrayList<Integer> sorties = IndexReponses.rechercherSorties(mot);
                listeFusionnee = fusion(listeFusionnee, sorties);
            }
        }

        // On cherche les réponses qui contiennent TOUS les mots significatifs (seuil = nbMotsNonOutils)
        return maxOccurences(listeFusionnee, nbMotsNonOutils);
    }


    static public boolean estUnNombre(String s) {
        //{s est non vide}=>{résultat = true si s ne contient que des caractères représentant des chiffres (>='0'&<='9') et false sinon}
        if (s == null || s.isEmpty()) return false;
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }


    static public ArrayList<Integer> selectionReponsesCandidates(String question,
                                                                 ArrayList<Integer> candidates,
                                                                 Index IndexFormes,
                                                                 ArrayList<String> reponses,
                                                                 ArrayList<String> formesReponses,
                                                                 ArrayList<String> motsOutils) {
        //{}=>{résultat = vecteur des identifiants de réponses (parmi les candidates) dont la forme est cohérente
        // avec la question.
        // remarque 1 : utilise decoupeEnMots, existeChaineDicho, rechercherSorties, fusion, maxOccurences, calculForme
        // remarque 2 : l'algorithme procède en 2 temps. D'abord il trouve les formes de réponses qui répondent à la question.
        // puis ajoute au résultat l'identifiant des réponses candidates qui respectent au moins une de ces formes.
        // remarque 3 : pour trouver les formes de réponses qui répondent à la question, on utilise l'index des formes, et on sélectionne
        // en appelant maxOccurences (avec seuil = nombre des mots-outils de la question) celles associées dans l'index à tous les mots-outils de la question.
        // remarque 4 : seuls les NBMOTS_FORME premiers mots-outils de la question sont pris en compte}
        ArrayList<String> motsQ = decoupeEnMots(question);
        ArrayList<Integer> fusionFormes = new ArrayList<>();
        int nbOutilsQ = 0;

        for (String m : motsQ) {
            if (nbOutilsQ >= NBMOTS_FORME) break;
            if (existeChaineDicho(motsOutils, m)) {
                String cle = m + "_" + nbOutilsQ;
                ArrayList<Integer> sorties = IndexFormes.rechercherSorties(cle);
                fusionFormes = fusion(fusionFormes, sorties);
                nbOutilsQ++;
            }
        }

        // On ne garde que les formes qui matchent TOUS les mots outils de la question
        ArrayList<Integer> idsFormesCompatibles = maxOccurences(fusionFormes, nbOutilsQ);

        // 2. Filtrer les candidats de l'étape 1
        ArrayList<Integer> resultatsFinaux = new ArrayList<>();

        for (Integer idRep : candidates) {
            String texteReponse = reponses.get(idRep);
            String formeCandidate = calculForme(texteReponse, motsOutils);
            int idFormeCandidate = -1;
            for(int i=0; i<formesReponses.size(); i++) {
                if(formesReponses.get(i).equals(formeCandidate)) {
                    idFormeCandidate = i;
                }
            }

            // On garde si la forme de cette réponse fait partie des formes compatibles
            // OU si aucune forme compatible n'a été trouvée (fallback, optionnel mais conseillé)
            if (idsFormesCompatibles.contains(idFormeCandidate)) {
                resultatsFinaux.add(idRep);
                }
            }

        // Si le filtrage par forme est trop strict et renvoie vide, on peut décider
        // de renvoyer les candidats de l'étape 1 (mais le sujet demande d'être strict)
        return resultatsFinaux;
    }

    static public boolean reponseExiste(String reponse,
                                        Index indexReponses,
                                        ArrayList<String> reponses,
                                        ArrayList<String> motsOutils) {
        //{}=>{résultat = true si la reponse est présente dans reponses et false sinon.
        // remarque 1 : utilise decoupeEnMots, rechercherSortiePourEntree, existeChaineDicho, rechercherSorties, fusion, maxOccurences
        // remarque 2 : Le vecteur reponses n'est pas trié. Afin d'éviter le coûteux parcours séquentiel du
        // vecteur, on utilise indexReponses pour trouver les réponses contenant tous les mots non outils de la
        // reponse, puis on vérifie si l'une d'entre elle est identique à reponse.}
        ArrayList<Integer> candidats = Utilitaire.constructionReponsesCandidates(reponse, indexReponses, motsOutils);

        for (Integer id : candidats) {
            if (reponses.get(id).equalsIgnoreCase(reponse)) {
                return true;
            }
        }

        return false;
    }


    static public boolean formeQuestionReponseExiste(String question,
                                                     String reponse,
                                                     Index indexFormes,
                                                     ArrayList<String> formesReponses,
                                                     ArrayList<String> motsOutils) {
        //{}=>{résultat = * true si la forme de reponse est présente dans formesReponses
        // et qu'elle est accessible à partir des mots de la question en utilisant indexFormes.
        //                * false sinon.
        // remarque 1 : utilise decoupeEnMots, rechercherSortiePourEntree, existeChaineDicho, rechercherSorties, fusion, maxOccurences, calculForme
        // remarque 2 : Le vecteur formesReponses n'est pas trié. Afin d'éviter le coûteux parcours séquentiel du
        // vecteur, et afin de vérifier l'accessibilité à partir des mots de la question en utilisant indexFormes,
        // on utilise indexFormes pour trouver les formes indexées par les mots-outils de la
        // question, puis on vérifie si l'une de ces formes est identique à la forme de reponse.
        // remarque 3 : seuls les NBMOTS_FORME premiers mots-outils de question sont pris en compte}
        // 1. Calculer la forme de la réponse
        String forme = Utilitaire.calculForme(reponse, motsOutils);

        // 2. Chercher son indice dans formesReponses
        int idForme = Utilitaire.rechercherChaine(formesReponses, forme);
        if (idForme == -1) return false;

        // 3. Trouver les formes accessibles depuis la question
        ArrayList<String> motsQ = Utilitaire.decoupeEnMots(question);
        ArrayList<Integer> fusionFormes = new ArrayList<>();
        int nbOutils = 0;

        for (String m : motsQ) {
            if (nbOutils >= Utilitaire.NBMOTS_FORME) break;

            if (Utilitaire.existeChaineDicho(motsOutils, m)) {
                String cle = m + "_" + nbOutils;
                ArrayList<Integer> sorties = indexFormes.rechercherSorties(cle);
                fusionFormes = Utilitaire.fusion(fusionFormes, sorties);
                nbOutils++;
            }
        }

        ArrayList<Integer> formesCompatibles = Utilitaire.maxOccurences(fusionFormes, nbOutils);

        // 4. Vérifier si la forme de la réponse fait partie des compatibles
        return formesCompatibles.contains(idForme);
    }
}
