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
public class RecepteurNRZT extends Modulateur<Float, Boolean> {
    public RecepteurNRZT(int taillePeriode, float aMax, float aMin) {
        super(taillePeriode, aMax, aMin);
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
        this.informationEmise = conversionAN(this.informationRecue, Code.RZ);

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
     * @return l'information convertie en valeurs logiques
     */
    public Information<Boolean> conversionAN(Information<Float> informationAnalogique, Code code) throws InformationNonConformeException {
        if (validerParametres(code)) {
            return null;
        }
        System.out.println(informationAnalogique);
        if (informationAnalogique == null) {
            throw new InformationNonConformeException();
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
