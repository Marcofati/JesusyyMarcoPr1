import java.util.ArrayList;

public class Solucion {

    private ArrayList<Integer> recorrido;
    private double coste;

    public Solucion() {
        recorrido = new ArrayList<>();
        coste = 0;
    }

    public Solucion(ArrayList<Integer> recorrido, double coste) {
        this.recorrido = recorrido;
        this.coste = coste;
    }

    public ArrayList<Integer> getRecorrido() {
        return recorrido;
    }

    public double getCoste() {
        return coste;
    }

    public void setRecorrido(ArrayList<Integer> recorrido) {
        this.recorrido = recorrido;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }

    @Override
    public String toString() {
        return "Recorrido: " + recorrido + "\nCoste: " + coste;
    }
}