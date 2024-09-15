package modulation;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import sources.SourceInterface;
import utils.Code;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe Abstraite d'un composant émetteur d'informations dont
 * les éléments sont de type R en entrée et de type E en sortie.
 * L'entrée de l'émetteur implémente l'interface DestinationInterface,
 * la sortie de l'émetteur implémente l'interface SourceInterface.
 */
public abstract class Modulateur<R, E> implements DestinationInterface<R>, SourceInterface<E> {
    /**
     * La liste des composants destination connectés en sortie de l'émetteur.
     */
    protected List<DestinationInterface<E>> destinationsConnectees;

    /**
     * L'information reçue en entrée de l'émetteur.
     */
    protected Information<R> informationRecue;

    /**
     * L'information émise en sortie de l'émetteur.
     */
    protected Information<E> informationEmise;

    /**
     * Taille de la période.
     */
    protected int taillePeriode;

    /**
     * Constructeur factorisant les initialisations communes aux
     * réalisations de la classe abstraite Emetteur.
     *
     * @param taillePeriode la taille de la période
     */
    public Modulateur(int taillePeriode) {
        this.destinationsConnectees = new ArrayList<>();
        this.informationRecue = null;
        this.informationEmise = null;
        this.taillePeriode = taillePeriode;
    }

    /**
     * Retourne la dernière information reçue en entrée de l'émetteur.
     *
     * @return l'information reçue
     */
    public Information<R> getInformationRecue() {
        return this.informationRecue;
    }

    /**
     * Retourne la dernière information émise en sortie de l'émetteur.
     *
     * @return l'information émise
     */
    public Information<E> getInformationEmise() {
        return this.informationEmise;
    }

    /**
     * Connecte une destination à la sortie de l'émetteur.
     *
     * @param destination la destination à connecter
     */
    @Override
    public void connecter(DestinationInterface<E> destination) {
        destinationsConnectees.add(destination);
    }

    /**
     * Déconnecte une destination de la sortie de l'émetteur.
     *
     * @param destination la destination à déconnecter
     */
    public void deconnecter(DestinationInterface<E> destination) {
        destinationsConnectees.remove(destination);
    }

    /**
     * Méthode pour valider les paramètres aMin, aMax et le type de codage.
     *
     * @param aMax la valeur maximale
     * @param aMin la valeur minimale
     * @param code le type de codage
     * @return true si les paramètres sont valides, false sinon
     */
    protected boolean validerParametres(float aMax, float aMin, Code code) throws InformationNonConformeException {
        if (aMin >= aMax) {
            throw new InformationNonConformeException("Erreur: aMin >= aMax");
        }

        if (aMax < 0) {
            throw new InformationNonConformeException("Erreur: aMax < 0");
        }

        if ((code.equals(Code.NRZ) || code.equals(Code.NRZT)) && aMin > 0) {
            throw new InformationNonConformeException("Erreur: aMin > 0 pour le codage NRZ/NRZT");
        }

        if ((code.equals(Code.RZ)) && aMin != 0) {
            throw new InformationNonConformeException("Erreur: aMin != 0 pour le codage RZ");
        }

        return false;
    }

    /**
     * Reçoit une information et appelle la méthode émettre.
     *
     * @param information l'information reçue
     * @throws InformationNonConformeException si l'information est non conforme
     */
    public abstract void recevoir(Information<R> information) throws InformationNonConformeException;

    /**
     * Émet l'information construite par l'émetteur.
     *
     * @throws InformationNonConformeException si l'information est non conforme
     */
    public abstract void emettre() throws InformationNonConformeException;
}
