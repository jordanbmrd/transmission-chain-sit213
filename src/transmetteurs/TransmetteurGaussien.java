package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import utils.Form;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class TransmetteurGaussien extends Transmetteur<Float, Float> {
    private final boolean saveNoiseToCSV = false;    // Mettre à true pour enregistrer le bruit dans un CSV

    private final int nbEch;
    private final float SNRdB;
    private final int seed;

    private float variance;
    private float snrReel;
    private float ebN0dB;
    private float puissanceMoyenneSignal;
    private float puissanceMoyenneBruit;
    private Random random;

    public TransmetteurGaussien(int nbEch, float SNRdB, int seed) {
        this.nbEch = nbEch;
        this.SNRdB = SNRdB;
        this.seed = seed;
        initialiserRandom();
    }

    public TransmetteurGaussien(int nbEch, float SNRdB) {
        this(nbEch, SNRdB, 0);
    }

    /**
     * Initialise le générateur de nombres aléatoires avec ou sans graine.
     */
    private void initialiserRandom() {
        if (seed != 0) {
            this.random = new Random(seed);
        } else {
            this.random = new Random();
        }
    }

    /**
     * Reçoit une information et émet l'information bruitée.
     *
     * @param information l'information reçue
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        if (information == null) {
            throw new InformationNonConformeException("L'information reçue est nulle.");
        }
        this.informationRecue = information;
        emettre();
    }

    /**
     * Émet l'information construite par le transmetteur après ajout du bruit gaussien.
     *
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationRecue == null) {
            throw new InformationNonConformeException("Aucune information reçue à émettre.");
        }

        calculerPuissanceMoyenneSignal();
        calculerVariance();

        this.informationEmise = ajouterBruit(this.informationRecue);

        if (saveNoiseToCSV) {
            // Enregistrement dans un fichier CSV
            String csvFileName = "valeurs_bruit_gaussien.csv";

            try (PrintWriter writer = new PrintWriter(new FileWriter(csvFileName))) {
                // Écriture de l'en-tête du CSV (facultatif)
                writer.println("Bruit gaussien");

                // Écriture des valeurs dans le fichier CSV
                for (Float value : getBruitList()) {
                    writer.println(value);
                }

                System.out.println("Données enregistrées dans " + csvFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        calculerPuissanceMoyenneBruit();
        calculerSNRReel();
        calculerEbN0dB();

        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }

    /**
     * Calcule la puissance moyenne du signal reçu.
     */
    private void calculerPuissanceMoyenneSignal() {
        float somme = 0;
        for (float value : this.informationRecue) {
            somme += value * value;
        }
        this.puissanceMoyenneSignal = somme / this.informationRecue.nbElements();
    }

    private void calculerPuissanceMoyenneBruit() {
        float somme = 0;
        for (float value : this.bruitList) {
            somme += value * value;
        }
        this.puissanceMoyenneBruit = somme / this.bruitList.nbElements();
    }

    /**
     * Calcule la variance du bruit en fonction du SNR.
     */
    private void calculerVariance() {
        this.variance = (this.puissanceMoyenneSignal * nbEch) / (float) (2 * Math.pow(10, SNRdB / 10));
    }

    /**
     * Calcule le SNR réel obtenu
     */
    private void calculerSNRReel() {
        this.snrReel = (float) (10 * Math.log10((this.puissanceMoyenneSignal * nbEch) / (2 * this.puissanceMoyenneBruit)));
    }

    /**
     * Calcule le rapport Eb/N0 en dB
     */
    private void calculerEbN0dB() {
        this.ebN0dB = (float) (10 * Math.log10(Math.pow(10, (this.puissanceMoyenneSignal * nbEch) / (2 * this.puissanceMoyenneBruit))));
    }

    /**
     * Ajoute un bruit gaussien à l'information reçue.
     *
     * @param informationRecue l'information sans bruit
     * @return l'information avec le bruit ajouté
     * @throws InformationNonConformeException si l'information est nulle
     */
    private Information<Float> ajouterBruit(Information<Float> informationRecue) throws InformationNonConformeException {
        if (informationRecue == null) {
            throw new InformationNonConformeException("L'information non bruitée est nulle.");
        }

        Information<Float> informationBruitee = new Information<>();
        for (float value : informationRecue) {
            double bruit = random.nextGaussian() * Math.sqrt(variance);
            bruitList.add((float) bruit);
            informationBruitee.add(value + (float) bruit);
        }

        return informationBruitee;
    }

    @Override
    public float getVariance() {
        return this.variance;
    }

    @Override
    public float getPuissanceMoyenneBruit() {
        return this.puissanceMoyenneBruit;
    }

    @Override
    public float getSNRReel() {
        return this.snrReel;
    }

    @Override
    public float getEbN0dB() {
        return this.ebN0dB;
    }
}
