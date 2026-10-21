import java.util.ArrayList;

public class Solucion {
    private ArrayList<Integer> recorrido;
    private double coste;
    public Solucion() { recorrido = new ArrayList<>(); coste = 0; }
    public Solucion(ArrayList<Integer> recorrido, double coste) {
        this.recorrido = new ArrayList<>(recorrido); this.coste = coste;
    }
    public ArrayList<Integer> getRecorrido() { return new ArrayList<>(recorrido); }
    public double getCoste() { return coste; }
    public void setRecorrido(ArrayList<Integer> recorrido) { this.recorrido = new ArrayList<>(recorrido); }
    public void setCoste(double coste) { this.coste = coste; }
    public Solucion copia() { return new Solucion(recorrido, coste); }
    @Override public String toString() { return "Recorrido: " + recorrido + "\nCoste: " + coste; }
}
