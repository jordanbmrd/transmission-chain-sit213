package tests;

import information.Information;
import org.junit.Before;
import org.junit.Test;
import sources.SourceFixe;

import static org.junit.Assert.*;

public class SourceFixeTest {

    private SourceFixe sourceFixe;

    @Before
    public void setUp() {
        String messageString = "10101"; // Example test message
        sourceFixe = new SourceFixe(messageString);
    }

    @Test
    public void testConstructor() {
        // Ensure the information is generated correctly from the message string
        Information<Boolean> expectedInformation = new Information<>();
        expectedInformation.add(true);  // '1' -> true
        expectedInformation.add(false); // '0' -> false
        expectedInformation.add(true);  // '1' -> true
        expectedInformation.add(false); // '0' -> false
        expectedInformation.add(true);  // '1' -> true

        assertEquals("The generated information should match the expected information",
                expectedInformation, sourceFixe.informationGeneree);
    }

    @Test
    public void testInformationEmise() {
        // The informationEmise should be the same as informationGeneree
        assertEquals("The emitted information should be the same as the generated information",
                sourceFixe.informationGeneree, sourceFixe.getInformationEmise());
    }

    @Test
    public void testMessageStringWithOnlyOnes() {
        // Test case where the messageString is all '1's
        String allOnes = "11111";
        sourceFixe = new SourceFixe(allOnes);

        Information<Boolean> expectedInformation = new Information<>();
        for (int i = 0; i < allOnes.length(); ++i) {
            expectedInformation.add(true); // '1' -> true
        }

        assertEquals("The generated information should be all true for '1's",
                expectedInformation, sourceFixe.informationGeneree);
    }

    @Test
    public void testMessageStringWithOnlyZeros() {
        // Test case where the messageString is all '0's
        String allZeros = "00000";
        sourceFixe = new SourceFixe(allZeros);

        Information<Boolean> expectedInformation = new Information<>();
        for (int i = 0; i < allZeros.length(); ++i) {
            expectedInformation.add(false); // '0' -> false
        }

        assertEquals("The generated information should be all false for '0's",
                expectedInformation, sourceFixe.informationGeneree);
    }

    @Test
    public void testEmptyMessageString() {
        // Test case with an empty message string
        String emptyMessage = "";
        sourceFixe = new SourceFixe(emptyMessage);

        Information<Boolean> expectedInformation = new Information<>();

        assertEquals("The generated information should be empty for an empty message string",
                expectedInformation, sourceFixe.informationGeneree);
    }
}
