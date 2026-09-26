import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Problema {

    private String nombre;
    private int dimension;

    private ArrayList<Ciudad> ciudades;
    private double[][] matrizDistancias;

    public Problema(String ruta) {
        ciudades = new ArrayList<>();

        cargarFichero(ruta);
        calcularMatrizDistancias();
    }

    private void cargarFichero(String ruta) {

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {

            String linea;
            boolean leyendoCiudades = false;

            while ((linea = br.readLine()) != null) {

                linea = linea.trim();

                if (linea.isEmpty()) {
                    continue;
                }

                if (linea.startsWith("NAME")) {
                    String[] partes = linea.split(":");
                    nombre = partes[1].trim();
                }

                else if (linea.startsWith("DIMENSION")) {
                    String[] partes = linea.split(":");
                    dimension = Integer.parseInt(partes[1].trim());
                }

                else if (linea.equals("NODE_COORD_SECTION")) {
                    leyendoCiudades = true;
                }

                else if (linea.equals("EOF")) {
                    break;
                }

                else if (leyendoCiudades) {

                    String[] partes = linea.split("\\s+");

                    int id = Integer.parseInt(partes[0]);
                    double x = Double.parseDouble(partes[1]);
                    double y = Double.parseDouble(partes[2]);

                    ciudades.add(new Ciudad(id, x, y));
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el fichero: " + e.getMessage());
        }
    }

    private void calcularMatrizDistancias() {

        matrizDistancias = new double[ciudades.size()][ciudades.size()];

        for (int i = 0; i < ciudades.size(); i++) {

            for (int j = 0; j < ciudades.size(); j++) {

                matrizDistancias[i][j] =
                        ciudades.get(i).distancia(ciudades.get(j));
            }
        }
    }

    public String getNombre() {
        return nombre;
    }

    public int getDimension() {
        return dimension;
    }

    public ArrayList<Ciudad> getCiudades() {
        return ciudades;
    }

    public double[][] getMatrizDistancias() {
        return matrizDistancias;
    }
}