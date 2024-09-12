package emetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import sources.SourceInterface;

import java.util.LinkedList;

/**
 * Classe Abstraite d'un composant émetteur d'informations dont
 * les éléments sont de type R en entrée et de type E en sortie;
 * l'entrée de l'émetteur implémente l'interface DestinationInterface,
 * la sortie de l'émetteur implémente l'interface SourceInterface
 */
public abstract class Emetteur<R, E> implements DestinationInterface<R>, SourceInterface<E> {

    /**
     * la liste des composants destination connectés en sortie de l'émetteur
     */
    protected LinkedList<DestinationInterface<E>> destinationsConnectees;

    /**
     * l'information reçue en entrée de l'émetteur
     */
    protected Information<R> informationRecue;

    /**
     * l'information émise en sortie de l'émetteur
     */
    protected Information<E> informationEmise;

    /**
     * un constructeur factorisant les initialisations communes aux
     * réalisations de la classe abstraite Emetteur
     */
    public Emetteur() {
        destinationsConnectees = new LinkedList<>();
        informationRecue = null;
        informationEmise = null;
    }

    /**
     * retourne la dernière information reçue en entrée de l'émetteur
     * @return une information
     */
    public Information<R> getInformationRecue() {
        return this.informationRecue;
    }

    /**
     * retourne la dernière information émise en sortie de l'émetteur
     * @return une information
     */
    public Information<E> getInformationEmise() {
        return this.informationEmise;
    }

    /**
     * connecte une destination à la sortie de l'émetteur
     * @param destination la destination à connecter
     */
    @Override
    public void connecter(DestinationInterface<E> destination) {
        destinationsConnectees.add(destination);
    }

    /**
     * déconnecte une destination de la sortie de l'émetteur
     * @param destination la destination à déconnecter
     */
    public void deconnecter(DestinationInterface<E> destination) {
        destinationsConnectees.remove(destination);
    }

    /**
     * reçoit une information. Cette méthode, en fin d'exécution,
     * appelle la méthode émettre.
     * @param information l'information reçue
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    public abstract void recevoir(Information<R> information) throws InformationNonConformeException;

    /**
     * émet l'information construite par l'émetteur
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    public abstract void emettre() throws InformationNonConformeException;
}
