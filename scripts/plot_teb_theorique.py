import pandas as pd
import matplotlib.pyplot as plt

# Charger le fichier CSV
file_path = '../valeurs_teb_theorique_nrz.csv'  # Mettez le chemin correct ici
data = pd.read_csv(file_path)

# Extraire les colonnes Eb/N0 et P_e pour RZ
eb_n0 = data['Eb/N0 (dB)']
pe_rz = data['P_e NRZ']

# Tracer la probabilité d'erreur en fonction de Eb/N0 en dB
plt.figure(figsize=(10, 6))

plt.plot(eb_n0, pe_rz, label="P_e NRZ", marker='o')

# Ajouter les étiquettes et le titre
plt.xlabel("SNR par bit (dB)")
plt.ylabel("TEB")
plt.yscale('log')  # Utiliser une échelle logarithmique pour une meilleure visualisation
plt.grid(True)
plt.legend()

plt.xlim(-10, 10)  # Limites en dB
plt.ylim(10**-5, 10**-1)  # Limites du TEB

# Afficher le graphe
plt.show()
