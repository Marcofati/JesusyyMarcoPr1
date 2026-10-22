import java.util.Arrays;

/** Máscara Don't Look Bits y recorrido cíclico de índices. */
public class DLB {
    private final boolean[] bits;
    public DLB(int n) { if (n < 0) throw new IllegalArgumentException("Tamaño negativo"); bits = new boolean[n]; }
    public boolean get(int i) { return bits[i]; }
    public void set(int i, boolean valor) { bits[i] = valor; }
    public void reiniciar() { Arrays.fill(bits, false); }
    public int size() { return bits.length; }

    /** Índice activo desde start, inspeccionando como máximo una vuelta; -1 si no hay ninguno. */
    public int siguienteActivo(int start) {
        int n = bits.length;
        for (int k = 0; k < n; k++) {
            int i = Math.floorMod(start + k, n);
            if (!bits[i]) return i;
        }
        return -1;
    }

    /** Siguiente j válido para 2-opt, con vuelta cíclica y sin arcos adyacentes. */
    public static int siguienteJ(int i, int desplazamiento, int n) {
        if (n < 4) return -1;
        int offset = Math.max(2, desplazamiento);
        if (offset > n - 2) return -1;
        return (i + offset) % n;
    }

    public void mostrar() { for (boolean bit : bits) System.out.print((bit ? 1 : 0) + " "); System.out.println(); }
}
