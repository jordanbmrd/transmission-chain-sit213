import pandas as pd
import matplotlib.pyplot as plt

# Fonction pour générer un graphique à partir d'un fichier CSV
def tracer_teb(file_path, title):
    # Charger le fichier CSV
    data = pd.read_csv(file_path)

    # Extraire les colonnes de TEB pour chaque modulation
    teb_rz = data['TEB RZ']
    teb_nrz = data['TEB NRZ']
    teb_nrzt = data['TEB NRZT']

    # Générer l'axe X correspondant aux simulations/configurations
    simulations = data.index + 1  # Numérotation des simulations

    # Tracer le TEB pour chaque type de modulation (en semilog sur l'axe Y)
    plt.figure(figsize=(10, 6))

    plt.semilogy(simulations, teb_rz, label="TEB RZ", marker='o')
    plt.semilogy(simulations, teb_nrz, label="TEB NRZ", marker='s')
    plt.semilogy(simulations, teb_nrzt, label="TEB NRZT", marker='^')

    # Ajouter les étiquettes, le titre, et la légende
    plt.xlabel("Simulation / Configuration")
    plt.ylabel("TEB")
    plt.title(title)
    plt.grid(True, which="both", linestyle='--')
    plt.legend()

    # Afficher le graphique
    plt.show()

# Tracer les graphiques pour chaque fichier
tracer_teb('../valeurs_teb_multi_trajets.csv', 'TEB pour configurations de trajets multiples')
tracer_teb('../valeurs_teb_amplitudes_multiples.csv', 'TEB pour variations d\'amplitudes multiples')
tracer_teb('../valeurs_teb_decalages_augments.csv', 'TEB pour variations de décalages augmentés')
