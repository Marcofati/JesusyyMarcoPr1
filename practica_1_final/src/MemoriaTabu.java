import java.util.HashMap;
import java.util.Map;

/** MCP y MLP dispersas: almacenan únicamente pares usados, evitando reservar dos matrices n*n. */
public class MemoriaTabu {
    private final Map<Long, Integer> mcp = new HashMap<>();
    private final Map<Long, Integer> mlp = new HashMap<>();
    private final int tenencia;
    public MemoriaTabu(int tenencia) { this.tenencia = tenencia; }

    private long clave(int a, int b) {
        int menor = Math.min(a, b), mayor = Math.max(a, b);
        return (((long) menor) << 32) | (mayor & 0xffffffffL);
    }
    public boolean esTabu(int a, int b) { return mcp.getOrDefault(clave(a, b), 0) > 0; }
    public void marcarTabu(int a, int b) { mcp.put(clave(a, b), tenencia); }
    public void avanzarIteracion() {
        for (Map.Entry<Long, Integer> e : mcp.entrySet()) e.setValue(e.getValue() - 1);
        mcp.entrySet().removeIf(e -> e.getValue() <= 0);
    }
    public void registrarArco(int a, int b) { long k = clave(a, b); mlp.put(k, mlp.getOrDefault(k, 0) + 1); }
    public int frecuenciaArco(int a, int b) { return mlp.getOrDefault(clave(a, b), 0); }
    public void registrarSolucion(Solucion s) {
        java.util.ArrayList<Integer> r = s.getRecorrido();
        for (int i = 0; i < r.size(); i++) registrarArco(r.get(i), r.get((i + 1) % r.size()));
    }
    public void reiniciarMCP() { mcp.clear(); }
    public int cantidadArcosRegistrados() { return mlp.size(); }
}
