import pandas as pd
import matplotlib.pyplot as plt

# Charger le fichier CSV (ajustez le chemin vers le fichier généré)
file_path = '../comparaison_courbe_codeur.csv'
data = pd.read_csv(file_path)

# Extraire les colonnes Eb/N0 et TEB pratique avec et sans l'option -codeur
eb_n0 = data['Eb/N0 (dB)']
teb_sans_codeur = data['TEB Pratique NRZ (sans codeur)']
teb_avec_codeur = data['TEB Pratique NRZ (avec codeur)']

# Définir le seuil minimum de TEB
seuil_teb = 1e-4

# Filtrer les valeurs de TEB supérieures ou égales au seuil pour la comparaison
teb_sans_codeur = teb_sans_codeur[teb_sans_codeur >= seuil_teb]
teb_avec_codeur = teb_avec_codeur[teb_avec_codeur >= seuil_teb]

# Correspondre les valeurs Eb/N0 aux valeurs filtrées de TEB
eb_n0_sans_codeur = eb_n0[:len(teb_sans_codeur)]
eb_n0_avec_codeur = eb_n0[:len(teb_avec_codeur)]

# Tracer le TEB en fonction de Eb/N0 pour les simulations avec et sans l'option -codeur
plt.figure(figsize=(10, 6))

plt.semilogy(eb_n0_sans_codeur, teb_sans_codeur, label="TEB Pratique NRZ (sans codeur)", marker='o', color='blue')
plt.semilogy(eb_n0_avec_codeur, teb_avec_codeur, label="TEB Pratique NRZ (avec codeur)", marker='s', color='green')

# Ajouter les étiquettes et le titre
plt.xlabel("Eb/N0 par bit (dB)")
plt.ylabel("TEB")
plt.title("Comparaison du TEB Pratique NRZ avec et sans Codeur")
plt.grid(True, which="both", linestyle='--')
plt.legend()

# Afficher le graphe
plt.show()
