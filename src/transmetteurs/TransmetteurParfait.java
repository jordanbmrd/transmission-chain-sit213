package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

/**
 * Classe représentant un transmetteur parfait qui relaie directement
 * l'information reçue vers les destinations connectées sans altération.
 *
 * @param <T> le type des informations transmises.
 */
public class TransmetteurParfait<T> extends Transmetteur<T, T> {

    /**
     * Reçoit une information et la transmet.
     *
     * @param information l'information reçue.
     * @throws InformationNonConformeException si l'information est nulle ou non conforme.
     */
    @Override
    public void recevoir(Information<T> information) throws InformationNonConformeException {
        this.informationRecue = information;
        emettre();
    }

    /**
     * Émet l'information reçue vers les destinations connectées.
     *
     * @throws InformationNonConformeException si l'information est nulle.
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationRecue == null) {
            throw new InformationNonConformeException();
        }
        this.informationEmise = this.informationRecue;
        for (DestinationInterface<T> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }
}
