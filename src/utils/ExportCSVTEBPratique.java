package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import simulateur.Simulateur;
import utils.tp6.Utils;

/**
 * La classe {@code ExportCSVTEBPratique} effectue des simulations de transmission de données
 * en faisant varier le rapport signal/bruit (SNR). Elle génère un fichier CSV contenant
 * le taux d'erreur binaire (TEB) pour différents formats de modulation (RZ, NRZ, NRZT).
 *
 * Le SNR varie de -10 dB à 15 dB, et pour chaque valeur,
 * le TEB est calculé et stocké dans un fichier CSV.
 */
public class ExportCSVTEBPratique {

    /**
     * Instance de la classe {@link Simulateur} permettant de réaliser les simulations.
     */
    protected Simulateur simulateur;

    /**
     * Format de modulation utilisé pour la simulation (RZ, NRZ, NRZT).
     */
    protected String format;

    /**
     * Nom du fichier CSV dans lequel seront enregistrés les résultats des simulations.
     */
    protected String fichierCSV;

    /**
     * Liste des paramètres utilisés pour initialiser le simulateur.
     */
    protected LinkedList<String> parametres = new LinkedList<>();

    /**
     * Constructeur par défaut de la classe {@code ExportCSVTEBPratique}.
     * Initialise les paramètres de simulation de base (seed et nombre de messages)
     * et définit le nom du fichier CSV de sortie.
     */
    public ExportCSVTEBPratique() {
        // Initialisation des paramètres de base
        String[] arguments = new String[] { "-seed", "100", "-mess", "100000" };
        parametres.addAll(Arrays.asList(arguments));

        // Nom du fichier CSV où seront stockés les résultats
        this.fichierCSV = "valeurs_teb_pratique.csv";
    }

    /**
     * Exécute les simulations en fonction du SNR, enregistre les résultats
     * pour chaque valeur dans un fichier CSV.
     *
     * <p>Pour chaque valeur de SNR, le TEB est calculé pour trois formats de modulation (RZ, NRZ, NRZT).</p>
     *
     * @see #executerSimulation(String, float)
     */
    private void lancerSimulations() {
        System.out.println("Lancement des simulations pour " + this.fichierCSV + "...");

        // Tableau des formats de modulation
        Form[] formats = new Form[] { Form.RZ, Form.NRZ, Form.NRZT };

        // Calcul du nombre total d'itérations (valeurs SNR * formats)
        int totalIterations = (15 - (-10) + 1) * formats.length; // 26 valeurs SNR et 3 formats
        int currentIteration = 0;

        try (FileWriter csvWriter = new FileWriter(fichierCSV)) {
            // En-tête du fichier CSV
            csvWriter.append("SnrPb");
            csvWriter.append(",");
            csvWriter.append("TEB RZ");
            csvWriter.append(",");
            csvWriter.append("TEB NRZ");
            csvWriter.append(",");
            csvWriter.append("TEB NRZT");
            csvWriter.append("\n");

            // Simulation pour chaque valeur de SNR
            for (int snr = -10; snr <= 15; snr++) {
                csvWriter.append(String.valueOf(snr));
                for (Form format : formats) {
                    // Exécution de la simulation et récupération du TEB
                    float ber = executerSimulation(format, snr);
                    csvWriter.append(",");
                    csvWriter.append(String.valueOf(ber));

                    // Mise à jour et affichage de la barre de progression
                    currentIteration++;
                    int progressPercentage = (currentIteration * 100) / totalIterations;
                    Utils.printProgressBar(progressPercentage);
                }
                csvWriter.append("\n");
            }

            System.out.println("\nSimulations terminées. Résultats enregistrés dans " + this.fichierCSV);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exécute une simulation pour un format de modulation donné et une valeur de SNR donnée.
     *
     * <p>La méthode initialise le simulateur avec les paramètres spécifiques (SNR, format),
     * lance la simulation, et retourne le taux d'erreur binaire (TEB) obtenu.</p>
     *
     * @param format Le format de modulation utilisé pour la simulation (RZ, NRZ, NRZT).
     * @param snr    Le rapport signal/bruit spécifié en dB.
     * @return Le taux d'erreur binaire (TEB) calculé pour la simulation.
     */
    private float executerSimulation(Form format, float snr) {
        float ber = 0;
        LinkedList<String> argumentsLocaux = new LinkedList<>(this.parametres);
        try {
            // Ajout des paramètres spécifiques de la simulation (SNR, format)
            argumentsLocaux.add("-snrpb");
            argumentsLocaux.add(String.valueOf(snr));
            argumentsLocaux.add("-form");
            argumentsLocaux.add(String.valueOf(format));

            // Création et exécution du simulateur
            Simulateur simulateur = new Simulateur(argumentsLocaux.toArray(new String[0]));
            simulateur.execute();
            ber = simulateur.calculTauxErreurBinaire(); // Récupération du TEB
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ber;
    }

    public static void main(String[] args) {
        ExportCSVTEBPratique simu = new ExportCSVTEBPratique();
        simu.lancerSimulations();
    }
}
