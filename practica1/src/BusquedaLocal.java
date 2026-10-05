import java.util.ArrayList;
import java.util.Collections;

public class BusquedaLocal {

    private Problema problema;
    private static final int MAX_ITERACIONES = 10000;

    public BusquedaLocal(Problema problema) {
        this.problema = problema;
    }

    public Solucion ejecutar(Solucion solucionInicial) {
        
        ArrayList<Integer> recorrido = new ArrayList<>(solucionInicial.getRecorrido());
        double costeActual = solucionInicial.getCoste();

        int n = recorrido.size();
        // Vector máscara DLB inicializado todo a 0
        int[] dlb = new int[n];

        int iteraciones = 0;
        boolean mejoraEntorno = true;

        // Condición de parada: 1.000 iteraciones o que ninguna solución del entorno mejore
        while (iteraciones < MAX_ITERACIONES && mejoraEntorno) {
            mejoraEntorno = false;

            for (int i = 0; i < n; i++) {
                if (dlb[i] == 0) {
                    boolean improveFlag = false;

                    for (int j = i + 1; j < n; j++) {
                        // checkMove(i, j): calculamos la diferencia de coste por factorización
                        double delta = calcularDeltaIntercambio(recorrido, i, j);

                        if (delta < 0) {
                            // applyMove(i, j): intercambiamos las ciudades en las posiciones i y j
                            Collections.swap(recorrido, i, j);
                            costeActual += delta;

                            // Reactivamos los bits implicados en la mejora
                            dlb[i] = 0;
                            dlb[j] = 0;

                            improveFlag = true;
                            mejoraEntorno = true;
                            iteraciones++;

                            // Controlamos no superar el máximo de iteraciones dentro del bucle
                            if (iteraciones >= MAX_ITERACIONES) {
                                break;
                            }
                        }
                    }

                    // Si tras probar todos los movimientos asociados a i ninguno mejora, se desactiva
                    if (!improveFlag) {
                        dlb[i] = 1;
                    }
                }

                if (iteraciones >= MAX_ITERACIONES) {
                    break;
                }
            }
        }

        return new Solucion(recorrido, costeActual);
    }

    private double calcularDeltaIntercambio(ArrayList<Integer> recorrido, int i, int j) {
        double[][] matriz = problema.getMatrizDistancias();
        int n = recorrido.size();

        // Posiciones anterior y siguiente (usando módulo % n para conectar primera y última ciudad)
        int antI = (i - 1 + n) % n;
        int sigI = (i + 1) % n;
        int antJ = (j - 1 + n) % n;
        int sigJ = (j + 1) % n;

        // IDs de las ciudades ajustados a índices de matriz (0 a n-1)
        int cAntI = recorrido.get(antI) - 1;
        int cI    = recorrido.get(i) - 1;
        int cSigI = recorrido.get(sigI) - 1;

        int cAntJ = recorrido.get(antJ) - 1;
        int cJ    = recorrido.get(j) - 1;
        int cSigJ = recorrido.get(sigJ) - 1;

        double arcosDesaparecen;
        double arcosNuevos;

        // CASO ESPECIAL 1: Ciudades consecutivas en el vector (j es la siguiente a i)
        if (j == i + 1) {
            arcosDesaparecen = matriz[cAntI][cI] + matriz[cJ][cSigJ];
            arcosNuevos      = matriz[cAntI][cJ] + matriz[cI][cSigJ];
        }
        // CASO ESPECIAL 2: Intercambio entre la primera (0) y la última ciudad (n-1)
        else if (i == 0 && j == n - 1) {
            arcosDesaparecen = matriz[cAntJ][cJ] + matriz[cI][cSigI];
            arcosNuevos      = matriz[cAntJ][cI] + matriz[cJ][cSigI];
        }
        // CASO GENERAL: Ciudades separadas (se rompen 4 arcos y se crean 4 arcos nuevos)
        else {
            arcosDesaparecen = matriz[cAntI][cI] + matriz[cI][cSigI] +
                               matriz[cAntJ][cJ] + matriz[cJ][cSigJ];

            arcosNuevos      = matriz[cAntI][cJ] + matriz[cJ][cSigI] +
                               matriz[cAntJ][cI] + matriz[cI][cSigJ];
        }

        return -arcosDesaparecen + arcosNuevos;
    }
}
