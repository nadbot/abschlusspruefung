import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Klasse des Hauptalgorithmus,
 * welcher per Iterationsverfahren die Punkte der Länder entsprechend der daraufwirkenden Kräfte verschiebt,
 * um überlappende Kreise zu vermeiden und Abstände zwischen benachbarten Ländern zu minimieren
 *
 * @author ehamada
 */
public class Algorithmus {
    /**
     * Attribute:
     *
     * matrix Matrix der Kräfte, die von jedem Land auf jedes andere wirken
     * epsilon Doublewert, der die Veränderung zwischen zwei Iterationen berechnet
     * altePunkte Liste der Länder der vorherigen Iteration, bei der ersten entsprechen Koordinaten denen der Startländer
     * abbruchSchwelle Wert, wenn epsilon den Wert unterschreitet, wird das Iterationsverfahren beendet
     */
    private Double[][] matrix;
    private double epsilon;
    private ArrayList<Land> altePunkte = new ArrayList<>();
    private double abbruchSchwelle = 0.0005;

    /**
     * Methode zum initialen Verschieben der Punkte, bevor die Kräfte berechnet werdene
     */
    private void vorIteration(ArrayList<Land> laender, double value) {
        double rMin = Double.MAX_VALUE;
        for(Land land: laender) {
            land.x = land.x * 10;
            land.y = land.y * 10;
            rMin = Double.min(rMin,land.radius);
        }
        for(Land land:laender){
            land.radius=3*land.radius/rMin;
        }

    }

    /**
     * Hauptmethode zum berechnen der neuen Koordinaten der Länder
     *
     * @param laender Liste der Länder, die angepasst werden sollen
     * @return Gibt Anzahl der Iterationen zurück, die benötigt wurden, bis das Iterationsverfahren die gewünschte Genauigkeit erreicht hat
     */
    public int calculate(ArrayList<Land> laender) {
        //Wert, um wie viel jedes Land sich bewegt
        // (in Aufgabenstellung wurde 0.5 empfohlen, allerdings gehen die Kreise damit zu weit auseinander)
//        double value = 0.01;
        double value = 0.005;
        this.matrix = new Double[laender.size()][laender.size()];   //Initialisieren der Matrix
        int iterationen = 0;

        for (int i = 0; i < laender.size(); i++) {
            //Fülle Matrix mit Nullen, falls 2 Länder später keine Beziehung haben, würde der Zugriff auf das Element sonst null zurückgeben
            Arrays.fill(matrix[i], 0.);
            Land l = laender.get(i);
            this.altePunkte.add(new Land(l.x, l.y, l.radius, l.name));  //Fülle die Liste der alten Punkte
        }
        //Abbruchbedingung: Genauigkeit erreicht oder mehr als x Iterationen.
        // Genauigkeit alleine reicht nicht, da 2 Kräfte theoretisch den Punkt abwechselnd hin und her schieben könnten, was zu einer Endlosschleife führt
        vorIteration(laender, value); //wende Voriterationen an, müste normalerweise in Schleife geschehen, allerdings sollte es nur einmal angewendet werden
        do{
            for (int i = 0; i < laender.size(); i++) {
                Land land = laender.get(i);
                for (int j = 0; j < laender.size(); j++) {
                    Land land2 = laender.get(j);
                    //gehe für jedes Land durch alle anderen Länder
                    if (!land2.name.equals(land.name)) {    //Falls beide Länder identisch, gehe zum nächsten Element
                        //Berechne Kraft mit der Formel (Distanz von A und B) - (Summe der Radii)

                        double kraft = (Math.sqrt(Math.pow(land2.x - land.x, 2) + Math.pow(land2.y - land.y, 2))) - (land2.radius + land.radius);
                        if (kraft < 0) {
                            //Wenn Kraft < 0 handelt es sich um Abstoßungskräfte, da Kreise sich überlappen
                            matrix[i][j] = kraft;
                        } else {
                            //Sonst handelt es sich um Anziehungskräfte
                            //Diese werden nur angewendet, wenn die beiden Länder Nachbarn sind
                            if (land.nachbarn.contains(land2)) {
                                matrix[i][j] = kraft;
                            }
                        }
                    }
                }
            }
            //Alle Kräfte für alle Länder sind jetzt berechnet, also wende sie der Reihe nach an
            for (int i = 0; i < laender.size(); i++) {
                Land land = laender.get(i);
                for (int j = 0; j < laender.size(); j++) {
                    Land land2 = laender.get(j);
                    //Iteriere wieder für jedes Land über alle anderen Länder
                    if (!land.name.equals(land2.name)) { //Wenn Länder gleich, gehe zum nächsten Element
                        double d = Math.sqrt(Math.pow(land2.x - land.x, 2) + Math.pow(land2.y - land.y, 2)); //berechne Abstand der beiden Punkte
                        //Verändere x und y Koordinaten des Landes
                        //Dazu addiert man zur jeweiligen Koordinate (die Kraft (matrix[i][j])) * (dem Faktor(value)) * (der Koordinate des Vektors von einem zum anderen Land) / (die Distanz der beiden Punkte)
                        land.x = land.x + (matrix[i][j] * value / d * (land2.x - land.x));
                        land.y = land.y + (matrix[i][j] * value / d * (land2.y - land.y));
                    }
                }
            }
            //Berechne daraus resultierende Veränderung zu vorherigen Daten
            epsilon = 0;
            for (int i = 0; i < laender.size(); i++) {
                Land l = laender.get(i);
                Land alt = this.altePunkte.get(i);
                epsilon += Math.sqrt(Math.pow(l.x - alt.x, 2) + Math.pow(l.y - alt.y, 2));
                //setze die Punkte der alten Länder auf die neuen
                alt.x = l.x;
                alt.y = l.y;
            }
            iterationen += 1;
            if(iterationen%10000==0){
                System.out.println(iterationen);
            }
        } while (epsilon > this.abbruchSchwelle && iterationen < 100000);

        return iterationen; //gebe Anzahl Durchläufe zurück
    }

}
