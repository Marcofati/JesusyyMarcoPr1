import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

/** Greedy aleatorizado: selecciona aleatoriamente una de las K ciudades más prometedoras. */
public class GreedyAleatorizado {
    private final Problema problema;
    private final Random random;
    private final int k;

    /** Constructor compatible con el valor K=5 del enunciado. */
    public GreedyAleatorizado(Problema problema, long semilla) { this(problema, semilla, 5); }

    public GreedyAleatorizado(Problema problema, long semilla, int k) {
        if (k < 1) throw new IllegalArgumentException("K debe ser >= 1");
        this.problema = problema;
        this.random = new Random(semilla);
        this.k = k;
    }

    public Solucion ejecutar() {
        ArrayList<ParCiudadDistancia> disponibles = crearVectorOrdenado();
        ArrayList<Integer> recorrido = new ArrayList<>(disponibles.size());
        while (!disponibles.isEmpty()) {
            int posicion = random.nextInt(Math.min(k, disponibles.size()));
            recorrido.add(disponibles.remove(posicion).ciudad);
        }
        return new Solucion(recorrido, Greedy.calcularCoste(problema, recorrido));
    }

    private ArrayList<ParCiudadDistancia> crearVectorOrdenado() {
        int n = problema.getDimension();
        double[] sumas = problema.getSumasDistancias();
        ArrayList<ParCiudadDistancia> vector = new ArrayList<>(n);
        for (int i = 0; i < n; i++) vector.add(new ParCiudadDistancia(problema.getCiudadPorIndice(i).getId(), sumas[i]));
        vector.sort(Comparator.comparingDouble(p -> p.distancia));
        return vector;
    }

    private static class ParCiudadDistancia {
        private final int ciudad;
        private final double distancia;
        private ParCiudadDistancia(int ciudad, double distancia) { this.ciudad = ciudad; this.distancia = distancia; }
    }
}
