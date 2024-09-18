package tests;

import information.Information;
import information.InformationNonConformeException;
import modulation.emetteurs.Emetteur;
import org.junit.Before;
import org.junit.Test;
import utils.Code;
import visualisations.SondeAnalogique;

import static org.junit.Assert.*;

public class EmetteurTest {

    private Emetteur emetteurNRZ;
    private Emetteur emetteurNRZNeg;
    private Emetteur emetteurRZ;
    private Emetteur emetteurNRZT;


    Boolean[] serie = new Boolean[]{true, true, false, true, false, false, false, true, false, true}; //  1101000101
    private final Information<Boolean> information = new Information<>(serie);

    @Before
    public void setUp() {
        emetteurRZ = new Emetteur(10, 1.0f, 0.0f, Code.RZ);
        emetteurNRZ = new Emetteur(10, 1.0f, 0.0f, Code.NRZ);
        emetteurNRZNeg = new Emetteur(10, 1.0f, -1.0f, Code.NRZ);
        emetteurNRZT = new Emetteur(10, 1.0f, 0.0f, Code.NRZT);
    }


    @Test
    public void testEmettre() throws InformationNonConformeException {

        emetteurNRZ.recevoir(information);
        emetteurNRZ.emettre();
        assertNotNull(emetteurNRZ.getInformationEmise());

        emetteurNRZNeg.recevoir(information);
        emetteurNRZNeg.emettre();
        assertNotNull(emetteurNRZNeg.getInformationEmise());

        emetteurRZ.recevoir(information);
        emetteurRZ.emettre();
        assertNotNull(emetteurRZ.getInformationEmise());

        emetteurNRZT.recevoir(information);
        emetteurNRZT.emettre();
        assertNotNull(emetteurNRZT.getInformationEmise());
    }

    @Test
    public void testRecevoir() throws InformationNonConformeException {

        emetteurNRZ.recevoir(information);
        assertNotNull(emetteurNRZ.getInformationRecue());

        emetteurNRZNeg.recevoir(information);
        assertNotNull(emetteurNRZNeg.getInformationRecue());

        emetteurRZ.recevoir(information);
        assertNotNull(emetteurRZ.getInformationRecue());

        emetteurNRZT.recevoir(information);
        assertNotNull(emetteurNRZT.getInformationRecue());
    }

    @Test
    public void testConversionNA() throws InformationNonConformeException {

        Information<Float> resultNRZ = emetteurNRZ.conversionNA(information);
        assertEquals(100, resultNRZ.nbElements());
        assertEquals(1.0f, resultNRZ.iemeElement(0), 0.0f);
        assertEquals(0.0f, resultNRZ.iemeElement(20), 0.0f);

        Information<Float> resultNRZNeg = emetteurNRZNeg.conversionNA(information);
        assertEquals(100, resultNRZNeg.nbElements());
        assertEquals(1.0f, resultNRZNeg.iemeElement(0), 0.0f);
        assertEquals(-1.0f, resultNRZNeg.iemeElement(20), 0.0f);

        // Test pour le codage RZ
        Information<Float> resultRZ = emetteurRZ.conversionNA(information);
        assertEquals(100, resultRZ.nbElements());
        assertEquals(0.0f, resultRZ.iemeElement(0), 0.0f);
        assertEquals(1.0f, resultRZ.iemeElement(3), 0.0f);
        assertEquals(0.0f, resultRZ.iemeElement(20), 0.0f);

        // Test pour le codage NRZT
        Information<Float> resultNRZT = emetteurNRZT.conversionNA(information);
        assertEquals(100, resultNRZT.nbElements());
        assertEquals(0.0f, resultNRZT.iemeElement(0), 0.0f);
        assertEquals(1.0f, resultNRZT.iemeElement(10), 0.0f);
        assertEquals(0.0f, resultNRZT.iemeElement(20), 0.0f);

        // Vérification que le codage NRZT atteint bien la valeur maximale
        boolean atteintMax = false;
        for (int i = 0; i < resultNRZT.nbElements(); i++) {
            if (resultNRZT.iemeElement(i) == 1.0f) {
                atteintMax = true;
                break;
            }
        }
        assertTrue(atteintMax);

        // Utilisation de la sonde pour visualiser les valeurs
        SondeAnalogique sondeAnalogique = new SondeAnalogique("Sonde NRZT");
        this.emetteurNRZT.connecter(sondeAnalogique);
        emetteurNRZT.recevoir(information);
        emetteurNRZT.emettre();
        sondeAnalogique.recevoir(emetteurNRZT.getInformationEmise());
    }
}