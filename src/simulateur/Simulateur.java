package simulateur;

import destinations.Destination;
import destinations.DestinationFinale;
import information.Information;
import modulation.Modulateur;
import modulation.emetteurs.Emetteur;
import modulation.recepteurs.Recepteur;
import org.apache.commons.math3.special.Erf;
import sources.Source;
import sources.SourceAleatoire;
import sources.SourceFixe;
import transmetteurs.Transmetteur;
import transmetteurs.TransmetteurGaussien;
import transmetteurs.TransmetteurMultiTrajets;
import transmetteurs.TransmetteurParfait;
import utils.Form;
import visualisations.SondeAnalogique;
import visualisations.SondeLogique;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * La classe Simulateur permet de construire et simuler une chaîne de
 * transmission composée d'une Source, d'un ou plusieurs Transmetteurs et d'une Destination.
 * Elle permet également d'ajouter des sondes pour observer l'évolution des signaux dans la chaîne de transmission.
 * <p>
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
    private Form form = Form.RZ;

    /**
     * La valeur d'amplitude maximale pour la modulation.
     */
    private float aMax = 1f;

    /**
     * La valeur d'amplitude minimale pour la modulation.
     */
    private float aMin = 0f;

    private float snrpb = Float.NaN;    // Pas de valeur par défaut

    /**
     * La taille de la période utilisée pour la modulation.
     */
    private int nbEch = 30;

    /**
     * Décalage temporel (en nombre d'échantillons).
     * */
    private float[][] ti = null;

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
     * Le composant TransmetteurParfait pour les signaux logiques de la chaîne de transmission.
     */
    private Transmetteur<Boolean, Boolean> transmetteurLogique = null;

    /**
     * Le composant TransmetteurAnalogique pour les signaux analogiques de la chaîne de transmission.
     */
    private Transmetteur<Float, Float> transmetteurAnalogique = null;

    /**
     * Le composant TransmetteurMultiTrajets pour les signaux multi-trajets de la chaîne de transmission.
     * */
    private Transmetteur<Float, Float> transmetteurMultiTrajets = null;

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
            }
            else {
                this.source = new SourceAleatoire(nbBitsMess);
            }
        }
        else {
            this.source = new SourceFixe(messageString);
        }

        this.emetteur = new Emetteur(nbEch, aMax, aMin, form);
        // Sonde de l'émetteur
        if (affichage)
            this.emetteur.connecter(new SondeAnalogique("Émetteur " + form));

        this.source.connecter(this.emetteur);
        // Sonde de la source
        if (affichage)
            this.source.connecter(new SondeLogique("Source " + form, 200));

        // Si le SNR par bit est défini
        if (!Float.isNaN(snrpb)) {
            if (aleatoireAvecGerme) {
                this.transmetteurAnalogique = new TransmetteurGaussien(form, nbEch, snrpb, seed);
            }
            else {
                this.transmetteurAnalogique = new TransmetteurGaussien(form, nbEch, snrpb);
            }
        }
        else {
            this.transmetteurAnalogique = new TransmetteurParfait<>();
        }

        // Sonde du transmetteur analogique
        if (affichage)
            this.transmetteurAnalogique.connecter(new SondeAnalogique("Transmetteur analogique " + form));

        if (ti != null) {
            this.transmetteurMultiTrajets = new TransmetteurMultiTrajets(ti);

            // Connexion de l'émetteur au transmetteur multi-trajets
            this.emetteur.connecter(this.transmetteurMultiTrajets);

            // Puis du transmetteur multi-trajets au transmetteur analogique
            this.transmetteurMultiTrajets.connecter(this.transmetteurAnalogique);

            // Sonde du transmetteur multi-trajets
            if (affichage)
                this.transmetteurMultiTrajets.connecter(new SondeAnalogique("Transmetteur multi-trajets " + form));
        } else {
            // Connexion de l'émetteur au transmetteur analogique
            this.emetteur.connecter(this.transmetteurAnalogique);
        }

        this.recepteur = new Recepteur(nbEch, aMax, aMin, form);
        this.destination = new DestinationFinale();

        this.transmetteurAnalogique.connecter(this.recepteur);
        this.recepteur.connecter(this.destination);

        // Sonde du récepteur
        if (affichage)
            this.recepteur.connecter(new SondeLogique("Recepteur " + form, 200));
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
     *             <dt> -form c </dt><dd> définit le type de codage : NRZ, RZ ou NRZT</dd>
     *             <dt> -ampl aMin aMax </dt><dd> fixe les amplitudes minimales et maximales</dd>
     *             <dt> -ti dt ar </dt><dd> définit les couples de valeurs (décalage temporel, amplitude relative). 5 maximums.</dd>
     *             </dl>
     * @throws ArgumentsException si un des arguments est incorrect ou manquant.
     */
    private void analyseArguments(String[] args) throws ArgumentsException {
        Iterator<String> param = Arrays.asList(args).iterator();
        while (param.hasNext()) {
            String arg = param.next();
            switch (arg) {
                case "-s":
                    affichage = true;
                    break;
                case "-seed":
                    traiterSeed(param);
                    break;
                case "-mess":
                    traiterMessage(param);
                    break;
                case "-form":
                    traiterForm(param);
                    break;
                case "-ampl":
                    traiterAmpl(param);
                    break;
                case "-nbEch":
                    traiterNbEch(param);
                    break;
                case "-snrpb":
                    traiterSnrpb(param);
                    break;
                case "-ti":
                    traiterTi(param);
                    break;
                default:
                    throw new ArgumentsException("Option invalide : " + arg);
            }
        }
    }

    /**
     * Traite l'argument de la graine (seed) et met à jour les attributs correspondants.
     *
     * @param param l'itérateur sur les paramètres d'entrée.
     * @throws ArgumentsException si l'argument seed est invalide.
     */
    private void traiterSeed(Iterator<String> param) throws ArgumentsException {
        seed = parseIntegerArgument(param, "seed");
        aleatoireAvecGerme = true;
    }

    /**
     * Traite l'argument du message et met à jour les attributs correspondants.
     *
     * @param param l'itérateur sur les paramètres d'entrée.
     * @throws ArgumentsException si l'argument message est invalide.
     */
    private void traiterMessage(Iterator<String> param) throws ArgumentsException {
        String message = getNextArgument(param, "mess");

        if (message.matches("[0,1]{7,}")) {
            messageAleatoire = false;
            nbBitsMess = message.length();
            messageString = message;
        }
        else if (message.matches("[0-9]{1,6}")) {
            messageAleatoire = true;
            nbBitsMess = Integer.parseInt(message);
            if (nbBitsMess < 1) {
                throw new ArgumentsException("Valeur du paramètre -mess invalide.");
            }
        }
        else {
            throw new ArgumentsException("Valeur du paramètre -mess invalide.");
        }
    }

    /**
     * Traite l'argument de la forme de codage et met à jour les attributs correspondants.
     *
     * @param param l'itérateur sur les paramètres d'entrée.
     * @throws ArgumentsException si l'argument form est invalide.
     */
    private void traiterForm(Iterator<String> param) throws ArgumentsException {
        String formArg = getNextArgument(param, "form");
        try {
            form = Form.valueOf(formArg);
        } catch (IllegalArgumentException e) {
            throw new ArgumentsException("Valeur du paramètre -form invalide.");
        }
    }

    /**
     * Traite l'argument de l'amplitude et met à jour les attributs correspondants.
     *
     * @param param l'itérateur sur les paramètres d'entrée.
     * @throws ArgumentsException si l'argument amplitude est invalide.
     */
    private void traiterAmpl(Iterator<String> param) throws ArgumentsException {
        aMin = parseFloatArgument(param, "ampl aMin");
        aMax = parseFloatArgument(param, "ampl aMax");
        if (aMin >= aMax) {
            throw new ArgumentsException("La valeur aMin doit être strictement inférieure à aMax.");
        }
    }

    /**
     * Traite l'argument du nombre d'échantillons et met à jour les attributs correspondants.
     *
     * @param param l'itérateur sur les paramètres d'entrée.
     * @throws ArgumentsException si l'argument nbEch est invalide.
     */
    private void traiterNbEch(Iterator<String> param) throws ArgumentsException {
        nbEch = parseIntegerArgument(param, "nbEch");
        if (nbEch < 0) {
            throw new ArgumentsException("La valeur du paramètre -nbEch doit être entière et positive.");
        }
    }

    /**
     * Traite l'argument du rapport signal sur bruit par bit (snrpb) et met à jour les attributs correspondants.
     *
     * @param param l'itérateur sur les paramètres d'entrée.
     * @throws ArgumentsException si l'argument snrpb est invalide.
     */
    private void traiterSnrpb(Iterator<String> param) throws ArgumentsException {
        snrpb = parseFloatArgument(param, "snrpb");
    }

    /**
     * Traite l'argument des décalages temporels (ti) et met à jour les attributs correspondants.
     *
     * @param param l'itérateur sur les paramètres d'entrée.
     * @throws ArgumentsException si l'argument ti est invalide.
     */
    private void traiterTi(Iterator<String> param) throws ArgumentsException {
        LinkedList<float[]> tiList = new LinkedList<>();

        // Lecture des paires (dt, ar) pour les trajets indirects, jusqu'à un maximum de 5 paires
        for (int i = 0; i < 5 && param.hasNext(); i++) {
            int dt = parseIntegerArgument(param, "ti dt");
            float ar = parseFloatArgument(param, "ti ar");

            // Vérification des contraintes sur l'amplitude relative (ar doit être entre 0 et 1)
            if (ar < 0 || ar > 1) {
                throw new ArgumentsException("L'amplitude relative (ar) doit être comprise entre 0 et 1 pour le paramètre -ti.");
            }

            tiList.add(new float[]{dt, ar});
        }

        // Si aucun trajet indirect n'a été spécifié, ajouter les valeurs par défaut {0, 0.0f}
        if (tiList.isEmpty()) {
            tiList.add(new float[]{0, 0.0f});
        }

        // Convertir la liste en tableau 2D float[][]
        ti = new float[tiList.size()][2];
        for (int i = 0; i < tiList.size(); i++) {
            ti[i][0] = tiList.get(i)[0]; // dt
            ti[i][1] = tiList.get(i)[1]; // ar
        }
    }

    private String getNextArgument(Iterator<String> param, String paramName) throws ArgumentsException {
        if (!param.hasNext()) {
            throw new ArgumentsException("Paramètre -" + paramName + " manquant.");
        }
        return param.next();
    }

    private int parseIntegerArgument(Iterator<String> param, String paramName) throws ArgumentsException {
        try {
            return Integer.parseInt(getNextArgument(param, paramName));
        } catch (NumberFormatException e) {
            throw new ArgumentsException("Valeur du paramètre -" + paramName + " invalide.");
        }
    }

    private float parseFloatArgument(Iterator<String> param, String paramName) throws ArgumentsException {
        try {
            return Float.parseFloat(getNextArgument(param, paramName));
        } catch (NumberFormatException e) {
            throw new ArgumentsException("Valeur du paramètre -" + paramName + " invalide.");
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

    public double calculProbaErreur() {
        // Conversion du rapport Eb/N0 en linéaire
        float ebN0Lin = (float) Math.pow(10, this.transmetteurAnalogique.getEbN0dB() / 10);

        // Formule de calcul de la probabilité d'erreur
        return switch (form) {
            case Form.NRZ, Form.NRZT -> 0.5 * Erf.erfc(Math.sqrt(ebN0Lin));
            case Form.RZ -> 0.5*Erf.erfc((1/Math.sqrt(2))*Math.sqrt(ebN0Lin));
        };
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
            StringBuilder string = new StringBuilder("java Simulateur ");
            for (String arg : args) {
                string.append(arg).append(" ");
            }

            string.append("\n => TEB : ").append(simulateur.calculTauxErreurBinaire());

            string.append("\n - Nombre de bits de la séquence : ").append(simulateur.nbBitsMess);
            string.append("\n - Nombre d'échantillons par bit : ").append(simulateur.nbEch);

            if (!Float.isNaN(simulateur.snrpb)) {
                string.append("\n => Puissance moyenne du bruit : ").append(simulateur.transmetteurAnalogique.getPuissanceMoyenneBruit());
                string.append("\n => Variance : ").append(simulateur.transmetteurAnalogique.getVariance());
                string.append("\n => Rapport signal-sur-bruit (S/N, en dB) : ").append(simulateur.transmetteurAnalogique.getSNRReel());
                string.append("\n => Rapport Eb/N0 (en dB) : ").append(simulateur.transmetteurAnalogique.getEbN0dB());
                string.append("\n => Probabilité d'erreur (forme ").append(simulateur.form.toString()).append(") : ").append(simulateur.calculProbaErreur());
            }

            System.out.println(string);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            System.exit(-2);
        }
    }
}
