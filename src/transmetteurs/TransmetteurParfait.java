package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

public class TransmetteurParfait<T> extends Transmetteur<T, T> {
    /**
     * reçoit une information.  Cette méthode, en fin d'exécution,
     * appelle la méthode émettre.
     *
     * @param information l'information  reçue
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void recevoir(Information<T> information) throws InformationNonConformeException {
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
        this.informationEmise = this.informationRecue;

        // Émission vers les composants connectés
        for (DestinationInterface<T> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }
}
