package utils;

import simulateur.Simulateur;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

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
        double targetTEB = 1e-3;  // TEB voulu
        Map<String, List<String>> validCommandsMap = new HashMap<>();  // Commandes donnant un bon TEB

        // Initialisation des valeurs SNR (de 50 à -50)
        double[] snrValues = new double[101];
        for (int i = 0; i <= 100; i++) {
            snrValues[i] = 50 - i;
        }

        // Initialisation des valeurs nbEch (de 3 à 50)
        int[] nbEchValues = new int[48];
        for (int i = 0; i < 48; i++) {
            nbEchValues[i] = 3 + i;
        }

        String[] formValues = {"NRZ", "NRZT", "RZ"};  // Formes d'onde

        for (String form : formValues) {
            for (double snr : snrValues) {
                for (int nbEch : nbEchValues) {

                    // Construction des paramètres de la simulation
                    String[] params = {"-mess", "2000", "-snrpb", String.valueOf(snr), "-nbEch", String.valueOf(nbEch), "-form", form};

                    // Simulation et calcul du TEB
                    Simulateur simulateur = new Simulateur(params);
                    simulateur.execute();
                    double teb = simulateur.calculTauxErreurBinaire();

                    // Vérification si le TEB est inférieur au seuil cible
                    if (teb < targetTEB) {
                        String commandStr = String.join(" ", params) + " => TEB: " + new BigDecimal(teb);

                        // Ajouter la commande à la liste correspondante dans la map
                        validCommandsMap.computeIfAbsent(form, k -> new ArrayList<>()).add(commandStr);
                    }
                }
            }
        }

        // Enregistrement des commandes valides dans un fichier texte
        // Le fichier sera écrasé s'il existe déjà
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("valid_commands.txt"))) {
            // Trier les clés (forms)
            List<String> sortedForms = new ArrayList<>(validCommandsMap.keySet());
            Collections.sort(sortedForms);

            for (String form : sortedForms) {
                writer.write("Form: " + form);
                writer.newLine();
                for (String command : validCommandsMap.get(form)) {
                    writer.write("Valid command: " + command);
                    writer.newLine();
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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