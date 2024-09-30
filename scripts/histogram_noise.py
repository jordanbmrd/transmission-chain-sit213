import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy.stats import norm

# Charger le fichier CSV
data = pd.read_csv('../valeurs_bruit_gaussien.csv')

# Extraire les valeurs du 'Bruit gaussien'
values = data['Bruit gaussien']

# Créer un histogramme pour les valeurs du bruit gaussien
plt.figure(figsize=(8, 6))
plt.hist(values, bins=50, edgecolor='black', color='blue', density=True)

# Calculer la moyenne et l'écart-type des données
mu, sigma = values.mean(), values.std()

# Générer des valeurs pour la courbe de la gaussienne
xmin, xmax = plt.xlim()
x = np.linspace(xmin, xmax, 100)
p = norm.pdf(x, mu, sigma)

# Tracer la courbe de la gaussienne
plt.plot(x, p, 'r', linewidth=2, label='Gaussienne théorique')

# Ajouter les titres et les labels
plt.title('Histogramme des valeurs du bruit gaussien', fontsize=14)
plt.xlabel('Valeurs', fontsize=12)
plt.ylabel('Densité de probabilité', fontsize=12)

# Ajouter la légende
plt.legend()

# Afficher le graphique
plt.grid(True)
plt.show()
