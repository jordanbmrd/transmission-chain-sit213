package tests;

import org.junit.Test;
import simulateur.Simulateur;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;

public class TebTest {

    @Test
    public void testSimulationMultipleTimes() throws Exception {
        List<Float> tebList = new ArrayList<>();
        String[] params = {"-mess", "20000", "-form", "NRZT", "-nbEch", "9", "-snrpb", "5", "-ampl", "-5", "5"};

        for (int i = 0; i < 10; i++) { // Adjust the number of iterations as needed
            Simulateur simulateur = new Simulateur(params);
            simulateur.execute(); // Assuming execute() method exists in Simulateur class
            float teb = simulateur.calculTauxErreurBinaire(); // Assuming getTeb() method exists in Simulateur class
            tebList.add(teb);
        }

        System.out.println("TEB list: " + tebList);

        // Validate that the list is not empty
        assertFalse("The TEB list should not be empty", tebList.isEmpty());
    }
}