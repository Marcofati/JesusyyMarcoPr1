import java.util.ArrayList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Main {
    public static void main(String[] args) {
        Configurador config = new Configurador("config.txt");
        for (String rutaArchivo : config.getArchivos()) {
            System.out.println("\n==================================================");
            System.out.println("Cargando: " + rutaArchivo);
            Problema problema;
            try { problema = new Problema(rutaArchivo); }
            catch (RuntimeException e) { System.err.println("No se pudo cargar la instancia: " + e.getMessage()); continue; }
            System.out.println("PROBLEMA: " + problema.getNombre() + " (Ciudades: " + problema.getDimension() + ")");
            System.out.printf("Parámetros: K=%d | iteraciones=%d | estancamiento=%d%% | tenencia tabú=%d | oscilación=%d%%%n",
                    config.getKGRA(), config.getIteraciones(), config.getEstancamientoPorcentaje(),
                    config.getTenenciaTabu(), config.getOscilacionPorcentaje());
            for (String algoritmo : config.getAlgoritmos()) {
                switch (algoritmo) {
                    case "GRE": {
                        long inicio = System.nanoTime();
                        Solucion s = new Greedy(problema).ejecutar();
                        double ms = (System.nanoTime() - inicio) / 1_000_000.0;
                        imprimir("GRE", s, ms);
                        registrarLog(problema.getNombre(), "GRE", "", s.getCoste(), ms);
                        break;
                    }
                    case "GRA":
                        ejecutarProbabilistico("GRA", problema, config.getSemillas(), (semilla) -> new GreedyAleatorizado(problema, semilla, config.getKGRA()).ejecutar());
                        break;
                    case "BL":
                        ejecutarProbabilistico("BL", problema, config.getSemillas(), (semilla) -> {
                            Solucion inicial = new GreedyAleatorizado(problema, semilla, config.getKGRA()).ejecutar();
                            return new BusquedaLocal(problema, config.getIteraciones()).ejecutar(inicial);
                        });
                        break;
                    case "TA":
                        ejecutarProbabilistico("TA", problema, config.getSemillas(), (semilla) -> {
                            Solucion inicial = new GreedyAleatorizado(problema, semilla, config.getKGRA()).ejecutar();
                            return new BusquedaTabu(problema, semilla, config.getIteraciones(), config.getEstancamientoPorcentaje(), config.getTenenciaTabu(), config.getOscilacionPorcentaje()).ejecutar(inicial);
                        });
                        break;
                    default: System.err.println("Algoritmo no reconocido: " + algoritmo);
                }
            }
        }
    }

    private interface Ejecutor { Solucion ejecutar(long semilla); }
    private static void ejecutarProbabilistico(String nombre, Problema problema, ArrayList<Long> semillas, Ejecutor ejecutor) {
        System.out.println("\n--- " + nombre + " ---");
        ArrayList<Double> costes = new ArrayList<>();
        double sumaCostes = 0, sumaTiempos = 0;
        for (long semilla : semillas) {
            long inicio = System.nanoTime();
            Solucion s = ejecutor.ejecutar(semilla);
            double ms = (System.nanoTime() - inicio) / 1_000_000.0;
            costes.add(s.getCoste()); sumaCostes += s.getCoste(); sumaTiempos += ms;
            System.out.printf("Semilla: %d | Coste: %.4f | Tiempo: %.3f ms%n", semilla, s.getCoste(), ms);
            registrarLog(problema.getNombre(), nombre, Long.toString(semilla), s.getCoste(), ms);
        }
        double media = sumaCostes / costes.size(), sumaCuadrados = 0;
        double minimo = Double.POSITIVE_INFINITY, maximo = Double.NEGATIVE_INFINITY;
        for (double c : costes) { minimo = Math.min(minimo, c); maximo = Math.max(maximo, c); sumaCuadrados += (c - media) * (c - media); }
        double desviacion = Math.sqrt(sumaCuadrados / costes.size());
        System.out.printf("Resumen %s: mejor=%.4f | peor=%.4f | media=%.4f | desviación típica=%.4f | tiempo medio=%.3f ms%n",
                nombre, minimo, maximo, media, desviacion, sumaTiempos / costes.size());
    }
    private static void imprimir(String nombre, Solucion s, double ms) {
        System.out.printf("\n--- %s ---\nCoste: %.4f | Tiempo: %.3f ms%n", nombre, s.getCoste(), ms);
    }

    /** Guarda una fila por ejecución en CSV, legible desde Excel y herramientas de análisis. */
    private static void registrarLog(String instancia, String algoritmo, String semilla, double coste, double ms) {
        try {
            Path dir = Paths.get("logs");
            Files.createDirectories(dir);
            Path archivo = dir.resolve("resultados.csv");
            boolean nuevo = !Files.exists(archivo) || Files.size(archivo) == 0;
            StringBuilder fila = new StringBuilder();
            if (nuevo) fila.append("fecha_hora,instancia,algoritmo,semilla,coste,tiempo_ms\n");
            fila.append('"').append(java.time.LocalDateTime.now()).append("\",\"")
                    .append(instancia.replace("\"", "\"\"")).append("\",\"")
                    .append(algoritmo).append("\",\"").append(semilla).append("\",")
                    .append(String.format(java.util.Locale.ROOT, "%.8f,%.3f%n", coste, ms));
            Files.write(archivo, fila.toString().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("No se pudo escribir logs/resultados.csv: " + e.getMessage());
        }
    }
}
