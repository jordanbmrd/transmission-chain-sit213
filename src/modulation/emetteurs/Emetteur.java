package modulation.emetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import modulation.Modulateur;
import utils.Form;

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
            case RZ, NRZT -> miseEnForme(informationLogique);
        };
    }

    /**
     * Applique une mise en forme du signal en fonction du type de codage (RZ ou NRZT).
     *
     * @param informationLogique l'information logique initiale.
     * @return l'information analogique après mise en forme.
     */
    public Information<Float> miseEnForme(Information<Boolean> informationLogique) {
        int delta = nbEch / 3;
        int missing;
        Information<Float> informationMiseEnForme = new Information<>();

        switch (form) {
            case RZ:
                // Codage RZ : ajouter des périodes de repos (0) entre les symboles
                missing = nbEch - delta * 3;
                for (boolean information : informationLogique) {
                    for (int i = 0; i < delta; i++) {
                        informationMiseEnForme.add(0f); // 0 avant la partie active
                    }
                    for (int i = 0; i < delta + missing; i++) {
                        // Ajout de la partie active du signal
                        if (information) {
                            informationMiseEnForme.add(aMax);
                        } else {
                            informationMiseEnForme.add(aMin);
                        }
                    }
                    for (int i = 0; i < delta; i++) {
                        informationMiseEnForme.add(0f); // 0 après la partie active
                    }
                }
                break;

            case NRZT:
                // Codage NRZT : transitions progressives entre les niveaux
                for (int i = 0; i < informationLogique.nbElements(); i++) {
                    Boolean precedent = (i > 0) ? informationLogique.iemeElement(i - 1) : null;
                    Boolean current = informationLogique.iemeElement(i);
                    Boolean next = (i < informationLogique.nbElements() - 1) ? informationLogique.iemeElement(i + 1) : null;

                    missing = nbEch % 3;

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
                throw new IllegalArgumentException("Type de codage inconnu : " + form);
        }

        return informationMiseEnForme;
    }
}
