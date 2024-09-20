package tests;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
import org.junit.Before;
import org.junit.Test;
import information.Information;
import information.InformationNonConformeException;
import modulation.recepteurs.Recepteur;
import utils.Form;
import destinations.DestinationInterface;

/**
 * Classe de test pour la classe {@link modulation.recepteurs.Recepteur}.
 * Cette classe teste la capacité du récepteur à recevoir et convertir des signaux analogiques en signaux logiques.
 * Utilise EasyMock pour simuler les destinations connectées.
 */
public class RecepteurTest {

    private Recepteur recepteur;
    private DestinationInterface<Boolean> mockDestination;
    private Information<Float> informationAnalogiqueValide;
    private Information<Float> informationAnalogiqueInvalide;
    private Information<Float> informationAnalogiqueVide;

    /**
     * Initialisation des objets avant chaque test.
     * Crée une instance de {@link Recepteur} et une destination simulée.
     */
    @Before
    public void setUp() {
        recepteur = new Recepteur(2, 1.0f, 0.0f, Form.NRZ);
        mockDestination = createMock(DestinationInterface.class);
        recepteur.connecter(mockDestination);

        // Création de plusieurs jeux de données pour les différents tests
        informationAnalogiqueValide = new Information<>(new Float[]{1.0f, 0.0f, 1.0f, 0.0f});
        informationAnalogiqueInvalide = null;
        informationAnalogiqueVide = new Information<>(new Float[]{});
    }

    /**
     * Teste la réception et la conversion d'une information analogique valide.
     *
     * @throws InformationNonConformeException si l'information reçue est non conforme.
     */
    @Test
    public void testRecevoirInformationValide() throws InformationNonConformeException {
        mockDestination.recevoir(anyObject());
        expectLastCall();
        replay(mockDestination);

        recepteur.recevoir(informationAnalogiqueValide);

        // Vérifie que l'information reçue et l'information émise ne sont pas nulles
        assertEquals(informationAnalogiqueValide, recepteur.getInformationRecue());
        assertNotNull(recepteur.getInformationEmise());

        verify(mockDestination);
    }

    /**
     * Vérifie que la réception d'une information nulle lève une exception.
     *
     * @throws InformationNonConformeException attendue si l'information est non conforme.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirInformationInvalide() throws InformationNonConformeException {
        recepteur.recevoir(informationAnalogiqueInvalide);
    }

    /**
     * Vérifie que la réception d'une information vide lève une exception.
     *
     * @throws InformationNonConformeException attendue si l'information est vide.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirInformationVide() throws InformationNonConformeException {
        recepteur.recevoir(informationAnalogiqueVide);
    }

    /**
     * Teste l'émission d'une information valide après réception.
     *
     * @throws InformationNonConformeException si l'information reçue est non conforme.
     */
    @Test
    public void testEmettre() throws InformationNonConformeException {
        mockDestination.recevoir(anyObject());
        expectLastCall();
        replay(mockDestination);

        recepteur.recevoir(informationAnalogiqueValide);

        Information<Boolean> informationEmise = recepteur.getInformationEmise();
        assertNotNull(informationEmise);
        assertEquals(2, informationEmise.nbElements());
        assertEquals(Boolean.TRUE, informationEmise.iemeElement(0));
        assertEquals(Boolean.TRUE, informationEmise.iemeElement(1));

        verify(mockDestination);
    }

    /**
     * Teste la validation des paramètres du récepteur.
     *
     * @throws InformationNonConformeException si les paramètres sont invalides.
     */
    @Test
    public void testValiderParametres() throws InformationNonConformeException {
        assertTrue(recepteur.validerParametres(Form.NRZ));
    }

    /**
     * Vérifie que des paramètres invalides lèvent une exception.
     *
     * @throws InformationNonConformeException attendue si les paramètres sont incorrects.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testValiderParametresInvalide() throws InformationNonConformeException {
        Recepteur recepteurInvalide = new Recepteur(2, 0.0f, 1.0f, Form.NRZ);
        recepteurInvalide.validerParametres(Form.NRZ);
    }

    /**
     * Teste la conversion d'une information analogique en binaire.
     *
     * @throws InformationNonConformeException si l'information reçue est non conforme.
     */
    @Test
    public void testConversionAN() throws InformationNonConformeException {
        Information<Boolean> informationConvertie = recepteur.conversionAN(informationAnalogiqueValide);

        assertNotNull(informationConvertie);
        assertEquals(2, informationConvertie.nbElements());
        assertEquals(Boolean.TRUE, informationConvertie.iemeElement(0));
        assertEquals(Boolean.TRUE, informationConvertie.iemeElement(1));
    }

    /**
     * Vérifie que la conversion avec une information nulle lève une exception.
     *
     * @throws InformationNonConformeException attendue si l'information est non conforme.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testConversionANInformationInvalide() throws InformationNonConformeException {
        recepteur.conversionAN(informationAnalogiqueInvalide);
    }

    /**
     * Vérifie que la conversion avec une information vide lève une exception.
     *
     * @throws InformationNonConformeException attendue si l'information est vide.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testConversionANInformationVide() throws InformationNonConformeException {
        recepteur.conversionAN(informationAnalogiqueVide);
    }
}
