/** Máscara Don't Look Bits: false/0 = activa; true/1 = desactivada. */
public class DLB {
    private final boolean[] bits;
    public DLB(int n) { if (n < 0) throw new IllegalArgumentException("Tamaño negativo"); bits = new boolean[n]; }
    public boolean get(int i) { return bits[i]; }
    public void set(int i, boolean valor) { bits[i] = valor; }
    public void reiniciar() { java.util.Arrays.fill(bits, false); }
    public int size() { return bits.length; }
    public void mostrar() { for (boolean bit : bits) System.out.print((bit ? 1 : 0) + " "); System.out.println(); }
}
