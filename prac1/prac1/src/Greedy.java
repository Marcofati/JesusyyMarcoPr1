import java.util.ArrayList;
import java.util.Comparator;

public class Greedy {

    private Problema problema;

    public Greedy(Problema problema) {
        this.problema = problema;
    }

    public Solucion ejecutar() {

        ArrayList<Integer> ciudadesOrdenadas = obtenerCiudadesOrdenadas();

        double coste = calcularCoste(ciudadesOrdenadas);

        return new Solucion(ciudadesOrdenadas, coste);
    }

    private ArrayList<Integer> obtenerCiudadesOrdenadas() {

        ArrayList<Integer> resultado = new ArrayList<>();

        double[][] matriz = problema.getMatrizDistancias();
        int n = problema.getCiudades().size();

        ArrayList<ParCiudadDistancia> vector = new ArrayList<>();

        for (int i = 0; i < n; i++) {

            double suma = 0;

            for (int j = 0; j < n; j++) {
                suma += matriz[i][j];
            }

            int idCiudad = problema.getCiudades().get(i).getId();

            vector.add(new ParCiudadDistancia(idCiudad, suma));
        }

        vector.sort(Comparator.comparingDouble(ParCiudadDistancia::getDistancia));

        for (ParCiudadDistancia par : vector) {
            resultado.add(par.getCiudad());
        }

        return resultado;
    }

    private double calcularCoste(ArrayList<Integer> recorrido) {

        double[][] matriz = problema.getMatrizDistancias();
        ArrayList<Ciudad> ciudades = problema.getCiudades();

        double coste = 0;

        for (int i = 0; i < recorrido.size() - 1; i++) {

            int ciudad1 = recorrido.get(i) - 1;
            int ciudad2 = recorrido.get(i + 1) - 1;

            coste += matriz[ciudad1][ciudad2];
        }

        // Volver a la ciudad inicial
        int ultima = recorrido.get(recorrido.size() - 1) - 1;
        int primera = recorrido.get(0) - 1;

        coste += matriz[ultima][primera];

        return coste;
    }

    private static class ParCiudadDistancia {

        private int ciudad;
        private double distancia;

        public ParCiudadDistancia(int ciudad, double distancia) {
            this.ciudad = ciudad;
            this.distancia = distancia;
        }

        public int getCiudad() {
            return ciudad;
        }

        public double getDistancia() {
            return distancia;
        }
    }
}