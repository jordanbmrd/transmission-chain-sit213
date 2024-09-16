package modulation.recepteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import modulation.Modulateur;
import utils.Code;

/**
 * Classe représentant un récepteur qui reçoit des informations analogiques
 * et les convertit en informations logiques (binaire) via un processus
 * de conversion analogique-numérique (AN).
 */
public class Recepteur extends Modulateur<Float, Boolean> {

    /**
     * Constructeur du récepteur.
     *
     * @param taillePeriode la taille de la période d'échantillonnage
     * @param aMax valeur analogique maximale (utilisée pour détecter un signal logique 1)
     * @param aMin valeur analogique minimale (utilisée pour détecter un signal logique 0)
     * @param code le type de codage utilisé (NRZ, RZ, NRZT)
     */
    public Recepteur(int taillePeriode, float aMax, float aMin, Code code) {
        super(taillePeriode, aMax, aMin, code);
    }

    /**
     * Reçoit une information analogique. Cette méthode appelle ensuite la méthode {@code emettre}
     * pour traiter l'information.
     *
     * @param information l'information analogique reçue
     * @throws InformationNonConformeException si l'information reçue est nulle ou invalide
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        // Vérification de la conformité de l'information
        if (information == null) {
            throw new InformationNonConformeException("L'information reçue est nulle.");
        }
        this.informationRecue = information;

        // Appel de la méthode d'émission après réception
        emettre();
    }

    /**
     * Émet l'information convertie sous forme logique (binaire).
     * Cette méthode utilise la conversion analogique-numérique (AN) pour transformer
     * l'information reçue en valeurs logiques (0 ou 1).
     *
     * @throws InformationNonConformeException si l'information convertie comporte une anomalie
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        // Conversion de l'information analogique en information logique
        this.informationEmise = conversionAN(this.informationRecue);

        // Émission de l'information convertie à toutes les destinations connectées
        for (DestinationInterface<Boolean> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }

    /**
     * Conversion analogique-numérique (AN) de l'information reçue.
     * Cette méthode analyse l'information analogique et la convertit en une séquence
     * de valeurs logiques (booleans) selon la taille de période.
     *
     * @param informationAnalogique l'information analogique à convertir
     * @return l'information convertie sous forme logique (binaire)
     * @throws InformationNonConformeException si l'information reçue est nulle ou invalide
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

        // Création d'une nouvelle information logique (binaire)
        Information<Boolean> informationConvertie = new Information<>();

        // Compteur pour suivre l'avancement dans la période
        int compteur = 0;
        // Variable pour indiquer si la valeur maximale (aMax) a été trouvée dans la période
        boolean trouveMax = false;

        // Parcours de l'information analogique reçue
        for (float valeur : informationAnalogique) {
            // Si la valeur est égale à aMax, on détecte un bit 1
            if (valeur == aMax) {
                trouveMax = true;
            }

            // Incrément du compteur pour suivre la taille de la période
            compteur++;

            // Lorsque la période est atteinte, on ajoute un bit (true si aMax trouvé, sinon false)
            if (compteur == taillePeriode) {
                informationConvertie.add(trouveMax);  // Ajout du bit 1 ou 0
                compteur = 0;  // Réinitialisation du compteur pour la prochaine période
                trouveMax = false;  // Réinitialisation du flag pour détecter un nouveau 1
            }
        }

        // Retourne l'information convertie sous forme logique
        return informationConvertie;
    }
}
