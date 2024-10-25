<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a id="readme-top"></a>

[![Tests Status](https://gitlab-df.imt-atlantique.fr/m23maque/sit213/badges/main/pipeline.svg?key_text=Tests+Status&key_width=80)](https://gitlab-df.imt-atlantique.fr/m23maque/sit213/-/jobs)

<!-- PROJECT LOGO -->
<div align="center">
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="https://cdn-icons-png.flaticon.com/512/17/17239.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">SIT213 - Simulateur de chaîne de transmission</h3>

  <p align="center">
    Simulation d'un système de chaîne de transmission analogique en Java
    <br />
    <br />
    <a href="https://sit213-m23maque-03224fc893efd631ee75b3492a771a16117a0b8b074f6be.gitlab-df-pages.imt-atlantique.fr">Accéder à la Javadoc</a>
    ·
    <a href="https://gitlab-df.imt-atlantique.fr/m23maque/sit213/-/raw/main/deliverables/step-5/BAUMARD.GUILLOU.MAQUENNE.SIT213.Etape5.pdf?ref_type=heads&inline=false">Télécharger le rapport "Étape 5"</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table des matières</summary>
  <ol>
    <li><a href="#tp6-cas-détude">TP6: Cas d'étude</a></li>
    <li><a href="#tp5-transmission-analogique-avec-un-codage-de-canal">TP5: Transmission analogique avec un codage de canal</a></li>
    <li><a href="#tp4-transmission-analogique-avec-un-canal-bruité-à-trajets-multiples">TP4: Transmission analogique avec un canal bruité à trajets multiples</a></li>
    <li><a href="#tp3-transmission-analogique-avec-un-bruit-gaussien">TP3: Transmission analogique avec un bruit gaussien</a></li>
    <li><a href="#tp2-transmission-analogique-non-bruitée">TP2: Transmission analogique non bruitée</a></li>
    <li><a href="#tp1-transmission-élémentaire-back-to-back">TP1: Transmission élémentaire back-to-back</a></li>
  </ol>
</details>

## TP6: Cas d'étude

### Objectifs

- Utiliser le simulateur sur un cas d'étude précis (voir sujet).

### Modifications des scripts

- `plot_teb_comparaison.py` : Trace la courbe du TEB théorique et du TEB pratique pour la modulation NRZ.
- Suppression des scripts `plot_teb_vs_snr.py` et `plot_proba_erreur_vs_ebn0.py`.

### Modifications des classes

- Ajout des classes `ExportCSVEnvironnement1` et `ExportCSVEnvironnement2` pour générer des valeurs en fonction de différents paramètres et trouver les combinaisons qui répondent aux attentes du cas d'étude.

## TP5: Transmission analogique avec un codage de canal

### Objectifs

- Ajouter la possibilité d'utiliser un codage de canal pour permettre de :
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

- Ajouter la possibilité d'utiliser une transmission multi-trajets.

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