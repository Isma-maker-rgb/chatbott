import java.io.*;
import java.util.*;

public class Utilitaire {

    private static final int NBMOTS_FORME = 5; // On ne regarde que les 5 premiers mots-outils

    // --- LECTURE DES FICHIERS ---

    public static ArrayList<String> lireMotsOutils(String nomFichier) {
        ArrayList<String> liste = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileInputStream(nomFichier))) {
            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine().trim();
                if (!ligne.isEmpty()) liste.add(ligne);
            }
        } catch (IOException e) {
            System.err.println("Erreur lecture " + nomFichier);
        }
        return liste;
    }

    public static ArrayList<String> lireReponses(String nomFichier) {
        return lireFichierLigneParLigne(nomFichier);
    }

    public static ArrayList<String> lireQuestionsReponses(String nomFichier) {
        return lireFichierLigneParLigne(nomFichier);
    }

    private static ArrayList<String> lireFichierLigneParLigne(String nomFichier) {
        ArrayList<String> liste = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileInputStream(nomFichier))) {
            while (scanner.hasNextLine()) {
                liste.add(scanner.nextLine());
            }
        } catch (IOException e) {
            System.err.println("Erreur lecture " + nomFichier);
        }
        return liste;
    }

    // --- TRAITEMENT DES CHAINES ---

    private static ArrayList<String> decoupeEnMots(String contenu) {
        // Nettoyage : minuscule, et remplacement de tout ce qui n'est pas lettre/chiffre par espace
        String chaine = contenu.toLowerCase();
        // On remplace la ponctuation par des espaces
        chaine = chaine.replace('\'', ' ').replace('"', ' ').replace('.', ' ')
                       .replace(',', ' ').replace('?', ' ').replace('!', ' ')
                       .replace(':', ' ').replace(';', ' ').replace('-', ' ')
                       .replace('(', ' ').replace(')', ' ').replace('«', ' ')
                       .replace('»', ' ');

        String[] tab = chaine.split("\\s+"); // Split sur les espaces multiples
        ArrayList<String> resultat = new ArrayList<>();
        for (String s : tab) {
            if (!s.isEmpty()) resultat.add(s);
        }
        return resultat;
    }

    // --- ALGORITHMES DE RECHERCHE ET TRI ---

    public static void trierChaines(ArrayList<String> v) {
        // Tri à bulles ou insertion simple pour l'exercice (Java a Collections.sort mais on fait "à la main")
        // Ici j'utilise le tri par insertion pour la clarté
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

    private static boolean existeChaineDicho(ArrayList<String> lesChaines, String chaine) {
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

    public static boolean estUnNombre(String s) {
        if (s == null || s.isEmpty()) return false;
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    // --- ETAPE 1 : CONTENU ---

    // --- MODIFICATION DANS UTILITAIRE.JAVA ---

    // 1. On modifie l'indexation des réponses pour utiliser le thésaurus
    public static Index constructionIndexReponses(ArrayList<String> reponses, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        Index index = new Index();
        for (int i = 0; i < reponses.size(); i++) {
            ArrayList<String> mots = decoupeEnMots(reponses.get(i));
            for (String mot : mots) {
                // TRANSFORMATION ICI
                String motCanonique = thesaurus.rechercherSortiePourEntree(mot);
                
                if (!existeChaineDicho(motsOutils, motCanonique)) {
                    index.ajouterSortieAEntree(motCanonique, i);
                }
            }
        }
        return index;
    }

    // Fusionne deux listes triées en gardant les doublons (nécessaire pour maxOccurences)
    static ArrayList<Integer> fusion(ArrayList<Integer> v1, ArrayList<Integer> v2) {
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

    // Retourne les éléments qui apparaissent au moins 'seuil' fois
    static ArrayList<Integer> maxOccurences(ArrayList<Integer> v, int seuil) {
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

    // 2. On modifie la recherche des candidats pour utiliser le thésaurus
    public static ArrayList<Integer> constructionReponsesCandidates(String question, Index indexReponses, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        ArrayList<String> motsQuestion = decoupeEnMots(question);
        ArrayList<Integer> listeFusionnee = new ArrayList<>();
        int nbMotsSignificatifs = 0;

        for (String mot : motsQuestion) {
            // TRANSFORMATION ICI
            String motCanonique = thesaurus.rechercherSortiePourEntree(mot);
            
            if (!existeChaineDicho(motsOutils, motCanonique)) {
                nbMotsSignificatifs++;
                ArrayList<Integer> sorties = indexReponses.rechercherSorties(motCanonique);
                listeFusionnee = fusion(listeFusionnee, sorties);
            }
        }
        return maxOccurences(listeFusionnee, nbMotsSignificatifs);
    }

    // --- ETAPE 2 : FORME ---

    // Calcule la "signature" d'une phrase basée sur ses mots-outils (ex: "le ... a ... été ... par")
    static String calculForme(String chaine, ArrayList<String> motsOutils) {
        ArrayList<String> mots = decoupeEnMots(chaine);
        StringBuilder forme = new StringBuilder();
        int count = 0;

        for (String mot : mots) {
            if (count >= NBMOTS_FORME) break;
            
            if (existeChaineDicho(motsOutils, mot)) {
                if (forme.length() > 0) forme.append(" ");
                forme.append(mot);
                count++;
            } else if (estUnNombre(mot)) {
                // Pour gérer les dates/chiffres comme forme particulière
                if (forme.length() > 0) forme.append(" ");
                forme.append("num"); 
                count++;
            }
        }
        return forme.toString();
    }

    // Construit la liste des formes uniques trouvées dans le fichier reponses.txt
    public static ArrayList<String> constructionTableFormes(ArrayList<String> reponses, ArrayList<String> motsOutils) {
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
    
    // Helper simple pour trouver l'index d'une chaine dans une liste
    private static int rechercherIndice(ArrayList<String> liste, String mot) {
        for(int i=0; i<liste.size(); i++) {
            if(liste.get(i).equals(mot)) return i;
        }
        return -1;
    }

    // Construit l'index : Mots-outils de la question (+position) -> Indices des formes de réponses compatibles
    public static Index constructionIndexFormes(ArrayList<String> questionsReponses, ArrayList<String> formes, ArrayList<String> motsOutils) {
        Index index = new Index();

        for (String qr : questionsReponses) {
            int posSep = qr.indexOf("?");
            if (posSep == -1) continue;

            String question = qr.substring(0, posSep);
            String reponse = qr.substring(posSep + 1);

            // 1. Quelle est la forme de la réponse idéale ?
            String formeReponse = calculForme(reponse, motsOutils);
            int idForme = rechercherIndice(formes, formeReponse);

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

    public static ArrayList<Integer> selectionReponsesCandidates(String question,
                                                                 ArrayList<Integer> candidates,
                                                                 Index indexFormes,
                                                                 ArrayList<String> reponses,
                                                                 ArrayList<String> formesReponses,
                                                                 ArrayList<String> motsOutils) {
        
        // 1. Trouver les IDs des formes compatibles avec la question
        ArrayList<String> motsQ = decoupeEnMots(question);
        ArrayList<Integer> fusionFormes = new ArrayList<>();
        int nbOutilsQ = 0;

        for (String m : motsQ) {
            if (nbOutilsQ >= NBMOTS_FORME) break;
            if (existeChaineDicho(motsOutils, m)) {
                String cle = m + "_" + nbOutilsQ;
                ArrayList<Integer> sorties = indexFormes.rechercherSorties(cle);
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
            int idFormeCandidate = rechercherIndice(formesReponses, formeCandidate);

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

    public static void sauvegarderDansFichier(String contenu, String nomFichier) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier, true))) {
            writer.write(contenu);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erreur d'écriture : " + e.getMessage());
        }
    }

    // Nouvelle méthode pour extraire les mots-clés d'une phrase en utilisant le thésaurus
    public static ArrayList<String> extraireMotsCles(String phrase, ArrayList<String> motsOutils, Thesaurus thesaurus) {
        ArrayList<String> mots = decoupeEnMots(phrase);
        ArrayList<String> resultat = new ArrayList<>();
        for (String m : mots) {
            String canonique = thesaurus.rechercherSortiePourEntree(m.toLowerCase());
            if (!existeChaineDicho(motsOutils, canonique)) {
                resultat.add(canonique);
            }
        }
        return resultat;
    }
}