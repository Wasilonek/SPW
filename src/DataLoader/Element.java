package DataLoader;

public class Element {
    private int id;
    private int column;
    private int[] elementNodes;

    Element(int id, int column, int[] nodes) {
        this.id = id;
        this.column = column;
        this.elementNodes = nodes;
    }

    public int getId() {
        return id;
    }

    public int getColumn() {
        return column;
    }

    public int[] getElementNodes() {
        return elementNodes;
    }
}
