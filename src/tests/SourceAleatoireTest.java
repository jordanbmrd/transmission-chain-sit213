package tests;

import information.Information;
import org.junit.Before;
import org.junit.Test;
import sources.SourceAleatoire;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Classe de test pour la classe {@link sources.SourceAleatoire}.
 * Vérifie la génération aléatoire d'une séquence de bits, ainsi que la reproductibilité
 * avec une seed fixe.
 */
public class SourceAleatoireTest {

    private SourceAleatoire sourceAleatoire;

    /**
     * Initialise une séquence aléatoire de 5 bits pour les tests.
     */
    @Before
    public void setUp() {
        sourceAleatoire = new SourceAleatoire(5);
    }

    /**
     * Teste le constructeur sans seed, avec une séquence de longueur définie.
     */
    @Test
    public void testConstructorWithRandomBits() {
        int sequenceLength = 5;
        sourceAleatoire = new SourceAleatoire(sequenceLength);

        assertEquals("La séquence générée doit avoir la longueur spécifiée", sequenceLength, sourceAleatoire.informationGeneree.nbElements());

        for (int i = 0; i < sequenceLength; i++) {
            assertNotNull("Chaque élément de la séquence doit être un Boolean", sourceAleatoire.informationGeneree.iemeElement(i));
        }
    }

    /**
     * Teste le constructeur avec une seed fixe pour garantir la reproductibilité des résultats.
     */
    @Test
    public void testConstructorWithSeed() {
        int sequenceLength = 5;
        int seed = 42;
        sourceAleatoire = new SourceAleatoire(sequenceLength, seed);

        Random random = new Random(seed);
        Information<Boolean> expectedInformation = new Information<>();
        for (int i = 0; i < sequenceLength; i++) {
            expectedInformation.add(random.nextBoolean());
        }

        assertEquals("La séquence générée avec une graine fixe doit correspondre à la séquence attendue", expectedInformation, sourceAleatoire.informationGeneree);
    }

    /**
     * Teste la méthode {@code getInformationEmise} et vérifie que l'information émise est correcte.
     */
    @Test
    public void testInformationEmise() {
        int sequenceLength = 5;
        sourceAleatoire = new SourceAleatoire(sequenceLength);

        assertEquals("L'information émise doit correspondre à l'information générée", sourceAleatoire.informationGeneree, sourceAleatoire.getInformationEmise());
    }

    /**
     * Teste la génération d'une séquence de longueur zéro.
     */
    @Test
    public void testZeroLengthSequence() {
        sourceAleatoire = new SourceAleatoire(0);

        assertEquals("L'information générée doit être vide", 0, sourceAleatoire.informationGeneree.nbElements());
    }

    /**
     * Vérifie la reproductibilité des séquences générées avec la même seed.
     */
    @Test
    public void testSequenceWithSeedReproducibility() {
        int sequenceLength = 10;
        int seed = 12345;
        SourceAleatoire source1 = new SourceAleatoire(sequenceLength, seed);
        SourceAleatoire source2 = new SourceAleatoire(sequenceLength, seed);

        assertEquals("Les séquences générées avec la même graine doivent être identiques", source1.informationGeneree, source2.informationGeneree);
    }

    /**
     * Vérifie que différentes seeds produisent des séquences différentes.
     */
    @Test
    public void testDifferentSeedsProduceDifferentSequences() {
        int sequenceLength = 10;
        SourceAleatoire source1 = new SourceAleatoire(sequenceLength, 12345);
        SourceAleatoire source2 = new SourceAleatoire(sequenceLength, 67890);

        assertNotEquals("Les séquences générées avec des graines différentes ne doivent pas être identiques", source1.informationGeneree, source2.informationGeneree);
    }
}
