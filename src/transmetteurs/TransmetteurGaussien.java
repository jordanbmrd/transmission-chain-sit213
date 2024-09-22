package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

import java.util.Random;

public class TransmetteurGaussien extends Transmetteur<Float, Float> {
    private final int nbEch;
    private final float SNRdB;
    private final int seed;

    private float variance;
    private float puissanceMoyenneSignal;
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

        calculerVariance();

        Information<Float> informationBruitee = ajouterBruit(this.informationRecue);
        this.informationEmise = informationBruitee; // Récup l'information émise (pour les tests)

        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationBruitee);
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

    /**
     * Calcule la variance du bruit en fonction du SNR.
     */
    private void calculerVariance() {
        calculerPuissanceMoyenneSignal();
        this.variance = (this.puissanceMoyenneSignal * nbEch) / (float) (2 * Math.pow(10, SNRdB / 10));
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
            informationBruitee.add(value + (float) bruit);
        }

        return informationBruitee;
    }
}
