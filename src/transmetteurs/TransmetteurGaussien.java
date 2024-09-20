package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

public class TransmetteurGaussien extends Transmetteur<Float, Float> {

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
        this.informationEmise = this.informationRecue;
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }

    private Information<Float> ajouterBruit(Information<Float> informationNonBruitee) throws InformationNonConformeException {
        Information<Float> informationBruitee = new Information<>();

        if (informationNonBruitee == null) {
            throw new InformationNonConformeException();
        }

        return informationBruitee;
    }
}
