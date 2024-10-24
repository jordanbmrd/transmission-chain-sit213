package tests;

import org.junit.Test;
import simulateur.Simulateur;
import utils.Form;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;

/**
 * Classe de test pour différences fonctions liées au TEB.
 */
public class TEBTest {
    /**
     * Exécute une simulation pour une forme de modulation et retourne une les valeurs du SNR et du TEB
     *
     * @param modulation La forme de modulation à utiliser pour la simulation (NRZ, RZ, NRZT)
     * @return Une Map où les clés sont les valeurs de SNR et les valeurs sont les TEB correspondants
     * @throws Exception Si une erreur se produit lors de l'exécution de la simulation
     */
    private Map<Integer, Double> runSimulation(Form modulation) throws Exception {
        Map<Integer, Double> snrToTebMap = new LinkedHashMap<>();
        int snr = 25;
        double teb = 0.00;

        while (teb <= 0.01) {
            String[] params = {"-mess", "20000", "-form", modulation.toString(), "-nbEch", "9", "-snrpb", String.valueOf(snr)};
            Simulateur simulateur = new Simulateur(params);
            simulateur.execute();
            teb = simulateur.calculTauxErreurBinaire();
            snrToTebMap.put(snr, teb);
            snr--;
        }

        return snrToTebMap;
    }

    /**
     * Exécute une simulation pour chaque forme de modulation et affiche les résultats
     *
     * @throws Exception Si une erreur se produit lors de l'exécution de la simulation
     */
    @Test
    public void testSimulationMultipleTimes() throws Exception {
        Map<Integer, Double> snrToTebMapNRZT = runSimulation(Form.NRZT);
        System.out.println("NRZT = SNR to TEB map: " + snrToTebMapNRZT);

        Map<Integer, Double> snrToTebMapNRZ = runSimulation(Form.NRZ);
        System.out.println("NRZ = SNR to TEB map: " + snrToTebMapNRZ);

        Map<Integer, Double> snrToTebMapRZ = runSimulation(Form.RZ);
        System.out.println("RZ = SNR to TEB map: " + snrToTebMapRZ);

        // Validate that the maps are not empty
        assertFalse("The SNR to TEB map for NRZT should not be empty", snrToTebMapNRZT.isEmpty());
        assertFalse("The SNR to TEB map for NRZ should not be empty", snrToTebMapNRZ.isEmpty());
        assertFalse("The SNR to TEB map for RZ should not be empty", snrToTebMapRZ.isEmpty());
    }
}