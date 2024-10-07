package codage;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

/**
 * La classe Decodeur est responsable du décodage d'informations codées en utilisant
 * un code basé sur deux trames : "010" et "101". Elle étend la classe abstraite
 * AbstractCodeur pour traiter des informations de type Boolean.
 *
 * <p>
 * Le Decodeur peut corriger des erreurs dans les trames et émettre les informations
 * décodées vers des destinations connectées.
 * </p>
 */
public class Decodeur extends AbstractCodeur<Boolean, Boolean> {

    /**
     * Décode les informations codées en utilisant des trames spécifiques.
     *
     * @param informationCodee L'information codée à décoder, de type Information<Boolean>.
     * @return L'information décodée, de type Information<Boolean>.
     */
    public Information<Boolean> decoder(Information<Boolean> informationCodee) {
        Information<Boolean> informationDecodee = new Information<>();
        int tailleTrame = informationCodee.nbElements() / 3;

        for (int i = 0; i < tailleTrame; i++) {
            boolean bit1, bit2, bit3;
            bit1 = informationCodee.iemeElement(i * 3);
            bit2 = informationCodee.iemeElement(i * 3 + 1);
            bit3 = informationCodee.iemeElement(i * 3 + 2);

            // Cas 1 : La trame correspond exactement à "010" -> décoder comme 0
            if (!bit1 && bit2 && !bit3) {
                informationDecodee.add(false);
            }
            // Cas 2 : La trame correspond exactement à "101" -> décoder comme 1
            else if (bit1 && !bit2 && bit3) {
                informationDecodee.add(true);
            }
            // Cas 3 : Sinon, il faut corriger une erreur en modifiant un seul bit
            else {
                // Calculer la distance de Hamming avec "010" et "101"
                int distance010 = hammingDistance(bit1, bit2, bit3, false, true, false);
                int distance101 = hammingDistance(bit1, bit2, bit3, true, false, true);

                // Si plus proche de "010", décoder comme 0
                if (distance010 <= distance101) {
                    informationDecodee.add(false);
                }
                // Sinon, plus proche de "101", décoder comme 1
                else {
                    informationDecodee.add(true);
                }
            }
        }

        return informationDecodee;
    }

    /**
     * Calcule la distance de Hamming entre la trame donnée et une trame de référence.
     *
     * @param bit1 Le premier bit à comparer.
     * @param bit2 Le deuxième bit à comparer.
     * @param bit3 Le troisième bit à comparer.
     * @param ref1 Le premier bit de référence.
     * @param ref2 Le deuxième bit de référence.
     * @param ref3 Le troisième bit de référence.
     * @return La distance de Hamming entre les bits donnés et la référence.
     */
    private int hammingDistance(boolean bit1, boolean bit2, boolean bit3, boolean ref1, boolean ref2, boolean ref3) {
        int distance = 0;
        if (bit1 != ref1) distance++;
        if (bit2 != ref2) distance++;
        if (bit3 != ref3) distance++;
        return distance;
    }

    /**
     * Émet l'information contenue dans une source après décodage.
     *
     * @throws InformationNonConformeException si l'Information comporte une anomalie,
     *                                          comme le fait d'être nulle.
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationRecue == null) {
            throw new InformationNonConformeException("L'information reçue est nulle");
        }

        this.informationEmise = decoder(this.informationRecue);

        for (DestinationInterface<Boolean> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }
}
