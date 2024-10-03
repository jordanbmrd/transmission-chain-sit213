package tests;

import information.Information;
import org.junit.Before;
import org.junit.Test;
import simulateur.Simulateur;
import transmetteurs.TransmetteurGaussien;
import transmetteurs.TransmetteurMultiTrajets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransmetteurMultiTrajetsTest {

    private TransmetteurMultiTrajets transmetteurMultiTrajets;
    private final float[][] trajets = {{100, 1.0f}, {20, 0.75f}, {5, 0.5f}, {80, 0.1f}, {120, 0.8f}};

    /**
     * Initialise le transmetteur multi-trajets avec les trajets de test
     */
    @Before
    public void setUp() {
        transmetteurMultiTrajets = new TransmetteurMultiTrajets(trajets);
    }

    /**
     * Teste le nombre d'éléments de l'information émise par le transmetteur multi-trajets
     *
     * @throws Exception si une exception est levée
     */
    @Test
    public void nbMultiTrajet() throws Exception {
        // Créer une information de test
        Information<Float> information = new Information<>(new Float[]{1.0f, 2.0f, 3.0f, 4.0f, 5.0f});
        transmetteurMultiTrajets.recevoir(information);

        // Récupérer l'information émise par le transmetteur
        Information<Float> informationRecue = transmetteurMultiTrajets.getInformationEmise();
        assertNotNull(transmetteurMultiTrajets.getInformationEmise());
        assertEquals(5, informationRecue.nbElements());

        // Vérifier que chaque élément de l'information reçue n'est pas nulle
        for (int i = 0; i < 5; i++) {
            assertNotNull(informationRecue.iemeElement(i));
        }
    }

    /**
     * Teste le teb pour chaque configuration de multi-trajets
     */
    @Test
    public void tebAugmente() throws Exception {
        // SM = sans multiTrajet
        String[] paramsSM = {"-mess", "200", "-seed", "1234", "-form", "RZ", "-nbEch", "30", "-snrpb", "-2"};
        Simulateur simulateurSM = new Simulateur(paramsSM);
        simulateurSM.execute();
        double tebSM = simulateurSM.calculTauxErreurBinaire();
        System.out.println("TEB SM: " + tebSM + "\n");

        String[] commande = {"-mess", "200", "-seed", "1234", "-form", "RZ", "-nbEch", "30", "-snrpb", "-2", "-ti"};

        // AM = Avec multiTrajet
        for (int i = 1; i <= trajets.length; i++) {
            // Créer une liste de paramètres pour la simulation avec multi-trajets
            List<String> paramsAMList = new ArrayList<>(Arrays.asList(commande));

            // Ajouter les trajets un par un sous forme de String
            for (int j = 0; j < i; j++) {
                paramsAMList.add(String.valueOf((int) trajets[j][0]));  // 1er parametre en int
                paramsAMList.add(String.valueOf(trajets[j][1]));
            }

            // Convertir la liste en tableau String[]
            String[] paramsAM = paramsAMList.toArray(new String[0]);

            Simulateur simulateurAM = new Simulateur(paramsAM);
            simulateurAM.execute();
            double tebAM = simulateurAM.calculTauxErreurBinaire();

            // Afficher les valeurs de tebAM et la commande paramsAM associée
            System.out.println("TEB AM: " + tebAM);
            System.out.println("Commande paramsAM: " + Arrays.toString(paramsAM) + ", Nombre de MultiTrajet : " + i + "\n");
        }
    }

    /**
     * Teste le snr pour chaque configuration de multi-trajets
     */
    @Test
    public void snrAugmente() throws Exception {
        // SM = sans multiTrajet
        Information<Float> information = new Information<>(new Float[]{1.0f, 2.0f, 3.0f, 4.0f, 5.0f});
        TransmetteurGaussien transmetteurGaussien = new TransmetteurGaussien(100, 5, 22);
        transmetteurGaussien.recevoir(information);
        float snrReelSM = transmetteurGaussien.getSNRReel();

        System.out.println("SNR réel sans multi-trajets : " + snrReelSM + "\n");

        // AM = Avec multiTrajet
        for (int i = 1; i <= trajets.length; i++) {
            // Créer un transmetteur multi-trajets avec les i premiers trajets
            TransmetteurMultiTrajets transmetteurMultiTrajets = new TransmetteurMultiTrajets(Arrays.copyOf(trajets, i));
            transmetteurMultiTrajets.connecter(transmetteurGaussien);
            transmetteurMultiTrajets.recevoir(information);
            float snrReelAM = transmetteurGaussien.getSNRReel();

            // Afficher les valeurs de snrReelAM et la configuration des trajets
            System.out.println("SNR réel (nombre de MultiTrajet : " + i + ") :" + snrReelAM);
        }
    }
}