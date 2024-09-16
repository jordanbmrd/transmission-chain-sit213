package tests;

import information.Information;
import org.junit.Before;
import org.junit.Test;
import sources.SourceFixe;

import static org.junit.Assert.assertEquals;

public class SourceFixeTest {

    private SourceFixe sourceFixe;

    @Before
    public void setUp() {
        String messageString = "10101"; // Test message
        sourceFixe = new SourceFixe(messageString);
    }

    /**
     * Test the constructor of SourceFixe
     */
    @Test
    public void testConstructor() {
        // Ensure the information is generated correctly from the message string
        Information<Boolean> expectedInformation = new Information<>();
        expectedInformation.add(true);  // '1' -> true
        expectedInformation.add(false); // '0' -> false
        expectedInformation.add(true);  // '1' -> true
        expectedInformation.add(false); // '0' -> false
        expectedInformation.add(true);  // '1' -> true

        assertEquals("The generated information should match the expected information", expectedInformation, sourceFixe.informationGeneree);
    }

    /**
     * Test the informationEmise method of SourceFixe
     */
    @Test
    public void testInformationEmise() {
        // The informationEmise should be the same as informationGeneree
        assertEquals("The emitted information should be the same as the generated information", sourceFixe.informationGeneree, sourceFixe.getInformationEmise());
    }

    /**
     * Test the informationGeneree method of SourceFixe
     */
    @Test
    public void testMessageStringWithOnlyOnes() {
        // Test case where the messageString is all '1's
        String ones = "11111";
        sourceFixe = new SourceFixe(ones);

        Information<Boolean> expectedInformation = new Information<>();

        for (int i = 0; i < ones.length(); ++i) {
            expectedInformation.add(true); // '1' -> true
        }

        assertEquals("The generated information should be all true for '1's", expectedInformation, sourceFixe.informationGeneree);
    }

    /**
     * Test the informationGeneree method of SourceFixe
     */
    @Test
    public void testMessageStringWithOnlyZeros() {
        // Test case where the messageString is all '0's
        String zeros = "00000";
        sourceFixe = new SourceFixe(zeros);

        Information<Boolean> expectedInformation = new Information<>();

        for (int i = 0; i < zeros.length(); ++i) {
            expectedInformation.add(false); // '0' -> false
        }

        assertEquals("The generated information should be all false for '0's", expectedInformation, sourceFixe.informationGeneree);
    }

    /**
     * Test the informationGeneree method of SourceFixe
     */
    @Test
    public void testMessageWithAlternatingBits() {
        // Test case with alternating '1's and '0's
        String alternatingBits = "101010";
        sourceFixe = new SourceFixe(alternatingBits);

        Information<Boolean> expectedInformation = new Information<>();

        for (char bit : alternatingBits.toCharArray()) {
            expectedInformation.add(bit == '1'); // '1' -> true, '0' -> false
        }

        assertEquals("The generated information should match the alternating bits", expectedInformation, sourceFixe.informationGeneree);
    }

    /**
     * Test the informationGeneree method of SourceFixe
     */
    @Test
    public void testEmptyMessageString() {
        // Test case with an empty message string
        String emptyMessage = "";
        sourceFixe = new SourceFixe(emptyMessage);

        Information<Boolean> expectedInformation = new Information<>();

        assertEquals("The generated information should be empty for an empty message string", expectedInformation, sourceFixe.informationGeneree);
    }
}
