package codeurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import sources.SourceInterface;

public class Decodeur<R, E> implements DestinationInterface<R>, SourceInterface<E> {
    /**
     * pour obtenir la dernière information reçue par une destination.
     *
     * @return une information
     */
    @Override
    public Information<R> getInformationRecue() {
        return null;
    }

    /**
     * pour recevoir une information de la source qui nous est
     * connectée
     *
     * @param information l'information  à recevoir
     * @throws InformationNonConformeException si l'Information comporte des anomalies
     */
    @Override
    public void recevoir(Information<R> information) throws InformationNonConformeException {

    }

    /**
     * pour obtenir la dernière information émise par une source.
     *
     * @return une information
     */
    @Override
    public Information<E> getInformationEmise() {
        return null;
    }

    /**
     * pour connecter une destination à la source
     *
     * @param destination la destination à connecter
     */
    @Override
    public void connecter(DestinationInterface<E> destination) {

    }

    /**
     * pour émettre l'information contenue dans une source
     *
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void emettre() throws InformationNonConformeException {

    }
}
