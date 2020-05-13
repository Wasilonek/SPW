package DataLoader;

import javafx.stage.FileChooser;

import java.io.File;
import java.util.*;

public class Loader {
    private List<Node> listOfNodes;
    private List<Element> listOfElements;

    public List<Node> getListOfNodes() {
        return listOfNodes;
    }

    public List<Element> getListOfElements() {
        return listOfElements;
    }

    public String getFileName() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            return selectedFile.toString();
        }
        return "";
    }

    public void loadDataFromFile(String filePath) throws Exception {
        listOfNodes = new ArrayList<>();
        listOfElements = new ArrayList<>();

        String section = "";
        String[] splittedLine;

        try (Scanner scanner = new Scanner(new File(filePath))) {

            while (scanner.hasNext()) {

                String currentLine = scanner.nextLine().trim();

                if (currentLine.equals("*NODE")) {
                    section = "NODE";
                    continue;
                }

                if (currentLine.equals("*ELEMENT_SOLID")) {
                    section = "ELEMENT";
                    continue;
                }

                if (currentLine.equals("*END")) {
                    break;
                }

                switch (section) {
                    case "NODE":
                        splittedLine = currentLine.split(",");

                        int id = Integer.parseInt(splittedLine[0].trim());
                        double x = Double.parseDouble(splittedLine[1].trim());
                        double y = Double.parseDouble(splittedLine[2].trim());
                        double z = Double.parseDouble(splittedLine[3].trim());
                        double prop = Double.parseDouble(splittedLine[4].trim());

                        listOfNodes.add(
                                new Node(id, new Point(x, y, z), prop));
                        break;

                    case "ELEMENT":
                        splittedLine = currentLine.split(",");

                        int elementId = Integer.parseInt(splittedLine[0].trim());
                        int column = Integer.parseInt(splittedLine[1].trim());

                        String[] nodesAsString = Arrays.copyOfRange(splittedLine, 2, splittedLine.length);
                        int[] nodes = new int[nodesAsString.length];

                        for (int i = 0; i < nodesAsString.length; i++) {
                            nodes[i] = (Integer.parseInt(nodesAsString[i].trim()));
                        }

                        listOfElements.add(
                                new Element(elementId, column, nodes));
                        break;
                }

            }
        } catch (Exception o) {
            throw new Exception();
        }
    }
}
