public class Ciudad {

    private int id;
    private double x;
    private double y;

    public Ciudad(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double distancia(Ciudad otra) {
        double dx = x - otra.x;
        double dy = y - otra.y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public String toString() {
        return id + " (" + x + ", " + y + ")";
    }
}
