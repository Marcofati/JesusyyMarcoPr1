public class Ciudad {
    private final int id;
    private final double x;
    private final double y;
    public Ciudad(int id, double x, double y) { this.id = id; this.x = x; this.y = y; }
    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double distancia(Ciudad otra) {
        double dx = x - otra.x, dy = y - otra.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    @Override public String toString() { return id + " (" + x + ", " + y + ")"; }
}
