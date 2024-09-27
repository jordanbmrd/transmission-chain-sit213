package transmetteurs;

import information.Information;
import information.InformationNonConformeException;

public class TransmetteurMultiTrajets extends Transmetteur<Float, Float> {
    /**
     * reçoit une information. Cette méthode, en fin d'exécution,
     * appelle la méthode émettre.
     *
     * @param information l'information reçue
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {

    }

    /**
     * émet l'information construite par le transmetteur
     *
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void emettre() throws InformationNonConformeException {

    }
}
