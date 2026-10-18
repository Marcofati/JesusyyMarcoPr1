import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Oscilación estratégica: con la probabilidad configurada intensifica (mejor global)
 * y en caso contrario diversifica, usando las frecuencias de arcos de la MLP.
 */
public class OscilacionEstrategica {
    private final Problema problema;
    private final Random random;
    private final int porcentajeIntensificacion;
    private final int iteracionesBL;

    public OscilacionEstrategica(Problema problema, long semilla) {
        this(problema, semilla, 50, 10000);
    }

    public OscilacionEstrategica(Problema problema, long semilla, int porcentajeIntensificacion, int iteracionesBL) {
        if (porcentajeIntensificacion < 0 || porcentajeIntensificacion > 100)
            throw new IllegalArgumentException("El porcentaje de oscilación debe estar entre 0 y 100");
        this.problema = problema;
        this.random = new Random(semilla);
        this.porcentajeIntensificacion = porcentajeIntensificacion;
        this.iteracionesBL = iteracionesBL;
    }

    public Solucion generar(Solucion actual, Solucion mejorGlobal, MemoriaTabu memoria) {
        boolean intensificar = random.nextInt(100) < porcentajeIntensificacion;
        ArrayList<Integer> ruta = (intensificar ? mejorGlobal : actual).getRecorrido();
        if (ruta.size() >= 4) {
            if (intensificar) {
                Solucion base = new Solucion(ruta, Greedy.calcularCoste(problema, ruta));
                ruta = new BusquedaLocal(problema, iteracionesBL).ejecutar(base).getRecorrido();
            } else {
                diversificarConMLP(ruta, memoria);
            }
        }
        return new Solucion(ruta, Greedy.calcularCoste(problema, ruta));
    }

    /**
     * Prueba varias perturbaciones 2-opt y conserva la que introduce arcos menos visitados
     * según la MLP. Así la memoria a largo plazo participa realmente en la diversificación.
     */
    private void diversificarConMLP(ArrayList<Integer> ruta, MemoriaTabu memoria) {
        int n = ruta.size();
        int movimientos = Math.max(1, n / 10);
        for (int k = 0; k < movimientos; k++) {
            int mejorI = -1, mejorJ = -1;
            long menorFrecuencia = Long.MAX_VALUE;
            int intentos = Math.min(30, Math.max(5, n));
            for (int intento = 0; intento < intentos; intento++) {
                int i = random.nextInt(n - 2);
                int j = i + 2 + random.nextInt(n - i - 2);
                if (i == 0 && j == n - 1) continue;
                int a = ruta.get(i), b = ruta.get((i + 1) % n);
                int c = ruta.get(j), d = ruta.get((j + 1) % n);
                long frecuencia = (long) memoria.frecuenciaArco(a, c) + memoria.frecuenciaArco(b, d);
                if (frecuencia < menorFrecuencia) {
                    menorFrecuencia = frecuencia;
                    mejorI = i;
                    mejorJ = j;
                }
            }
            if (mejorI >= 0) Collections.reverse(ruta.subList(mejorI + 1, mejorJ + 1));
        }
    }
}
