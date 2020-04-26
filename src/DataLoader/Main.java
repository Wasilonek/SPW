package DataLoader;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class Main extends Application  {

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("HBox Experiment 1");

        Button button = new Button("My Button");
        button.setOnAction((actionEvent -> {
           Loader loader = new Loader();
            try {
                String fileName = loader.getFileName();
                if(!fileName.equals("")) {
                    loader.loadDataFromFile(fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        Scene scene = new Scene(button, 200, 100);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}