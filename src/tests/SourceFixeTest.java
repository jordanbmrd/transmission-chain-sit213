package tests;

import information.Information;
import org.junit.Before;
import org.junit.Test;
import sources.SourceFixe;

import static org.junit.Assert.assertEquals;

/**
 * Classe de test pour la classe {@link sources.SourceFixe}.
 * Vérifie que les informations binaires sont générées correctement à partir d'une chaîne de caractères.
 */
public class SourceFixeTest {

    private SourceFixe sourceFixe;

    /**
     * Initialisation de la source fixe avant chaque test, avec une chaîne de caractères binaire.
     */
    @Before
    public void setUp() {
        String messageString = "10101";
        sourceFixe = new SourceFixe(messageString);
    }

    /**
     * Teste le constructeur et vérifie que l'information générée correspond à la chaîne de caractères fournie.
     */
    @Test
    public void testConstructor() {
        Information<Boolean> expectedInformation = new Information<>();
        expectedInformation.add(true);
        expectedInformation.add(false);
        expectedInformation.add(true);
        expectedInformation.add(false);
        expectedInformation.add(true);

        assertEquals("L'information générée doit correspondre à la chaîne de caractères fournie", expectedInformation, sourceFixe.informationGeneree);
    }

    /**
     * Teste la méthode {@code getInformationEmise} et vérifie que l'information émise est correcte.
     */
    @Test
    public void testInformationEmise() {
        assertEquals("L'information émise doit être identique à l'information générée", sourceFixe.informationGeneree, sourceFixe.getInformationEmise());
    }

    /**
     * Teste la génération d'une information uniquement composée de bits égaux à '1'.
     */
    @Test
    public void testMessageStringWithOnlyOnes() {
        String ones = "11111";
        sourceFixe = new SourceFixe(ones);

        Information<Boolean> expectedInformation = new Information<>();
        for (int i = 0; i < ones.length(); i++) {
            expectedInformation.add(true);
        }

        assertEquals("L'information générée doit être composée uniquement de valeurs true", expectedInformation, sourceFixe.informationGeneree);
    }

    /**
     * Teste la génération d'une information uniquement composée de bits égaux à '0'.
     */
    @Test
    public void testMessageStringWithOnlyZeros() {
        String zeros = "00000";
        sourceFixe = new SourceFixe(zeros);

        Information<Boolean> expectedInformation = new Information<>();
        for (int i = 0; i < zeros.length(); i++) {
            expectedInformation.add(false);
        }

        assertEquals("L'information générée doit être composée uniquement de valeurs false", expectedInformation, sourceFixe.informationGeneree);
    }

    /**
     * Teste la génération d'une information avec une alternance de bits '1' et '0'.
     */
    @Test
    public void testMessageWithAlternatingBits() {
        String alternatingBits = "101010";
        sourceFixe = new SourceFixe(alternatingBits);

        Information<Boolean> expectedInformation = new Information<>();
        for (char bit : alternatingBits.toCharArray()) {
            expectedInformation.add(bit == '1');
        }

        assertEquals("L'information générée doit correspondre à l'alternance des bits", expectedInformation, sourceFixe.informationGeneree);
    }

    /**
     * Teste la génération d'une information à partir d'une chaîne de caractères vide.
     */
    @Test
    public void testEmptyMessageString() {
        String emptyMessage = "";
        sourceFixe = new SourceFixe(emptyMessage);

        Information<Boolean> expectedInformation = new Information<>();

        assertEquals("L'information générée doit être vide pour une chaîne de caractères vide", expectedInformation, sourceFixe.informationGeneree);
    }
}
