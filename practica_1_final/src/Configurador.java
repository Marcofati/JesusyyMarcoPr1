import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/** Lee la configuración de la práctica. */
public class Configurador {
    private final ArrayList<String> archivos = new ArrayList<>();
    private final ArrayList<String> algoritmos = new ArrayList<>();
    private final ArrayList<Long> semillas = new ArrayList<>();

    // Valores por defecto exigidos en el enunciado.
    private int kGRA = 5;
    private int iteraciones = 10000;
    private int estancamientoPorcentaje = 5;
    private int tenenciaTabu = 10;
    private int oscilacionPorcentaje = 50;

    public Configurador(String ruta) {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
                String[] partes = linea.split("=", 2);
                if (partes.length != 2) continue;
                String clave = partes[0].trim();
                String valor = partes[1].trim();
                switch (clave) {
                    case "Archivos":
                        for (String v : valor.split("\\s+")) if (!v.isEmpty()) archivos.add(v);
                        break;
                    case "Algoritmos":
                        for (String v : valor.split("\\s+")) if (!v.isEmpty()) algoritmos.add(v.toUpperCase());
                        break;
                    case "Semillas":
                        for (String v : valor.split("\\s+")) if (!v.isEmpty()) semillas.add(Long.parseLong(v));
                        break;
                    case "OtrosParametros":
                        leerOtrosParametros(valor);
                        break;
                    default:
                        System.err.println("Aviso: parámetro desconocido en config.txt: " + clave);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer la configuración " + ruta + ": " + e.getMessage(), e);
        }
        if (semillas.isEmpty()) semillas.add(1L);
        validarParametros();
    }

    /** Orden obligatorio: K de GRA, iteraciones, % estancamiento, tenencia tabú, % oscilación. */
    private void leerOtrosParametros(String valor) {
        String[] p = valor.trim().split("\\s+");
        if (p.length != 5) {
            throw new IllegalArgumentException("OtrosParametros debe contener exactamente 5 enteros: K iteraciones estancamiento% tenenciaTabu oscilacion%.");
        }
        kGRA = Integer.parseInt(p[0]);
        iteraciones = Integer.parseInt(p[1]);
        estancamientoPorcentaje = Integer.parseInt(p[2]);
        tenenciaTabu = Integer.parseInt(p[3]);
        oscilacionPorcentaje = Integer.parseInt(p[4]);
    }

    private void validarParametros() {
        if (kGRA < 1) throw new IllegalArgumentException("K de GRA debe ser >= 1.");
        if (iteraciones < 1) throw new IllegalArgumentException("Iteraciones debe ser >= 1.");
        if (estancamientoPorcentaje < 1 || estancamientoPorcentaje > 100)
            throw new IllegalArgumentException("El porcentaje de estancamiento debe estar entre 1 y 100.");
        if (tenenciaTabu < 1) throw new IllegalArgumentException("La tenencia tabú debe ser >= 1.");
        if (oscilacionPorcentaje < 0 || oscilacionPorcentaje > 100)
            throw new IllegalArgumentException("El porcentaje de oscilación debe estar entre 0 y 100.");
    }

    public ArrayList<String> getArchivos() { return new ArrayList<>(archivos); }
    public ArrayList<String> getAlgoritmos() { return new ArrayList<>(algoritmos); }
    public ArrayList<Long> getSemillas() { return new ArrayList<>(semillas); }
    public int getKGRA() { return kGRA; }
    public int getIteraciones() { return iteraciones; }
    public int getEstancamientoPorcentaje() { return estancamientoPorcentaje; }
    public int getTenenciaTabu() { return tenenciaTabu; }
    public int getOscilacionPorcentaje() { return oscilacionPorcentaje; }
}
