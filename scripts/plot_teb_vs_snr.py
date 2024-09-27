import pandas as pd
import matplotlib.pyplot as plt

# Charger le fichier CSV
file_path = '../valeurs_teb_par_snr.csv'
data = pd.read_csv(file_path)

# Extraire les colonnes SNR et TEB pour chaque modulation
snr = data['SnrPb']
teb_rz = data['TEB RZ']
teb_nrz = data['TEB NRZ']
teb_nrzt = data['TEB NRZT']

# Tracer le TEB en fonction du SNR pour chaque type de modulation
plt.figure(figsize=(10, 6))

plt.plot(snr, teb_rz, label="TEB RZ", marker='o')
plt.plot(snr, teb_nrz, label="TEB NRZ", marker='s')
plt.plot(snr, teb_nrzt, label="TEB NRZT", marker='^')

# Ajouter les étiquettes et le titre
plt.xlabel("SNR (dB)")
plt.ylabel("TEB")
plt.title("TEB en fonction du SNR pour les modulations RZ, NRZ et NRZT")
plt.grid(True)
plt.legend()

# Afficher le graphe
plt.show()
