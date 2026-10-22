import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Log independiente de una ejecución; nunca escribe el recorrido completo. */
public class LogEjecucion implements Closeable {
    private static final DateTimeFormatter MARCA_TIEMPO = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    private final BufferedWriter escritor;
    private final Path archivo;

    public LogEjecucion(String instancia, String algoritmo, String semilla) throws IOException {
        Path directorio = Paths.get("logs");
        Files.createDirectories(directorio);
        String base = limpiar(instancia) + "_" + limpiar(algoritmo) + "_" + limpiar(semilla)
                + "_" + LocalDateTime.now().format(MARCA_TIEMPO) + ".log";
        archivo = directorio.resolve(base);
        escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        escribir("Instancia: " + instancia + " | Algoritmo: " + algoritmo + " | Semilla: " + semilla);
        escribir("Formato de movimiento: ciudades asociadas al intercambio 2-opt y coste de la solución tras aceptarlo.");
    }

    private static String limpiar(String valor) {
        return valor.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public synchronized void movimiento(int ciudad1, int ciudad2, double coste) {
        escribir("Intercambio: ciudad1=" + ciudad1 + ", ciudad2=" + ciudad2
                + " | coste=" + String.format(java.util.Locale.ROOT, "%.8f", coste));
    }

    public synchronized void mensaje(String mensaje) { escribir(mensaje); }

    public Path getArchivo() { return archivo; }

    private void escribir(String linea) {
        try {
            escritor.write(linea);
            escritor.newLine();
            escritor.flush();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo escribir el log " + archivo + ": " + e.getMessage(), e);
        }
    }

    @Override public void close() throws IOException { escritor.close(); }
}
