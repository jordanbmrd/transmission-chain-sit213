package utils.tp6;

import simulateur.Simulateur;
import utils.Form;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * La classe ExportCSVEnvironnement2 fournit des méthodes pour calculer et exporter
 * des données liées aux communications numériques dans un environnement spécifique.
 */
public class ExportCSVEnvironnement2 {
    /**
     * Nom du fichier CSV dans lequel seront enregistrés les résultats des simulations.
     */
    protected String fichierCSV = "simulations_env2_ti.csv";

    /**
     * Calcule le TEB et Eb pour différentes simulations et enregistre les résultats dans un fichier CSV.
     * Affiche en console la ligne avec la plus petite valeur de Eb.
     *
     * @throws Exception Si une erreur survient pendant l'exécution.
     */
    public void lancerSimulations() throws Exception {
        System.out.println("Lancement des simulations pour " + this.fichierCSV + "...");

        double targetTEB = 1e-2;

        DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#.############", decimalSymbols);  // Jusqu'à 12 chiffres après la virgule

        // Initialisation des valeurs SNR
        double[] snrValues = new double[41];
        for (int i = 0; i <= 40; i++) {
            snrValues[i] = -25 + i;
        }

        Form[] formValues = {Form.NRZ, Form.NRZT};

        double n0DbmPerHz = -80;    // Valeur du sujet
        BigDecimal n0 = Utils.convertDbmPerHzToWPerHz(n0DbmPerHz);

        BigDecimal minEb = BigDecimal.valueOf(Double.MAX_VALUE);
        StringBuilder minEbLine = new StringBuilder();

        try (FileWriter csvWriter = new FileWriter(this.fichierCSV)) {
            // Entête du fichier CSV
            csvWriter.append("Forme,nbEch,SNRpb (dB),TEB,Eb (J)\n");

            // Calcul du nombre total d'itérations pour la barre de progression
            int totalIterations = formValues.length * snrValues.length;
            int currentIteration = 0;

            for (Form form : formValues) {
                for (double snr : snrValues) {
                    // Paramètres
                    String[] params = {"-mess", "10000", "-snrpb", String.valueOf(snr), "-nbEch", "30", "-ti", "79", "0.5", "-form", String.valueOf(form), "-codeur"};

                    // Temps avant l'exécution
                    long startTime = System.currentTimeMillis();

                    // Calcul du TEB
                    Simulateur simulateur = new Simulateur(params);
                    simulateur.execute();
                    double teb = simulateur.calculTauxErreurBinaire();

                    // Temps après l'exécution
                    long endTime = System.currentTimeMillis();

                    // Calcul du Eb
                    BigDecimal eb = Utils.getEbFromN0AndSnrpb((float) snr, n0);

                    if (teb < targetTEB) {
                        // Calcul du temps d'exécution
                        long executionTime = endTime - startTime;

                        // Enregistrement des résultats
                        String line = form + "," + "30" + "," + snr + "," + df.format(teb) + "," + eb.toPlainString();
                        csvWriter.append(line).append("\n");

                        if (eb.compareTo(minEb) < 0) {
                            minEb = eb;
                            minEbLine.setLength(0);  // Réinitialisation du StringBuilder
                            minEbLine.append("- Forme: ").append(form).append("\n")
                                    .append("- Nombre d'échantillons: ").append("30").append("\n")
                                    .append("- SNRpb (dB): ").append(snr).append("\n")
                                    .append("- TEB: ").append(df.format(teb)).append("\n")
                                    .append("- Eb (J): ").append(eb.toPlainString()).append("\n")
                                    .append("- Temps d'exécution: ").append(executionTime).append(" ms\n");
                        }
                    }

                    // Mise à jour et affichage de la barre de progression
                    currentIteration++;
                    int progressPercentage = (currentIteration * 100) / totalIterations;
                    Utils.printProgressBar(progressPercentage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!minEbLine.isEmpty()) {
            System.out.println("\nLigne avec le plus petit Eb :\n" + minEbLine.toString());
        }
    }

    public static void main(String[] args) {
        ExportCSVEnvironnement2 exportCSVEnvironnement2 = new ExportCSVEnvironnement2();
        try {
            exportCSVEnvironnement2.lancerSimulations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
