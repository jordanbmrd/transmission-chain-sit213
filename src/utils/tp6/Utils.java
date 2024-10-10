package utils.tp6;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * La classe Utils fournit des méthodes utilitaires pour les conversions et calculs
 * liés aux communications numériques.
 */
public class Utils {
    /**
     * Convertit une densité spectrale de bruit de dBm/Hz en W/Hz.
     *
     * @param n0DbmPerHz La densité spectrale de bruit en dBm/Hz.
     * @return La densité spectrale de bruit en W/Hz.
     */
    public static BigDecimal convertDbmPerHzToWPerHz(double n0DbmPerHz) {
        // Calculer la conversion en utilisant les fonctions exponentielles et logarithmiques.
        double result = Math.pow(10, (n0DbmPerHz / 10)) * 1e-3;
        // Retourner le résultat en BigDecimal pour garder la précision
        return new BigDecimal(result, MathContext.DECIMAL128).setScale(20, RoundingMode.HALF_UP);
    }

    /**
     * Calcule l'énergie par bit (Eb) à partir du rapport signal sur bruit par bit (SNRpb)
     * et de la densité spectrale de bruit (N0).
     *
     * @param snrpb Le rapport signal sur bruit par bit (SNRpb).
     * @param n0 La densité spectrale de bruit en W/Hz.
     * @return L'énergie par bit (Eb).
     * @throws IllegalArgumentException Si N0 est égal à zéro.
     */
    public static BigDecimal getEbFromN0AndSnrpb(float snrpb, BigDecimal n0) {
        if (n0.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("N0 ne peut pas être nul.");
        }
        // Calculer Eb en utilisant la précision BigDecimal
        BigDecimal snrpbBd = new BigDecimal(Math.pow(10, (snrpb / 10)), MathContext.DECIMAL128);
        return snrpbBd.multiply(n0, MathContext.DECIMAL128);
    }

    /**
     * Calcule la consommation énergétique en multipliant Eb par 10^10.
     *
     * @param eb L'énergie par bit (Eb).
     * @return La consommation énergétique.
     */
    public static BigDecimal calculConsommation(BigDecimal eb) {
        // Multiplier Eb par 10^4 et 10^6
        BigDecimal facteur1 = new BigDecimal(Math.pow(10, 4), MathContext.DECIMAL128);
        BigDecimal facteur2 = new BigDecimal(Math.pow(10, 6), MathContext.DECIMAL128);
        return eb.multiply(facteur1).multiply(facteur2, MathContext.DECIMAL128).setScale(20, RoundingMode.HALF_UP);
    }

    /**
     * Affiche une barre de progression dans la console.
     *
     * @param percent le pourcentage de progression (entre 0 et 100).
     */
    public static void printProgressBar(int percent) {
        // Longueur totale de la barre de progression
        final int totalChars = 50;
        int charsToDisplay = totalChars * percent / 100;
        StringBuilder progressBar = new StringBuilder("[");
        for (int i = 0; i < totalChars; i++) {
            if (i < charsToDisplay) {
                progressBar.append("#");
            } else {
                progressBar.append(" ");
            }
        }
        progressBar.append("] ").append(percent).append("%");
        System.out.print("\r" + progressBar.toString());
    }
}
