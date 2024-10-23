import pandas as pd
import matplotlib.pyplot as plt

# Charger le fichier CSV
file_path = '../comparaison_teb_nrz.csv'  # Assurez-vous que le chemin correspond au fichier généré
data = pd.read_csv(file_path)

# Extraire les colonnes Eb/N0, TEB théorique et TEB pratique
eb_n0 = data['Eb/N0 (dB)']
teb_theorique_nrz = data['TEB Théorique NRZ']
teb_pratique_nrz = data['TEB Pratique NRZ']

# Définir le seuil minimum de TEB
seuil_teb = 1e-4

# Filtrer les valeurs de TEB supérieures ou égales au seuil pour la comparaison
teb_theorique_nrz = teb_theorique_nrz[teb_theorique_nrz >= seuil_teb]
teb_pratique_nrz = teb_pratique_nrz[teb_pratique_nrz >= seuil_teb]

# Correspondre les valeurs Eb/N0 aux valeurs filtrées de TEB
eb_n0_theorique = eb_n0[:len(teb_theorique_nrz)]
eb_n0_pratique = eb_n0[:len(teb_pratique_nrz)]

# Tracer le TEB en fonction de Eb/N0 pour TEB théorique et TEB pratique (en semilog sur l'axe Y)
plt.figure(figsize=(10, 6))

plt.semilogy(eb_n0_theorique, teb_theorique_nrz, label="TEB Théorique NRZ", marker='o', color='blue')
plt.semilogy(eb_n0_pratique, teb_pratique_nrz, label="TEB Pratique NRZ", marker='s', color='red')

# Ajouter les étiquettes et le titre
plt.xlabel("Eb/N0 par bit (dB)")
plt.ylabel("TEB")
plt.title("Comparaison du TEB Théorique et Pratique pour NRZ")
plt.grid(True, which="both", linestyle='--')
plt.legend()

# Afficher le graphe
plt.show()
