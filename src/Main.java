import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;

/**
 * Hauptklasse, die die anderen Klassen aufruft.
 * Dieses Programm erstellt für eine Eingabedatei mit Ländern, deren Kennwerten und Beziehungen zueinander eine Ausgabedatei.
 * Die Aufgabe des Programms ist es, eine Landkarte zu erstellen, die sowohl die Beziehungen und Positionen der Länder so gut wie möglich darstellt,
 * sowie die Größe des Landes als einen Kreis entsprechend der Größe des Kennwerts darstellt.
 * Die Ausgabedatei enthält Kommandos, die mit dem Programm gnuplot ausgeführt werden können und dann die entsprechende Karte ausgeben.
 */
public class Main {


    /**
     * Main Methode, wird beim Ausführen des Programms aufgerufen und ruft dann die Klassen Einlesen, Algorithmus und Ausgabe der Reihe nach auf.
     *
     * @param args Dateiname und Pfad der input Datei
     */
    public static void main(String args[]) {
        boolean fehler = false; //Boolean, wird true, wenn ein Fehler aufgetreten ist
        Einlesen einlesen = new Einlesen(); //Instanziere andere Klassen
        Algorithmus algorithmus = new Algorithmus();
        Ausgabe ausgabe = new Ausgabe();
        String datei = "Beispiel1";
        String pfad = "Beispiele/";   //Defaultpfad ist src/ kann als übergabeparameter angegeben werden
        switch (args.length) {
            case 2: pfad = args[1];
            case 1: datei = args[0];
        }
        String outputDatei = datei + ".out"; //Outputdatei ist per default inputname.out, kann als übergabeparameter angegeben werden

        ArrayList<Land> laender = new ArrayList<>();
        //Setze Werte für Errorfälle, je nach Exception variiert die errorMessage und der ExitCode
        String errorMessage = "";
        String text = "";
        int exitCode = 0; //ExitCode=0 heißt Programm war Erfolgreich, ExitCode>0 heißt erwarteter Fehler und ExitCode<0 heißt unerwarteter Fehler
        try {
            //Versuche Datei auszulesen, dafür wird Klasse Einlesen genutzt. Exceptions werden hier behandelt und in die Ausgabedatei geschrieben
            laender = einlesen.leseDatei(datei, pfad);
        } catch (NumberFormatException numberFormat) {  //Beim Parsen der Werte in Double oder int ist ein Fehler aufgetreten
            text = "Datei konnte nicht eingelesen werden, Ungültiges Format beim Parsen:";
            errorMessage = numberFormat.getMessage();
            exitCode = 1; //erwarteter Fehler
            fehler = true;
        } catch (InvalidPropertiesFormatException format) {  //Beim Einlesen ist ein Fehler aufgetreten, der in den Äquivalenzklassen beschrieben wurde
            text = "Datei konnte nicht eingelesen werden:";
            errorMessage = format.getMessage();
            exitCode = 1; //erwarteter Fehler
            fehler = true;
        } catch (Exception e) { //Fange alle sonstigen Fehler
            text = "Unerwarteter Fehler ist aufgetreten beim Einlesen der Datei: ";
            errorMessage = e.getMessage();
            exitCode = -1; //unerwarteter Fehler
            fehler = true;
        }
        if (fehler) {
            try {
                ausgabe.ausgabeFehler(text, errorMessage, outputDatei, pfad);
                System.exit(exitCode);
            } catch (Exception ex) {
                text = "Fehler beim Schreiben in Ausgabedatei, überprüfe ob Programm Dateien erstellen darf. Fehlermeldung: ";
                System.out.println(text + ex.getMessage());
                System.exit(1);
            }
        }
        //Rufe Hauptalgorithmus auf, erhalte Anzahl der Iterationen, die benötigt wurden
        int iterationen = algorithmus.calculate(laender);

        //Gebe Ergebnisse aus
        try {
            File f = ausgabe.ausgabe(laender, outputDatei, pfad, iterationen, einlesen.getName());
        } catch (IOException io) { //Falls Fehler in Ausgabedatei entsteht, gebe es in der Kommandozeile aus
            text = "Fehler beim Schreiben in Ausgabedatei, überprüfe ob Programm Dateien erstellen darf";
            System.out.println(text + io.getMessage());
            System.exit(1);
        } catch (Exception e) {
            text = "Unerwarteter Fehler ist aufgetreten beim Ausgeben der Datei: ";
            errorMessage = e.getMessage();
            System.out.println(text + errorMessage);
            System.exit(-1);    //unerwarteter Fehler

        }
    }

}
