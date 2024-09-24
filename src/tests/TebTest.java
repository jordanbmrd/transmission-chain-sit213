package tests;

import org.junit.Test;
import simulateur.Simulateur;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;

public class TebTest {

    private Map<Integer, Double> runSimulation(String modulation) throws Exception {
        Map<Integer, Double> snrToTebMap = new LinkedHashMap<>(); // la HashMap classique fou le bordel dans l'affichage
        int snr = 25;
        double teb = 0.00;

        while (teb <= 0.01) {
            String[] params = {"-mess", "20000", "-form", modulation, "-nbEch", "9", "-snrpb", String.valueOf(snr)};
            Simulateur simulateur = new Simulateur(params);
            simulateur.execute();
            teb = simulateur.calculTauxErreurBinaire();
            snrToTebMap.put(snr, teb);
            snr--;
        }

        return snrToTebMap;
    }

    @Test
    public void testSimulationMultipleTimes() throws Exception {
        Map<Integer, Double> snrToTebMapNRZT = runSimulation("NRZT");
        System.out.println("NRZT = SNR to TEB map: " + snrToTebMapNRZT);

        Map<Integer, Double> snrToTebMapNRZ = runSimulation("NRZ");
        System.out.println("NRZ = SNR to TEB map: " + snrToTebMapNRZ);

        Map<Integer, Double> snrToTebMapRZ = runSimulation("RZ");
        System.out.println("RZ = SNR to TEB map: " + snrToTebMapRZ);

        // Validate that the maps are not empty
        assertFalse("The SNR to TEB map for NRZT should not be empty", snrToTebMapNRZT.isEmpty());
        assertFalse("The SNR to TEB map for NRZ should not be empty", snrToTebMapNRZ.isEmpty());
        assertFalse("The SNR to TEB map for RZ should not be empty", snrToTebMapRZ.isEmpty());
    }
}