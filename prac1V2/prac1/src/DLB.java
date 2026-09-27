public class DLB {
    private boolean[] dlb;

    // Constructor
    public DLB(int n) {
        dlb = new boolean[n];
    }

    // Devuelve el valor del bit i
    public boolean get(int i) {
        return dlb[i];
    }

    // Cambia el valor del bit i
    public void set(int i, boolean valor) {
        dlb[i] = valor;
    }

    // Pone todos los bits a 0
    public void reiniciar() {
        for (int i = 0; i < dlb.length; i++) {
            dlb[i] = false;
        }
    }

    // Devuelve el tamaño de la máscara
    public int size() {
        return dlb.length;
    }

    // Muestra la máscara por pantalla
    public void mostrar() {
        for (int i = 0; i < dlb.length; i++) {
            System.out.print((dlb[i] ? 1 : 0) + " ");
        }

        System.out.println();
    }
}
