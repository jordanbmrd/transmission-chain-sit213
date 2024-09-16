package modulation.emetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import modulation.Modulateur;
import utils.Code;

/**
 * Emetteur qui transmet parfaitement les informations reçues
 * sans altération.
 */
public class EmetteurNRZT extends Modulateur<Boolean, Float> {

    public EmetteurNRZT(int taillePeriode, float aMax, float aMin) {
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
        this.informationEmise = conversionNA(this.informationRecue, Code.NRZT);

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
        if (informationLogique == null || informationLogique.nbElements() == 0) {
            throw new InformationNonConformeException();
        }

        Information<Float> informationConvertie = new Information<>();
        int taille = informationLogique.nbElements();

        for (int i = 0; i < taille; i++) {
            Boolean precedent = (i > 0) ? informationLogique.iemeElement(i - 1) : null;
            Boolean current = informationLogique.iemeElement(i);
            Boolean next = (i < taille - 1) ? informationLogique.iemeElement(i + 1) : null;

            int delta = taillePeriode / 3;
            int missing = taillePeriode % 3;

            if (current != null && current) {
                // Génération des valeurs pour un bit à 1
                for (int j = 0; j < delta; j++) {
                    informationConvertie.add((precedent != null && precedent) ? aMax : (float) j / delta * aMax);
                }
                for (int j = 0; j < delta + missing; j++) {
                    informationConvertie.add(aMax);
                }
                for (int j = 0; j < delta; j++) {
                    informationConvertie.add((next != null && next) ? aMax : (float) (delta - j) / delta * aMax);
                }
            } else {
                // Génération des valeurs pour un bit à 0
                for (int j = 0; j < delta; j++) {
                    informationConvertie.add((precedent != null && !precedent) ? aMin : (float) j / delta * aMin);
                }
                for (int j = 0; j < delta + missing; j++) {
                    informationConvertie.add(aMin);
                }
                for (int j = 0; j < delta; j++) {
                    informationConvertie.add((next != null && !next) ? aMin : (float) (delta - j) / delta * aMin);
                }
            }
        }

        return informationConvertie;
    }
}
