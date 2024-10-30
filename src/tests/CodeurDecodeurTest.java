package tests;


import codage.Codeur;
import codage.Decodeur;
import information.Information;
import org.junit.Before;
import org.junit.Test;
import simulateur.Simulateur;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Classe de test pour les classes {@link codage.Codeur} et {@link codage.Decodeur}.
 */
public class CodeurDecodeurTest {

    private Information<Boolean> bitsAvantCodage;
    private Information<Boolean> bitsApresCodageAttendu;

    @Before
    public void setUp() {
        // 0 = 010, 1 = 101
        bitsAvantCodage = new Information<>(new Boolean[]{true, false, true, false, true, false}); // 101010
        bitsApresCodageAttendu = new Information<>(new Boolean[]{true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false, true, false}); // 101 010 101 010 101 010
    }

    /*
    Vérifie que l'encodeur et le décodeur fonctionnent correctement
     */
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
    Vérifie que l'encodeur transforme correctement les bits selon les règles spécifiées :
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
   Vérfie que l'ensemble de la chaine de transmission est fonctionnel
   */
    @Test
    public void testChaine() throws Exception {
        // Créer un codeur
        Codeur codeur = new Codeur();
        Decodeur decodeur = new Decodeur();

        // Vérifier que l'encodeur transforme correctement les bits
        Information<Boolean> bitsApresCodage = codeur.encoder(bitsAvantCodage);
        assertEquals(bitsApresCodageAttendu, bitsApresCodage);

        // Vérifier que le décodeur transforme correctement les séquences de 3 bits
        Information<Boolean> bitsDecodes = decodeur.decoder(bitsApresCodage);
        assertEquals(bitsAvantCodage, bitsDecodes);
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
    test de perfomances en faisant un avant-après (TEB, SNR)
    */
    @Test
    public void testPerfomance() throws Exception {
        // Créer un simulateur avec codeur
        String[] paramsAvecCodeur = {"-mess", "1000", "-seed", "10", "-form", "NRZT", "-nbEch", "9", "-codeur", "-snrpb", "-5"};
        Simulateur simulateurAvecCodeur = new Simulateur(paramsAvecCodeur);

        simulateurAvecCodeur.execute();

        // Calculer le TEB
        double tebAvecCodeur = simulateurAvecCodeur.calculTauxErreurBinaire();

        // Afficher les résultats avec codeur
        System.out.println("TEB avec codeur: " + tebAvecCodeur);

        // Créer un simulateur mais sans le codeur
        String[] paramsSansCodeur = {"-mess", "1000", "-seed", "10", "-form", "NRZT", "-nbEch", "9", "-snrpb", "-5"};
        Simulateur simulateurSansCodeur = new Simulateur(paramsSansCodeur);

        simulateurSansCodeur.execute();

        double tebSansCodeur = simulateurSansCodeur.calculTauxErreurBinaire();

        // Afficher les résultats sans codeur
        System.out.println("TEB sans codeur: " + tebSansCodeur);

        assertTrue(tebAvecCodeur < tebSansCodeur);
    }

    /*
    Vérifie le comportement du simulateur avec des entrées invalides ou des conditions extrêmes (très faible ou très fort SNR)
    */
    @Test
    public void testSNR() throws Exception {
        String[] paramsFaible = {"-mess", "1000000", "-seed", "1234", "-form", "NRZT", "-nbEch", "9", "-codeur", "-snrpb", "-100"};
        Simulateur snrTresFaible = new Simulateur(paramsFaible);

        snrTresFaible.execute();

        double tebSnrFaible = snrTresFaible.calculTauxErreurBinaire();
        System.out.println("TEB avec un snr très faible (-100) : " + tebSnrFaible);

        String[] paramsFort = {"-mess", "1000000", "-seed", "1234", "-form", "NRZT", "-nbEch", "9", "-codeur", "-snrpb", "100"};
        Simulateur snrTresFort = new Simulateur(paramsFort);

        snrTresFort.execute();

        double tebSnrFort = snrTresFort.calculTauxErreurBinaire();
        System.out.println("TEB avec un snr très fort (100) : " + tebSnrFort);
    }

}