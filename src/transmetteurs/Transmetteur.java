package transmetteurs;

import sources.*;
import destinations.*;
import information.*;

import java.util.*;

/** 
 * Classe Abstraite d'un composant transmetteur d'informations dont
 * les éléments sont de type R en entrée et de type E en sortie;
 * l'entrée du transmetteur implémente l'interface
 * DestinationInterface, la sortie du transmetteur implémente
 * l'interface SourceInterface
 * @author prou
 */
public abstract  class Transmetteur <R,E> implements  DestinationInterface <R>, SourceInterface <E> {
   
    /** 
     * la liste des composants destination connectés en sortie du transmetteur 
     */
    protected LinkedList <DestinationInterface <E>> destinationsConnectees;
   
    /** 
     * l'information reçue en entrée du transmetteur 
     */
    protected Information <R>  informationRecue;
		
    /** 
     * l'information émise en sortie du transmetteur
     */		
    protected Information <E>  informationEmise;

    /**
     * les informations de bruit générés
     * */
    protected Information<Float> bruitList = new Information<>();
   
    /** 
     * un constructeur factorisant les initialisations communes aux
     * réalisations de la classe abstraite Transmetteur
     */
    public Transmetteur() {
	destinationsConnectees = new LinkedList <DestinationInterface <E>> ();
	informationRecue = null;
	informationEmise = null;
    }
   	
    /**
     * retourne la dernière information reçue en entrée du
     * transmetteur
     * @return une information   
     */
    public Information <R>  getInformationRecue() {
	return this.informationRecue;
    }

    /**
     * retourne la dernière information émise en sortie du
     * transmetteur
     * @return une information   
     */
    public Information <E>  getInformationEmise() {
	return this.informationEmise;
    }

    /**
     * connecte une destination à la sortie du transmetteur
     * @param destination  la destination à connecter
     */
    public void connecter (DestinationInterface <E> destination) {
	destinationsConnectees.add(destination); 
    }

    /**
     * déconnecte une destination de la la sortie du transmetteur
     * @param destination  la destination à déconnecter
     */
    public void deconnecter (DestinationInterface <E> destination) {
	destinationsConnectees.remove(destination); 
    }
   	    
    /**
     * reçoit une information.  Cette méthode, en fin d'exécution,
     * appelle la méthode émettre.
     * @param information  l'information  reçue
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    public  abstract void recevoir(Information <R> information) throws InformationNonConformeException;
   
    /**
     * émet l'information construite par le transmetteur
     * @throws InformationNonConformeException si l'Information comporte une anomalie
     */
    public  abstract void emettre() throws InformationNonConformeException;

    /**
     * Renvoie la puissance de bruit moyenne
     * @return puissance de bruit moyenne calculée
     * */
    public float getPuissanceMoyenneBruit() {
        return Float.NaN;
    }

    /**
     * Renvoie la valeur de SNR réel
     * @return valeur de SNR réel calculée
     * */
    public float getSNRReel() {
        return Float.NaN;
    }

    /**
     * Renvoie la valeur de Eb/N0 en dB
     * @return valeur de Eb/N0 calculée
     * */
    public float getEbN0dB() {
        return Float.NaN;
    }

    /**
     * Renvoie la valeur de la variance
     * @return valeur de la variance calculée
     * */
    public float getVariance() {
        return Float.NaN;
    }

    /**
     * Renvoie la liste contenant les échantillons de bruit générés
     * @return liste contenant les échantillons de bruit générés
     * */
    public Information<Float> getBruitList() {
        return this.bruitList;
    }
}
