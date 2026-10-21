import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/** Búsqueda tabú con 2-opt, MCP, MLP, estancamiento y oscilación estratégica. */
public class BusquedaTabu {
    private final Problema problema;
    private final long semilla;
    private final int maxIteraciones;
    private final int porcentajeEstancamiento;
    private final int tenenciaTabu;
    private final int porcentajeOscilacion;
    private final Random random;

    /** Constructor compatible con los parámetros originales del enunciado. */
    public BusquedaTabu(Problema problema, long semilla) {
        this(problema, semilla, 10000, 5, 10, 50);
    }

    public BusquedaTabu(Problema problema, long semilla, int maxIteraciones,
                        int porcentajeEstancamiento, int tenenciaTabu, int porcentajeOscilacion) {
        if (maxIteraciones < 1) throw new IllegalArgumentException("Las iteraciones deben ser >= 1");
        if (porcentajeEstancamiento < 1 || porcentajeEstancamiento > 100)
            throw new IllegalArgumentException("El porcentaje de estancamiento debe estar entre 1 y 100");
        if (tenenciaTabu < 1) throw new IllegalArgumentException("La tenencia tabú debe ser >= 1");
        if (porcentajeOscilacion < 0 || porcentajeOscilacion > 100)
            throw new IllegalArgumentException("El porcentaje de oscilación debe estar entre 0 y 100");
        this.problema = problema;
        this.semilla = semilla;
        this.maxIteraciones = maxIteraciones;
        this.porcentajeEstancamiento = porcentajeEstancamiento;
        this.tenenciaTabu = tenenciaTabu;
        this.porcentajeOscilacion = porcentajeOscilacion;
        this.random = new Random(semilla ^ 0xD1B54A32D192ED03L);
    }

    public Solucion ejecutar(Solucion solucionInicial) {
        MemoriaTabu memoria = new MemoriaTabu(tenenciaTabu);
        OscilacionEstrategica oscilacion = new OscilacionEstrategica(
                problema, semilla ^ 0x9E3779B97F4A7C15L, porcentajeOscilacion, maxIteraciones);
        Solucion actual = solucionInicial.copia();
        Solucion mejorMomento = actual.copia();
        Solucion mejorGlobal = actual.copia();
        memoria.registrarSolucion(actual);
        int empeoramientos = 0;
        int umbralEstancamiento = Math.max(1, (int) Math.ceil(maxIteraciones * porcentajeEstancamiento / 100.0));
        int n = actual.getRecorrido().size();
        if (n < 4) return actual;
        // En instancias grandes se usa un vecindario 2-opt muestreado y acotado.
        // El muestreo evita que cada iteración examine O(n²) movimientos.
        long movimientosTotales = (long) n * (n - 3) / 2;
        int limiteEvaluaciones = movimientosTotales <= 20000 ? (int) movimientosTotales : 20000;

        for (int iter = 0; iter < maxIteraciones; iter++) {
            ArrayList<Integer> ruta = actual.getRecorrido();
            Movimiento mejorMovimiento = null;
            // Busca el mejor vecino admisible, aunque empeore. No se aplica criterio de aspiración.
            if (movimientosTotales <= limiteEvaluaciones) {
              for (int i = 0; i < n; i++) {
                for (int j = i + 2; j < n; j++) {
                    if (i == 0 && j == n - 1) continue;
                    int a = ruta.get(i), b = ruta.get((i + 1) % n);
                    int c = ruta.get(j), d = ruta.get((j + 1) % n);
                    // Se descarta el movimiento si alguno de los arcos que entraría está tabú.
                    if (memoria.esTabu(a, c) || memoria.esTabu(b, d)) continue;
                    double delta = delta2Opt(ruta, i, j);
                    if (mejorMovimiento == null || delta < mejorMovimiento.delta) {
                        mejorMovimiento = new Movimiento(i, j, delta, a, b, c, d);
                    }
                }
              }
            } else {
              for (int evaluacion = 0; evaluacion < limiteEvaluaciones; evaluacion++) {
                int i = random.nextInt(n);
                int offset = 2 + random.nextInt(n - 3);
                int j = (i + offset) % n;
                // La pareja de arcos es no ordenada; canonicalízala para aplicar
                // la inversión con subList(i+1, j+1) sin cruzar el final del vector.
                if (j < i) { int temporal = i; i = j; j = temporal; }
                if (i == 0 && j == n - 1) continue;
                int a = ruta.get(i), b = ruta.get((i + 1) % n);
                int c = ruta.get(j), d = ruta.get((j + 1) % n);
                if (memoria.esTabu(a, c) || memoria.esTabu(b, d)) continue;
                double delta = delta2Opt(ruta, i, j);
                if (mejorMovimiento == null || delta < mejorMovimiento.delta)
                    mejorMovimiento = new Movimiento(i, j, delta, a, b, c, d);
              }
            }

            // Sin criterio de aspiración. Si todo está tabú, se vacía MCP y se consume la iteración.
            if (mejorMovimiento == null) {
                memoria.reiniciarMCP();
                continue;
            }

            // La tenencia se reduce al avanzar la iteración; después se marca el movimiento actual.
            memoria.avanzarIteracion();
            Collections.reverse(ruta.subList(mejorMovimiento.i + 1, mejorMovimiento.j + 1));
            actual = new Solucion(ruta, Greedy.calcularCoste(problema, ruta));
            // Los arcos eliminados quedan tabú para evitar revertir inmediatamente el movimiento.
            memoria.marcarTabu(mejorMovimiento.a, mejorMovimiento.b);
            memoria.marcarTabu(mejorMovimiento.c, mejorMovimiento.d);
            memoria.registrarSolucion(actual);

            if (actual.getCoste() < mejorGlobal.getCoste() - 1e-9) mejorGlobal = actual.copia();
            if (actual.getCoste() < mejorMomento.getCoste() - 1e-9) {
                mejorMomento = actual.copia();
                empeoramientos = 0;
            } else {
                empeoramientos++;
            }

            if (empeoramientos >= umbralEstancamiento) {
                actual = oscilacion.generar(actual, mejorGlobal, memoria);
                mejorMomento = actual.copia();
                memoria.reiniciarMCP(); // MLP se conserva.
                memoria.registrarSolucion(actual);
                empeoramientos = 0;
                if (actual.getCoste() < mejorGlobal.getCoste() - 1e-9) mejorGlobal = actual.copia();
            }
        }
        return mejorGlobal;
    }

    private double delta2Opt(ArrayList<Integer> ruta, int i, int j) {
        int n = ruta.size();
        int a = ruta.get(i), b = ruta.get((i + 1) % n);
        int c = ruta.get(j), d = ruta.get((j + 1) % n);
        return problema.distanciaCiudades(a, c) + problema.distanciaCiudades(b, d)
                - problema.distanciaCiudades(a, b) - problema.distanciaCiudades(c, d);
    }

    private static class Movimiento {
        final int i, j, a, b, c, d;
        final double delta;
        Movimiento(int i, int j, double delta, int a, int b, int c, int d) {
            this.i = i; this.j = j; this.delta = delta;
            this.a = a; this.b = b; this.c = c; this.d = d;
        }
    }
}
