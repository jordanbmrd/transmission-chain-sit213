package tests;

import destinations.DestinationFinale;
import information.Information;
import information.InformationNonConformeException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Classe de test pour la classe {@link destinations.DestinationFinale}.
 * Vérifie que la destination finale reçoit et stocke correctement les informations binaires.
 */
public class DestinationFinaleTest {

    private DestinationFinale destinationFinale;

    /**
     * Initialisation de l'objet {@link DestinationFinale} avant chaque test.
     */
    @Before
    public void setUp() {
        destinationFinale = new DestinationFinale();
    }

    /**
     * Teste la réception d'une information valide par la destination finale.
     *
     * @throws InformationNonConformeException si l'information est non conforme.
     */
    @Test
    public void testRecevoirValidInformation() throws InformationNonConformeException {
        // Prépare une information valide et vérifie sa réception
        Information<Boolean> information = new Information<>();
        information.add(true);
        information.add(false);
        information.add(true);

        destinationFinale.recevoir(information);

        assertEquals("L'information reçue doit correspondre à l'information d'entrée", information, destinationFinale.getInformationRecue());
    }

    /**
     * Vérifie que la réception d'une information nulle lève une exception.
     *
     * @throws InformationNonConformeException attendue si l'information est nulle.
     */
    @Test(expected = InformationNonConformeException.class)
    public void testRecevoirWithNullInformationThrowsException() throws InformationNonConformeException {
        // Appel avec une information nulle, exception attendue
        destinationFinale.recevoir(null);
    }

    /**
     * Teste la réception d'une information vide.
     *
     * @throws InformationNonConformeException si l'information reçue est non conforme.
     */
    @Test
    public void testRecevoirEmptyInformation() throws InformationNonConformeException {
        // Prépare une information vide et vérifie sa réception
        Information<Boolean> emptyInformation = new Information<>();

        destinationFinale.recevoir(emptyInformation);

        assertEquals("L'information reçue doit être vide", 0, destinationFinale.getInformationRecue().nbElements());
    }

    /**
     * Teste la réception d'une information avec un seul élément.
     *
     * @throws InformationNonConformeException si l'information reçue est non conforme.
     */
    @Test
    public void testRecevoirSingleElementInformation() throws InformationNonConformeException {
        // Prépare une information avec un seul élément
        Information<Boolean> singleElementInformation = new Information<>();
        singleElementInformation.add(true);

        destinationFinale.recevoir(singleElementInformation);

        assertEquals("L'information reçue doit contenir un seul élément", singleElementInformation, destinationFinale.getInformationRecue());
    }
}
