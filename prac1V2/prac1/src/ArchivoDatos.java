import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ArchivoDatos {

    private String nombre;
    private int dimension;
    private ArrayList<Ciudad> ciudades;
    private double[][] matrizDistancias;

    public ArchivoDatos(String rutaArchivo) {

        ciudades = new ArrayList<>();

        String linea;

        try {

            FileReader f = new FileReader(rutaArchivo);
            BufferedReader b = new BufferedReader(f);

            while ((linea = b.readLine()) != null) {

                linea = linea.trim();

                // Obtener el nombre del problema
                if (linea.startsWith("NAME")) {

                    String[] partes = linea.split(":");

                    if (partes.length > 1) {
                        nombre = partes[1].trim();
                    }
                }

                // Obtener la dimensión
                else if (linea.startsWith("DIMENSION")) {

                    String[] partes = linea.split(":");

                    if (partes.length > 1) {
                        dimension = Integer.parseInt(partes[1].trim());
                    }
                }

                // Comenzar a leer las ciudades
                else if (linea.equals("NODE_COORD_SECTION")) {

                    for (int i = 0; i < dimension; i++) {

                        linea = b.readLine();

                        String[] partes = linea.trim().split("\\s+");

                        int id = Integer.parseInt(partes[0]);
                        double x = Double.parseDouble(partes[1]);
                        double y = Double.parseDouble(partes[2]);

                        Ciudad ciudad = new Ciudad(id, x, y);

                        ciudades.add(ciudad);
                    }

                    break;
                }
            }

            b.close();

            // Crear la matriz de distancias
            matrizDistancias = new double[dimension][dimension];

            for (int i = 0; i < dimension; i++) {

                for (int j = 0; j < dimension; j++) {

                    if (i == j) {
                        matrizDistancias[i][j] = 0;
                    }
                    else {
                        matrizDistancias[i][j] =
                                ciudades.get(i).distancia(ciudades.get(j));
                    }
                }
            }

        }
        catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e);
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