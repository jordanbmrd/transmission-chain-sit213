package modulation.recepteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import modulation.Modulateur;
import utils.Form;

/**
 * Classe représentant un récepteur qui convertit des informations analogiques en informations logiques.
 */
public class Recepteur extends Modulateur<Float, Boolean> {

    /**
     * Constructeur du récepteur qui initialise les paramètres du récepteur
     * comme la période d'échantillonnage, les valeurs d'amplitude et le type de codage.
     *
     * @param nbEch la durée d'une période d'échantillonnage.
     * @param aMax la valeur analogique maximale.
     * @param aMin la valeur analogique minimale.
     * @param form le type de codage utilisé (ex : NRZ, RZ, NRZT).
     */
    public Recepteur(int nbEch, float aMax, float aMin, Form form) {
        super(nbEch, aMax, aMin, form);
    }

    /**
     * Reçoit une information analogique.
     *
     * @param information l'information analogique reçue.
     * @throws InformationNonConformeException si l'information est nulle ou non conforme.
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        if (information == null) {
            throw new InformationNonConformeException("L'information reçue est nulle.");
        }
        this.informationRecue = information;
        emettre();
    }

    /**
     * Émet l'information convertie sous forme logique (binaire).
     *
     * @throws InformationNonConformeException si l'information convertie comporte une anomalie.
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        this.informationEmise = conversionAN(this.informationRecue);
        for (DestinationInterface<Boolean> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }

    /**
     * Convertit une information analogique en information logique (binaire).
     *
     * @param informationAnalogique l'information analogique à convertir.
     * @return l'information convertie sous forme logique (binaire).
     * @throws InformationNonConformeException si l'information est nulle ou invalide.
     */
    public Information<Boolean> conversionAN(Information<Float> informationAnalogique) throws InformationNonConformeException {
        // Vérification de la validité des paramètres
        if (!validerParametres(form)) {
            return null;
        }

        // Vérification de la conformité de l'information
        if (informationAnalogique == null || informationAnalogique.nbElements() == 0) {
            throw new InformationNonConformeException("L'information analogique est nulle ou vide.");
        }

        Information<Boolean> informationConvertie = new Information<>();

        int compteur = 0;
        float moyenne = 0;

        boolean condition = switch(form) {
            case NRZ, NRZT -> compteur < nbEch;
            case RZ -> compteur >= nbEch / 3 && compteur <= 2 * nbEch / 3;  // Partie différente de 0
        };

        // Parcours de l'information analogique reçue
        for (float information : informationAnalogique) {
            // Calcul de la moyenne de chaque période
            if (condition) {
                moyenne += information;
            }

            compteur++;

            // À la fin de la période, ajout de la valeur dans l'information décodée
            if (compteur == nbEch) {
                moyenne /= (float) nbEch / 3;

                boolean value = moyenne > (aMax + aMin) / 2;
                informationConvertie.add(value);

                compteur = 0;
                moyenne = 0;
            }
        }

        return informationConvertie;
    }
}
