package tests;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConformeException;
import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import transmetteurs.TransmetteurGaussien;

import static org.junit.jupiter.api.Assertions.*;

class TransmetteurGaussienTest {

    private TransmetteurGaussien transmetteurGaussien;
    private DestinationInterface<Float> mockDestination;
    private final Float[] floats = new Float[]{1.0f, 2.0f, 3.0f};

    @BeforeEach
    void setUp() {
        transmetteurGaussien = new TransmetteurGaussien(10, 0.0f, 22);
        mockDestination = EasyMock.createMock(DestinationInterface.class);
        transmetteurGaussien.connecter(mockDestination);
    }

    @Test
    void ajoutBruit() throws InformationNonConformeException {
        Information<Float> information = new Information<>(floats);

        // Prépare le mock pour recevoir l'information bruitée
        mockDestination.recevoir(EasyMock.anyObject(Information.class));
        EasyMock.replay(mockDestination);

        // Reçoit l'information et émet l'information bruitée
        transmetteurGaussien.recevoir(information);

        // Vérifie que le mock a bien reçu l'information bruitée
        EasyMock.verify(mockDestination);

        // Récupère l'information bruitée émise
        Information<Float> informationBruitee = transmetteurGaussien.getInformationEmise();
        assertNotNull(informationBruitee);
        assertEquals(information.nbElements(), informationBruitee.nbElements());

        // Vérifie que chaque élément de l'information bruitée est différent de l'original
        for (int i = 0; i < information.nbElements(); i++) {
            assertNotEquals(information.iemeElement(i), informationBruitee.iemeElement(i));
        }
    }

    @Test
    void recevoirInformationVide() throws InformationNonConformeException {
        Information<Float> information = new Information<>();

        transmetteurGaussien.recevoir(information);
        Information<Float> informationBruitee = transmetteurGaussien.getInformationEmise();

        assertNotNull(informationBruitee);
        assertEquals(0, informationBruitee.nbElements());
    }

    @Test
    void emettreAvecValeursNegatives() throws InformationNonConformeException {
        Information<Float> information = new Information<>(new Float[]{-1.0f, -2.0f, -3.0f});

        transmetteurGaussien.recevoir(information);

        Information<Float> informationBruitee = transmetteurGaussien.getInformationEmise();
        assertNotNull(informationBruitee);
        assertEquals(information.nbElements(), informationBruitee.nbElements());

        for (int i = 0; i < information.nbElements(); i++) {
            assertNotEquals(information.iemeElement(i), informationBruitee.iemeElement(i));
        }
    }
}