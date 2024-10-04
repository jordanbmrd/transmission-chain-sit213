package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import simulateur.Simulateur;

/**
 * La classe {@code ExportCSVProbaErreurParEbN0} effectue des simulations de transmission de données
 * en faisant varier le rapport énergie par bit sur bruit (Eb/N0). Elle génère un fichier CSV contenant
 * la probabilité d'erreur (P_e) pour différents formats de modulation (RZ, NRZ, NRZT).
 *
 * L'Eb/N0 varie de 0 dB à 20 dB, et pour chaque valeur,
 * la P_e est calculée et stockée dans un fichier CSV.
 */
public class ExportCSVProbaErreurParEbN0 {

    /**
     * Nom du fichier CSV dans lequel seront enregistrés les résultats des simulations.
     */
    protected String fichierCSV;

    /**
     * Liste des paramètres utilisés pour initialiser le simulateur.
     */
    protected LinkedList<String> parametres = new LinkedList<>();

    /**
     * Constructeur par défaut de la classe {@code ExportCSVProbaErreurParEbN0}.
     * Initialise les paramètres de simulation de base (seed et nombre de messages)
     * et définit le nom du fichier CSV de sortie.
     */
    public ExportCSVProbaErreurParEbN0() {
        // Initialisation des paramètres de base
        String[] arguments = new String[] { "-seed", "100", "-mess", "100000" };
        parametres.addAll(Arrays.asList(arguments));

        // Nom du fichier CSV où seront stockés les résultats
        this.fichierCSV = "valeurs_proba_erreur_par_ebn0.csv";
    }

    /**
     * Exécute les simulations en fonction de Eb/N0, enregistre les résultats
     * pour chaque valeur dans un fichier CSV.
     *
     * <p>Pour chaque valeur de Eb/N0, la probabilité d'erreur (P_e) est calculée
     * pour trois formats de modulation (RZ, NRZ, NRZT).</p>
     */
    private void lancerSimulations() {
        System.out.println("Lancement des simulations pour " + this.fichierCSV + "...");
        try (FileWriter csvWriter = new FileWriter(fichierCSV)) {
            // En-tête du fichier CSV
            csvWriter.append("Eb/N0 (dB)");
            csvWriter.append(",");
            csvWriter.append("P_e RZ");
            csvWriter.append(",");
            csvWriter.append("P_e NRZ");
            csvWriter.append(",");
            csvWriter.append("P_e NRZT");
            csvWriter.append("\n");

            String[] formats = new String[] { "RZ", "NRZ", "NRZT" };

            // Simulation pour chaque valeur d'Eb/N0
            for (int ebN0 = -10; ebN0 <= 9; ebN0++) {
                csvWriter.append(String.valueOf(ebN0));
                for (String format : formats) {
                    // Exécution de la simulation et récupération de P_e
                    double probaErreur = executerSimulation(format, ebN0);
                    csvWriter.append(",");
                    csvWriter.append(String.valueOf(probaErreur));
                }
                csvWriter.append("\n");
            }

            System.out.println("Simulations terminées. Résultats enregistrés dans " + this.fichierCSV);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exécute une simulation pour un format de modulation donné et une valeur d'Eb/N0 donnée.
     *
     * <p>La méthode initialise le simulateur avec les paramètres spécifiques (format),
     * lance la simulation, et retourne la probabilité d'erreur (P_e) obtenue.</p>
     *
     * @param format Le format de modulation utilisé pour la simulation (RZ, NRZ, NRZT).
     * @return La probabilité d'erreur (P_e) calculée pour la simulation.
     */
    private double executerSimulation(String format, float ebN0) {
        double probaErreur = 0;
        LinkedList<String> argumentsLocaux = new LinkedList<>(this.parametres);
        try {
            // Ajout des paramètres spécifiques de la simulation (format)
            argumentsLocaux.add("-form");
            argumentsLocaux.add(format);
            argumentsLocaux.add("-snrpb");
            argumentsLocaux.add(String.valueOf(ebN0 - 10)); // On ajuste SNR pour le simulateur

            // Création et exécution du simulateur
            Simulateur simulateur = new Simulateur(argumentsLocaux.toArray(new String[0]));
            simulateur.execute();
            probaErreur = simulateur.calculProbaErreur(); // Récupération de P_e
        } catch (Exception e) {
            e.printStackTrace();
        }
        return probaErreur;
    }

    /**
     * Point d'entrée du programme.
     *
     * <p>Crée une instance de la classe {@code ExportCSVProbaErreurParEbN0} et
     * lance les simulations.</p>
     *
     * @param args Arguments passés en ligne de commande (non utilisés dans cette version).
     */
    public static void main(String[] args) {
        ExportCSVProbaErreurParEbN0 simu = new ExportCSVProbaErreurParEbN0();
        simu.lancerSimulations();
    }
}
