import java.util.ArrayList;
import java.util.Comparator;

public class Greedy {
    private final Problema problema;
    public Greedy(Problema problema) { this.problema = problema; }

    public Solucion ejecutar() {
        int n = problema.getDimension();
        ArrayList<ParCiudadDistancia> vector = new ArrayList<>(n);
        // Se calcula la suma de distancias sin guardar la matriz completa en memoria.
        double[] sumas = problema.getSumasDistancias();
        for (int i = 0; i < n; i++) {
            vector.add(new ParCiudadDistancia(problema.getCiudadPorIndice(i).getId(), sumas[i]));
        }
        vector.sort(Comparator.comparingDouble(ParCiudadDistancia::getDistancia));
        ArrayList<Integer> recorrido = new ArrayList<>(n);
        for (ParCiudadDistancia p : vector) recorrido.add(p.ciudad);
        return new Solucion(recorrido, calcularCoste(problema, recorrido));
    }

    static double calcularCoste(Problema problema, ArrayList<Integer> recorrido) {
        double coste = 0;
        for (int i = 0; i < recorrido.size(); i++) {
            int actual = recorrido.get(i), siguiente = recorrido.get((i + 1) % recorrido.size());
            coste += problema.distanciaCiudades(actual, siguiente);
        }
        return coste;
    }
    private static class ParCiudadDistancia {
        private final int ciudad; private final double distancia;
        private ParCiudadDistancia(int ciudad, double distancia) { this.ciudad = ciudad; this.distancia = distancia; }
        private int getCiudad() { return ciudad; }
        private double getDistancia() { return distancia; }
    }
}
