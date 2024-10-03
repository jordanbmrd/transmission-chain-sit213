package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;

/**
 * La classe {@code TransmetteurMultiTrajets} représente un transmetteur capable de gérer
 * plusieurs trajets pour émettre des signaux avec atténuation et décalage.
 *
 * <p>Ce transmetteur utilise un tableau de trajets où chaque trajet est défini par un
 * décalage et un coefficient d'atténuation. Lorsqu'il reçoit des informations, il les
 * combine selon ces trajets avant de les émettre vers des destinations connectées.</p>
 *
 * @param <T> Le type des données à transmettre.
 * @param <R> Le type des données émises.
 */
public class TransmetteurMultiTrajets extends Transmetteur<Float, Float> {
    private final float[][] ti; // Matrice des trajets (décalage et atténuation)
    private int decalageMaximum; // Décalage maximum parmi les trajets

    /**
     * Constructeur de la classe {@code TransmetteurMultiTrajets}.
     *
     * @param ti Un tableau à deux dimensions représentant les trajets, où chaque élément
     *           contient un décalage (int) et un coefficient d'atténuation (float).
     */
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
     * Reçoit des informations et les émet immédiatement.
     *
     * @param information Les informations à recevoir.
     * @throws InformationNonConformeException Si l'information reçue n'est pas conforme.
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConformeException {
        this.informationRecue = information;
        emettre();
    }

    /**
     * Émet les informations reçues vers toutes les destinations connectées.
     *
     * @throws InformationNonConformeException Si aucune information n'a été reçue à émettre.
     */
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

    /**
     * Génère un signal combiné à partir des informations reçues.
     *
     * @param information Les informations reçues.
     * @return Un objet {@code Information} contenant le signal combiné.
     */
    private Information<Float> genererSignalCombine(Information<Float> information) {
        Information<Float> signalRetarde = genererSignalRetardeEtAttenue(information);
        Information<Float> informationFinale = new Information<>();

        for (int i = 0; i < information.nbElements(); i++) {
            signalRetarde.setIemeElement(i, signalRetarde.iemeElement(i) + information.iemeElement(i));
        }

        for (int i = 0; i < signalRetarde.nbElements(); i++) {
            informationFinale.add(signalRetarde.iemeElement(i));
        }

        return informationFinale;
    }

    /**
     * Génère un signal retardé et atténué à partir des informations reçues selon les trajets.
     *
     * @param information Les informations reçues.
     * @return Un objet {@code Information} contenant le signal retardé et atténué.
     */
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
