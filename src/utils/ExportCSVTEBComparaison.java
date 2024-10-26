package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import simulateur.Simulateur;
import utils.tp6.Utils;

/**
 * La classe {@code ExportCSVTEBComparaison} effectue des simulations pour comparer
 * le TEB théorique et le TEB pratique de la modulation NRZ en fonction de Eb/N0 (théorique)
 * et SNR (pratique).
 *
 * Les résultats sont exportés dans un fichier CSV pour chaque valeur de Eb/N0 et SNR.
 */
public class ExportCSVTEBComparaison {

    /**
     * Nom du fichier CSV dans lequel seront enregistrés les résultats des simulations.
     */
    protected String fichierCSV;

    /**
     * Liste des paramètres utilisés pour initialiser le simulateur.
     */
    protected LinkedList<String> parametres = new LinkedList<>();

    /**
     * Constructeur par défaut de la classe {@code ExportCSVTEBComparaison}.
     * Initialise les paramètres de simulation de base (seed et nombre de messages)
     * et définit le nom du fichier CSV de sortie.
     */
    public ExportCSVTEBComparaison() {
        // Initialisation des paramètres de base
        String[] arguments = new String[] { "-seed", "100", "-mess", "100000" };
        parametres.addAll(Arrays.asList(arguments));

        // Nom du fichier CSV où seront stockés les résultats
        this.fichierCSV = "comparaison_teb_nrz.csv";
    }

    /**
     * Exécute les simulations en fonction de Eb/N0 (théorique) et SNR (pratique),
     * enregistre les résultats pour chaque valeur dans un fichier CSV.
     */
    private void lancerSimulations() {
        System.out.println("Lancement des simulations pour " + this.fichierCSV + "...");

        // Calcul du nombre total d'itérations (Eb/N0 de -10 à 15, soit 25 valeurs)
        int totalIterations = 25;
        int currentIteration = 0;

        try (FileWriter csvWriter = new FileWriter(fichierCSV)) {
            // En-tête du fichier CSV
            csvWriter.append("Eb/N0 (dB),TEB Théorique NRZ,TEB Pratique NRZ\n");

            // Simulation pour chaque valeur de Eb/N0 et SNR (qui suit Eb/N0)
            for (int ebN0 = -10; ebN0 <= 14; ebN0++) {
                csvWriter.append(String.valueOf(ebN0));

                // Calcul du TEB théorique pour NRZ
                double tebTheorique = executerSimulationTheorique(ebN0);
                csvWriter.append(",");
                csvWriter.append(String.valueOf(tebTheorique));

                // Calcul du TEB pratique pour NRZ (SNR ajusté à Eb/N0)
                float tebPratique = executerSimulationPratique(ebN0);
                csvWriter.append(",");
                csvWriter.append(String.valueOf(tebPratique));

                csvWriter.append("\n");

                // Mise à jour et affichage de la barre de progression
                currentIteration++;
                int progressPercentage = (currentIteration * 100) / totalIterations;
                Utils.printProgressBar(progressPercentage);
            }

            System.out.println("\nSimulations terminées. Résultats enregistrés dans " + this.fichierCSV);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exécute une simulation pour obtenir le TEB théorique en fonction de Eb/N0.
     *
     * @param ebN0 La valeur d'Eb/N0 en dB.
     * @return Le TEB théorique calculé.
     */
    private double executerSimulationTheorique(float ebN0) {
        double tebTheorique = 0;
        LinkedList<String> argumentsLocaux = new LinkedList<>(this.parametres);
        try {
            // Ajout des paramètres pour NRZ et Eb/N0
            argumentsLocaux.add("-form");
            argumentsLocaux.add(String.valueOf(Form.NRZ));
            argumentsLocaux.add("-snrpb");
            argumentsLocaux.add(String.valueOf(ebN0)); // Ajustement SNR pour le simulateur

            // Création et exécution du simulateur
            Simulateur simulateur = new Simulateur(argumentsLocaux.toArray(new String[0]));
            simulateur.execute();
            tebTheorique = simulateur.calculProbaErreur(); // Récupération du TEB théorique
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tebTheorique;
    }

    /**
     * Exécute une simulation pour obtenir le TEB pratique en fonction du SNR.
     *
     * @param snr Le rapport signal/bruit spécifié en dB.
     * @return Le TEB pratique calculé.
     */
    private float executerSimulationPratique(float snr) {
        float tebPratique = 0;
        LinkedList<String> argumentsLocaux = new LinkedList<>(this.parametres);
        try {
            // Ajout des paramètres pour NRZ et SNR
            argumentsLocaux.add("-snrpb");
            argumentsLocaux.add(String.valueOf(snr));
            argumentsLocaux.add("-form");
            argumentsLocaux.add(String.valueOf(Form.NRZ));

            // Création et exécution du simulateur
            Simulateur simulateur = new Simulateur(argumentsLocaux.toArray(new String[0]));
            simulateur.execute();
            tebPratique = simulateur.calculTauxErreurBinaire(); // Récupération du TEB pratique
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tebPratique;
    }

    public static void main(String[] args) {
        ExportCSVTEBComparaison simu = new ExportCSVTEBComparaison();
        simu.lancerSimulations();
    }
}
