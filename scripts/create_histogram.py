import pandas as pd
import matplotlib.pyplot as plt

# Load the CSV file
data = pd.read_csv('../valeurs_bruit_gaussien.csv')

# Create a histogram for the 'Bruit gaussien' column
plt.figure(figsize=(8, 6))
plt.hist(data['Bruit gaussien'], bins=30, edgecolor='black', color='blue', alpha=0.7)

# Adding labels and title
plt.title('Histogramme des valeurs du Bruit Gaussien', fontsize=16)
plt.xlabel('Valeurs', fontsize=12)
plt.ylabel('Fréquence', fontsize=12)

# Show the plot
plt.grid(True)
plt.show()
