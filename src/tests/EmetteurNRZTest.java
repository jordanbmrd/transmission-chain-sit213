package tests;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import modulation.emetteurs.EmetteurNRZ;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import utils.Code;

import static org.junit.Assert.*;

public class EmetteurNRZTest {

    private EmetteurNRZ emetteurNRZ;
    private DestinationInterface<Float> mockDestination;

    @Before
    public void setUp() {
        // Initialize the EmetteurNRZ with a taillePeriode of 3 for testing
        emetteurNRZ = new EmetteurNRZ(3);

        // Create a mock for DestinationInterface
        mockDestination = EasyMock.createMock(DestinationInterface.class);

        // Connect the mock destination to the transmitter
        emetteurNRZ.connecter(mockDestination);
    }

    @Test
    public void testRecevoirAndEmettreValidInformation() throws InformationNonConformeException {
        // Prepare valid Boolean information to be transmitted
        Information<Boolean> information = new Information<>();
        information.add(true);
        information.add(false);
        information.add(true);

        // Expected analog information using NRZ coding (aMax = 5, aMin = 0)
        Information<Float> expectedInformation = new Information<>();
        expectedInformation.add(5f); // true -> 5f
        expectedInformation.add(5f);
        expectedInformation.add(5f);
        expectedInformation.add(0f); // false -> 0f
        expectedInformation.add(0f);
        expectedInformation.add(0f);
        expectedInformation.add(5f); // true -> 5f
        expectedInformation.add(5f);
        expectedInformation.add(5f);

        // Expect the destination to receive the same information
        mockDestination.recevoir(expectedInformation);
        EasyMock.replay(mockDestination);

        // Call recevoir, which should call emettre and notify the mock destination
        emetteurNRZ.recevoir(information);

        // Verify that the destination received the emitted information
        EasyMock.verify(mockDestination);

        // Check that the received and emitted information are the same
        assertEquals("The emitted information should be the NRZ converted float information",
                expectedInformation, emetteurNRZ.getInformationEmise());
    }

    @Test
    public void testConversionNAWithNRZCode() throws InformationNonConformeException {
        // Prepare valid Boolean information
        Information<Boolean> information = new Information<>();
        information.add(true);
        information.add(false);

        // Expected analog conversion using NRZ coding (aMax = 5, aMin = 0)
        Information<Float> expectedInformation = new Information<>();
        expectedInformation.add(5f); // true -> 5f
        expectedInformation.add(5f);
        expectedInformation.add(5f);
        expectedInformation.add(0f); // false -> 0f
        expectedInformation.add(0f);
        expectedInformation.add(0f);

        // Perform the NA conversion
        Information<Float> convertedInformation = emetteurNRZ.conversionNA(information, Code.NRZ, 5f, 0f);

        // Check that the converted information matches the expected output
        assertEquals("The converted information should match the expected NRZ analog values",
                expectedInformation, convertedInformation);
    }

    @Test
    public void testEmettreWithNullInformationThrowsException() {
        try {
            // Call emettre with null information and expect an exception
            emetteurNRZ.recevoir(null);
            fail("Expected InformationNonConformeException to be thrown");
        } catch (InformationNonConformeException e) {
            // Test passed
        }
    }

    @Test
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        // Prepare empty Boolean information
        Information<Boolean> emptyInformation = new Information<>();

        // Expected empty float information
        Information<Float> expectedInformation = new Information<>();

        // Expect the destination to receive the empty information
        mockDestination.recevoir(expectedInformation);
        EasyMock.replay(mockDestination);

        // Call recevoir with the empty information
        emetteurNRZ.recevoir(emptyInformation);

        // Verify that the destination received the empty information
        EasyMock.verify(mockDestination);

        // Check that the emitted information is empty
        assertEquals("The emitted information should be empty",
                expectedInformation, emetteurNRZ.getInformationEmise());
    }

    @Test
    public void testConversionNAWithEmptyInformation() throws InformationNonConformeException {
        // Prepare empty Boolean information
        Information<Boolean> emptyInformation = new Information<>();

        // Perform the NA conversion
        Information<Float> convertedInformation = emetteurNRZ.conversionNA(emptyInformation, Code.NRZ, 5f, 0f);

        // Check that the converted information is empty
        assertEquals("The converted information should be empty", 0, convertedInformation.nbElements());
    }
}
