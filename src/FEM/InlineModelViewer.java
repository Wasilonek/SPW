package FEM;

import DataLoader.Element;
import DataLoader.Loader;
import DataLoader.Node;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.List;

public class InlineModelViewer extends Application {

    private static final int VIEWPORT_SIZE = 1000;
    private static final double MODEL_SCALE_FACTOR = 18000;
    private static final double MODEL_X_OFFSET = 0;
    private static final double MODEL_Y_OFFSET = 0;
    private static final double MODEL_Z_OFFSET = VIEWPORT_SIZE / 2;

    private PhongMaterial texturedMaterial = new PhongMaterial();
	private Group group;
	private Cube[] cubes;

	private Group buildScene() {

		//loade data from file
		Loader loader = new Loader();
		try {
			String fileName = loader.getFileName();
			if(!fileName.equals("")) {
				loader.loadDataFromFile(fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//convert nodes from list to map - for easy processing
		List<Element> listOfElements = loader.getListOfElements();
		List<Node> listOfNodes = loader.getListOfNodes();
		HashMap<Integer, Node> hm = new HashMap<>();
		for (Node node : listOfNodes){
			hm.put(node.getId(), node);
		}

		//group nodes into corresponding elements
		Node[][] nodesLoader = new Node[listOfElements.size()][8];
		int counter = 0;
		for (Element element : listOfElements){

			int counter2 = 0;
			for (int el : element.getElementNodes()){
				nodesLoader[counter][counter2++] = hm.get(el);
			}
			counter++;
		}

		//generate elements for 3D scene
		cubes = new Cube[listOfElements.size()];
		try {
			for (int i = 0; i < listOfElements.size(); i++){
				cubes[i] = new Cube(nodesLoader[i]);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		//set texture to each element
		texturedMaterial.setDiffuseMap(ColorInterpolation.colorPalette());
		for (Cube c : cubes) {
			c.getMeshView().setMaterial(texturedMaterial);
			c.getMeshView().setDrawMode(DrawMode.FILL);
		}

		//add all elements to scene
		group = new Group();
		for (Cube cube: cubes) {
			group.getChildren().add(cube.getMeshView());
		}
		group.setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
		group.setTranslateY(VIEWPORT_SIZE / 2 * 9.0 / 16 + MODEL_Y_OFFSET);
		group.setTranslateZ(VIEWPORT_SIZE / 2 + MODEL_Z_OFFSET);
		group.setScaleX(MODEL_SCALE_FACTOR);
		group.setScaleY(MODEL_SCALE_FACTOR);
		group.setScaleZ(MODEL_SCALE_FACTOR);

		return group;
	}

    @Override
    public void start(Stage stage) {
        Group group = buildScene();
        RotateTransition rotate = rotate3dGroup(group);

        VBox layout = new VBox(
                createControls(rotate),
                createScene3D(group)
        );

        stage.setTitle("Model Viewer");
		layout.setPrefSize(1000, 700);
        Scene scene = new Scene(layout, Color.CORNSILK);
        stage.setScene(scene);

        stage.show();
    }

    private SubScene createScene3D(Group group) {
        SubScene scene3d = new SubScene(group, VIEWPORT_SIZE, VIEWPORT_SIZE * 9.0/16, true, SceneAntialiasing.BALANCED);
		scene3d.setFill(Color.rgb(146, 181, 174));
		scene3d.setCamera(new PerspectiveCamera());
        return scene3d;
    }

    private VBox createControls(RotateTransition rotateTransition) {
        CheckBox cull = new CheckBox("Cull Back");
		for (Cube cube: cubes) {
			cube.getMeshView().cullFaceProperty().bind(
					Bindings.when(
							cull.selectedProperty())
							.then(CullFace.BACK)
							.otherwise(CullFace.NONE)
			);
		}

        CheckBox wireframe = new CheckBox("Wireframe");
		for (Cube cube: cubes) {
			cube.getMeshView().drawModeProperty().bind(
					Bindings.when(
							wireframe.selectedProperty())
							.then(DrawMode.LINE)
							.otherwise(DrawMode.FILL)
			);
		}

        CheckBox rotate = new CheckBox("Rotate");
        rotate.selectedProperty().addListener(observable -> {
            if (rotate.isSelected()) {
                rotateTransition.play();
            } else {
                rotateTransition.pause();
            }
        });

        CheckBox texture = new CheckBox("Texture");
        for (Cube cube: cubes) {
			cube.getMeshView().materialProperty().bind(
					Bindings.when(
							texture.selectedProperty())
							.then(texturedMaterial)
							.otherwise((PhongMaterial) null)
			);
		}

        VBox controls = new VBox(10, rotate, texture, cull, wireframe);
        controls.setPadding(new Insets(20));
        return controls;
    }

    private RotateTransition rotate3dGroup(Group group) {
        RotateTransition rotate = new RotateTransition(Duration.seconds(20), group);
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.setCycleCount(RotateTransition.INDEFINITE);

        return rotate;
    }

    public static void main(String[] args) {
        System.setProperty("prism.dirtyopts", "false");
        launch(args);
    }
}