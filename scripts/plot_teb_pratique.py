import pandas as pd
import matplotlib.pyplot as plt

# Charger le fichier CSV
file_path = '../valeurs_teb_pratique.csv'
data = pd.read_csv(file_path)

# Extraire les colonnes SNR et TEB pour chaque modulation
snr = data['SnrPb']
teb_rz = data['TEB RZ']
teb_nrz = data['TEB NRZ']
teb_nrzt = data['TEB NRZT']

# Définir le seuil minimum de TEB
seuil_teb = 1e-4

# Filtrer les valeurs de TEB supérieures ou égales au seuil
teb_rz = teb_rz[teb_rz >= seuil_teb]
teb_nrz = teb_nrz[teb_nrz >= seuil_teb]
teb_nrzt = teb_nrzt[teb_nrzt >= seuil_teb]

# Correspondre les valeurs SNR aux valeurs filtrées de TEB
snr_rz = snr[:len(teb_rz)]
snr_nrz = snr[:len(teb_nrz)]
snr_nrzt = snr[:len(teb_nrzt)]

# Tracer le TEB en fonction du SNR pour chaque type de modulation (en semilog sur l'axe Y)
plt.figure(figsize=(10, 6))

plt.semilogy(snr_rz, teb_rz, label="TEB RZ", marker='o')
plt.semilogy(snr_nrz, teb_nrz, label="TEB NRZ", marker='s')
plt.semilogy(snr_nrzt, teb_nrzt, label="TEB NRZT", marker='^')

# Ajouter les étiquettes et le titre
plt.xlabel("SNR par bit (dB)")
plt.ylabel("TEB")
plt.grid(True, which="both", linestyle='--')
plt.legend()

# Afficher le graphe
plt.show()
