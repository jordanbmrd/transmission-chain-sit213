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
public class Emetteur extends Modulateur<Boolean, Float> {

    public Emetteur(int taillePeriode, float aMax, float aMin, Code code) {
        super(taillePeriode, aMax, aMin, code);

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
        this.informationEmise = conversionNA(this.informationRecue, Code.NRZ);

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
        Information<Float> informationFinale = new Information<>();
        if (code.equals(Code.NRZ)) { informationFinale = informationConvertie; }
        else { informationFinale = miseEnForme(informationLogique, informationConvertie, this.code); }
        return informationFinale;
    }

    public Information<Float> miseEnForme(Information<Boolean> informationLogique, Information<Float> informationConvertie, Code code) {
        int delta = taillePeriode / 3;
        Information<Float> informationMiseEnForme = new Information<>();

        if (code.equals(Code.RZ)) {
            for (int i = 0; i < informationConvertie.nbElements(); i += taillePeriode) {
                for (int j = 0; j < delta; j++) {
                    informationMiseEnForme.add(0.0F);
                }
                for (int k = delta; k < 2*delta; k++) {
                    if (informationConvertie.iemeElement(i) == aMax) {
                        informationMiseEnForme.add(aMax);
                    } else {
                        informationMiseEnForme.add(aMin);
                    }
                }
                for (int l = 2*delta; l < taillePeriode; l++) {
                    informationMiseEnForme.add(0.0F);
                }
            }
        }

        else if (code.equals(Code.NRZT)) {
            for (int i = 0; i < informationConvertie.nbElements(); i++) {
                Boolean precedent = (i > 0) ? informationLogique.iemeElement(i - 1) : null;
                Boolean current = informationLogique.iemeElement(i);
                Boolean next = (i < informationConvertie.nbElements() - 1) ? informationLogique.iemeElement(i + 1) : null;

                int missing = taillePeriode % 3;

                if (current != null && current) {
                    // Génération des valeurs pour un bit à 1
                    for (int j = 0; j < delta; j++) {
                        informationMiseEnForme.add((precedent != null && precedent) ? aMax : (float) j / delta * aMax);
                    }
                    for (int j = 0; j < delta + missing; j++) {
                        informationMiseEnForme.add(aMax);
                    }
                    for (int j = 0; j < delta; j++) {
                        informationMiseEnForme.add((next != null && next) ? aMax : (float) (delta - j) / delta * aMax);
                    }
                } else {
                    // Génération des valeurs pour un bit à 0
                    for (int j = 0; j < delta; j++) {
                        informationMiseEnForme.add((precedent != null && !precedent) ? aMin : (float) j / delta * aMin);
                    }
                    for (int j = 0; j < delta + missing; j++) {
                        informationMiseEnForme.add(aMin);
                    }
                    for (int j = 0; j < delta; j++) {
                        informationMiseEnForme.add((next != null && !next) ? aMin : (float) (delta - j) / delta * aMin);
                    }
                }
            }
        }
        return informationMiseEnForme;
    }
}
