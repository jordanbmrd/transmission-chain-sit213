package modulation.emetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import modulation.Modulateur;
import utils.Code;

/**
 * Classe représentant un émetteur qui transforme des informations logiques en signaux analogiques
 * et les transmet à des destinations connectées en fonction du codage spécifié.
 */
public class Emetteur extends Modulateur<Boolean, Float> {

    /**
     * Constructeur de l'émetteur qui initialise la période de modulation, les valeurs d'amplitude,
     * et le type de codage utilisé.
     *
     * @param taillePeriode la durée d'une période de modulation.
     * @param aMax la valeur analogique maximale.
     * @param aMin la valeur analogique minimale.
     * @param code le type de codage utilisé (ex : NRZ, RZ, NRZT).
     */
    public Emetteur(int taillePeriode, float aMax, float aMin, Code code) {
        super(taillePeriode, aMax, aMin, code);
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
        if (!validerParametres(code)) {
            return null;
        }
        if (informationLogique == null) {
            throw new InformationNonConformeException("Information logique non conforme");
        }

        Information<Float> informationConvertie = new Information<>();
        for (Boolean element : informationLogique) {
            float valeurConvertie = element ? aMax : aMin;
            for (int i = 0; i < taillePeriode; i++) {
                informationConvertie.add(valeurConvertie);
            }
        }

        return switch (code) {
            case NRZ -> informationConvertie;
            case RZ, NRZT -> miseEnForme(informationLogique, informationConvertie);
        };
    }

    /**
     * Applique une mise en forme du signal en fonction du type de codage (RZ ou NRZT).
     *
     * @param informationLogique l'information logique initiale.
     * @param informationConvertie l'information analogique à mettre en forme.
     * @return l'information analogique après mise en forme.
     */
    public Information<Float> miseEnForme(Information<Boolean> informationLogique, Information<Float> informationConvertie) {
        int delta = taillePeriode / 3;
        Information<Float> informationMiseEnForme = new Information<>();

        switch (code) {
            case RZ:
                // Codage RZ : ajouter des périodes de repos (0) entre les symboles
                for (int i = 0; i < informationConvertie.nbElements(); i += taillePeriode) {
                    for (int j = 0; j < delta; j++) {
                        informationMiseEnForme.add(0.0F); // Repos avant la partie active
                    }
                    for (int k = delta; k < 2 * delta; k++) {
                        // Ajoute la partie active du signal
                        informationMiseEnForme.add(informationConvertie.iemeElement(i) == aMax ? aMax : aMin);
                    }
                    for (int l = 2 * delta; l < taillePeriode; l++) {
                        informationMiseEnForme.add(0.0F); // Repos après la partie active
                    }
                }
                break;

            case NRZT:
                // Codage NRZT : transitions progressives entre les niveaux
                for (int i = 0; i < informationLogique.nbElements(); i++) {
                    Boolean precedent = (i > 0) ? informationLogique.iemeElement(i - 1) : null;
                    Boolean current = informationLogique.iemeElement(i);
                    Boolean next = (i < informationLogique.nbElements() - 1) ? informationLogique.iemeElement(i + 1) : null;

                    int missing = taillePeriode % 3;

                    // Génération des symboles en fonction de la transition entre les bits
                    if (current != null && current) {
                        // Si le bit courant est 1
                        for (int j = 0; j < delta; j++) {
                            informationMiseEnForme.add((precedent != null && precedent) ? aMax : (float) j / delta * aMax);
                        }
                        for (int j = 0; j < delta + missing; j++) {
                            informationMiseEnForme.add(aMax); // Partie plate
                        }
                        for (int j = 0; j < delta; j++) {
                            informationMiseEnForme.add((next != null && next) ? aMax : (float) (delta - j) / delta * aMax);
                        }
                    } else {
                        // Si le bit courant est 0
                        for (int j = 0; j < delta; j++) {
                            informationMiseEnForme.add((precedent != null && !precedent) ? aMin : (float) j / delta * aMin);
                        }
                        for (int j = 0; j < delta + missing; j++) {
                            informationMiseEnForme.add(aMin); // Partie plate
                        }
                        for (int j = 0; j < delta; j++) {
                            informationMiseEnForme.add((next != null && !next) ? aMin : (float) (delta - j) / delta * aMin);
                        }
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("Type de codage inconnu : " + code);
        }

        return informationMiseEnForme;
    }
}
