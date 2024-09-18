package tests;

import destinations.DestinationFinale;
import information.Information;
import information.InformationNonConformeException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DestinationFinaleTest {

    private DestinationFinale destinationFinale;

    @Before
    public void setUp() {
        // Initialize the DestinationFinale instance
        destinationFinale = new DestinationFinale();
    }

    @Test
    public void testRecevoirValidInformation() throws InformationNonConformeException {
        // Prepare valid information to be received
        Information<Boolean> information = new Information<>();
        information.add(true);
        information.add(false);
        information.add(true);

        // Call recevoir to store the information
        destinationFinale.recevoir(information);

        // Verify that the received information matches the input
        assertEquals("The received information should match the input information", information, destinationFinale.getInformationRecue());
    }

    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirWithNullInformationThrowsException() throws InformationNonConformeException {
        // Call recevoir with null information, expecting an exception
        destinationFinale.recevoir(null);
    }

    @Test
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        // Prepare empty information
        Information<Boolean> emptyInformation = new Information<>();

        // Call recevoir with the empty information
        destinationFinale.recevoir(emptyInformation);

        // Verify that the received information is empty
        assertEquals("The received information should be empty", 0, destinationFinale.getInformationRecue().nbElements());
    }

    @Test
    public void testRecevoirSingleElementInformation() throws InformationNonConformeException {
        // Prepare information with a single element
        Information<Boolean> singleElementInformation = new Information<>();
        singleElementInformation.add(true);

        // Call recevoir with the single element information
        destinationFinale.recevoir(singleElementInformation);

        // Verify that the received information matches the input
        assertEquals("The received information should contain one element", singleElementInformation, destinationFinale.getInformationRecue());
    }
}
