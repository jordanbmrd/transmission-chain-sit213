package tests;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import transmetteurs.TransmetteurParfait;

import static org.junit.Assert.assertEquals;

/**
 * Classe de test pour la classe {@link transmetteurs.TransmetteurParfait}.
 * Utilise EasyMock pour simuler les destinations connectées et teste la fonctionnalité
 * de réception et d'émission d'informations binaires.
 */
public class TransmetteurParfaitTest {

    private TransmetteurParfait<Boolean> transmetteurParfait;
    private DestinationInterface<Boolean> mockDestination;

    /**
     * Initialisation des objets avant chaque test
     * Crée une instance de {@link TransmetteurParfait} et une destination simulée
     */
    @Before
    public void setUp() {
        transmetteurParfait = new TransmetteurParfait<>();
        mockDestination = EasyMock.createMock(DestinationInterface.class);
        transmetteurParfait.connecter(mockDestination);  // Connexion de la destination simulée
    }

    /**
     * Teste la méthode {@code recevoir} et vérifie que l'information est correctement transmise
     * à la destination connectée.
     *
     * @throws InformationNonConformeException si l'information est non conforme
     */
    @Test
    public void testRecevoirAndEmettreWithValidInformation() throws InformationNonConformeException {
        Information<Boolean> information = new Information<>();
        information.add(true);
        information.add(false);
        information.add(true);

        // Simule la réception de l'information par la destination
        mockDestination.recevoir(information);
        EasyMock.replay(mockDestination);

        // Le transmetteur reçoit l'information
        transmetteurParfait.recevoir(information);

        // Vérifie que la destination a bien reçu l'information et que l'information émise correspond à celle reçue
        EasyMock.verify(mockDestination);
        assertEquals("L'information émise doit être identique à l'information reçue", information, transmetteurParfait.getInformationEmise());
    }

    /**
     * Teste la méthode {@code emettre} avec plusieurs destinations connectées
     *
     * @throws InformationNonConformeException si l'information est non conforme
     */
    @Test
    public void testEmettreWithMultipleDestinations() throws InformationNonConformeException {
        DestinationInterface<Boolean> mockDestination1 = EasyMock.createMock(DestinationInterface.class);
        DestinationInterface<Boolean> mockDestination2 = EasyMock.createMock(DestinationInterface.class);

        // Connecte deux destinations au transmetteur
        transmetteurParfait.connecter(mockDestination1);
        transmetteurParfait.connecter(mockDestination2);

        Information<Boolean> information = new Information<>();
        information.add(true);
        information.add(false);

        // Simule la réception de l'information par les deux destinations
        mockDestination1.recevoir(information);
        mockDestination2.recevoir(information);
        EasyMock.replay(mockDestination1, mockDestination2);

        // Le transmetteur reçoit et transmet l'information aux deux destinations
        transmetteurParfait.recevoir(information);

        // Vérifie que les deux destinations ont bien reçu l'information et que celle-ci est correcte
        EasyMock.verify(mockDestination1, mockDestination2);
        assertEquals("L'information émise doit correspondre à l'information reçue", information, transmetteurParfait.getInformationEmise());
    }

    /**
     * Vérifie que la méthode {@code recevoir} lève une exception si l'information est nulle
     *
     * @throws InformationNonConformeException attendue si l'information est nulle
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirWithNullInformationThrowsException() throws InformationNonConformeException {
        transmetteurParfait.recevoir(null);
    }

    /**
     * Teste la réception d'une information vide et vérifie que l'information est correctement émise
     *
     * @throws InformationNonConformeException si l'information est non conforme
     */
    @Test
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        Information<Boolean> emptyInformation = new Information<>();

        // Simule la réception d'une information vide par la destination
        mockDestination.recevoir(emptyInformation);
        EasyMock.replay(mockDestination);

        // Le transmetteur reçoit une information vide
        transmetteurParfait.recevoir(emptyInformation);

        // Vérifie que la destination a bien reçu une information vide et que celle-ci a été émise correctement
        EasyMock.verify(mockDestination);
        assertEquals("L'information émise doit être vide", emptyInformation, transmetteurParfait.getInformationEmise());
    }
}
