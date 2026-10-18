import java.util.ArrayList;
import java.util.Collections;

/** Búsqueda local de primer mejor con operador 2-opt y máscara DLB. */
public class BusquedaLocal {
    private final Problema problema;
    private final int maxIteraciones;

    public BusquedaLocal(Problema problema) { this(problema, 10000); }
    public BusquedaLocal(Problema problema, int maxIteraciones) {
        if (maxIteraciones < 1) throw new IllegalArgumentException("Las iteraciones deben ser >= 1");
        this.problema = problema;
        this.maxIteraciones = maxIteraciones;
    }

    public Solucion ejecutar(Solucion solucionInicial) {
        ArrayList<Integer> ruta = solucionInicial.getRecorrido();
        double coste = Greedy.calcularCoste(problema, ruta);
        int n = ruta.size();
        if (n < 4) return new Solucion(ruta, coste);
        DLB dlb = new DLB(n);
        int iteraciones = 0;
        boolean encontroMejora = true;
        while (iteraciones < maxIteraciones && encontroMejora) {
            encontroMejora = false;
            buscar:
            for (int i = 0; i < n; i++) {
                if (dlb.get(i)) continue;
                boolean mejoraDeI = false;
                for (int j = i + 2; j < n; j++) {
                    if (i == 0 && j == n - 1) continue;
                    double delta = delta2Opt(ruta, i, j);
                    if (delta < -1e-9) {
                        Collections.reverse(ruta.subList(i + 1, j + 1));
                        coste += delta;
                        // Reactiva las ciudades/extremos afectados por el movimiento.
                        dlb.set(i, false);
                        dlb.set((i + 1) % n, false);
                        dlb.set(j, false);
                        dlb.set((j + 1) % n, false);
                        iteraciones++;
                        encontroMejora = true;
                        mejoraDeI = true;
                        break buscar; // primer mejor: acepta la primera mejora y reinicia
                    }
                }
                if (!mejoraDeI) dlb.set(i, true);
            }
        }
        // Recalcula al final para evitar acumulación de errores de coma flotante.
        return new Solucion(ruta, Greedy.calcularCoste(problema, ruta));
    }

    /** Delta factorizado 2-opt para TSP simétrico. i,j son índices de posiciones del recorrido. */
    double delta2Opt(ArrayList<Integer> ruta, int i, int j) {
        int n = ruta.size();
        int a = ruta.get(i), b = ruta.get((i + 1) % n);
        int c = ruta.get(j), d = ruta.get((j + 1) % n);
        double desaparecen = problema.distanciaCiudades(a, b) + problema.distanciaCiudades(c, d);
        double nuevos = problema.distanciaCiudades(a, c) + problema.distanciaCiudades(b, d);
        return nuevos - desaparecen;
    }
}
