package Version5;

import java.util.ArrayList;

/**
 * Ein Virtueller Spieler, welcher eine Zuordnung und Auswahl der zu spielenden Werte, abhängig von den Summen und Werten um welche Rundenbezogen gespielt wird, festlegt und an die Methode naechsterZug() aus der Klasse HolsDerGeier zurück gibt.
 * Spezialisierung der abstrakten Klasse HolsDerGeierSpieler
 * @author Jan Rohrbach 
 */
	//???	@return zuSpielendeKarte: Gibt den Wert der Karte zurück, welche ich als nächstes spielen möchte.
public class Strategy1 extends HolsDerGeierSpieler{

	/**
	 * Karten auf dem Stapel von 10 nach -5.
	 */
	private ArrayList<Integer> nochZuGewinnen=new ArrayList<Integer>();
	/**
	 * Karten auf dem Stapel sortiert nach persönlichem Nutzen.
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
     *Ungenutzt und bislang nicht benötigt. 
     */
    public Strategy1() {
	}
	
    /**
     * Leert und füllt alle vorhandenen ArrayLists. 
     */
	public void reset() {
		
		//leert und füllt ArrayList nochZuGewinnen von 10 bis -5
		nochZuGewinnen.clear();
        for (int i=10;i>-6;i--)
        	if (i!=0) {
        		nochZuGewinnen.add(i);
        	}
        
        //leert und füllt ArrayList nochZuGewinnenSortiert in bestimmter Reihenfolge von 15 bis -1
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
        
        //leert und füllt nochNichtGespielt von 15 bis 1
        nochNichtGespielt.clear();
        for (int i=15;i>0;i--) {           
            nochNichtGespielt.add(i);     
        }
        
        //leert und füllt vomGegnerNochNichtGelegt von 15 bis 1
        vomGegnerNochNichtGelegt.clear();        
        for (int i=15;i>0;i--) {
            vomGegnerNochNichtGelegt.add(i);
        }
	}

	/**
	 * Legt fest, welche Spielsituation vorliegt und verweist auf die passende Methode.
	 * Unterscheidet zwischen einem Stich oder einer standart Situation (kein Stich).
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @return zuSpielendeKarte: Gibt den Wert der Karte zurück, welche ich als nächstes spielen möchte.
	 */
	public int gibKarte(int naechsteKarte) {
		 
		// Initialisierung der Variablen, für die zu spielende Karte, und der Aktualisierung der zuletzt gespielten Karte des Gegners in der jeweiligen Runde.
		int zuSpielendeKarte = -99;
		int letzteKarteGegner = letzterZug();
		
		//aktualisierung der ArrayList vomGegnerNochNichtGeleget
		if (letzteKarteGegner != -99) {
			vomGegnerNochNichtGelegt.remove(vomGegnerNochNichtGelegt.indexOf(letzteKarteGegner));
		}
		
		//Möglichkeit 1: STICH 
		if (letzteKarteGegner == meineLetzteKarte) {
			zuSpielendeKarte = stich(naechsteKarte);
		}
		
		else
			
		//Zurücksetzen (erste Runde "initialisieren") der Summe
		summe = 0; 
		
			//Möglichkeit 2: STANDART ZUWEISUNG
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
	
	//Rückgabe des zu spielenden Wertes an nachsterZug() aus HolsDerGeier
	return zuSpielendeKarte;
	}

	/**
	 * Legt fest auf Grund aller vorhandenen Daten, ob um die zu gewinnende Summe gespielt werden soll und verweist auf die zugehörige Methode. 
	 * Unterscheidet zwischen mustHave (will ich DEFINITIV haben), noNeed (benötige ich NICHT), StandardPosSumme (Summe ist kleiner als die aktuelle höchste Karte, welche man noch bekommen kann, aber größer als 0.), StandartNegSumme (Summe ist kleiner als 0 aber größer als die aktuelle kleinste Karte, welche man noch bekommen kann).
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @param summe Wert aller Karten, um welchen diese Runde gespielt wird.
	 * @return ret: Gibt den Wert der Karte, welcher ausgespielt werden soll, an die Methode gibKarte weiter.
	 */
	private int stich(int naechsteKarte) {
		// Initialisierung der Variablen, für die zu spielende Karte, und der Aktualisierung der Summe in der jeweiligen Runde.
		int ret = -99;
		summe = (summe + naechsteKarte);
		
		//Summe > höchste noch verfügbare Karte *WILL ICH HABEN* verweise auf mustHave()
			if (summe >= nochZuGewinnenSortiert.get(0)) {
				
				ret = mustHave(naechsteKarte);
				
			}
			
		//Summe < niedrigste noch verfügbare Karte *WILL ICH NICHT HABEN* verweise auf noNeed()
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
			
		//gibt zu spielenden Wert and gibKarte() zurück
		return ret;
	}
	
	/**
	 * Legt fest, welcher Wert standardmäßig für den Wert der vorliegenden Karte gespielt werden soll. 
	 * Unterscheidet zwischen einem aktuellen positiven oder negativen Wert.
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @return ret: Gibt den Wert der Karte, welcher ausgespielt werden soll, an die Methode gibKarte weiter.
	 */
	private int standard(int naechsteKarte) {
		// Initialisierung der Variablen, für die zu spielende Karte
		int ret = -99;
		
		for (int i=0; i<(nochZuGewinnenSortiert.size()); i++) {
			
			//für NEGATIVE "naechsteKarte"
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
			
			//für POSITIVE "naechsteKarte"
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
		
		//gibt Wert für zu spielende Karte an gibKarte() zurück
		return ret;
	}
	
	/**
	 * Legt fest, welcher Wert in dieser speziellen Situation eines Stiches gelegt werden soll.
	 * Situation: Summe ist kleiner als 0 aber größer als die aktuelle kleinste Karte, welche man noch bekommen kann.
	 * Vergleicht die Summe der Karten, um die gespielt wird, mit allen Karten, welche man noch gewinnen kann und legt abhängig von der derzeitig offen liegenden Karte einen zu spielenden Wert fest.
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @param summe Wert aller Karten, um welchen diese Runde gespielt wird.
	 * @return ret: Gibt den Wert der Karte, welcher ausgespielt werden soll, an die Methode stich weiter.
	 */
	private int standardNegSumme(int naechsteKarte, int summe) {
		// Initialisierung der Variablen, für die zu spielende Karte
		int ret = -99;
		
		//sucht einen passenden Wert für die aktuelle Summe aus
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

		//gibt Wert für zu spielende Karte an stich() oder selten auch an noNeed() zurück
		return ret;
	}

	/**
	 * Legt fest, welcher Wert in dieser speziellen Situation eines Stiches gelegt werden soll. 
	 * Situation: Summe ist kleiner als die aktuelle höchste Karte, welche man noch bekommen kann, aber größer als 0.
	 * Vergleicht die Summe der Karten, um die gespielt wird, mit allen Karten, welche man noch gewinnen kann und legt abhängig von der derzeitig offen liegenden Karte einen zu spielenden Wert fest.
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @param summe Wert aller Karten, um welchen diese Runde gespielt wird.
	 * @return ret: Gibt den Wert der Karte, welcher ausgespielt werden soll, an die Methode stich weiter.
	 */
	private int standardPosSumme(int naechsteKarte, int summe) {
		// Initialisierung der Variablen, für die zu spielende Karte
		int ret = -99;
		
		//sucht einen passenden Wert für die aktuelle Summe aus
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
		//gibt Wert für zu spielende Karte an stich() zurück
		return ret;
	}

	/**
	 * Legt fest, welcher Wert in dieser speziellen Situation eines Stiches gelegt werden soll.
	 * Situation: aktuelle Summe wird NICHT benötigt.
	 * Legt abhängig von der derzeit offenliegenden Karte einen zu spielenden Wert fest und entscheidet im Falle einer negativen offenliegenden Karte, die Priorität der Summe und den davon abhängige Wert, welcher gespielt werden soll oder verweist gegebenenfalls auf eine passende Methode.
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @param summe Wert aller Karten, um welchen diese Runde gespielt wird.
	 * @return ret: Gibt den Wert der Karte, welcher ausgespielt werden soll, an die Methode stich weiter.
	 */
	private int noNeed(int naechsteKarte, int summe) {
		// Initialisierung der Variablen, für die zu spielende Karte
		int ret = -99;
		
		//für Positive
		if(summe >= 0) {				
			ret = nochNichtGespielt.get(nochNichtGespielt.size()-1);	
		}
			
		//für Negativ relevante
		else 
			if(nochZuGewinnen.get(0) < (-summe)) {
				
				//für POSITIVE "naechsteKarte"
				if(naechsteKarte > 0) {
					ret = nochNichtGespielt.get(0);
				}
				
				//für NEGATIVE "naechsteKarte"
				else 
					if(naechsteKarte < 0) {
						ret = nochNichtGespielt.get(nochNichtGespielt.size()-1);
					}
		}
		
		//für Negative irrelevant
		else	
			if(summe < 0) {				
				ret = standardNegSumme(naechsteKarte, summe);					
			}
		//gibt Wert für zu spielende Karte an stich() zurück	
		return ret;
	}

	/**
	 * Legt fest, welcher Wert in dieser speziellen Situation eines Stiches gelegt werden soll.
	 * Situation: aktuelle Summe wird DEFINITIV benötigt.
	 * Gibt abhängig von der derzeit offen liegenden Karte an, ob der höchste oder niedrigste Wert gespielt werden soll, welchen ich auf meiner Hand besitze um diese Summe, um welche diese Runde gespielt wird, zu bekommen.
	 * @param naechsteKarte Wert der Karte, welche diese Runde aufgedeckt wurde.
	 * @param summe Wert aller Karten, um welchen diese Runde gespielt wird.
	 * @return ret: Gibt den Wert der Karte, welcher ausgespielt werden soll, an die Methode stich weiter.
	 */
	private int mustHave(int naechsteKarte) {
		// Initialisierung der Variablen, für die zu spielende Karte
		int ret =-99;
		
		//für POSITIVE "naechsteKarte"
		if (naechsteKarte > 0) {
			ret = nochNichtGespielt.get(0);
		}
		
			//für NEGATIVE "naechsteKarte"
		else 
			if (naechsteKarte < 0){
				ret = nochNichtGespielt.get(nochNichtGespielt.size()-1);
			}
		
		//gibt Wert für zu spielende Karte an stich() zurück
		return ret;
	}

}
