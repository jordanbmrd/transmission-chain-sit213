package destinations;

import information.Information;
import information.InformationNonConformeException;

/**
 * Classe représentant la destination finale dans la chaîne de transmission.
 * Cette classe reçoit des informations binaires et les stocke.
 */
public class DestinationFinale extends Destination<Boolean> {

    /**
     * Reçoit une information binaire.
     *
     * @param information l'information binaire à recevoir.
     * @throws InformationNonConformeException si l'information est nulle ou non conforme.
     */
    @Override
    public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
        if (information == null) {
            throw new InformationNonConformeException();
        }
        this.informationRecue = information;
    }
}