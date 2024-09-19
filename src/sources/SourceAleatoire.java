package sources;

import information.Information;
import java.util.Random;

/**
 * Classe SourceAleatoire génère aléatoirement une séquence de bits (booléens).
 * Elle hérite de la classe Source et génère une Information contenant un certain nombre de bits aléatoires.
 */
public class SourceAleatoire extends Source<Boolean> {

    /**
     * Constructeur de la classe SourceAleatoire.
     * Génère une séquence aléatoire de bits de taille spécifiée.
     *
     * @param nbBitsMess le nombre de bits à générer dans la séquence.
     */
    public SourceAleatoire(int nbBitsMess) {
        super();
        this.informationGeneree = new Information<Boolean>();

        Random random = new Random();
        // Génération de nbBitsMess bits aléatoires
        for (int i = 0; i < nbBitsMess; i++) {
            this.informationGeneree.add(random.nextBoolean());
        }

        this.informationEmise = this.informationGeneree;
    }

    /**
     * Constructeur de la classe SourceAleatoire avec graine (seed).
     * Génère une séquence aléatoire de bits de taille spécifiée avec une graine pour la reproductibilité.
     *
     * @param taille la taille de la séquence de bits à générer.
     * @param seed la graine utilisée pour initialiser le générateur aléatoire (permet la reproductibilité des séquences).
     */
    public SourceAleatoire(int taille, int seed) {
        super();
        this.informationGeneree = new Information<>();

        Random random = new Random(seed);
        // Génération de taille bits aléatoires avec graine
        for (int i = 0; i < taille; i++) {
            this.informationGeneree.add(random.nextBoolean());
        }

        this.informationEmise = this.informationGeneree;
    }
}