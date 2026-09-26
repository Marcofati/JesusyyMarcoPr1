public class Main {

    public static void main(String[] args) {

        String ruta = "ch130.tsp";

        ArchivoDatos archivo = new ArchivoDatos(ruta);

        System.out.println("Problema: " + archivo.getNombre());
        System.out.println("Número de ciudades: " + archivo.getDimension());

        // GREEDY

        long inicioGreedy = System.nanoTime();

        Greedy greedy = new Greedy(archivo);
        Solucion solucionGreedy = greedy.ejecutar();

        long finGreedy = System.nanoTime();

        double tiempoGreedy = (finGreedy - inicioGreedy) / 1_000_000.0;

        System.out.println("\n===== GREEDY =====");
        System.out.println("Coste: " + solucionGreedy.getCoste());
        System.out.println("Tiempo: " + tiempoGreedy + " ms");

        // GREEDY ALEATORIZADO

        long semilla = 12345678L;

        long inicioGRA = System.nanoTime();

        GreedyAleatorizado gra =
                new GreedyAleatorizado(archivo, semilla);

        Solucion solucionGRA = gra.ejecutar();

        long finGRA = System.nanoTime();

        double tiempoGRA = (finGRA - inicioGRA) / 1_000_000.0;

        System.out.println("\n===== GREEDY ALEATORIZADO =====");
        System.out.println("Semilla: " + semilla);
        System.out.println("Coste: " + solucionGRA.getCoste());
        System.out.println("Tiempo: " + tiempoGRA + " ms");
    }
}