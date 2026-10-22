import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Configurador config = new Configurador("config.txt");
        for (String rutaArchivo : config.getArchivos()) {
            System.out.println("\n==================================================");
            System.out.println("Cargando: " + rutaArchivo);
            Problema problema;
            try { problema = new Problema(rutaArchivo); }
            catch (RuntimeException e) {
                System.err.println("No se pudo cargar la instancia: " + e.getMessage());
                continue;
            }
            System.out.printf("PROBLEMA: %s (Ciudades: %d)%n", problema.getNombre(), problema.getDimension());
            System.out.printf("Parámetros: K=%d | iteraciones=%d | estancamiento=%d%% | tenencia tabú=%d | oscilación=%d%%%n",
                    config.getKGRA(), config.getIteraciones(), config.getEstancamientoPorcentaje(),
                    config.getTenenciaTabu(), config.getOscilacionPorcentaje());
            for (String algoritmo : config.getAlgoritmos()) {
                switch (algoritmo) {
                    case "GRE":
                        ejecutarGreedy(problema);
                        break;
                    case "GRA":
                        ejecutarProbabilistico("GRA", problema, config.getSemillas(),
                                (semilla, log) -> new GreedyAleatorizado(problema, semilla, config.getKGRA()).ejecutar());
                        break;
                    case "BL":
                        ejecutarProbabilistico("BL", problema, config.getSemillas(), (semilla, log) -> {
                            Solucion inicial = new GreedyAleatorizado(problema, semilla, config.getKGRA()).ejecutar();
                            log.mensaje("Comienza búsqueda local desde la solución GRA.");
                            return new BusquedaLocal(problema, config.getIteraciones()).ejecutar(inicial, log);
                        });
                        break;
                    case "TA":
                    case "TABU":
                        ejecutarProbabilistico("TA", problema, config.getSemillas(), (semilla, log) -> {
                            Solucion inicial = new GreedyAleatorizado(problema, semilla, config.getKGRA()).ejecutar();
                            log.mensaje("Comienza búsqueda tabú desde la solución GRA.");
                            return new BusquedaTabu(problema, semilla, config.getIteraciones(),
                                    config.getEstancamientoPorcentaje(), config.getTenenciaTabu(),
                                    config.getOscilacionPorcentaje()).ejecutar(inicial, log);
                        });
                        break;
                    default:
                        System.err.println("Algoritmo no reconocido: " + algoritmo);
                }
            }
        }
    }

    private interface Ejecutor { Solucion ejecutar(long semilla, LogEjecucion log); }

    private static void ejecutarGreedy(Problema problema) {
        long inicio = System.nanoTime();
        try (LogEjecucion log = new LogEjecucion(problema.getNombre(), "GRE", "determinista")) {
            Solucion solucion = new Greedy(problema).ejecutar();
            double ms = (System.nanoTime() - inicio) / 1_000_000.0;
            imprimir("GRE", solucion, ms);
            log.mensaje(String.format(java.util.Locale.ROOT,
                    "Resultado final: coste=%.8f | tiempo_ms=%.3f", solucion.getCoste(), ms));
            System.out.println("Log: " + log.getArchivo());
        } catch (IOException e) {
            System.err.println("No se pudo crear o cerrar el log de GRE: " + e.getMessage());
        }
    }

    private static void ejecutarProbabilistico(String nombre, Problema problema,
                                               ArrayList<Long> semillas, Ejecutor ejecutor) {
        System.out.println("\n--- " + nombre + " ---");
        ArrayList<Double> costes = new ArrayList<>();
        double sumaCostes = 0, sumaTiempos = 0;
        for (long semilla : semillas) {
            long inicio = System.nanoTime();
            try (LogEjecucion log = new LogEjecucion(problema.getNombre(), nombre, Long.toString(semilla))) {
                Solucion solucion = ejecutor.ejecutar(semilla, log);
                double ms = (System.nanoTime() - inicio) / 1_000_000.0;
                costes.add(solucion.getCoste());
                sumaCostes += solucion.getCoste();
                sumaTiempos += ms;
                log.mensaje(String.format(java.util.Locale.ROOT,
                        "Resultado final: coste=%.8f | tiempo_ms=%.3f", solucion.getCoste(), ms));
                System.out.printf("Semilla: %d | Coste: %.4f | Tiempo: %.3f ms%n", semilla, solucion.getCoste(), ms);
                System.out.println("Log: " + log.getArchivo());
            } catch (IOException e) {
                System.err.println("No se pudo crear o cerrar el log de " + nombre + " para semilla " + semilla
                        + ": " + e.getMessage());
            }
        }
        if (costes.isEmpty()) return;
        double media = sumaCostes / costes.size(), sumaCuadrados = 0;
        double minimo = Double.POSITIVE_INFINITY, maximo = Double.NEGATIVE_INFINITY;
        for (double coste : costes) {
            minimo = Math.min(minimo, coste);
            maximo = Math.max(maximo, coste);
            sumaCuadrados += (coste - media) * (coste - media);
        }
        double desviacion = Math.sqrt(sumaCuadrados / costes.size());
        System.out.printf("Resumen %s: mejor=%.4f | peor=%.4f | media=%.4f | desviación típica=%.4f | tiempo medio=%.3f ms%n",
                nombre, minimo, maximo, media, desviacion, sumaTiempos / costes.size());
    }

    private static void imprimir(String nombre, Solucion solucion, double ms) {
        System.out.printf("\n--- %s ---\nCoste: %.4f | Tiempo: %.3f ms%n", nombre, solucion.getCoste(), ms);
    }
}
