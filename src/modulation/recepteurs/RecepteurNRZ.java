package modulation.recepteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import modulation.Modulateur;
import utils.Code;

/**
 * Emetteur qui transmet parfaitement les informations reçues
 * sans altération.s
 */
public class RecepteurNRZ extends Modulateur<Float, Boolean> {
    public RecepteurNRZ(int taillePeriode) {
        super(taillePeriode);
    }

    /**
     * reçoit une information. Cette méthode, en fin d'exécution,
     * appelle la méthode émettre.
     * @param information l'information reçue
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        this.informationRecue = information;
        emettre();
    }

    /**
     * émet l'information construite par l'émetteur
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        this.informationEmise = conversionAN(this.informationRecue, Code.NRZ, 5, 0);

        // Émission vers les composants connectés
        for (DestinationInterface<Boolean> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }

    /**
     * Conversion analogique-numérique (AN) d'une information analogique.
     *
     * @param informationAnalogique l'information analogique à convertir
     * @param code le type de codage utilisé
     * @param aMax la valeur maximale pour la conversion
     * @param aMin la valeur minimale pour la conversion
     * @return l'information convertie en valeurs logiques
     */
    public Information<Boolean> conversionAN(Information<Float> informationAnalogique, Code code, float aMax, float aMin) {
        if (!validerParametres(aMax, aMin, code)) {
            return null;
        }

        Information<Boolean> informationConvertie = new Information<>();
        int compteur = 0;
        boolean trouveMax = false;

        for (float valeur : informationAnalogique) {
            if (valeur == aMax) {
                trouveMax = true;
            }

            compteur++;
            if (compteur == taillePeriode) {
                informationConvertie.add(trouveMax);
                compteur = 0;
                trouveMax = false;
            }
        }

        return informationConvertie;
    }
}
