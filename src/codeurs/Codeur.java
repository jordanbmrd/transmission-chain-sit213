package codeurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import sources.SourceInterface;

import java.util.List;

public class Codeur<R, E> implements DestinationInterface<R>, SourceInterface<E> {
    /**
     * Liste des destinations connectées à la sortie du modulateur.
     * Ces destinations recevront l'information après modulation.
     */
    protected List<DestinationInterface<E>> destinationsConnectees;

    /**
     * L'information reçue en entrée du modulateur.
     */
    protected Information<R> informationRecue;

    /**
     * L'information émise en sortie du modulateur après traitement.
     */
    protected Information<E> informationEmise;

    /**
     * pour obtenir la dernière information reçue par une destination.
     *
     * @return une information
     */
    @Override
    public Information<R> getInformationRecue() {
        return this.informationRecue;
    }

    /**
     * pour obtenir la dernière information émise par une source.
     *
     * @return une information
     */
    @Override
    public Information<E> getInformationEmise() {
        return this.informationEmise;
    }

    /**
     * pour recevoir une information de la source qui nous est
     * connectée
     *
     * @param information l'information à recevoir
     * @throws InformationNonConformeException si l'Information comporte des anomalies
     */
    @Override
    public void recevoir(Information<R> information) throws InformationNonConformeException {
        this.informationRecue = information;
        emettre();
    }

    /**
     * pour connecter une destination à la source
     *
     * @param destination la destination à connecter
     */
    @Override
    public void connecter(DestinationInterface<E> destination) {
        destinationsConnectees.add(destination);  // Ajoute la destination à la liste des destinations connectées
    }

    /**
     * pour émettre l'information contenue dans une source
     *
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationRecue == null) {
            throw new InformationNonConformeException("L'information reçue est nulle");
        }

        this.informationEmise = (Information<E>) appliquerCodage(this.informationEmise);

        for (DestinationInterface<E> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }

    private Information<Boolean> appliquerCodage(Information<E> information) {
        Information<Boolean> informationLogique = new Information<>();
        Information<Boolean> informationCodee = new Information<>();

        for (boolean bit : informationLogique) {
            if (bit) {  // bit = 1
                informationCodee.add(true);
                informationCodee.add(false);
                informationCodee.add(true);
            } else {    // bit = 0
                informationCodee.add(false);
                informationCodee.add(true);
                informationCodee.add(false);
            }
        }

        return informationCodee;
    }
}
