import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;

/**
 * Klasse zum Einlesen der Dateien. Sie wird von der Main Klasse aufgerufen und liest dann ein syntaktisch korrektes File ein, das unter Pfad + Dateiname zu finden ist.
 * Anschließend werden die Informationen in entsprechende Datentypen gespeichert.
 *
 * @author ehamada
 */
public class Einlesen {
    /**
     * Attribute:
     *
     * @name Name des Kennwerts
     * @laender ArrayList mit den Ländern
     */
    private String name;
    private ArrayList<Land> laender;

    /**
     * Methode zum Einlesen einer Datei. Die Datei muss das folgende Format haben.
     * Die Abstände zwischen den Werten können dabei ein oder mehrere Leerzeichen oder ein Tab sein
     * <code>
     * Fläche der Staaten <br>
     * # Staat Fläche Längengrad Breitengrad <br>
     * D    357    10.0    51.3 <br>
     * NL   42      5.3     52.2 <br>
     * B    33      4.8     50.7 <br>
     * L    3       6.1     49.8 <br>
     * F    544     2.8     47.4 <br>
     * CH   41      8.2     46.9 <br>
     * A    84      14.2    47.6 <br>
     * CZ   79      15.3    49.8 <br>
     * PL   313     18.9    52.2 <br>
     * DK   43      9.6     56.0 <br>
     * # Nachbarschaften <br>
     * D:  NL  B   L   F   CH  A  CZ   PL  DK <br>
     * NL: B <br>
     * B:  L   F <br>
     * L:  F <br>
     * F:  CH <br>
     * CH: A <br>
     * A:  CZ <br>
     * CZ: PL <br>
     * </code>
     *
     * @param datei Dateiname der Datei, die ausgelesen werden soll
     * @param pfad  Pfad zu der Datei
     * @return Returns ArrayList von Land, die die einzelnen ausgelesenen Länder enthält
     * @throws Exception Falls Fehler beim Lesen auftreten, werden diese an die aufrufende Klasse weitergeleitet und da behandelt.
     */
    public ArrayList<Land> leseDatei(String datei, String pfad) throws Exception {
        File file = new File(pfad + datei);

        BufferedReader br = new BufferedReader(new FileReader(file));

        this.laender = new ArrayList<>();
        String line;
        int i = 0;

        while ((line = br.readLine()) != null) {
            if (line.contains("#")) {
                //Falls Zeile ein # enthält, also ein Kommentar enthält (nicht zwangsläufig am Anfang der Zeile),
                // wird alles ab dem # abgeschnitten, der Kommentar also ignoriert.
                line = line.split("#")[0];
            }
            if (line.length() != 0) {   //Ignoriere leere Zeilen
                if (i == 0) {
                    // suche erste richtige Zeile -> Name
                    this.name = line;
                } else if (line.contains(":")) {    //Falls Zeile einen Doppelpunkt enthält, handelt es sich um die Beziehungen
                    String[] split = line.split(":");   //Teile am Doppelpunkt, erster Teil = Land, zweiter Teil = Liste von Nachbarn vom Land
                    String name = split[0];

                    if(split.length!=2){
                        throw new InvalidPropertiesFormatException("Eine Seite der Beziehung ist leer");
                    }
                    String[] nachbarn = split[1].trim().split("( |\t)+");   //Teile Liste von Nachbarn an Leerzeichen oder Tabs
                    Land erstesLand = new Land(-1, -1, -1, "False"); //Erstelle Dummy Land, wird später überschrieben
                    for (Land land : this.laender) {
                        if (land.name.equals(name)) {   //Iteriere über alle Länder und suche Land, welches dem ersten Element entspricht
                            erstesLand = land;
                        }
                    }
                    if(!this.laender.contains(erstesLand)){
                        throw new InvalidPropertiesFormatException("Land mit dem Namen "+name+" existiert nicht");
                    }
                    for (int j = 0; j < nachbarn.length; j++) {     //Iteriere über Liste von Nachbarn
                        for (Land land : this.laender) {            //Iteriere für jeden Nachbarn über Liste von Ländern
                            if (land.name.equals(nachbarn[j])) {
                                if(land.nachbarn.contains(erstesLand)){  //überspringe doppelte Länder
                                    continue;
                                }
                                //Wenn richtiges Element gefunden, füge Nachbar als Nachbar vom erstes Land hinzu
                                // und das erste Element als Nachbar vom Nachbarn (um bidirektional zu sein)
                                erstesLand.nachbarn.add(land);
                                land.nachbarn.add(erstesLand);
                            } //Falls Beziehung vorhanden, aber Land nicht existiert, wird Land nicht als Beziehung hinzugefügt
                        }
                    }

                } else {
                    //Zeile enthält keinen Doppelpunkt
                    String[] split = line.split("( |\t)+"); //Splitte an Leerzeichen oder Tabs
                    String name = split[0];
                    //Versuche Werte zu parsen, wenn Fehler geworfen wird, behandle ihn in Main
                    //Wenn nicht parseble wird eine NumberFormatException geworfen
                    //Wenn nicht vorhanden wird eine NullpointerException geworfen (auch wenn Wert vorhanden sein muss, da File syntaktisch korrekt)
                    int kennwert = Integer.parseInt(split[1]);
                    if(kennwert<=0){
                        throw new NumberFormatException("Fläche des Kreises kann nicht <=0 sein");
                    }
                    double laenge = Double.parseDouble(split[2]);
                    double breite = Double.parseDouble(split[3]);
                    if(Math.abs(laenge)>Double.MAX_VALUE || Math.abs(breite)>Double.MAX_VALUE ){
                        throw new NumberFormatException("Wert ist zu groß/ zu klein für Double");
                    }
                    double radius = berechneRadius(kennwert);   //Rufe Methode berechne Radius auf
                    Land land = new Land(laenge, breite, radius, name); //Erstelle neues Land mit den Werten und
                    //Dieser Teil wird immer vor der else if aufgerufen, daher wird hier nachbarn initialisiert
                    land.nachbarn = new ArrayList<>();
                    boolean exists = false;
                    for(Land doppelt: this.laender){    //prüfe ob Land schon existiert
                        if(doppelt.name.equals(name)){
                            exists = true;
                        }else if(doppelt.x == laenge && doppelt.y == breite){
                            throw new InvalidPropertiesFormatException("2 Länder haben dieselbe Lage, aber unterschiedliche Namen");
                        }
                    }
                    if(!exists) {
                        this.laender.add(land);     //Füge Land an Liste an.
                    }
                }
                i++; //Wird benötigt um erste Zeile zu finden
            }

        }
        if(i==0){   //keine Zeile gefunden
            throw new Exception("Eingabedatei ist leer");
        }

        return this.laender;
    }

    /**
     * Methode zum berechnen des Radius. Dazu gilt die Formel
     * Kennwert = r^2*Math.PI <=> r= Math.sqrt(Kennwert/Math.PI)
     *
     * @param wert Kennwert, der aus Eingabedatei ausgelesen wurde
     * @return Returns berechneten radius
     */
    private double berechneRadius(int wert) {
        return Math.sqrt(wert / Math.PI);
    }


    /**
     * Getter für den Namen des Kennwertes. Wird für Ausgabedatei benötigt.
     *
     * @return Name des Kennwertes
     */
    public String getName() {
        return this.name;
    }
}
