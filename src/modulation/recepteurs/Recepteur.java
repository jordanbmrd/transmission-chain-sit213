package modulation.recepteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import modulation.Modulateur;
import utils.Code;

/**
 * Classe représentant un récepteur qui convertit des informations analogiques en informations logiques.
 */
public class Recepteur extends Modulateur<Float, Boolean> {

    /**
     * Constructeur du récepteur qui initialise les paramètres du récepteur
     * comme la période d'échantillonnage, les valeurs d'amplitude et le type de codage.
     *
     * @param taillePeriode la durée d'une période d'échantillonnage.
     * @param aMax la valeur analogique maximale.
     * @param aMin la valeur analogique minimale.
     * @param code le type de codage utilisé (ex : NRZ, RZ, NRZT).
     */
    public Recepteur(int taillePeriode, float aMax, float aMin, Code code) {
        super(taillePeriode, aMax, aMin, code);
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
        if (!validerParametres(code)) {
            return null;
        }

        // Vérification de la conformité de l'information
        if (informationAnalogique == null || informationAnalogique.nbElements() == 0) {
            throw new InformationNonConformeException("L'information analogique est nulle ou vide.");
        }

        Information<Boolean> informationConvertie = new Information<>();

        int compteur = 0;
        boolean trouveMax = false;

        // Parcours de l'information analogique reçue
        for (float valeur : informationAnalogique) {
            // Si la valeur est égale à aMax, on détecte un bit 1
            if (valeur == aMax) {
                trouveMax = true;
            }

            compteur++;

            // Lorsque la période est atteinte, on ajoute un bit (true si aMax trouvé, sinon false)
            if (compteur == taillePeriode) {
                informationConvertie.add(trouveMax);  // Ajout du bit 1 ou 0
                compteur = 0;  // Réinitialisation du compteur pour la prochaine période
                trouveMax = false;  // Réinitialisation du flag pour détecter un nouveau 1
            }
        }

        return informationConvertie;
    }
}
