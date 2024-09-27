import pandas as pd
import matplotlib.pyplot as plt

# Charger le fichier CSV
data = pd.read_csv('../valeurs_bruit_gaussien.csv')

# Créer un histogramme pour le 'Bruit gaussien'
plt.figure(figsize=(8, 6))
plt.hist(data['Bruit gaussien'], bins=30, edgecolor='black', color='blue', alpha=0.7)

# Ajouter les titres et labels
plt.title('Histogramme des valeurs du Bruit Gaussien', fontsize=16)
plt.xlabel('Valeurs', fontsize=12)
plt.ylabel('Fréquence', fontsize=12)

# Afficher le graphique
plt.grid(True)
plt.show()
