import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Klasse zum Ausgeben der der Ergebnisse in einem Format, das man mit <a href="http://www.gnuplot.info">gnuplot</a> plotten kann.
 *
 * @author ehamada
 */
public class Ausgabe {
    /**
     * Attribute:
     *
     * @laender ArrayList der Länder, die ausgegeben werden sollen
     * @xmax Maximaler X Wert aller Kreise (Mittelwert des Kreises + Radius), wird zur Skalierung benötigt
     * @xmin Minimaler X Wert aller Kreise (Mittelwert des Kreises - Radius), wird zur Skalierung benötigt
     * @ymax Maximaler Y Wert aller Kreise (Mittelwert des Kreises + Radius), wird zur Skalierung benötigt
     * @ymin Minimaler Y Wert aller Kreise (Mittelwert des Kreises - Radius), wird zur Skalierung benötigt
     */
    private ArrayList<Land> laender;
    private double xmax = -Double.MAX_VALUE;
    private double xmin = Double.MAX_VALUE;
    private double ymax = -Double.MAX_VALUE;
    private double ymin = Double.MAX_VALUE;

    /**
     * Private Methode zum Skalieren der Ausgabe, damit Feld quadratisch ist. Wird nur von der Methode ausgabe aufgerufen.
     * Dafür wird für jedes Land der maximale und minimale Wert in x und y Richtung des Kreises genommen, um die Extremwerte zu erhalten.
     * Danach wird das Maximum von (xmax-xmin) und (ymax-ymin) genommen und der kleinere Wert auf die gleiche Größe gesetzt.
     */
    private void skaliere() {
        for (Land land : this.laender) {
            xmin = Double.min(xmin, land.x - land.radius);
            xmax = Double.max(xmax, land.x + land.radius);
            ymin = Double.min(ymin, land.y - land.radius);
            ymax = Double.max(ymax, land.y + land.radius);
        }
        double a = Double.max(xmax - xmin, ymax - ymin);
        xmax = xmin + a;
        ymax = ymin + a;
    }

    /**
     * Methode zum Ausgeben in eine Datei. Dazu werden die übergebenen Werte in ein Template eingesetzt und dann in die Datei geschrieben.
     * Das Template entspricht der allgemeinen Form beschrieben auf Seite 4 der Aufgabenstellung der IHK.
     * Mithilfe des Templates kann die resultierende Datei mit gnuplot geplottet werden
     * um durch Kreise verschiedener Größe die Kennwerte der einzelnen Länder graphisch darzustellen.
     * Die Position der Kreise stellt dabei die Positions- und Lagebeziehungen der Länder möglichst gut dar.
     *
     * @param laender     Arraylist der Länder, die dargestellt werden sollen
     * @param datei       String des Dateinamens, unter dem die Ausgabedatei erstellt werden soll.
     * @param pfad        Pfad unter dem die Ausgabedatei gespeichert wird
     * @param iterationen Anzahl der Iterationen, die vom Hauptalgorithmus benötigt wurden. Wird im Titel des Plots angegeben
     * @param name        Name des Kennwerts. Wird im Titel angegeben.
     * @return Returns erstelltes File
     * @throws Exception Wirft Exception, falls Fehler beim Schreiben in Datei entsteht.
     */
    public File ausgabe(ArrayList<Land> laender, String datei, String pfad, int iterationen, String name) throws Exception {
        this.laender = laender;
        skaliere(); // skaliere Range der Werte
        FileWriter fileWriter = new FileWriter(pfad + datei);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf("reset\nset xrange [%.15f:%.15f]\n", xmin, xmax);
        printWriter.printf("set yrange [%.15f:%.15f]\n", ymin, ymax);
        printWriter.printf("set size ratio 1.0\n");
        printWriter.printf("set title \"%s, Iteration: %d\"\n", name, iterationen);
        printWriter.printf("unset xtics\nunset ytics\n$data << EOD\n");
        for (Land land : this.laender) { //Schreibe für jedes Land die Positionen, den Radius, den Namen (Autokennzeichen) als Label und einer ID für die Farbe in die Datei
            printWriter.printf("%f %f %f %s %d\n", land.x, land.y, land.radius, land.name, this.laender.indexOf(land));
        }
        printWriter.printf("EOD\nplot \\\n'$data' using 1:2:3:5 with circles lc var notitle, \\\n'$data' using 1:2:4:5 with labels font \"arial,9\" tc variable notitle");
        printWriter.close();
        return new File(pfad + datei);
    }

    /**
     * Methode zum Ausgeben eines Fehlers. Wenn beim Einlesen oder im Hauptalgorithmus ein Fehler entsteht, wird er in die Ausgabedatei geschrieben.
     * Falls beim Ausgeben ein Fehler entsteht,
     *
     * @param text   Text, der als Hilfestellung dient, enthält Informationen zur Klasse, die das Problem hat und zur Art des Fehlers
     * @param fehler Details zum Fehler, entspricht exception.getMessage()
     * @param datei  String des Dateinamens, unter dem die Ausgabedatei erstellt werden soll
     * @param pfad   Pfad unter dem die Ausgabedatei gespeichert wird
     * @return Returns erstelltes File
     * @throws Exception Wirft Exception, falls Fehler beim Schreiben in Datei entsteht.
     */
    public File ausgabeFehler(String text, String fehler, String datei, String pfad) throws Exception {
        FileWriter fileWriter = new FileWriter(pfad + datei);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf("Fehler in Datei:\n%s %s", text, fehler);
        printWriter.close();
        return new File(pfad + datei);

    }
}
