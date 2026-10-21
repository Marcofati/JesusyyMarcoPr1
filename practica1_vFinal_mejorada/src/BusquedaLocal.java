import java.util.ArrayList;
import java.util.Collections;

/** Búsqueda local de primer mejor con 2-opt y DLB cíclico. */
public class BusquedaLocal {
    private final Problema problema;
    private final int maxIteraciones;

    public BusquedaLocal(Problema problema) { this(problema, 10000); }
    public BusquedaLocal(Problema problema, int maxIteraciones) {
        if (maxIteraciones < 1) throw new IllegalArgumentException("Las iteraciones deben ser >= 1");
        this.problema = problema; this.maxIteraciones = maxIteraciones;
    }

    public Solucion ejecutar(Solucion solucionInicial) {
        ArrayList<Integer> ruta = solucionInicial.getRecorrido();
        double coste = Greedy.calcularCoste(problema, ruta);
        int n = ruta.size();
        if (n < 4) return new Solucion(ruta, coste);
        DLB dlb = new DLB(n);
        int iteraciones = 0, cursor = 0;
        while (iteraciones < maxIteraciones) {
            int i = dlb.siguienteActivo(cursor);
            if (i < 0) break;
            boolean mejora = false;
            // j se recorre cíclicamente desde i+2 hasta i+n-2.
            for (int offset = 2; offset <= n - 2; offset++) {
                int j = DLB.siguienteJ(i, offset, n);
                double delta = delta2Opt(ruta, i, j);
                if (delta < -1e-9) {
                    invertirSegmentoCircular(ruta, (i + 1) % n, j);
                    coste += delta;
                    // Los dos extremos de los arcos afectados vuelven a ser prometedores.
                    dlb.set(i, false); dlb.set((i + 1) % n, false);
                    dlb.set(j, false); dlb.set((j + 1) % n, false);
                    iteraciones++;
                    mejora = true;
                    cursor = i; // continúa desde este punto, con una vuelta completa disponible
                    break;
                }
            }
            if (!mejora) {
                dlb.set(i, true);
                cursor = (i + 1) % n;
            }
        }
        return new Solucion(ruta, Greedy.calcularCoste(problema, ruta));
    }

    /** Delta factorizado 2-opt para TSP simétrico. */
    double delta2Opt(ArrayList<Integer> ruta, int i, int j) {
        int n = ruta.size();
        int a = ruta.get(i), b = ruta.get((i + 1) % n);
        int c = ruta.get(j), d = ruta.get((j + 1) % n);
        return problema.distanciaCiudades(a, c) + problema.distanciaCiudades(b, d)
                - problema.distanciaCiudades(a, b) - problema.distanciaCiudades(c, d);
    }

    private void invertirSegmentoCircular(ArrayList<Integer> ruta, int inicio, int fin) {
        int n = ruta.size();
        ArrayList<Integer> segmento = new ArrayList<>();
        for (int p = inicio; ; p = (p + 1) % n) {
            segmento.add(ruta.get(p));
            if (p == fin) break;
        }
        Collections.reverse(segmento);
        for (int k = 0, p = inicio; k < segmento.size(); k++, p = (p + 1) % n)
            ruta.set(p, segmento.get(k));
    }
}
