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

    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        this.informationRecue = information;
        emettre();
    }

    @Override
    public void emettre() throws InformationNonConformeException {
        if (this.informationRecue == null) {
            throw new InformationNonConformeException("Aucune information reçue à émettre.");
        }

        this.informationEmise = genererSignalCombine(this.informationRecue);

        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(this.informationEmise);
        }
    }

    private Information<Float> genererSignalCombine(Information<Float> information) {
        Information<Float> signalRetarde = genererSignalRetardeEtAttenue(information);
        Information<Float> informationFinale = new Information<>();

        // Ajout des valeurs au signal retardé avec vérification des indices
        for (int j = 0; j < information.nbElements() && (j + this.decalageMaximum) < signalRetarde.nbElements(); j++) {
            informationFinale.add(signalRetarde.iemeElement(j + this.decalageMaximum) + information.iemeElement(j));
        }

        return informationFinale;
    }

    private Information<Float> genererSignalRetardeEtAttenue(Information<Float> information) {
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
                if (index < informationGeneree.nbElements()) {
                    informationGeneree.setIemeElement(index, informationGeneree.iemeElement(index) + info * ar);
                }
                index++;
            }
        }

        return informationGeneree;
    }
}
