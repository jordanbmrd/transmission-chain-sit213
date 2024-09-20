package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

import java.util.LinkedList;
import java.util.Random;

public class TransmetteurGaussien extends Transmetteur<Float, Float> {
    private final int nbEch;
    private final float SNRdB;
    private final int seed;

    private float variance;
    private float puissanceMoyenneSignal;

    public TransmetteurGaussien(int nbEch, float SNRdB, int seed) {
        this.nbEch = nbEch;
        this.SNRdB = SNRdB;
        this.seed = seed;
    }

    public TransmetteurGaussien(int nbEch, float SNRdB) {
        this(nbEch, SNRdB, 0);
    }

    /**
     * reçoit une information.  Cette méthode, en fin d'exécution,
     * appelle la méthode émettre.
     *
     * @param information l'information  reçue
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        this.informationRecue = information;
        emettre();
    }

    /**
     * émet l'information construite par le transmetteur
     *
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationRecue == null) {
            throw new InformationNonConformeException();
        }

        calculerVariance();

        this.informationEmise = ajouterBruit(this.informationRecue);
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }

    private void calculerPuissanceMoyenneSignal() {
        float somme = 0;
        for (float value : this.informationRecue) {
            somme += (float) Math.pow(value, 2);
        }
        this.puissanceMoyenneSignal = somme / this.informationRecue.nbElements();
    }

    private void calculerVariance() {
        calculerPuissanceMoyenneSignal();
        this.variance = (this.puissanceMoyenneSignal * nbEch) / (float) (2 * Math.pow(10, SNRdB / 10));
    }

    private Information<Float> ajouterBruit(Information<Float> informationNonBruitee) throws InformationNonConformeException {
        Information<Float> informationBruitee = new Information<>();

        if (informationNonBruitee == null) {
            throw new InformationNonConformeException();
        }

        Random random;
        double bruit;

        if (seed != 0) {
            random = new Random(seed);
        } else {
            random = new Random();
        }

        calculerVariance();

        for (float value : informationNonBruitee) {
            bruit = random.nextGaussian() * Math.sqrt(variance);
            informationBruitee.add(value + (float) bruit);
        }

        return informationBruitee;
    }
}
