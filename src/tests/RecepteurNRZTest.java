package tests;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import modulation.recepteurs.RecepteurNRZ;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import utils.Code;

import static org.junit.Assert.*;

public class RecepteurNRZTest {

    private RecepteurNRZ recepteurNRZ;
    private DestinationInterface<Boolean> mockDestination;

    @Before
    public void setUp() {
        // Initialize the RecepteurNRZ with a taillePeriode of 3 for testing
        recepteurNRZ = new RecepteurNRZ(3);

        // Create a mock for DestinationInterface
        mockDestination = EasyMock.createMock(DestinationInterface.class);

        // Connect the mock destination to the receiver
        recepteurNRZ.connecter(mockDestination);
    }

    @Test
    public void testRecevoirAndEmettreValidInformation() throws InformationNonConformeException {
        // Prepare valid analog float information to be received
        Information<Float> information = new Information<>();
        information.add(5f); // true (NRZ: aMax)
        information.add(5f);
        information.add(5f);
        information.add(0f); // false (NRZ: aMin)
        information.add(0f);
        information.add(0f);
        information.add(5f); // true (NRZ: aMax)
        information.add(5f);
        information.add(5f);

        // Expected Boolean information after AN conversion (NRZ: 5 -> true, 0 -> false)
        Information<Boolean> expectedInformation = new Information<>();
        expectedInformation.add(true);
        expectedInformation.add(false);
        expectedInformation.add(true);

        // Expect the destination to receive the same Boolean information
        mockDestination.recevoir(expectedInformation);
        EasyMock.replay(mockDestination);

        // Call recevoir, which should call emettre and notify the mock destination
        recepteurNRZ.recevoir(information);

        // Verify that the destination received the emitted information
        EasyMock.verify(mockDestination);

        // Check that the emitted information is the converted Boolean information
        assertEquals("The emitted information should be the AN converted Boolean information",
                expectedInformation, recepteurNRZ.getInformationEmise());
    }

    @Test
    public void testConversionANWithNRZCode() throws InformationNonConformeException {
        // Prepare analog float information for NRZ conversion
        Information<Float> analogInformation = new Information<>();
        analogInformation.add(5f); // true (NRZ: aMax)
        analogInformation.add(5f);
        analogInformation.add(5f);
        analogInformation.add(0f); // false (NRZ: aMin)
        analogInformation.add(0f);
        analogInformation.add(0f);

        // Expected Boolean information after AN conversion
        Information<Boolean> expectedInformation = new Information<>();
        expectedInformation.add(true);
        expectedInformation.add(false);

        // Perform the AN conversion
        Information<Boolean> convertedInformation = recepteurNRZ.conversionAN(analogInformation, Code.NRZ, 5f, 0f);

        // Check that the converted information matches the expected Boolean output
        assertEquals("The converted information should match the expected NRZ Boolean values",
                expectedInformation, convertedInformation);
    }

    @Test
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        // Prepare empty analog information
        Information<Float> emptyInformation = new Information<>();

        // Expected empty Boolean information after conversion
        Information<Boolean> expectedInformation = new Information<>();

        // Expect the mock destination to receive the empty information
        mockDestination.recevoir(expectedInformation);
        EasyMock.replay(mockDestination);

        // Call recevoir with the empty information
        recepteurNRZ.recevoir(emptyInformation);

        // Verify that the destination received the empty information
        EasyMock.verify(mockDestination);

        // Check that the emitted information is empty
        assertEquals("The emitted information should be empty",
                expectedInformation, recepteurNRZ.getInformationEmise());
    }

    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirWithNullInformationThrowsException() throws InformationNonConformeException {
        // Call recevoir with null information, expecting an exception
        recepteurNRZ.recevoir(null);
    }

    @Test
    public void testConversionANWithEmptyInformation() throws InformationNonConformeException {
        // Prepare empty analog information
        Information<Float> emptyInformation = new Information<>();

        // Perform the AN conversion
        Information<Boolean> convertedInformation = recepteurNRZ.conversionAN(emptyInformation, Code.NRZ, 5f, 0f);

        // Check that the converted information is empty
        assertEquals("The converted information should be empty", 0, convertedInformation.nbElements());
    }

    @Test
    public void testConversionANWithNonMatchingTaillePeriode() throws InformationNonConformeException {
        // Prepare analog information with non-matching period size
        Information<Float> analogInformation = new Information<>();
        analogInformation.add(5f); // true (NRZ: aMax)
        analogInformation.add(0f); // false (NRZ: aMin)

        // Perform the AN conversion (should result in incomplete conversion)
        Information<Boolean> convertedInformation = recepteurNRZ.conversionAN(analogInformation, Code.NRZ, 5f, 0f);

        // Expected: no Boolean values because the period size doesn't match
        assertEquals("The converted information should be empty due to non-matching period size", 0, convertedInformation.nbElements());
    }
}
