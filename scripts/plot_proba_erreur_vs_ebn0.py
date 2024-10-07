import pandas as pd
import matplotlib.pyplot as plt

# Charger le fichier CSV
file_path = '../valeurs_proba_erreur_par_ebn0.csv'  # Mettez le chemin correct ici
data = pd.read_csv(file_path)

# Extraire les colonnes Eb/N0 et P_e
eb_n0 = data['Eb/N0 (dB)']
eb_n0_lin = 10**(eb_n0/10)
pe_nrz = data['P_e NRZ']

# Tracer la probabilité d'erreur en fonction de Eb/N0
plt.figure(figsize=(10, 6))

plt.plot(eb_n0_lin, pe_nrz, label="P_e NRZ", marker='s')

# Ajouter les étiquettes et le titre
plt.xlabel("SNR par bit (linéaire)")
plt.ylabel("TEB")
plt.yscale('log')  # Utiliser une échelle logarithmique pour une meilleure visualisation
plt.grid(True)
plt.legend()

plt.xlim(0, 5)
plt.ylim(10**-5, 10**-1)

# Afficher le graphe
plt.show()
