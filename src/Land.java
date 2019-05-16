import java.util.ArrayList;

/**
 * Einfache Hilfsklasse zum Darstellen eines Landes. Da es eine Hilfsklasse ist, sind alle Attribute public.
 *
 * @author ehamada
 * <p>
 * Attribute <br>
 * x - x Koordinate des Mittelpunktes/ Schwerpunktes des Landes <br>
 * y - y Koordinate des Mittelpunktes/ Schwerpunktes des Landes <br>
 * radius - Radius des Kreises des Landes <br>
 * name - Name des Landes (Autokennzeichen), wird zur Identifikation genutzt <br>
 * nachbarn - ArrayList von Nachbarländern
 */
public class Land {
    public double x;
    public double y;
    public double radius;
    public String name;
    public ArrayList<Land> nachbarn;

    /**
     * Konstruktor der Klasse, erstellt ein neues Objekt der Klasse mit den übergebenen Werten
     *
     * @param x      x Koordinate des Mittelpunktes/ Schwerpunktes des Landes
     * @param y      y Koordinate des Mittelpunktes/ Schwerpunktes des Landes
     * @param radius Radius des Kreises des Landes
     * @param name   Name des Landes (Autokennzeichen), wird zur Identifikation genutzt
     */
    public Land(double x, double y, double radius, String name) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.name = name;
    }
}
