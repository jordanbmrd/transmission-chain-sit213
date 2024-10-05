package tests;


import codage.Codeur;
import codage.Decodeur;
import information.Information;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CodeurDecodeurTest {

    private Information<Boolean> bitsAvantCodage;
    private Information<Boolean> bitsApresCodageAttendu;

    @Before
    public void setUp() {
        // 0 = 010, 1 = 101
        bitsAvantCodage = new Information<>(new Boolean[]{true, false, true, false, true, false}); // 101010
        bitsApresCodageAttendu = new Information<>(new Boolean[]{true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false}); // 101 010 101 010 101 010
    }

    @Test
    public void testCodeur() throws Exception {
        // Créer un codeur
        Codeur codeur = new Codeur();
        Decodeur decodeur = new Decodeur();

        // Vérifier que l'encodeur transforme correctement les bits
        Information<Boolean> bitsApresCodage = codeur.encoder(bitsAvantCodage);
        assertEquals(bitsApresCodageAttendu, bitsApresCodage);

        // Vérifier que le décodeur transforme correctement les séquences de 3 bits
        Information<Boolean> bitsDecodes = decodeur.decoder(bitsApresCodageAttendu);
        assertEquals(bitsAvantCodage, bitsDecodes);
    }

    /*
    Vérifiez que l'encodeur transforme correctement les bits selon les règles spécifiées :
        0 -> 010
        1 -> 101
    */
    @Test
    public void testTransformation() throws Exception {
        // Créer un codeur
        Codeur codeur = new Codeur();

        // Vérifier que l'encodeur transforme correctement les bits
        Information<Boolean> bitsApresCodage = codeur.encoder(bitsAvantCodage);
        assertEquals(bitsApresCodageAttendu, bitsApresCodage);
    }

    /*
    Vérifiez que le décodeur détecte et corrige une erreur dans un paquet de 3 bits
    */
    @Test
    public void testDetectionCorrection() throws Exception {
        // Créer un décodeur
        Decodeur decodeur = new Decodeur();

        // Introduire une erreur dans les bits encodés
        //10101 et une erreur 110 qui doit correspondre à un 0
        Information<Boolean> bitsAvecErreur = new Information<>(new Boolean[]{true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, true, true, false}); // 101 010 101 010 101 110 (erreur dans le dernier paquet)

        // Vérifier que le décodeur corrige l'erreur et transforme correctement les séquences de 3 bits
        Information<Boolean> bitsDecodes = decodeur.decoder(bitsAvecErreur);
        assertEquals(bitsAvantCodage, bitsDecodes);
    }

    /*
    Vérfier que l'ensemble de la chaine fonctionne
    */


    /*
    test de perfomances en faisant un avant-après (TEB,SNR, profiler)
    */

    /*
    Vérifiez le comportement du simulateur avec des entrées invalides ou des conditions extrêmes (très faible ou très fort SNR)
    */

}