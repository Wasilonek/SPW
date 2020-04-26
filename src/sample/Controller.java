package sample;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private SubScene    sub_scene;
    private Group       group;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        group = new Group();
        sub_scene = new SubScene(group, 200, 200);
        sub_scene.setFill(Color.SILVER);
    }

    public void say_hello(ActionEvent actionEvent) {
        Sphere sphere = new Sphere(10);
        group.getChildren().add(sphere);
    }


}
