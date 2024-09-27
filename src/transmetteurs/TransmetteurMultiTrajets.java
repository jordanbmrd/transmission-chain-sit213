package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

public class TransmetteurMultiTrajets extends Transmetteur<Float, Float> {
    private final float[][] ti;
    private int decalageMaximum;

    public TransmetteurMultiTrajets(float[][] ti) {
        super();
        this.ti = ti;
        this.decalageMaximum = 0;

        // Calcul du décalage maximum
        for (float[] trajet : ti) {
            decalageMaximum = Math.max(decalageMaximum, (int) trajet[0]);
        }
    }

    /**
     * Reçoit une information. Cette méthode, en fin d'exécution,
     * appelle la méthode émettre.
     *
     * @param information l'information reçue
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        this.informationRecue = information;
        emettre();
    }

    /**
     * Émet l'information construite par le transmetteur
     *
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationRecue == null) {
            throw new InformationNonConformeException("Aucune information reçue à émettre.");
        }

        this.informationEmise = genererSignalFinal(this.informationRecue);

        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }

    private Information<Float> genererSignalFinal(Information<Float> information) {
        Information<Float> signalRetarde = genererSignalRetarde(information);
        Information<Float> informationFinale = new Information<>();

        // Ajout des valeurs au signal retardé
        for (int i = 0; i < information.nbElements(); i++) {
            signalRetarde.setIemeElement(i, signalRetarde.iemeElement(i) + information.iemeElement(i));
        }

        // Construction de l'information finale
        for (int j = 0; j < information.nbElements(); j++) {
            informationFinale.add(signalRetarde.iemeElement(j + this.decalageMaximum));
        }

        return informationFinale;
    }

    private Information<Float> genererSignalRetarde(Information<Float> information) {
        int length = information.nbElements() + this.decalageMaximum;
        Information<Float> informationGeneree = new Information<>();

        // Initialisation du signal retardé avec des zéros
        for (int i = 0; i < length; i++) {
            informationGeneree.add(0f);
        }

        // Application des trajets
        for (float[] trajet : this.ti) {
            int dt = (int) trajet[0]; // Décalage
            float ar = trajet[1];      // Atténuation

            int index = dt;
            for (float info : information) {
                informationGeneree.setIemeElement(index, informationGeneree.iemeElement(index) + info * ar);
                index++;
            }
        }

        return informationGeneree;
    }
}
