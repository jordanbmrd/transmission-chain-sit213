package destinations;

import information.Information;
import information.InformationNonConformeException;

public class DestinationFinale extends Destination<Float> {
    /**
     * reçoit une information
     *
     * @param information l'information  à recevoir
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        this.informationRecue = information;
    }
}
