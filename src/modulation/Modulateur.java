package modulation;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import sources.SourceInterface;
import utils.Form;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite représentant un modulateur (émetteur/récepteur) d'informations.
 * Le modulateur reçoit des informations de type R et émet des informations de type E.
 * Un modulateur peut être relié à plusieurs destinations connectées.
 *
 * @param <R> Type des informations reçues (entrée)
 * @param <E> Type des informations émises (sortie)
 */
public abstract class Modulateur<R, E> implements DestinationInterface<R>, SourceInterface<E> {

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
     * Taille de la période utilisée pour la modulation.
     */
    protected int nbEch;

    /**
     * Valeur analogique maximale (ex : amplitude max pour une onde).
     */
    protected float aMax;

    /**
     * Valeur analogique minimale (ex : amplitude min pour une onde).
     */
    protected float aMin;

    /**
     * Type de codage utilisé pour la modulation (ex : NRZ, RZ, NRZT).
     */
    protected Form form;

    /**
     * Constructeur du modulateur.
     * Initialise les paramètres communs à tous les modulateurs.
     *
     * @param nbEch La taille de la période de modulation
     * @param aMax L'amplitude maximale
     * @param aMin L'amplitude minimale
     * @param form Le type de codage utilisé pour la modulation
     */
    public Modulateur(int nbEch, float aMax, float aMin, Form form) {
        this.destinationsConnectees = new ArrayList<>(); // Initialisation des destinations connectées
        this.informationRecue = null;  // L'information reçue est initialement nulle
        this.informationEmise = null;  // L'information émise est initialement nulle
        this.nbEch = nbEch;  // Définit la taille de la période
        this.aMax = aMax;  // Définit l'amplitude maximale
        this.aMin = aMin;  // Définit l'amplitude minimale
        this.form = form;  // Définit le type de codage
    }

    /**
     * Retourne l'information reçue par le modulateur.
     *
     * @return L'information reçue
     */
    public Information<R> getInformationRecue() {
        return this.informationRecue;
    }

    /**
     * Retourne l'information émise par le modulateur.
     *
     * @return L'information émise
     */
    public Information<E> getInformationEmise() {
        return this.informationEmise;
    }

    /**
     * Connecte une destination à la sortie du modulateur.
     * Cette destination recevra les informations émises par le modulateur.
     *
     * @param destination La destination à connecter
     */
    @Override
    public void connecter(DestinationInterface<E> destination) {
        destinationsConnectees.add(destination);  // Ajoute la destination à la liste des destinations connectées
    }

    /**
     * Valide les paramètres du modulateur (aMin, aMax et le type de codage).
     * Vérifie la cohérence des valeurs d'amplitudes et des règles spécifiques à chaque codage.
     *
     * @param form Le type de codage utilisé
     * @return true si les paramètres sont valides, false sinon
     * @throws InformationNonConformeException si les paramètres sont invalides
     */
    public boolean validerParametres(Form form) throws InformationNonConformeException {
        // Vérifie que aMin est strictement inférieur à aMax
        if (aMin >= aMax) {
            throw new InformationNonConformeException("Erreur: aMin doit être strictement inférieur à aMax");
        }

        // Vérifie que aMax est supérieur ou égal à 0
        if (aMax < 0) {
            throw new InformationNonConformeException("Erreur: aMax doit être supérieur ou égal à 0");
        }

        // Pour NRZ et NRZT, aMin doit être inférieur ou égal à 0
        if ((form.equals(Form.NRZ) || form.equals(Form.NRZT)) && aMin > 0) {
            throw new InformationNonConformeException("Erreur: aMin doit être inférieur ou égal à 0 pour le codage NRZ/NRZT");
        }

        // Pour RZ, aMin doit être exactement égal à 0
        if (form.equals(Form.RZ) && aMin != 0) {
            throw new InformationNonConformeException("Erreur: aMin doit être égal à 0 pour le codage RZ");
        }

        return true;  // Retourne true si les paramètres sont valides
    }

    /**
     * Méthode abstraite pour recevoir une information.
     * Les sous-classes doivent implémenter cette méthode pour traiter
     * l'information reçue et appeler la méthode {@code emettre}.
     *
     * @param information L'information reçue
     * @throws InformationNonConformeException si l'information est non conforme
     */
    public abstract void recevoir(Information<R> information) throws InformationNonConformeException;

    /**
     * Méthode abstraite pour émettre l'information traitée.
     * Les sous-classes doivent implémenter cette méthode pour émettre
     * l'information après traitement.
     *
     * @throws InformationNonConformeException si l'information est non conforme
     */
    public abstract void emettre() throws InformationNonConformeException;
}
