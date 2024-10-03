package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import simulateur.Simulateur;

public class ExportCSVMultiTrajets {

    private static final String FICHIER_TRAJETS_MULTIPLES = "valeurs_teb_multi_trajets.csv";
    private static final String FICHIER_AMPLITUDES_MULTIPLES = "valeurs_teb_amplitudes_multiples.csv";
    private static final String FICHIER_DECALAGES_AUGMENTES = "valeurs_teb_decalages_augments.csv";

    private static final String[] PARAMETRES_BASE = new String[]{"-mess", "200", "-seed", "5678", "-nbEch", "30", "-snrpb", "-2"};
    private static final String[] FORMATS_MODULATION = new String[]{"RZ", "NRZ", "NRZT"};

    protected Simulateur simulateur;
    protected LinkedList<String> parametres;

    public ExportCSVMultiTrajets() {
        this.parametres = new LinkedList<>(Arrays.asList(PARAMETRES_BASE));
    }

    public void lancerSimulations() {
        String[] trajets = {
                "",
                "-ti 120 0.6",
                "-ti 120 0.6 20 0.75",
                "-ti 120 0.6 20 0.75 5 0.5",
                "-ti 120 0.6 20 0.75 5 0.5 80 0.3",
                "-ti 120 0.6 20 0.75 5 0.5 80 0.3 200 0.8"
        };
        lancerEtEnregistrerSimulations(trajets);
    }

    public void lancerSimulationsAmplitudesMultiples() {
        float[] amplitudes = {0.3f, 0.75f, 0.5f};
        lancerSimulationsVariationAmplitudes(amplitudes);
    }

    public void lancerSimulationsDecalagesAugmentes() {
        int[] decalages = {100, 20, 5};
        lancerSimulationsVariationDecalages(decalages);
    }

    private void lancerEtEnregistrerSimulations(String[] trajets) {
        try (FileWriter csvWriter = new FileWriter(ExportCSVMultiTrajets.FICHIER_TRAJETS_MULTIPLES)) {
            csvWriter.append("Configuration,TEB RZ,TEB NRZ,TEB NRZT\n");

            for (int i = 0; i < trajets.length; i++) {
                csvWriter.append(String.valueOf((i + 1))).append(" trajet");

                for (String format : FORMATS_MODULATION) {
                    float ber = executerSimulation(format, trajets[i]);
                    csvWriter.append(",").append(String.valueOf(ber));
                }
                csvWriter.append("\n");
            }
            System.out.println("Résultats enregistrés dans " + ExportCSVMultiTrajets.FICHIER_TRAJETS_MULTIPLES);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void lancerSimulationsVariationAmplitudes(float[] amplitudesInitiales) {
        try (FileWriter csvWriter = new FileWriter(ExportCSVMultiTrajets.FICHIER_AMPLITUDES_MULTIPLES)) {
            csvWriter.append("Simulation,TEB RZ,TEB NRZ,TEB NRZT\n");

            for (int i = 1; i <= 10; i++) {
                csvWriter.append("Simulation ").append(String.valueOf(i));

                String trajetsConfig = "-ti 100 "
                        + limiterAmplitude(amplitudesInitiales[0])
                        + " 20 "
                        + limiterAmplitude(amplitudesInitiales[1])
                        + " 5 "
                        + limiterAmplitude(amplitudesInitiales[2]);

                for (String format : FORMATS_MODULATION) {
                    float ber = executerSimulation(format, trajetsConfig);
                    csvWriter.append(",").append(String.valueOf(ber));
                }
                csvWriter.append("\n");

                // Mettre à jour les amplitudes pour la prochaine itération
                for (int j = 0; j < amplitudesInitiales.length; j++) {
                    amplitudesInitiales[j] = Math.min(amplitudesInitiales[j] * (float) 1.05, 1.0f);
                }
            }
            System.out.println("Résultats des amplitudes multiples enregistrés dans " + ExportCSVMultiTrajets.FICHIER_AMPLITUDES_MULTIPLES);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float limiterAmplitude(float amplitude) {
        return Math.min(amplitude, 1.0f);
    }

    private void lancerSimulationsVariationDecalages(int[] decalagesInitiales) {
        try (FileWriter csvWriter = new FileWriter(ExportCSVMultiTrajets.FICHIER_DECALAGES_AUGMENTES)) {
            csvWriter.append("Simulation,TEB RZ,TEB NRZ,TEB NRZT\n");

            for (int i = 1; i <= 10; i++) {
                csvWriter.append("Simulation ").append(String.valueOf(i));

                String trajetsConfig = "-ti "
                        + decalagesInitiales[0]
                        + " 0.3 "
                        + decalagesInitiales[1]
                        + " 0.75 "
                        + decalagesInitiales[2]
                        + " 0.5";

                for (String format : FORMATS_MODULATION) {
                    float ber = executerSimulation(format, trajetsConfig);
                    csvWriter.append(",").append(String.valueOf(ber));
                }
                csvWriter.append("\n");

                for (int j = 0; j < decalagesInitiales.length; j++) {
                    decalagesInitiales[j] += 20;
                }
            }
            System.out.println("Résultats des décalages augmentés enregistrés dans " + ExportCSVMultiTrajets.FICHIER_DECALAGES_AUGMENTES);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float executerSimulation(String format, String trajetsConfig) {
        float ber = 0;
        LinkedList<String> argumentsLocaux = new LinkedList<>(this.parametres);
        try {
            argumentsLocaux.add("-form");
            argumentsLocaux.add(format);
            if (!trajetsConfig.isEmpty()) {
                argumentsLocaux.addAll(Arrays.asList(trajetsConfig.split(" ")));
            }

            Simulateur simulateur = new Simulateur(argumentsLocaux.toArray(new String[0]));
            simulateur.execute();
            ber = simulateur.calculTauxErreurBinaire();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ber;
    }

    public static void main(String[] args) {
        ExportCSVMultiTrajets simu = new ExportCSVMultiTrajets();

        simu.lancerSimulations();
        simu.lancerSimulationsAmplitudesMultiples();
        simu.lancerSimulationsDecalagesAugmentes();

        System.out.println("Toutes les simulations sont terminées et les fichiers CSV ont été générés.");
    }
}
