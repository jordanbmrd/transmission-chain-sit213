package tests;

import information.Information;
import org.junit.Before;
import org.junit.Test;
import transmetteurs.TransmetteurMultiTrajets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

;

public class TransmetteurMultiTrajetsTest {

    private TransmetteurMultiTrajets transmetteurMultiTrajets;

    @Before
    public void setUp() {
        float[][] trajets = {{20, 0.4f}, {30, 0.3f}, {40, 0.2f}, {50, 0.1f}, {120, 0.05f}};
        transmetteurMultiTrajets = new TransmetteurMultiTrajets(trajets);
    }

    @Test
    public void nbMultiTrajet() throws Exception {
        Information<Float> information = new Information<>(new Float[]{1.0f, 2.0f, 3.0f, 4.0f, 5.0f});
        transmetteurMultiTrajets.recevoir(information);

        Information<Float> informationRecue = transmetteurMultiTrajets.getInformationEmise();
        assertNotNull(informationRecue);
        assertEquals(5, informationRecue.nbElements());

        for (int i = 0; i < 5; i++) {
            assertNotNull(informationRecue.iemeElement(i));
        }
    }

    @Test
    public void augmentePasLeSNR() {
    }


}