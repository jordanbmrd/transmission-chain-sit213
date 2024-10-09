package utils;

import simulateur.Simulateur;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * La classe Utils fournit des méthodes utilitaires pour les conversions et calculs
 * liés aux communications numériques.
 */
public class Utils {

    /**
     * Convertit une densité spectrale de bruit de dBm/Hz en W/Hz.
     *
     * @param n0DbmPerHz la densité spectrale de bruit en dBm/Hz.
     * @return la densité spectrale de bruit en W/Hz.
     * @throws IllegalArgumentException si la valeur d'entrée est invalide.
     */
    public static double convertDbmPerHzToWPerHz(double n0DbmPerHz) {
        return Math.pow(10, (n0DbmPerHz - 30) / 10);
    }

    /**
     * Calcule l'énergie par bit (Eb) à partir du rapport signal sur bruit par bit (SNRpb)
     * et de la densité spectrale de bruit (N0).
     *
     * @param snrpb le rapport signal sur bruit par bit (SNRpb).
     * @param n0    la densité spectrale de bruit en W/Hz.
     * @return l'énergie par bit (Eb).
     * @throws IllegalArgumentException si N0 est égal à zéro.
     */
    public static float getEbFromN0AndSnrpb(float snrpb, float n0) {
        if (n0 == 0) {
            throw new IllegalArgumentException("N0 ne peut pas être nul.");
        }
        return snrpb * n0;
    }


    public void testTebIdeal() throws Exception {
        // Déclaration des paramètres de la simulation
        double targetTEB = 1e-3;  // Seuil cible de TEB
        List<String> validCommands = new ArrayList<>();  // Liste des commandes valides

        // Initialisation des valeurs SNR (exemple : -50, -49, -48, ..., 50)
        double[] snrValues = new double[101];
        for (int i = 0; i <= 100; i++) {
            snrValues[i] = 50 - i;
        }

        // Initialisation des valeurs nbEch (exemple : 3, 4, ..., 50)
        int[] nbEchValues = new int[48];
        for (int i = 0; i < 48; i++) {
            nbEchValues[i] = 3 + i;  // Va de 3 à 50 (3 inclus, 50 exclus)
        }

        // Autres valeurs possibles pour les tests
        double[] amplValues = {0.5, 1.0, 1.5};  // Valeurs d'amplitude
        String[] formValues = {"NRZT", "RZ", "NRZ"};  // Formes d'onde

        int testCount = 0;  // Compteur de tests effectués

        // Boucles imbriquées pour tester toutes les combinaisons de paramètres
        for (double snr : snrValues) {
            for (int nbEch : nbEchValues) {
                for (double ampl : amplValues) {
                    for (String form : formValues) {
                        // Construction des paramètres de la simulation
                        String[] params = {"-mess", "2000", "-snrpb", String.valueOf(snr), "-nbEch", String.valueOf(nbEch), "-form", form};

                        // Simulation et calcul du TEB
                        Simulateur simulateur = new Simulateur(params);
                        simulateur.execute();
                        double teb = simulateur.calculTauxErreurBinaire();

                        // Vérification si le TEB est inférieur au seuil cible
                        if (teb < targetTEB) {
                            validCommands.add(String.join(" ", params) + " => TEB: " + new BigDecimal(teb));
                            if (validCommands.size() >= 10) {  // Arrêter si 10 commandes valides trouvées
                                break;
                            }
                        }

                        testCount++;
                        if (testCount >= 2000) {  // Arrêter si le nombre de tests dépasse 2000
                            // break;
                        }
                    }
                    if (validCommands.size() >= 10 || testCount >= 2000) {
                        //break;
                    }
                }
                if (validCommands.size() >= 10 || testCount >= 2000) {
                    //break;
                }
            }
            if (validCommands.size() >= 10 || testCount >= 2000) {
                //break;
            }
        }

        // Affichage du nombre total de tests effectués
        System.out.println("Nombre total de tests effectués: " + testCount);

        // Enregistrement des commandes valides dans un fichier texte
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("valid_commands.txt"))) {
            for (String command : validCommands) {
                writer.write("Valid command: " + command);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Afficher les commandes valides sur la console
        validCommands.forEach(System.out::println);
    }

    static public void main(String[] args) {
        Utils utils = new Utils();
        try {
            utils.testTebIdeal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}