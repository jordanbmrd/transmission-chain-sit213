package tests;

import information.Information;
import org.junit.Before;
import org.junit.Test;
import sources.SourceAleatoire;

import java.util.Random;

import static org.junit.Assert.*;

public class SourceAleatoireTest {

    private SourceAleatoire sourceAleatoire;

    @Before
    public void setUp() {
        // Initialize with a sequence of 5 random bits for testing
        sourceAleatoire = new SourceAleatoire(5);
    }

    @Test
    public void testConstructorWithRandomBits() {
        // Test constructor without seed, sequence size 5
        int sequenceLength = 5;
        sourceAleatoire = new SourceAleatoire(sequenceLength);

        // Check that the size of the generated information is correct
        assertEquals("The generated sequence should have the correct length", sequenceLength, sourceAleatoire.informationGeneree.nbElements());

        // Since it's randomly generated, we cannot test the exact values, but we can
        // check if the information contains only boolean values (true/false).
        for (int i = 0; i < sequenceLength; i++) {
            assertNotNull("Each bit in the sequence should be a Boolean value", sourceAleatoire.informationGeneree.iemeElement(i));
        }
    }

    @Test
    public void testConstructorWithSeed() {
        // Test constructor with a fixed seed for reproducibility
        int sequenceLength = 5;
        int seed = 42;
        sourceAleatoire = new SourceAleatoire(sequenceLength, seed);

        Random random = new Random(seed);
        Information<Boolean> expectedInformation = new Information<>();
        for (int i = 0; i < sequenceLength; i++) {
            expectedInformation.add(random.nextBoolean());
        }

        assertEquals("The generated sequence with seed should match the expected sequence", expectedInformation, sourceAleatoire.informationGeneree);
    }

    @Test
    public void testInformationEmise() {
        // Test that informationEmise is the same as informationGeneree
        int sequenceLength = 5;
        sourceAleatoire = new SourceAleatoire(sequenceLength);

        assertEquals("The emitted information should match the generated information", sourceAleatoire.informationGeneree, sourceAleatoire.getInformationEmise());
    }

    @Test
    public void testZeroLengthSequence() {
        // Test with a zero-length sequence
        sourceAleatoire = new SourceAleatoire(0);

        assertEquals("The generated information should be empty", 0, sourceAleatoire.informationGeneree.nbElements());
    }

    @Test
    public void testSequenceWithSeedReproducibility() {
        // Test reproducibility of the generated sequence with the same seed
        int sequenceLength = 10;
        int seed = 12345;
        SourceAleatoire source1 = new SourceAleatoire(sequenceLength, seed);
        SourceAleatoire source2 = new SourceAleatoire(sequenceLength, seed);

        assertEquals("The two generated sequences with the same seed should be identical", source1.informationGeneree, source2.informationGeneree);
    }

    @Test
    public void testDifferentSeedsProduceDifferentSequences() {
        // Test that different seeds produce different sequences
        int sequenceLength = 10;
        SourceAleatoire source1 = new SourceAleatoire(sequenceLength, 12345);
        SourceAleatoire source2 = new SourceAleatoire(sequenceLength, 67890);

        assertNotEquals("Sequences generated with different seeds should not be identical", source1.informationGeneree, source2.informationGeneree);
    }
}
