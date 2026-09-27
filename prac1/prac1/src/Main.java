public class Main {

    public static void main(String[] args) {

        String ruta = "prac1/pr144.tsp";
        Problema problema = new Problema(ruta);

        System.out.println("Problema: " + problema.getNombre());
        System.out.println("Número de ciudades: " + problema.getDimension());

        // 1. GREEDY DETERMINISTA
        long inicioGreedy = System.nanoTime();
        Greedy greedy = new Greedy(problema);
        Solucion solucionGreedy = greedy.ejecutar();
        long finGreedy = System.nanoTime();
        double tiempoGreedy = (finGreedy - inicioGreedy) / 1_000_000.0;

        System.out.println("\n===== GREEDY =====");
        System.out.println("Coste: " + solucionGreedy.getCoste());
        System.out.println("Tiempo: " + tiempoGreedy + " ms");

        // 2. GREEDY ALEATORIZADO Y 3. BÚSQUEDA LOCAL (3 ejecuciones con semillas)
        long[] semillas = {12345678, 23456781, 34567812}; 

        for (int i = 0; i < semillas.length; i++) {
            long semilla = semillas[i];
            System.out.println("\n================ EJECUCIÓN " + (i + 1) + " (Semilla: " + semilla + ") ================");

            // --- GREEDY ALEATORIZADO ---
            long inicioGRA = System.nanoTime();
            GreedyAleatorizado gra = new GreedyAleatorizado(problema, semilla);
            Solucion solucionGRA = gra.ejecutar();
            long finGRA = System.nanoTime();
            double tiempoGRA = (finGRA - inicioGRA) / 1_000_000.0;

            System.out.println("[GREEDY ALEATORIZADO] Coste: " + solucionGRA.getCoste() + " | Tiempo: " + tiempoGRA + " ms");

            // --- BÚSQUEDA LOCAL DEL PRIMER MEJOR ---
            long inicioBL = System.nanoTime();
            BusquedaLocal bl = new BusquedaLocal(problema);
            // Le pasamos como punto de partida la solución generada por el Greedy Aleatorizado
            Solucion solucionBL = bl.ejecutar(solucionGRA);
            long finBL = System.nanoTime();
            double tiempoBL = (finBL - inicioBL) / 1_000_000.0;

            System.out.println("[BÚSQUEDA LOCAL DLB]  Coste: " + solucionBL.getCoste() + " | Tiempo: " + tiempoBL + " ms");
        }
    }
}