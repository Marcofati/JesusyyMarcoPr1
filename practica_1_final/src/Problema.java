import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/** Lee una instancia TSP y calcula distancias bajo demanda para no reservar n*n doubles. */
public class Problema {
    private String nombre;
    private int dimension;
    private final ArrayList<Ciudad> ciudades = new ArrayList<>();
    private double[] sumasDistancias;

    public Problema(String ruta) {
        cargarFichero(ruta);
        if (ciudades.isEmpty()) {
            throw new IllegalArgumentException("No se pudieron cargar ciudades desde: " + ruta);
        }
        if (dimension == 0) dimension = ciudades.size();
        if (ciudades.size() != dimension) {
            throw new IllegalArgumentException("La dimensión indicada (" + dimension +
                    ") no coincide con las ciudades leídas (" + ciudades.size() + ") en " + ruta);
        }
    }

    private void cargarFichero(String ruta) {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            boolean leyendoCiudades = false;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                if (linea.startsWith("NAME")) {
                    String[] p = linea.split(":", 2);
                    if (p.length > 1) nombre = p[1].trim();
                } else if (linea.startsWith("DIMENSION")) {
                    String[] p = linea.split(":", 2);
                    if (p.length > 1) dimension = Integer.parseInt(p[1].trim());
                } else if (linea.equals("NODE_COORD_SECTION")) {
                    leyendoCiudades = true;
                } else if (linea.equals("EOF")) {
                    break;
                } else if (leyendoCiudades) {
                    String[] p = linea.split("\\s+");
                    if (p.length >= 3) {
                        ciudades.add(new Ciudad(Integer.parseInt(p[0]),
                                Double.parseDouble(p[1]), Double.parseDouble(p[2])));
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error al leer el fichero " + ruta + ": " + e.getMessage(), e);
        }
        if (nombre == null) nombre = "instancia";
    }

    public String getNombre() { return nombre; }
    public int getDimension() { return dimension; }
    public ArrayList<Ciudad> getCiudades() { return new ArrayList<>(ciudades); }
    public Ciudad getCiudadPorIndice(int indice) { return ciudades.get(indice); }
    public int getIndicePorId(int id) { return id - 1; }
    public double distanciaIndices(int i, int j) { return ciudades.get(i).distancia(ciudades.get(j)); }
    /** Suma de distancias de cada ciudad al resto; se calcula una sola vez y ocupa O(n) memoria. */
    public double[] getSumasDistancias() {
        if (sumasDistancias == null) {
            int n = ciudades.size();
            sumasDistancias = new double[n];
            for (int i = 0; i < n; i++) for (int j = i + 1; j < n; j++) {
                double d = distanciaIndices(i, j);
                sumasDistancias[i] += d;
                sumasDistancias[j] += d;
            }
        }
        return sumasDistancias.clone();
    }
    public double distanciaCiudades(int id1, int id2) {
        return ciudades.get(getIndicePorId(id1)).distancia(ciudades.get(getIndicePorId(id2)));
    }
    /** Compatibilidad con versiones anteriores. No crea una matriz gigante: usar distanciaIndices. */
    @Deprecated
    public double[][] getMatrizDistancias() {
        int n = ciudades.size();
        double[][] matriz = new double[n][n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) matriz[i][j] = distanciaIndices(i, j);
        return matriz;
    }
}
