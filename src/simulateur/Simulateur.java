package simulateur;

import destinations.Destination;
import destinations.DestinationFinale;
import modulation.Modulateur;
import modulation.emetteurs.EmetteurNRZ;
import modulation.emetteurs.EmetteurRZ;
import information.Information;
import modulation.recepteurs.RecepteurNRZ;
import modulation.recepteurs.RecepteurRZ;
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
 * transmission composée d'une Source, d'un nombre variable de
 * Transmetteur(s) et d'une Destination.
 *
 * @author cousin
 * @author prou
 */
public class Simulateur {

    /**
     * indique si le Simulateur utilise des sondes d'affichage
     */
    private boolean affichage = false;

    /**
     * indique si le Simulateur utilise un message généré de manière aléatoire (message imposé sinon)
     */
    private boolean messageAleatoire = true;

    /**
     * indique si le Simulateur utilise un germe pour initialiser les générateurs aléatoires
     */
    private boolean aleatoireAvecGerme = false;

    /**
     * la valeur de la semence utilisée pour les générateurs aléatoires
     */
    private Integer seed = null; // pas de semence par défaut

    /**
     * la longueur du message aléatoire à transmettre si un message n'est pas imposé
     */
    private int nbBitsMess = 100;

    /**
     * la chaîne de caractères correspondant à m dans l'argument -mess m
     */
    private String messageString = "100";

    /*
    * Code à utiliser
    * */
    private Code code = null;

    /*
    * Amplitudes
    * */
    private float aMax = 1f;
    private float aMin = 0f;

    /*
    * Taille de la période
    * */
    private int taillePeriode = 30;

    /**
     * le  composant Source de la chaine de transmission
     */
    private Source<Boolean> source = null;

    /**
     * le  composant Emetteur de la chaine de transmission
     */
    private Modulateur<Boolean, Float> emetteur = null;

    /**
     * le composant Recepteur de la chaine de transmission
     */
    private Modulateur<Float, Boolean> recepteur = null;

    /**
     * le  composant Transmetteur parfait logique de la chaine de transmission
     */
    private Transmetteur<Boolean, Boolean> transmetteurLogique = null;
    private Transmetteur<Float, Float> transmetteurAnalogique = null;

    /**
     * le  composant Destination de la chaine de transmission
     */
    private Destination<Boolean> destination = null;


    /**
     * Le constructeur de Simulateur construit une chaîne de
     * transmission composée d'une Source <Boolean>, d'une Destination
     * <Boolean> et de Transmetteur(s) [voir la méthode
     * analyseArguments]...  <br> Les différents composants de la
     * chaîne de transmission (Source, Transmetteur(s), Destination,
     * Sonde(s) de visualisation) sont créés et connectés.
     *
     * @param args le tableau des différents arguments.
     * @throws ArgumentsException si un des arguments est incorrect
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
        //this.emetteur = new EmetteurNRZ(taillePeriode);
        this.emetteur = new EmetteurRZ(taillePeriode, aMax, aMin);
        //this.transmetteurLogique = new TransmetteurParfait<>();
        this.transmetteurAnalogique = new TransmetteurParfait<>();
        //this.recepteur = new RecepteurNRZ(taillePeriode);
        this.recepteur = new RecepteurRZ(taillePeriode, aMax, aMin);
        this.destination = new DestinationFinale();

        // Connexion des différents composants
        this.source.connecter(this.emetteur);
        this.emetteur.connecter(this.transmetteurAnalogique);
        this.transmetteurAnalogique.connecter(this.recepteur);
        this.recepteur.connecter(this.destination);

        // Connexion des sondes (si l'option -s est pas utilisée)
        if (affichage) {
            this.source.connecter(new SondeLogique("source", 200));
            this.emetteur.connecter(new SondeAnalogique("emetteur"));
            this.recepteur.connecter(new SondeLogique("recepteur", 200));
        }
    }


    /**
     * La méthode analyseArguments extrait d'un tableau de chaînes de
     * caractères les différentes options de la simulation.  <br>Elle met
     * à jour les attributs correspondants du Simulateur.
     *
     * @param args le tableau des différents arguments.
     *             <br>
     *             <br>Les arguments autorisés sont :
     *             <br>
     *             <dl>
     *             <dt> -mess m  </dt><dd> m (String) constitué de 7 ou plus digits à 0 | 1, le message à transmettre</dd>
     *             <dt> -mess m  </dt><dd> m (int) constitué de 1 à 6 digits, le nombre de bits du message "aléatoire" à transmettre</dd>
     *             <dt> -s </dt><dd> pour demander l'utilisation des sondes d'affichage</dd>
     *             <dt> -seed v </dt><dd> v (int) d'initialisation pour les générateurs aléatoires</dd>
     *             </dl>
     * @throws ArgumentsException si un des arguments est incorrect.
     */
    private void analyseArguments(String[] args) throws ArgumentsException {
        for (int i = 0; i < args.length; i++) { // traiter les arguments 1 par 1
            if (args[i].matches("-s")) {
                affichage = true;
            } else if (args[i].matches("-seed")) {
                aleatoireAvecGerme = true;
                i++;
                // traiter la valeur associee
                try {
                    seed = Integer.parseInt(args[i]);
                } catch (Exception e) {
                    throw new ArgumentsException("Valeur du parametre -seed  invalide :" + args[i]);
                }
            } else if (args[i].matches("-mess")) {
                i++;
                // traiter la valeur associee
                messageString = args[i];
                if (args[i].matches("[0,1]{7,}")) { // au moins 7 digits
                    messageAleatoire = false;
                    nbBitsMess = args[i].length();
                } else if (args[i].matches("[0-9]{1,6}")) { // de 1 à 6 chiffres
                    messageAleatoire = true;
                    nbBitsMess = Integer.parseInt(args[i]);
                    if (nbBitsMess < 1)
                        throw new ArgumentsException("Valeur du parametre -mess invalide : " + nbBitsMess);
                } else
                    throw new ArgumentsException("Valeur du parametre -mess invalide : " + args[i]);
            } else if (args[i].matches("-code")) {
                i++;
                // traiter la valeur associee
                if (args[i].matches(String.valueOf(Code.NRZ))){
                    this.code = Code.NRZ;
                } else if (args[i].matches(String.valueOf(Code.NRZT))){
                    this.code = Code.NRZT;
                } else if (args[i].matches(String.valueOf(Code.RZ))){
                    this.code = Code.RZ;
                } else {
                    throw new ArgumentsException("Valeur du parametre -code invalide : " + args[i]);
                }
            } else if (args[i].matches("-aMax")) {
                i++;
                // traiter la valeur associee
                try {
                    aMax = Float.parseFloat(args[i]);
                } catch (Exception e) {
                    throw new ArgumentsException("Valeur du parametre -aMax  invalide :" + args[i]);
                }
            } else if (args[i].matches("-aMin")) {
                i++;
                // traiter la valeur associee
                try {
                    aMin = Float.parseFloat(args[i]);
                } catch (Exception e) {
                    throw new ArgumentsException("Valeur du parametre -aMin  invalide :" + args[i]);
                }
            }

            else throw new ArgumentsException("Option invalide :" + args[i]);
        }

    }


    /**
     * La méthode execute effectue un envoi de message par la source
     * de la chaîne de transmission du Simulateur.
     *
     * @throws Exception si un problème survient lors de l'exécution
     */
    public void execute() throws Exception {
        // La source émet le message
        source.emettre();
    }


    /**
     * La méthode qui calcule le taux d'erreur binaire en comparant
     * les bits du message émis avec ceux du message reçu.
     *
     * @return La valeur du Taux dErreur Binaire.
     */
    public float calculTauxErreurBinaire() {
        Information<Boolean> messageEmis = this.source.getInformationEmise();
        Information<Boolean> messageRecu = this.destination.getInformationRecue();

        // Vérification de la taille des messages
        if (messageEmis.nbElements() != messageRecu.nbElements()) {
            throw new IllegalArgumentException("La taille du message émis est différente de celle du message reçu.");
        }

        int nbBits = messageRecu.nbElements();
        if (nbBits == 0) {
            return 0f;
        }

        int nbErreurs = 0;

        // Parcours des éléments pour comparer bit par bit
        for (int i = 0; i < nbBits; ++i) {
            if (messageEmis.iemeElement(i) != messageRecu.iemeElement(i)) {
                nbErreurs++;
            }
        }

        // Calcul du taux d'erreur
        return (float) nbErreurs / nbBits;
    }


    /**
     * La fonction main instancie un Simulateur à l'aide des
     * arguments paramètres et affiche le résultat de l'exécution
     * d'une transmission.
     *
     * @param args les différents arguments qui serviront à l'instanciation du Simulateur.
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
            String s = "java  Simulateur  ";
            for (int i = 0; i < args.length; i++) { //copier tous les paramètres de simulation
                s += args[i] + "  ";
            }
            System.out.println(s + "  =>   TEB : " + simulateur.calculTauxErreurBinaire());
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            System.exit(-2);
        }
    }
}

