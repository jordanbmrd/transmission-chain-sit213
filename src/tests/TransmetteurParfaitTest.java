package tests;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import transmetteurs.TransmetteurParfait;

import static org.junit.Assert.*;

public class TransmetteurParfaitTest {

    private TransmetteurParfait<Boolean> transmetteurParfait;
    private DestinationInterface<Boolean> mockDestination;

    @Before
    public void setUp() {
        // Create the TransmetteurParfait instance
        transmetteurParfait = new TransmetteurParfait<>();

        // Create a mock for DestinationInterface
        mockDestination = EasyMock.createMock(DestinationInterface.class);

        // Connect the mock destination to the transmitter
        transmetteurParfait.connecter(mockDestination);
    }

    @Test
    public void testRecevoirAndEmettreWithValidInformation() throws InformationNonConformeException {
        // Prepare the information to be transmitted
        Information<Boolean> information = new Information<>();
        information.add(true);
        information.add(false);
        information.add(true);

        // Expect the destination to receive the same information
        mockDestination.recevoir(information);
        EasyMock.replay(mockDestination);

        // Call recevoir, which should call emettre and notify the mock destination
        transmetteurParfait.recevoir(information);

        // Verify that the destination received the emitted information
        EasyMock.verify(mockDestination);

        // Check that the received and emitted information are the same
        assertEquals("The emitted information should be the same as the received information",
                information, transmetteurParfait.getInformationEmise());
    }

    @Test
    public void testEmettreWithMultipleDestinations() throws InformationNonConformeException {
        // Create two mock destinations
        DestinationInterface<Boolean> mockDestination1 = EasyMock.createMock(DestinationInterface.class);
        DestinationInterface<Boolean> mockDestination2 = EasyMock.createMock(DestinationInterface.class);

        // Connect the two mock destinations
        transmetteurParfait.connecter(mockDestination1);
        transmetteurParfait.connecter(mockDestination2);

        // Prepare the information to be transmitted
        Information<Boolean> information = new Information<>();
        information.add(true);
        information.add(false);

        // Expect both mock destinations to receive the same information
        mockDestination1.recevoir(information);
        mockDestination2.recevoir(information);
        EasyMock.replay(mockDestination1, mockDestination2);

        // Call recevoir, which should trigger emettre and notify both mock destinations
        transmetteurParfait.recevoir(information);

        // Verify that both destinations received the emitted information
        EasyMock.verify(mockDestination1, mockDestination2);

        // Check that the emitted information is correct
        assertEquals("The emitted information should match the received information",
                information, transmetteurParfait.getInformationEmise());
    }

    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirWithNullInformationThrowsException() throws InformationNonConformeException {
        // Call recevoir with null information, expecting an exception
        transmetteurParfait.recevoir(null);
    }

    @Test
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        // Prepare empty information
        Information<Boolean> emptyInformation = new Information<>();

        // Expect the mock destination to receive the empty information
        mockDestination.recevoir(emptyInformation);
        EasyMock.replay(mockDestination);

        // Call recevoir with the empty information
        transmetteurParfait.recevoir(emptyInformation);

        // Verify the destination received the empty information
        EasyMock.verify(mockDestination);

        // Check that the emitted information is empty
        assertEquals("The emitted information should be empty",
                emptyInformation, transmetteurParfait.getInformationEmise());
    }
}
