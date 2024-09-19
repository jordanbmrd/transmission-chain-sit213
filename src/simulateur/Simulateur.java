package simulateur;

import destinations.Destination;
import destinations.DestinationFinale;
import modulation.Modulateur;
import modulation.emetteurs.Emetteur;
import information.Information;
import modulation.recepteurs.Recepteur;
import sources.Source;
import sources.SourceAleatoire;
import sources.SourceFixe;
import transmetteurs.Transmetteur;
import transmetteurs.TransmetteurParfait;
import utils.Code;
import visualisations.SondeAnalogique;
import visualisations.SondeLogique;

/**
 * La classe Simulateur permet de construire et simuler une chaîne de
 * transmission composée d'une Source, d'un ou plusieurs Transmetteurs et d'une Destination.
 * Elle permet également d'ajouter des sondes pour observer l'évolution des signaux dans la chaîne de transmission.
 *
 * Les composants de la chaîne de transmission (Source, Emetteur, Transmetteur, Recepteur, Destination)
 * sont créés et connectés selon les paramètres fournis en entrée.
 *
 * @version 1.0
 * @since 2024
 */
public class Simulateur {

    /**
     * Indique si le Simulateur utilise des sondes d'affichage.
     */
    private boolean affichage = false;

    /**
     * Indique si le Simulateur génère un message de manière aléatoire.
     * Si faux, un message fixe est utilisé.
     */
    private boolean messageAleatoire = true;

    /**
     * Indique si le Simulateur utilise un germe pour initialiser les générateurs aléatoires.
     */
    private boolean aleatoireAvecGerme = false;

    /**
     * La valeur du germe utilisé pour initialiser les générateurs aléatoires (null par défaut).
     */
    private Integer seed = null;

    /**
     * La longueur du message aléatoire à transmettre si un message n'est pas imposé.
     */
    private int nbBitsMess = 100;

    /**
     * La chaîne de caractères représentant le message à transmettre si un message fixe est imposé.
     */
    private String messageString = "100";

    /**
     * Le type de codage utilisé pour la modulation (par défaut NRZ).
     */
    private Code code = Code.NRZ;

    /**
     * La valeur d'amplitude maximale pour la modulation.
     */
    private float aMax = 1f;

    /**
     * La valeur d'amplitude minimale pour la modulation.
     */
    private float aMin = 0f;

    /**
     * La taille de la période utilisée pour la modulation.
     */
    private int taillePeriode = 30;

    /**
     * Le composant Source de la chaîne de transmission.
     */
    private Source<Boolean> source = null;

    /**
     * Le composant Emetteur de la chaîne de transmission.
     */
    private Modulateur<Boolean, Float> emetteur = null;

    /**
     * Le composant Recepteur de la chaîne de transmission.
     */
    private Modulateur<Float, Boolean> recepteur = null;

    /**
     * Le composant Transmetteur parfait pour les signaux logiques de la chaîne de transmission.
     */
    private Transmetteur<Boolean, Boolean> transmetteurLogique = null;

    /**
     * Le composant Transmetteur parfait pour les signaux analogiques de la chaîne de transmission.
     */
    private Transmetteur<Float, Float> transmetteurAnalogique = null;

    /**
     * Le composant Destination de la chaîne de transmission.
     */
    private Destination<Boolean> destination = null;

    /**
     * Le constructeur de Simulateur permet de construire une chaîne de
     * transmission composée d'une Source <Boolean>, d'un Emetteur, d'un Recepteur et d'une Destination.
     * Les composants de la chaîne sont créés et connectés en fonction des arguments fournis.
     *
     * @param args le tableau des différents arguments de simulation.
     * @throws ArgumentsException si un des arguments est incorrect.
     */
    public Simulateur(String[] args) throws ArgumentsException {
        // Analyser et récupérer les arguments
        analyseArguments(args);

        // Choix de la source en fonction des paramètres
        if (messageAleatoire) {
            if (aleatoireAvecGerme) {
                this.source = new SourceAleatoire(nbBitsMess, seed);
            } else {
                this.source = new SourceAleatoire(nbBitsMess);
            }
        } else {
            this.source = new SourceFixe(messageString);
        }

        // Instanciation des composants
        this.emetteur = new Emetteur(taillePeriode, aMax, aMin, code);
        this.transmetteurAnalogique = new TransmetteurParfait<>();
        this.recepteur = new Recepteur(taillePeriode, aMax, aMin, code);
        this.destination = new DestinationFinale();

        // Connexion des différents composants
        this.source.connecter(this.emetteur);
        this.emetteur.connecter(this.transmetteurAnalogique);
        this.transmetteurAnalogique.connecter(this.recepteur);
        this.recepteur.connecter(this.destination);

        // Connexion des sondes (si l'option -s est utilisée)
        if (affichage) {
            this.source.connecter(new SondeLogique("Source " + code, 200));
            this.emetteur.connecter(new SondeAnalogique("Emetteur " + code));
            this.transmetteurAnalogique.connecter(new SondeAnalogique("Transmetteur " + code));
            this.recepteur.connecter(new SondeLogique("Recepteur " + code, 200));
        }
    }

    /**
     * La méthode analyseArguments extrait les différentes options de simulation depuis le tableau d'arguments
     * et met à jour les attributs correspondants.
     *
     * @param args le tableau des différents arguments de simulation.
     *             <br>
     *             Les options autorisées sont :
     *             <dl>
     *             <dt> -mess m </dt><dd> un message à transmettre : soit une chaîne de bits (7 ou plus) ou un nombre entier (1 à 6 chiffres) pour un message aléatoire</dd>
     *             <dt> -s </dt><dd> active les sondes d'affichage pour la simulation</dd>
     *             <dt> -seed v </dt><dd> initialise le générateur aléatoire avec la valeur v</dd>
     *             <dt> -code c </dt><dd> définit le type de codage : NRZ, RZ ou NRZT</dd>
     *             <dt> -aMax v </dt><dd> fixe l'amplitude maximale à v</dd>
     *             <dt> -aMin v </dt><dd> fixe l'amplitude minimale à v</dd>
     *             </dl>
     * @throws ArgumentsException si un des arguments est incorrect ou manquant.
     */
    private void analyseArguments(String[] args) throws ArgumentsException {
        // Traiter chaque argument
        for (int i = 0; i < args.length; i++) {
            // Diverses options de simulation
            if (args[i].matches("-s")) {
                affichage = true;
            } else if (args[i].matches("-seed")) {
                aleatoireAvecGerme = true;
                i++;
                try {
                    seed = Integer.parseInt(args[i]);
                } catch (Exception e) {
                    throw new ArgumentsException("Valeur du paramètre -seed invalide : " + args[i]);
                }
            } else if (args[i].matches("-mess")) {
                i++;
                messageString = args[i];
                if (args[i].matches("[0,1]{7,}")) {
                    messageAleatoire = false;
                    nbBitsMess = args[i].length();
                } else if (args[i].matches("[0-9]{1,6}")) {
                    messageAleatoire = true;
                    nbBitsMess = Integer.parseInt(args[i]);
                    if (nbBitsMess < 1) {
                        throw new ArgumentsException("Valeur du paramètre -mess invalide : " + nbBitsMess);
                    }
                } else {
                    throw new ArgumentsException("Valeur du paramètre -mess invalide : " + args[i]);
                }
            } else if (args[i].matches("-code")) {
                i++;
                switch (args[i]) {
                    case "NRZ":
                        this.code = Code.NRZ;
                        break;
                    case "NRZT":
                        this.code = Code.NRZT;
                        break;
                    case "RZ":
                        this.code = Code.RZ;
                        break;
                    default:
                        throw new ArgumentsException("Valeur du paramètre -code invalide : " + args[i]);
                }
            } else if (args[i].matches("-aMax")) {
                i++;
                try {
                    aMax = Float.parseFloat(args[i]);
                } catch (Exception e) {
                    throw new ArgumentsException("Valeur du paramètre -aMax invalide : " + args[i]);
                }
            } else if (args[i].matches("-aMin")) {
                i++;
                try {
                    aMin = Float.parseFloat(args[i]);
                } catch (Exception e) {
                    throw new ArgumentsException("Valeur du paramètre -aMin invalide : " + args[i]);
                }
            } else {
                throw new ArgumentsException("Option invalide : " + args[i]);
            }
        }
    }

    /**
     * La méthode execute effectue l'émission du message par la Source de la chaîne de transmission.
     *
     * @throws Exception si une erreur survient lors de l'exécution de la simulation.
     */
    public void execute() throws Exception {
        source.emettre();
    }

    /**
     * Calcule le Taux d'Erreur Binaire (TEB) en comparant le message émis avec le message reçu.
     *
     * @return le Taux d'Erreur Binaire (TEB).
     */
    public float calculTauxErreurBinaire() {
        Information<Boolean> messageEmis = this.source.getInformationEmise();
        Information<Boolean> messageRecu = this.destination.getInformationRecue();

        if (messageEmis.nbElements() != messageRecu.nbElements()) {
            throw new IllegalArgumentException("La taille du message émis est différente de celle du message reçu.");
        }

        int nbBits = messageRecu.nbElements();
        if (nbBits == 0) {
            return 0f;
        }

        int nbErreurs = 0;
        for (int i = 0; i < nbBits; ++i) {
            if (!messageEmis.iemeElement(i).equals(messageRecu.iemeElement(i))) {
                nbErreurs++;
            }
        }

        return (float) nbErreurs / nbBits;
    }

    /**
     * La méthode main crée une instance de Simulateur avec les arguments fournis,
     * exécute la simulation et affiche le résultat du Taux d'Erreur Binaire (TEB).
     *
     * @param args les différents arguments pour la configuration de la simulation.
     */
    public static void main(String[] args) {
        Simulateur simulateur = null;

        try {
            simulateur = new Simulateur(args);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(-1);
        }

        try {
            simulateur.execute();
            String s = "java Simulateur ";
            for (String arg : args) {
                s += arg + " ";
            }
            System.out.println(s + " => TEB : " + simulateur.calculTauxErreurBinaire());
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            System.exit(-2);
        }
    }
}
