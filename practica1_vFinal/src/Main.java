import java.util.ArrayList;

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
                        imprimir("GRE", s, (System.nanoTime() - inicio) / 1_000_000.0);
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
}
