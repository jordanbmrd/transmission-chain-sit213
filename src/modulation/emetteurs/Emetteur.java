package modulation.emetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import modulation.Modulateur;
import utils.Form;
import utils.NRZTTransition;

import java.util.Iterator;

/**
 * Classe représentant un émetteur qui transforme des informations logiques en signaux analogiques
 * et les transmet à des destinations connectées en fonction du codage spécifié.
 */
public class Emetteur extends Modulateur<Boolean, Float> {

    /**
     * Constructeur de l'émetteur qui initialise la période de modulation, les valeurs d'amplitude,
     * et le type de codage utilisé.
     *
     * @param nbEch la durée d'une période de modulation.
     * @param aMax la valeur analogique maximale.
     * @param aMin la valeur analogique minimale.
     * @param form le type de codage utilisé (ex : NRZ, RZ, NRZT).
     */
    public Emetteur(int nbEch, float aMax, float aMin, Form form) {
        super(nbEch, aMax, aMin, form);
    }

    /**
     * Reçoit une information binaire (logique).
     *
     * @param information l'information logique reçue.
     * @throws InformationNonConformeException si l'information reçue est nulle ou non conforme.
     */
    @Override
    public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
        this.informationRecue = information;
        emettre();
    }

    /**
     * Émet l'information convertie sous forme analogique.
     *
     * @throws InformationNonConformeException si l'information reçue est nulle.
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationRecue == null) {
            throw new InformationNonConformeException("L'information reçue est nulle");
        }
        this.informationEmise = conversionNA(this.informationRecue);
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }

    /**
     * Convertit une information logique en signal analogique selon le codage spécifié.
     *
     * @param informationLogique l'information logique à convertir.
     * @return l'information convertie sous forme analogique.
     * @throws InformationNonConformeException si l'information logique est nulle ou invalide.
     */
    public Information<Float> conversionNA(Information<Boolean> informationLogique) throws InformationNonConformeException {
        if (!validerParametres(form)) {
            return null;
        }
        if (informationLogique == null) {
            throw new InformationNonConformeException("Information logique non conforme");
        }

        Information<Float> informationConvertie = new Information<>();
        for (Boolean element : informationLogique) {
            float valeurConvertie = element ? aMax : aMin;
            for (int i = 0; i < nbEch; i++) {
                informationConvertie.add(valeurConvertie);
            }
        }

        return switch (form) {
            case NRZ -> informationConvertie;
            case RZ -> miseEnFormeRZ(informationLogique);
            case NRZT -> miseEnFormeNRZT(informationLogique);
        };
    }

    /**
     * Applique une mise en forme du signal pour la modulation RZ (Return-to-Zero).
     *
     * @param informationLogique l'information logique à convertir.
     * @return l'information analogique mise en forme selon le codage RZ.
     */
    public Information<Float> miseEnFormeRZ(Information<Boolean> informationLogique) {
        int delta = nbEch / 3;
        int missing;
        Information<Float> informationMiseEnForme = new Information<>();

        // Codage RZ : ajouter des périodes de repos (0) entre les symboles
        missing = nbEch - delta * 3;
        for (boolean information : informationLogique) {
            ajouterValeur(0f, delta, informationMiseEnForme);   // 0 avant la partie active
            ajouterValeur(information ? aMax : aMin, delta + missing, informationMiseEnForme);
            ajouterValeur(0f, delta, informationMiseEnForme);   // 0 après la partie active
        }

        return informationMiseEnForme;
    }

    /**
     * Applique une mise en forme du signal pour la modulation NRZT (Non-Return-to-Zero with Transitions).
     *
     * @param informationLogique l'information logique à convertir.
     * @return l'information analogique mise en forme selon le codage NRZT.
     */
    public Information<Float> miseEnFormeNRZT(Information<Boolean> informationLogique) {
        Information<Float> informationMiseEnForme = new Information<>();

        Iterator<Boolean> iterateur = informationLogique.iterator();
        Iterator<Boolean> iterateurDecale = informationLogique.iterator();

        Boolean current,
                precedent = null,
                next = null;

        if (iterateurDecale.hasNext())
            iterateurDecale.next();

        while (iterateur.hasNext()) {
            current = iterateur.next();

            if (iterateurDecale.hasNext())
                next = iterateurDecale.next();

            convertirSymbole(precedent, current, next, informationMiseEnForme);
            precedent = current;
        }

        return informationMiseEnForme;
    }

    /**
     * Convertit un symbole logique en signal analogique avec gestion des transitions selon NRZT.
     *
     * @param precedent le symbole précédent (peut être null au début).
     * @param current le symbole logique actuel.
     * @param next le symbole suivant (peut être null à la fin).
     * @param informationMiseEnForme l'information analogique à compléter.
     */
    protected void convertirSymbole(Boolean precedent, Boolean current, Boolean next, Information<Float> informationMiseEnForme) {
        int delta = nbEch / 3;
        if (nbEch % 3 != 0) {
            int missing = nbEch - delta * 3;
            if (current) {
                ajouterTransition(NRZTTransition.DEBUT, precedent != null && precedent, aMax, delta, informationMiseEnForme);
                ajouterValeur(aMax, delta + missing, informationMiseEnForme);
                ajouterTransition(NRZTTransition.FIN, next, aMax, delta, informationMiseEnForme);
            } else {
                ajouterTransition(NRZTTransition.DEBUT, precedent != null && !precedent, aMin, delta, informationMiseEnForme);
                ajouterValeur(aMin, delta + missing, informationMiseEnForme);
                ajouterTransition(NRZTTransition.FIN, !next, aMin, delta, informationMiseEnForme);
            }
            return;
        }

        if (current) {
            ajouterTransition(NRZTTransition.DEBUT, precedent != null && precedent, aMax, delta, informationMiseEnForme);
            ajouterValeur(aMax, delta, informationMiseEnForme);
            ajouterTransition(NRZTTransition.FIN, !next, aMax, delta, informationMiseEnForme);
        } else {
            ajouterTransition(NRZTTransition.DEBUT, precedent != null && !precedent, aMin, delta, informationMiseEnForme);
            ajouterValeur(aMin, delta, informationMiseEnForme);
            ajouterTransition(NRZTTransition.FIN, !next, aMin, delta, informationMiseEnForme);
        }
    }

    /**
     * Ajoute une transition progressive entre deux niveaux de signal dans le codage NRZT.
     *
     * @param position la position de la transition (début ou fin).
     * @param condition indique si la transition doit être directe ou progressive.
     * @param value la valeur de l'amplitude cible (aMax ou aMin).
     * @param delta le nombre d'échantillons à utiliser pour la transition.
     * @param informationMiseEnForme l'information analogique à compléter.
     */
    private void ajouterTransition(NRZTTransition position, boolean condition, float value, float delta, Information<Float> informationMiseEnForme) {
        for (int j = 0; j < delta; j++) {
            if (condition)
                informationMiseEnForme.add(value);
            else
                informationMiseEnForme.add(position == NRZTTransition.DEBUT ? (float) j / delta * value : (delta - j) / delta * value);
        }
    }

    /**
     * Ajoute une séquence constante d'une valeur analogique sur un nombre d'échantillons donné.
     *
     * @param value la valeur analogique à ajouter.
     * @param delta le nombre d'échantillons sur lequel ajouter cette valeur.
     * @param informationMiseEnForme l'information analogique à compléter.
     */
    private void ajouterValeur(float value, float delta, Information<Float> informationMiseEnForme) {
        for (int j = 0; j < delta; j++) {
            informationMiseEnForme.add(value);
        }
    }
}
