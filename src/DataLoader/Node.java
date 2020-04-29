package DataLoader;

public class Node {
    private int id;
    private Point cords;
    private double prop;

    public Node(int id, Point cords, double prop) {
        this.id = id;
        this.cords = cords;
        this.prop = prop;
    }

    public int getId() {
        return id;
    }

    public Point getCords() {
        return cords;
    }

    public double getProp() {
        return prop;
    }
}
