package codeurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

public class Decodeur extends AbstractCodeur<Boolean, Boolean> {
    private Information<Boolean> appliquerCodage(Information<Boolean> information) {
        Information<Boolean> informationCodee = new Information<>();

        for (boolean bit : information) {
            if (bit) {  // bit = 1
                informationCodee.add(true);
                informationCodee.add(false);
                informationCodee.add(true);
            } else {    // bit = 0
                informationCodee.add(false);
                informationCodee.add(true);
                informationCodee.add(false);
            }
        }

        return informationCodee;
    }

    /**
     * pour émettre l'information contenue dans une source
     *
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationRecue == null) {
            throw new InformationNonConformeException("L'information reçue est nulle");
        }

        this.informationEmise = appliquerCodage(this.informationEmise);

        for (DestinationInterface<Boolean> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }
}
