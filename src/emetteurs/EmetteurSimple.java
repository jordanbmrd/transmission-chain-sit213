package emetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import utils.Code;

/**
 * Emetteur qui transmet parfaitement les informations reçues
 * sans altération.s
 */
public class EmetteurSimple extends Emetteur<Boolean, Float> {

    /**
     * reçoit une information. Cette méthode, en fin d'exécution,
     * appelle la méthode émettre.
     * @param information l'information reçue
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
        this.informationRecue = information;
        emettre();
    }

    /**
     * émet l'information construite par l'émetteur
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        this.informationEmise = conversionNA(this.informationRecue, Code.NRZ, 5, 0);

        // Émission vers les composants connectés
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }
}
