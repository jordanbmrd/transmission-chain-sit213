import pandas as pd
import matplotlib.pyplot as plt

# Charger le fichier CSV
file_path = '../valeurs_proba_erreur_par_ebn0.csv'  # Mettez le chemin correct ici
data = pd.read_csv(file_path)

# Extraire les colonnes Eb/N0 et P_e pour chaque modulation
eb_n0 = data['Eb/N0 (dB)']
pe_rz = data['P_e RZ']
pe_nrz = data['P_e NRZ']
pe_nrzt = data['P_e NRZT']

# Tracer la probabilité d'erreur en fonction de Eb/N0 pour chaque type de modulation
plt.figure(figsize=(10, 6))

plt.plot(eb_n0, pe_rz, label="P_e RZ", marker='o')
plt.plot(eb_n0, pe_nrz, label="P_e NRZ", marker='s')
plt.plot(eb_n0, pe_nrzt, label="P_e NRZT", marker='^')

# Ajouter les étiquettes et le titre
plt.xlabel("Eb/N0 (dB)")
plt.ylabel("Probabilité d'erreur (P_e)")
plt.title("Probabilité d'erreur en fonction d'Eb/N0 pour les modulations RZ, NRZ et NRZT")
plt.yscale('log')  # Utiliser une échelle logarithmique pour une meilleure visualisation
plt.grid(True)
plt.legend()

plt.xlim(0, 12.5)

# Afficher le graphe
plt.show()
