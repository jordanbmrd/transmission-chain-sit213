package codage;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

/**
 * La classe Codeur est responsable de l'encodage d'informations sous forme de bits
 * en utilisant un code basé sur deux trames : "010" pour le bit 0 et "101" pour le bit 1.
 * Elle étend la classe abstraite AbstractCodeur pour traiter des informations de type Boolean.
 *
 * <p>
 * Le Codeur prend une séquence de bits en entrée et produit une séquence codée en sortie.
 * </p>
 */
public class Codeur extends AbstractCodeur<Boolean, Boolean> {

    /**
     * Encode les informations en utilisant des trames spécifiques.
     *
     * @param information L'information à encoder, de type Information<Boolean>.
     * @return L'information encodée, de type Information<Boolean>.
     */
    private Information<Boolean> encoder(Information<Boolean> information) {
        Information<Boolean> informationCodee = new Information<>();

        for (boolean bit : information) {
            if (bit) {  // bit = 1 => 101
                informationCodee.add(true);
                informationCodee.add(false);
                informationCodee.add(true);
            } else {    // bit = 0 => 010
                informationCodee.add(false);
                informationCodee.add(true);
                informationCodee.add(false);
            }
        }

        return informationCodee;
    }

    /**
     * Émet l'information contenue dans une source après encodage.
     *
     * @throws InformationNonConformeException si l'Information comporte une anomalie,
     *                                          comme le fait d'être nulle.
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationRecue == null) {
            throw new InformationNonConformeException("L'information reçue est nulle");
        }

        this.informationEmise = encoder(this.informationRecue);

        for (DestinationInterface<Boolean> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }
}
