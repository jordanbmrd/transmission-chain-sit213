# SIT213

## TP2: Transmission analogique non bruitée

### Ajout de nouveaux flags au CLI

- `-code [string]`: Choix du code à utiliser. **Pas de valeur par défaut**. Les options disponibles sont :
  - `NRZ`
  - `NRZT`
  - `RZ`
- `-aMax [float]`: Choix de l'amplitude max.
- `-aMin [float]`: Choix de l'amplitude min.

Tous les flags sont obligatoires. Une erreur est renvoyée sinon.

### Ajout du répertoire `librairies`

Ce répertoire contient un ensemble de fichiers .jar qui permettent d'importer des fonctionnalités déjà codées.
Il contient les fichiers suivant :
- `easymock-5.4.0.jar`: Permet de simuler le résultat de fonctions.
- `junit-4.13.1.jar`: Permet de réaliser des tests unitaires pour tester le bon fonctionnement des méthodes.
- `hamcrest-core-1.3.jar`: Dépendance pour JUnit.

## TP1: Transmission élémentaire back-to-back

### Contenu de l'archive à livrer

- `bin/`: Répertoire contenant le code compilé.
- `docs/`: Documentation générée automatiquement sous forme de Javadoc.
- `src/`: Répertoire contenant le code source du projet.
- `cleanAll`: Script Bash qui nettoie le projet en supprimant l'archive générée, les fichiers compilés et la documentation.
- `compile`: Script Bash utilisé pour compiler le projet.
- `genDoc`: Script Bash pour générer la documentation Javadoc à partir du code source.
- `runTests`: Script Bash qui exécute les tests pour vérifier le bon fonctionnement du projet.
- `README.md`: Fichier de documentation (ce fichier) qui détaille les composants et scripts du projet.