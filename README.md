# SIT213

## TP5: Transmission analogique avec un codage de canal

### Objectifs

- Rajouter la possibilité d'utiliser un codage de canal pour permettre de :
  - Repérer et réparer des erreurs eventuelles lors de la transmission.
  - Réduire le taux d'erreur binaire de la chaîne de transmission.
- Mesurer les améliorations.

### Ajout de nouveaux flags au CLI

- `-codeur`: Utilisation d'un codage de canal. Valeur par défaut : non utilisé.

### Modifications des scripts

- Modification du script `runTests` pour y ajouter une fonction permettant de shuffle les flags du CLI.

### Modifications des classes

- Ajout des classes `Codeur` et `Decodeur` pour utiliser un canal de codage.
- Ajout de la classe `CodeurDecodeurTest` pour tester le comportement des classes `Codeur` et `Décodeur`.
- Modification de la classe `Simulateur` pour prendre en compte le flag `-codeur`.

## TP4: Transmission analogique avec un canal bruité à trajets multiples

### Objectifs

- Rajouter la possibilité d'utiliser une transmission multi-trajets.

### Ajout de nouveaux flags au CLI

- `-ti <[int] [float]> <[int] [float]> ...`: Utilisation d'une transmission analogique multi-trajets. 5 couples de valeurs au maximum.
  - `dt` (int) précise le décalage temporel (en nombre d'échantillons)
  - `ar` (float) précise l'amplitude relative du signal du trajet indirect par rapport à celle du trajet direct.

### Ajout de scripts

- `plot_teb_multi_trajets.py`: Trace les courbes de la probabilité d'erreur binaire en fonction :
  - Du nombre de trajets indirects ajoutés.
  - De l'amplitude des trajets indirects.
  - Du décalage temporel des trajets indirects.

### Modifications des classes

- Ajout de la classe `TransmetteurMultiTrajets` pour utiliser un canal de transmission à trajets multiples.
- Ajout de la classe `TransmetteurMultiTrajetsTest` pour tester le comportement de la classe `TransmetteurMultiTrajets`.

## TP3: Transmission analogique avec un bruit gaussien

### Objectifs

- Générer un bruit gaussien et l'ajouter lors de la transmission.
- Vérifier que le bruit suit bien une loi gaussienne en traçant un histogramme.

### Ajout de nouveaux flags au CLI

- `-snrpb [float]`: Choix du RSB par bit en dB. Valeur par défaut : 0.

### Ajout du répertoire `scripts`
Ce répertoire contient des scripts utiles au projet et aux tests visuels :

- `histogram_noise.py` : Trace l'histogramme des valeurs de bruit gaussien.
- `plot_teb_vs_snr.py` : Trace la courbe du TEB en fonction du SNR pour les différentes modulations.
- `plot_proba_erreur_vs_ebn0.py`: Trace la courbe de la probabilité d'erreur binaire en fonction du rapport Eb/N0 pour les différentes modulations.

### Modifications des classes

- Ajout de la classe `TransmetteurGaussien` pour générer un signal analogique avec un bruit gaussien.
- Ajout de la classe `TransmetteurGaussienTest` pour tester le comportement de la classe `TransmetteurGaussien`
- Modifications des classes `Simulateur`, `Emetteur` et `Recepteur` pour intégrer le transmetteur gaussien.

## TP2: Transmission analogique non bruitée

### Ajout de nouveaux flags au CLI

- `-form [string]`: Choix du form à utiliser. Valeur par défaut : RZ. Les options disponibles sont :
  - `NRZ`
  - `NRZT`
  - `RZ`
- `-ampl [float] [float]`: Choix de l'amplitude, la première valeur est l'amplitude minimale et la seconde est l'amplitude maximale. Valeurs par défaut : 0 et 1.
- `-nbEch [int]`: Choix du nombre d'échantillons par bit. Valeur par défaut : 30.

### Ajout du répertoire `librairies`

Ce répertoire contient un ensemble de fichiers .jar qui permettent d'importer des fonctionnalités déjà codées.
Il contient les fichiers suivant :
- `easymock-5.4.0.jar`: Permet de simuler le résultat de fonctions.
- `junit-4.13.1.jar`: Permet de réaliser des tests unitaires pour tester le bon fonctionnement des méthodes.
- `hamcrest-core-1.3.jar`: Dépendance pour JUnit.

## TP1: Transmission élémentaire back-to-back

### Contenu de l'archive à livrer

- `bin/`: Répertoire contenant le form compilé.
- `docs/`: Documentation générée automatiquement sous forme de Javadoc.
- `src/`: Répertoire contenant le form source du projet.
- `cleanAll`: Script Bash qui nettoie le projet en supprimant l'archive générée, les fichiers compilés et la documentation.
- `compile`: Script Bash utilisé pour compiler le projet.
- `genDoc`: Script Bash pour générer la documentation Javadoc à partir du form source.
- `runTests`: Script Bash qui exécute les tests pour vérifier le bon fonctionnement du projet.
- `README.md`: Fichier de documentation (ce fichier) qui détaille les composants et scripts du projet.
