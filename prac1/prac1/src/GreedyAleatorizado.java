import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class GreedyAleatorizado {

    private Problema problema;
    private Random random;

    private static final int K = 5;

    public GreedyAleatorizado(Problema problema, long semilla) {
        this.problema = problema;
        this.random = new Random(semilla);
    }

    public Solucion ejecutar() {

        ArrayList<ParCiudadDistancia> disponibles = crearVectorOrdenado();

        ArrayList<Integer> recorrido = new ArrayList<>();

        while (!disponibles.isEmpty()) {

            int limite = Math.min(K, disponibles.size());

            int posicion = random.nextInt(limite);

            ParCiudadDistancia seleccionada = disponibles.remove(posicion);

            recorrido.add(seleccionada.getCiudad());
        }

        double coste = calcularCoste(recorrido);

        return new Solucion(recorrido, coste);
    }

    private ArrayList<ParCiudadDistancia> crearVectorOrdenado() {

        ArrayList<ParCiudadDistancia> vector = new ArrayList<>();

        double[][] matriz = problema.getMatrizDistancias();

        for (int i = 0; i < problema.getCiudades().size(); i++) {

            double suma = 0;

            for (int j = 0; j < problema.getCiudades().size(); j++) {
                suma += matriz[i][j];
            }

            int idCiudad = problema.getCiudades().get(i).getId();

            vector.add(new ParCiudadDistancia(idCiudad, suma));
        }

        vector.sort(Comparator.comparingDouble(ParCiudadDistancia::getDistancia));

        return vector;
    }

    private double calcularCoste(ArrayList<Integer> recorrido) {

        double[][] matriz = problema.getMatrizDistancias();

        double coste = 0;

        for (int i = 0; i < recorrido.size() - 1; i++) {

            int ciudad1 = recorrido.get(i) - 1;
            int ciudad2 = recorrido.get(i + 1) - 1;

            coste += matriz[ciudad1][ciudad2];
        }

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