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
 * La classe ExportCSVEnvironnement1 fournit des méthodes pour calculer et exporter
 * des données liées aux communications numériques dans un environnement spécifique.
 */
public class ExportCSVEnvironnement1 {
    /**
     * Nom du fichier CSV dans lequel seront enregistrés les résultats des simulations.
     */
    protected String fichierCSV = "simulations_env1_codeur.csv";

    /**
     * Calcule le TEB et Eb pour différentes simulations et enregistre les résultats dans un fichier CSV.
     * Affiche en console la ligne avec la plus petite valeur de Eb.
     *
     * @throws Exception Si une erreur survient pendant l'exécution.
     */
    public void lancerSimulations() throws Exception {
        System.out.println("Lancement des simulations pour " + this.fichierCSV + "...");

        double targetTEB = 1e-3;

        DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#.############", decimalSymbols);

        // Initialisation des valeurs SNR
        double[] snrValues = new double[51];
        for (int i = 0; i <= 50; i++) {
            snrValues[i] = 25 - i;
        }

        // Initialisation des valeurs nbEch
        int[] nbEchValues = new int[]{3, 9, 21, 30, 45, 60};

        Form[] formValues = {Form.NRZ, Form.NRZT, Form.RZ};

        double n0DbmPerHz = -80;    // Valeur du sujet
        BigDecimal n0 = Utils.convertDbmPerHzToWPerHz(n0DbmPerHz);

        BigDecimal minEb = BigDecimal.valueOf(Double.MAX_VALUE);
        StringBuilder minEbLine = new StringBuilder();

        try (FileWriter csvWriter = new FileWriter(this.fichierCSV)) {
            // Entête du fichier CSV
            csvWriter.append("Forme,nbEch,SNRpb (dB),TEB,Eb (J)\n");

            // Calcul du nombre total d'itérations pour la barre de progression
            int totalIterations = formValues.length * snrValues.length * nbEchValues.length;
            int currentIteration = 0;

            for (Form form : formValues) {
                for (double snr : snrValues) {
                    for (int nbEch : nbEchValues) {
                        // Paramètres
                        String[] params = {"-mess", "10000", "-snrpb", String.valueOf(snr), "-nbEch", String.valueOf(nbEch),  "-form", String.valueOf(form), "-codeur"};

                        // Calcul du TEB
                        Simulateur simulateur = new Simulateur(params);
                        simulateur.execute();
                        double teb = simulateur.calculTauxErreurBinaire();

                        // Calcul du Eb
                        BigDecimal eb = Utils.getEbFromN0AndSnrpb((float) snr, n0);

                        if (teb < targetTEB) {
                            // Enregistrement des résultats
                            String line = form + "," + nbEch + "," + snr + "," + df.format(teb) + "," + eb.toPlainString() + "\n";
                            csvWriter.append(line);

                            if (eb.compareTo(minEb) < 0) {
                                minEb = eb;
                                minEbLine.setLength(0);  // Réinitialiser le StringBuilder
                                minEbLine.append("- Forme: ").append(form).append("\n")
                                        .append("- Nombre d'échantillons: ").append(nbEch).append("\n")
                                        .append("- SNRpb (dB): ").append(snr).append("\n")
                                        .append("- TEB: ").append(df.format(teb)).append("\n")
                                        .append("- Eb (J): ").append(eb.toPlainString()).append("\n");
                            }
                        }

                        // Mise à jour et affichage de la barre de progression
                        currentIteration++;
                        int progressPercentage = (currentIteration * 100) / totalIterations;
                        Utils.printProgressBar(progressPercentage);
                    }
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
        ExportCSVEnvironnement1 exportCSVEnvironnement1 = new ExportCSVEnvironnement1();
        try {
            exportCSVEnvironnement1.lancerSimulations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
