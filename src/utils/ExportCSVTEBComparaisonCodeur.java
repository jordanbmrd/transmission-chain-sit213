package utils;

import simulateur.Simulateur;
import utils.tp6.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * La classe {@code ExportCSVTEBComparaisonCodeur} effectue des simulations pour comparer
 * la courbe pratique du NRZ avec et sans l'option -codeur en fonction de Eb/N0.
 *
 * Les résultats sont exportés dans un fichier CSV pour chaque valeur de Eb/N0.
 */
public class ExportCSVTEBComparaisonCodeur {

    /**
     * Nom du fichier CSV dans lequel seront enregistrés les résultats des simulations.
     */
    protected String fichierCSV;

    /**
     * Liste des paramètres utilisés pour initialiser le simulateur.
     */
    protected LinkedList<String> parametres = new LinkedList<>();

    /**
     * Constructeur par défaut de la classe {@code ExportCSVComparaisonCodeur}.
     * Initialise les paramètres de simulation de base (seed et nombre de messages)
     * et définit le nom du fichier CSV de sortie.
     */
    public ExportCSVTEBComparaisonCodeur() {
        // Initialisation des paramètres de base
        String[] arguments = new String[] { "-seed", "100", "-mess", "100000" };
        parametres.addAll(Arrays.asList(arguments));

        // Nom du fichier CSV où seront stockés les résultats
        this.fichierCSV = "comparaison_courbe_codeur.csv";
    }

    /**
     * Exécute les simulations en fonction de Eb/N0 (théorique),
     * enregistre les résultats pour chaque valeur avec et sans l'option -codeur dans un fichier CSV.
     */
    private void lancerSimulations() {
        System.out.println("Lancement des simulations pour " + this.fichierCSV + "...");

        // Calcul du nombre total d'itérations (Eb/N0 de -10 à 15, soit 25 valeurs)
        int totalIterations = 25;
        int currentIteration = 0;

        try (FileWriter csvWriter = new FileWriter(fichierCSV)) {
            // En-tête du fichier CSV
            csvWriter.append("Eb/N0 (dB),TEB Pratique NRZ (sans codeur),TEB Pratique NRZ (avec codeur)\n");

            // Simulation pour chaque valeur de Eb/N0
            for (int ebN0 = -10; ebN0 <= 14; ebN0++) {
                csvWriter.append(String.valueOf(ebN0));

                // Calcul du TEB pratique pour NRZ sans codeur
                float tebSansCodeur = executerSimulationPratique(ebN0, false);
                csvWriter.append(",");
                csvWriter.append(String.valueOf(tebSansCodeur));

                // Calcul du TEB pratique pour NRZ avec codeur
                float tebAvecCodeur = executerSimulationPratique(ebN0, true);
                csvWriter.append(",");
                csvWriter.append(String.valueOf(tebAvecCodeur));

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
     * Exécute une simulation pour obtenir le TEB pratique en fonction du SNR,
     * avec ou sans l'option -codeur.
     *
     * @param snr Le rapport signal/bruit spécifié en dB.
     * @param avecCodeur Indique si l'option -codeur doit être incluse.
     * @return Le TEB pratique calculé.
     */
    private float executerSimulationPratique(float snr, boolean avecCodeur) {
        float tebPratique = 0;
        LinkedList<String> argumentsLocaux = new LinkedList<>(this.parametres);
        try {
            // Ajout des paramètres pour NRZ et SNR
            argumentsLocaux.add("-snrpb");
            argumentsLocaux.add(String.valueOf(snr));
            argumentsLocaux.add("-form");
            argumentsLocaux.add(String.valueOf(Form.NRZ));

            // Ajout de l'option -codeur si nécessaire
            if (avecCodeur) {
                argumentsLocaux.add("-codeur");
            }

            // Création et exécution du simulateur
            Simulateur simulateur = new Simulateur(argumentsLocaux.toArray(new String[0]));
            simulateur.execute();
            tebPratique = simulateur.calculTauxErreurBinaire();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tebPratique;
    }

    public static void main(String[] args) {
        ExportCSVTEBComparaisonCodeur simu = new ExportCSVTEBComparaisonCodeur();
        simu.lancerSimulations();
    }
}
