package utils;

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
     * @param n0 la densité spectrale de bruit en W/Hz.
     * @return l'énergie par bit (Eb).
     * @throws IllegalArgumentException si N0 est égal à zéro.
     */
    public static float getEbFromN0AndSnrpb(float snrpb, float n0) {
        if (n0 == 0) {
            throw new IllegalArgumentException("N0 ne peut pas être nul.");
        }
        return snrpb * n0;
    }
}