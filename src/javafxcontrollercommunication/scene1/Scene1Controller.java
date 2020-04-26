package javafxcontrollercommunication.scene1;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafxcontrollercommunication.scene2.Scene2Controller;

/**
 *
 * @author Genuine Coder
 */
public class Scene1Controller implements Initializable {

    @FXML
    private TextField inputField;
    @FXML
    private Button actionBtn;

    private int WIDTH = 500, HEIGHT = 500;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        actionBtn.setOnAction(event -> {
            loadSceneAndSendMessage();
        });
    }
    
    public void receiveMessage(String message){
        
    }

    private Box createAndGetBox(){
        Box box = new Box(100, 100, 100);

        Rotate rxBox = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
        Rotate ryBox = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        Rotate rzBox = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
        rxBox.setAngle(30);
        ryBox.setAngle(50);
        rzBox.setAngle(30);
        box.getTransforms().addAll(rxBox, ryBox, rzBox);

        box.translateXProperty().set(WIDTH/2);
        box.translateYProperty().set(HEIGHT/2);

        return box;
    }

    private MeshView createAndGetPyramid(int tX, int tY, int tZ){
        TriangleMesh pyramidMesh = new TriangleMesh();
        pyramidMesh.getTexCoords().addAll(0,0);

        float h = 150;                    // Height
        float s = 300;                    // Side
        pyramidMesh.getPoints().addAll(
                0,    0,    0,            // Point 0 - Top
                0,    h,    -s/2,         // Point 1 - Front
                -s/2, h,    0,            // Point 2 - Left
                s/2,  h,    0,            // Point 3 - Back
                0,    h,    s/2           // Point 4 - Right
        );

        pyramidMesh.getFaces().addAll(
                0,0,  2,0,  1,0,          // Front left face
                0,0,  1,0,  3,0,          // Front right face
                0,0,  3,0,  4,0,          // Back right face
                0,0,  4,0,  2,0,          // Back left face
                4,0,  1,0,  2,0,          // Bottom rear face
                4,0,  3,0,  1,0           // Bottom front face
        );

        MeshView pyramid = new MeshView(pyramidMesh);
        pyramid.setDrawMode(DrawMode.FILL);
        pyramid.setTranslateX(tX);
        pyramid.setTranslateY(tY);
        pyramid.setTranslateZ(tZ);

        return pyramid;
    }

    private void loadSceneAndSendMessage() {
        try {
            Group group = new Group();

//            group.getChildren().add(createAndGetBox());
            group.getChildren().add(createAndGetPyramid(100, 100, 0));
            group.getChildren().add(createAndGetPyramid(300, 300, 100));



            Scene scene = new Scene(group, WIDTH, HEIGHT);
            scene.setFill(Color.SILVER);


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/javafxcontrollercommunication/scene2/scene2.fxml"));
            Parent root = loader.load();
            
            //Get controller of scene2
            Scene2Controller scene2Controller = loader.getController();
            scene2Controller.transferMessage(inputField.getText());
            
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Second Window");
            stage.show();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
