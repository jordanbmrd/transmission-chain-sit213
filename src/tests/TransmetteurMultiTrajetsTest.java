package tests;

import information.Information;
import org.junit.Before;
import org.junit.Test;
import simulateur.Simulateur;
import transmetteurs.TransmetteurGaussien;
import transmetteurs.TransmetteurMultiTrajets;
import utils.Form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransmetteurMultiTrajetsTest {

    private TransmetteurMultiTrajets transmetteurMultiTrajets;
    private final float[][] trajets = {{20, 0.4f}, {30, 0.3f}, {40, 0.2f}, {50, 0.1f}, {120, 0.05f}};

    @Before
    public void setUp() {
        transmetteurMultiTrajets = new TransmetteurMultiTrajets(trajets);
    }

    @Test
    public void nbMultiTrajet() throws Exception {
        Information<Float> information = new Information<>(new Float[]{1.0f, 2.0f, 3.0f, 4.0f, 5.0f});
        transmetteurMultiTrajets.recevoir(information);

        Information<Float> informationRecue = transmetteurMultiTrajets.getInformationEmise();
        assertNotNull(transmetteurMultiTrajets.getInformationEmise());
        //assertEquals(5, informationRecue.nbElements());

        for (int i = 0; i < 5; i++) {
            assertNotNull(informationRecue.iemeElement(i));
        }
    }

    @Test
    public void tebAugmente() throws Exception {
        // SM = sans multiTrajet
        String[] paramsSM = {"-mess", "10", "-seed", "1308", "-form", "NRZT", "-nbEch", "100", "-snrpb", "-3"};
        Simulateur simulateurSM = new Simulateur(paramsSM);
        simulateurSM.execute();
        double tebSM = simulateurSM.calculTauxErreurBinaire();
        System.out.println("TEB SM: " + tebSM + "\n");

        String[] commande = {"-mess", "10", "-seed", "1308", "-form", "NRZT", "-nbEch", "100", "-snrpb", "-3", "-ti"};

        // AM = Avec multiTrajet
        for (int i = 1; i <= trajets.length; i++) {
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
            System.out.println("Commande paramsAM: " + Arrays.toString(paramsAM) + "\n");
        }
    }

    @Test
    public void snrAugmente() throws Exception {
        // SM = sans multiTrajet
        Information<Float> information = new Information<>(new Float[]{1.0f, 2.0f, 3.0f, 4.0f, 5.0f});
        TransmetteurGaussien transmetteurGaussien = new TransmetteurGaussien(Form.RZ, 100, 0);
        transmetteurGaussien.recevoir(information);
        float snrReelSM = transmetteurGaussien.getSNRReel();

        System.out.println("SNR reel sans multiTrajet : " + snrReelSM + "\n");

        // AM = Avec multiTrajet
        for (int i = 1; i <= trajets.length; i++) {
            TransmetteurMultiTrajets transmetteurMultiTrajets = new TransmetteurMultiTrajets(Arrays.copyOf(trajets, i));
            transmetteurMultiTrajets.connecter(transmetteurGaussien);
            transmetteurMultiTrajets.recevoir(information);
            float snrReelAM = transmetteurGaussien.getSNRReel();

            // Afficher les valeurs de snrReelAM et la configuration des trajets
            System.out.println("SNR Reel " + snrReelAM + " Pour le trajet numero " + i);
        }
    }
}