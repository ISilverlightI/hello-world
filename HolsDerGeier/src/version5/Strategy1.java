package Version5;

import java.util.ArrayList;

/**
 * Ein Virtueller Spieler, welcher eine Zuordnung und Auswahl der zu spielenden Werte, abh�ngig von den Summen und Werten um welche Rundenbezogen gespielt wird, festlegt und an die Methode naechsterZug() aus der Klasse HolsDerGeier zur�ck gibt.
 * Spezialisierung der abstrakten Klasse HolsDerGeierSpieler
 * @author Jan Rohrbach 
 */
	//???	@return zuSpielendeKarte: Gibt den Wert der Karte zur�ck, welche ich als n�chstes spielen m�chte.
public class Strategy1 extends HolsDerGeierSpieler{

	/**
	 * Karten auf dem Stapel von 10 nach -5.
	 */
	private ArrayList<Integer> nochZuGewinnen=new ArrayList<Integer>();
	/**
	 * Karten auf dem Stapel sortiert nach pers�nlichem Nutzen.
	 */
    private ArrayList<Integer> nochZuGewinnenSortiert=new ArrayList<Integer>();
    /**
     * Karten auf meiner Hand.
     */
    private ArrayList<Integer> nochNichtGespielt=new ArrayList<Integer>();
    /**
     * Karten des Gegners.
     */
    private ArrayList<Integer> vomGegnerNochNichtGelegt=new ArrayList<Integer>();
    /**
     * meine zuletzt gelegte Karte.
     */
    private int meineLetzteKarte;
    /**
     * Summe der zu gewinnenden Werte in einer Runde im Falle eines Stiches.
     */
    private int summe;
	 
    /**
     *Konstruktor dieser Klasse. 
     *Ungenutzt und bislang nicht ben�tigt. 
     */
    public Strategy1() {
	}
	
    /**
     * Leert und f�llt alle vorhandenen ArrayLists. 
     */
	public void reset() {
		
		//leert und f�llt ArrayList nochZuGewinnen von 10 bis -5
		nochZuGewinnen.clear();
        for (int i=10;i>-6;i--)
        	if (i!=0) {
        		nochZuGewinnen.add(i);
        	}
        
        //leert und f�llt ArrayList nochZuGewinnenSortiert in bestimmter Reihenfolge von 15 bis -1
        //Reihenfolge: [10, 9, 8, 7, 6, 5, -5, 4, -4, 3, -3, 2, -2, 1, -1]
        nochZuGewinnenSortiert.clear();        
        for (int i=10;i>0;i--) {       	
        	if(i > 5)
        		nochZuGewinnenSortiert.add(i);
        	else
        		if(i < 6) {
            		nochZuGewinnenSortiert.add(i);
            		nochZuGewinnenSortiert.add(-i);
        		}        	
        }    	
        
        //leert und f�llt nochNichtGespielt von 15 bis 1
        nochNichtGespielt.clear();
        for (int i=15;i>0;i--) {           
            nochNichtGespielt.add(i);     
        }
        
        //leert und f�llt vomGegnerNochNichtGelegt von 15 bis 1
        vomGegnerNochNichtGelegt.clear();        
        for (int i=15;i>0;i--) {
            vomGegnerNochNichtGelegt.add(i);
        }
	}

	/**
	 * Legt fest, welche Spielsituation vorliegt und verweist auf die passende Methode.
	 * Unterscheidet zwischen einem Stich oder einer standart Situation (kein Stich).
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @return zuSpielendeKarte: Gibt den Wert der Karte zur�ck, welche ich als n�chstes spielen m�chte.
	 */
	public int gibKarte(int naechsteKarte) {
		 
		// Initialisierung der Variablen, f�r die zu spielende Karte, und der Aktualisierung der zuletzt gespielten Karte des Gegners in der jeweiligen Runde.
		int zuSpielendeKarte = -99;
		int letzteKarteGegner = letzterZug();
		
		//aktualisierung der ArrayList vomGegnerNochNichtGeleget
		if (letzteKarteGegner != -99) {
			vomGegnerNochNichtGelegt.remove(vomGegnerNochNichtGelegt.indexOf(letzteKarteGegner));
		}
		
		//M�glichkeit 1: STICH 
		if (letzteKarteGegner == meineLetzteKarte) {
			zuSpielendeKarte = stich(naechsteKarte);
		}
		
		else
			
		//Zur�cksetzen (erste Runde "initialisieren") der Summe
		summe = 0; 
		
			//M�glichkeit 2: STANDART ZUWEISUNG
			if(letzteKarteGegner != meineLetzteKarte) {
				zuSpielendeKarte = standard(naechsteKarte);
			}
			
				//Sicherheitshalber bei Fehler nimm aus der Mitte
			else {
				zuSpielendeKarte = nochNichtGespielt.get(nochZuGewinnen.size()/2);
			}
		
		
		//aktualisierung der ArrayLists nochZuGewinnenSortiert, nochZuGewinnen, nochNichtGespielt und der Variablen meineLetzteKarte zum Ende der Runde.
		nochZuGewinnenSortiert.remove(nochZuGewinnenSortiert.indexOf(naechsteKarte));
		nochZuGewinnen.remove(nochZuGewinnen.indexOf(naechsteKarte));
		nochNichtGespielt.remove(nochNichtGespielt.indexOf(zuSpielendeKarte));
		meineLetzteKarte = zuSpielendeKarte;
	
	//R�ckgabe des zu spielenden Wertes an nachsterZug() aus HolsDerGeier
	return zuSpielendeKarte;
	}

	/**
	 * Legt fest auf Grund aller vorhandenen Daten, ob um die zu gewinnende Summe gespielt werden soll und verweist auf die zugeh�rige Methode. 
	 * Unterscheidet zwischen mustHave (will ich DEFINITIV haben), noNeed (ben�tige ich NICHT), StandardPosSumme (Summe ist kleiner als die aktuelle h�chste Karte, welche man noch bekommen kann, aber gr��er als 0.), StandartNegSumme (Summe ist kleiner als 0 aber gr��er als die aktuelle kleinste Karte, welche man noch bekommen kann).
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @param summe Wert aller Karten, um welchen diese Runde gespielt wird.
	 * @return ret: Gibt den Wert der Karte, welcher ausgespielt werden soll, an die Methode gibKarte weiter.
	 */
	private int stich(int naechsteKarte) {
		// Initialisierung der Variablen, f�r die zu spielende Karte, und der Aktualisierung der Summe in der jeweiligen Runde.
		int ret = -99;
		summe = (summe + naechsteKarte);
		
		//Summe > h�chste noch verf�gbare Karte *WILL ICH HABEN* verweise auf mustHave()
			if (summe >= nochZuGewinnenSortiert.get(0)) {
				
				ret = mustHave(naechsteKarte);
				
			}
			
		//Summe < niedrigste noch verf�gbare Karte *WILL ICH NICHT HABEN* verweise auf noNeed()
   			else 
   				if (summe <= nochZuGewinnen.get(nochZuGewinnen.size()-1)) {
   			
   				ret = noNeed(naechsteKarte, summe);
   				
   				}
			
			//Summe dennoch >= 0 verweise auf standardPosSumme()
   				else
   					if (summe >= 0) {
   						ret = standardPosSumme(naechsteKarte, summe);
   					}
			
				//(alle Verbleibenden) Summe < 0 verweise auf standardNegSumme()	
   					else 
   						if(summe < 0) {
   							ret = standardNegSumme(naechsteKarte, summe);
   						}
			
		//gibt zu spielenden Wert and gibKarte() zur�ck
		return ret;
	}
	
	/**
	 * Legt fest, welcher Wert standardm��ig f�r den Wert der vorliegenden Karte gespielt werden soll. 
	 * Unterscheidet zwischen einem aktuellen positiven oder negativen Wert.
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @return ret: Gibt den Wert der Karte, welcher ausgespielt werden soll, an die Methode gibKarte weiter.
	 */
	private int standard(int naechsteKarte) {
		// Initialisierung der Variablen, f�r die zu spielende Karte
		int ret = -99;
		
		for (int i=0; i<(nochZuGewinnenSortiert.size()); i++) {
			
			//f�r NEGATIVE "naechsteKarte"
			if (naechsteKarte < 0) {
				
				if(Math.abs(naechsteKarte) == Math.abs(nochZuGewinnenSortiert.get(i))) {
				
					
					if(vomGegnerNochNichtGelegt.contains(vomGegnerNochNichtGelegt.indexOf(nochNichtGespielt.get(i))) == true && i > (nochNichtGespielt.size()/2)) {
						ret = nochNichtGespielt.get(nochNichtGespielt.size()-1);
					}
					else {
						ret = nochNichtGespielt.get(i);
					}
				}
			}
			
			//f�r POSITIVE "naechsteKarte"
			else
				if (naechsteKarte > 0) {
					
					if (naechsteKarte == (nochZuGewinnenSortiert.get(i))) {
						
						if(vomGegnerNochNichtGelegt.contains(vomGegnerNochNichtGelegt.indexOf(nochNichtGespielt.get(i))) == true && i < (nochNichtGespielt.size()/2)) {
							ret = nochNichtGespielt.get(nochNichtGespielt.size()-1);
						}
						else {
							ret = nochNichtGespielt.get(i);
						}
					}
				}			
		}
		
		//gibt Wert f�r zu spielende Karte an gibKarte() zur�ck
		return ret;
	}
	
	/**
	 * Legt fest, welcher Wert in dieser speziellen Situation eines Stiches gelegt werden soll.
	 * Situation: Summe ist kleiner als 0 aber gr��er als die aktuelle kleinste Karte, welche man noch bekommen kann.
	 * Vergleicht die Summe der Karten, um die gespielt wird, mit allen Karten, welche man noch gewinnen kann und legt abh�ngig von der derzeitig offen liegenden Karte einen zu spielenden Wert fest.
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @param summe Wert aller Karten, um welchen diese Runde gespielt wird.
	 * @return ret: Gibt den Wert der Karte, welcher ausgespielt werden soll, an die Methode stich weiter.
	 */
	private int standardNegSumme(int naechsteKarte, int summe) {
		// Initialisierung der Variablen, f�r die zu spielende Karte
		int ret = -99;
		
		//sucht einen passenden Wert f�r die aktuelle Summe aus
		for (int i=0; i<(nochZuGewinnenSortiert.size()); i++) {			
				
				if(Math.abs(summe) <= Math.abs(nochZuGewinnenSortiert.get(i))) {			
					
					//POSITIVE naechsteKarte
					if(naechsteKarte > 0) {
						ret = nochNichtGespielt.get(i);
					}
					
					//NEGATIVE naechsteKarte
					else
						if(naechsteKarte < 0) {
							ret = nochNichtGespielt.get((nochNichtGespielt.size()-1));
						}
				}
		}

		//gibt Wert f�r zu spielende Karte an stich() oder selten auch an noNeed() zur�ck
		return ret;
	}

	/**
	 * Legt fest, welcher Wert in dieser speziellen Situation eines Stiches gelegt werden soll. 
	 * Situation: Summe ist kleiner als die aktuelle h�chste Karte, welche man noch bekommen kann, aber gr��er als 0.
	 * Vergleicht die Summe der Karten, um die gespielt wird, mit allen Karten, welche man noch gewinnen kann und legt abh�ngig von der derzeitig offen liegenden Karte einen zu spielenden Wert fest.
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @param summe Wert aller Karten, um welchen diese Runde gespielt wird.
	 * @return ret: Gibt den Wert der Karte, welcher ausgespielt werden soll, an die Methode stich weiter.
	 */
	private int standardPosSumme(int naechsteKarte, int summe) {
		// Initialisierung der Variablen, f�r die zu spielende Karte
		int ret = -99;
		
		//sucht einen passenden Wert f�r die aktuelle Summe aus
		for (int i=0; i<(nochZuGewinnenSortiert.size()); i++) {
												
			if (summe <= (Math.abs(nochZuGewinnenSortiert.get(i)))) {
				
				//POSITIVE "naechsteKarte"
				if(naechsteKarte > 0) {
					ret = nochNichtGespielt.get(i);
				}
				
				//NEGATIVE "naechsteKarte"
				else
					if(naechsteKarte < 0) {
						ret = nochNichtGespielt.get((nochNichtGespielt.size()-1));
					}
			}			
		}
		//gibt Wert f�r zu spielende Karte an stich() zur�ck
		return ret;
	}

	/**
	 * Legt fest, welcher Wert in dieser speziellen Situation eines Stiches gelegt werden soll.
	 * Situation: aktuelle Summe wird NICHT ben�tigt.
	 * Legt abh�ngig von der derzeit offenliegenden Karte einen zu spielenden Wert fest und entscheidet im Falle einer negativen offenliegenden Karte, die Priorit�t der Summe und den davon abh�ngige Wert, welcher gespielt werden soll oder verweist gegebenenfalls auf eine passende Methode.
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @param summe Wert aller Karten, um welchen diese Runde gespielt wird.
	 * @return ret: Gibt den Wert der Karte, welcher ausgespielt werden soll, an die Methode stich weiter.
	 */
	private int noNeed(int naechsteKarte, int summe) {
		// Initialisierung der Variablen, f�r die zu spielende Karte
		int ret = -99;
		
		//f�r Positive
		if(summe >= 0) {				
			ret = nochNichtGespielt.get(nochNichtGespielt.size()-1);	
		}
			
		//f�r Negativ relevante
		else 
			if(nochZuGewinnen.get(0) < (-summe)) {
				
				//f�r POSITIVE "naechsteKarte"
				if(naechsteKarte > 0) {
					ret = nochNichtGespielt.get(0);
				}
				
				//f�r NEGATIVE "naechsteKarte"
				else 
					if(naechsteKarte < 0) {
						ret = nochNichtGespielt.get(nochNichtGespielt.size()-1);
					}
		}
		
		//f�r Negative irrelevant
		else	
			if(summe < 0) {				
				ret = standardNegSumme(naechsteKarte, summe);					
			}
		//gibt Wert f�r zu spielende Karte an stich() zur�ck	
		return ret;
	}

	/**
	 * Legt fest, welcher Wert in dieser speziellen Situation eines Stiches gelegt werden soll.
	 * Situation: aktuelle Summe wird DEFINITIV ben�tigt.
	 * Gibt abh�ngig von der derzeit offen liegenden Karte an, ob der h�chste oder niedrigste Wert gespielt werden soll, welchen ich auf meiner Hand besitze um diese Summe, um welche diese Runde gespielt wird, zu bekommen.
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @param summe Wert aller Karten, um welchen diese Runde gespielt wird.
	 * @return ret: Gibt den Wert der Karte, welcher ausgespielt werden soll, an die Methode stich weiter.
	 */
	private int mustHave(int naechsteKarte) {
		// Initialisierung der Variablen, f�r die zu spielende Karte
		int ret =-99;
		
		//f�r POSITIVE "naechsteKarte"
		if (naechsteKarte > 0) {
			ret = nochNichtGespielt.get(0);
		}
		
			//f�r NEGATIVE "naechsteKarte"
		else 
			if (naechsteKarte < 0){
				ret = nochNichtGespielt.get(nochNichtGespielt.size()-1);
			}
		
		//gibt Wert f�r zu spielende Karte an stich() zur�ck
		return ret;
	}

}
