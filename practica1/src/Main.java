public class Main {

    public static void main(String[] args) {

        Configurador config = new Configurador("config.txt");

        for (String rutaArchivo : config.getArchivos()) {

            Problema problema = new Problema(rutaArchivo);
            System.out.println("PROBLEMA: " + problema.getNombre() + " (Ciudades: " + problema.getDimension() + ")");

            for (String algoritmo : config.getAlgoritmos()) {

                switch (algoritmo) {

                    case "GRE":
                        long inicioGRE = System.nanoTime();
                        Greedy greedy = new Greedy(problema);
                        Solucion solGRE = greedy.ejecutar();
                        long finGRE = System.nanoTime();
                        double tiempoGRE = (finGRE - inicioGRE) / 1_000_000.0;

                        System.out.println("\n--- ALGORITMO GREEDY (GRE) ---");
                        System.out.println("Coste: " + solGRE.getCoste() + " | Tiempo: " + tiempoGRE + " ms");
                        break;

                    case "GRA":
                        System.out.println("\n--- ALGORITMO GREEDY ALEATORIZADO (GRA) ---");

                        for (Long semilla : config.getSemillas()) {
                            long inicioGRA = System.nanoTime();
                            GreedyAleatorizado gra = new GreedyAleatorizado(problema, semilla);
                            Solucion solGRA = gra.ejecutar();
                            long finGRA = System.nanoTime();
                            double tiempoGRA = (finGRA - inicioGRA) / 1_000_000.0;

                            System.out.println("Semilla: " + semilla + " -> Coste: " + solGRA.getCoste() + " | Tiempo: " + tiempoGRA + " ms");
                        }
                        break;

                    case "BL":
                        System.out.println("\n--- BÚSQUEDA LOCAL DEL PRIMER MEJOR (BL) ---");

                        for (Long semilla : config.getSemillas()) {
                            GreedyAleatorizado graInicial = new GreedyAleatorizado(problema, semilla);
                            Solucion solInicial = graInicial.ejecutar();

                            long inicioBL = System.nanoTime();
                            BusquedaLocal bl = new BusquedaLocal(problema);
                            Solucion solBL = bl.ejecutar(solInicial);
                            long finBL = System.nanoTime();
                            double tiempoBL = (finBL - inicioBL) / 1_000_000.0;

                            System.out.println("Semilla: " + semilla 
                                    + " | Coste Inicial (GRA): " + solInicial.getCoste()
                                    + " -> Coste Final (BL): " + solBL.getCoste() 
                                    + " | Tiempo BL: " + tiempoBL + " ms");
                        }
                        break;
                }
            }
        }
    }
}