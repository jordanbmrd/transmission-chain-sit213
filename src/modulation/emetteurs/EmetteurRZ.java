package modulation.emetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import modulation.Modulateur;
import utils.Code;

/**
 * Emetteur qui transmet parfaitement les informations reçues
 * sans altération.s
 */
public class EmetteurRZ extends Modulateur<Boolean, Float> {

    public EmetteurRZ(int taillePeriode, float aMax, float aMin) {
        super(taillePeriode, aMax, aMin);
    }

    /**
     * reçoit une information. Cette méthode, en fin d'exécution,
     * appelle la méthode émettre.
     * @param information l'information reçue
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void recevoir(Information<Boolean> information) throws InformationNonConformeException {
        this.informationRecue = information;
        emettre();
    }

    /**
     * émet l'information construite par l'émetteur
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationRecue == null) {
            throw new InformationNonConformeException();
        }
        this.informationEmise = conversionNA(this.informationRecue, Code.RZ);

        // Émission vers les composants connectés
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }

    /**
     * Conversion numérique-analogique (NA) d'une information logique.
     *
     * @param informationLogique l'information logique à convertir
     * @param code le type de codage utilisé
     * @return l'information convertie en valeurs analogiques
     */
    public Information<Float> conversionNA(Information<Boolean> informationLogique, Code code) throws InformationNonConformeException {
        if (validerParametres(code)) {
            return null;
        }
        if (informationLogique == null) {
            throw new InformationNonConformeException();
        }
        Information<Float> informationConvertie = new Information<>();
        for (Boolean element : informationLogique) {
            float valeurConvertie = element ? aMax : aMin;
            for (int i = 0; i < taillePeriode; i++) {
                informationConvertie.add(valeurConvertie);
            }
        }

        Information<Float> informationMiseEnForme = new Information<>();
        for (int i = 0; i < informationConvertie.nbElements(); i += taillePeriode) {
            for (int j = 0; j < taillePeriode/3; j++) {
                informationMiseEnForme.add(0.0F);
            }
            for (int k = taillePeriode/3; k < 2*taillePeriode/3; k++) {
                if (informationConvertie.iemeElement(i) == aMax) {
                    informationMiseEnForme.add(aMax);
                } else {
                    informationMiseEnForme.add(aMin);
                }
            }
            for (int l = 2*taillePeriode/3; l < taillePeriode; l++) {
                informationMiseEnForme.add(0.0F);
            }

        }

        return informationMiseEnForme;
    }
}
