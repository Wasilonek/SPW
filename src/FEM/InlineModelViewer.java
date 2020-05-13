package FEM;

import DataLoader.Element;
import DataLoader.Loader;
import DataLoader.Node;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InlineModelViewer extends Application {

	private static final int VIEWPORT_SIZE = 1000;
	private static final double MODEL_SCALE_FACTOR = 15000;
	private static final double MODEL_X_OFFSET = 0;
	private static final double MODEL_Y_OFFSET = 0;
	private static final double MODEL_Z_OFFSET = VIEWPORT_SIZE / 2;

	private double mouseOldX, mouseOldY = 0;
	private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
	private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
	private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
	private PerspectiveCamera camera;

	private PhongMaterial[] texturedMaterial;
	private Group group = new Group();
	private Cube[] cubes;

	private List<Element> listOfElements;
	private List<Node> listOfNodes;

	private List<Cube> listOfCubesX = new ArrayList<>();
	private List<Cube> listOfCubesY = new ArrayList<>();
	private List<Cube> listOfCubesZ = new ArrayList<>();

	private ColorPicker minColorPicker = new ColorPicker();
	private ColorPicker midColorPicker = new ColorPicker();
	private ColorPicker maxColorPicker = new ColorPicker();

	private double minProp, maxProp;
	private Label minPropLabel = new Label("Minimum property = ");
	private Label maxPropLabel = new Label("Maximum property = ");

	private Group buildScene() {

		if (!(listOfElements == null || listOfNodes == null)) {

			listOfCubesX = new ArrayList<>();
			listOfCubesY = new ArrayList<>();
			listOfCubesZ = new ArrayList<>();

			HashMap<Integer, Node> hm = new HashMap<>();

			double minX, maxX, minY, maxY, minZ, maxZ;
			minX = maxX = listOfNodes.get(0).getCords().getX();
			minY = maxY = listOfNodes.get(0).getCords().getY();
			minZ = maxZ = listOfNodes.get(0).getCords().getZ();

			minProp = maxProp = listOfNodes.get(0).getProp();

			for (Node node : listOfNodes) {
				hm.put(node.getId(), node);
				if (node.getProp() < minProp) {
					minProp = node.getProp();
				}
				if (node.getProp() > maxProp) {
					maxProp = node.getProp();
				}


				if (node.getCords().getX() < minX) {
					minX = node.getCords().getX();
				}
				if (node.getCords().getX() > maxX) {
					maxX = node.getCords().getX();
				}

				if (node.getCords().getY() < minY) {
					minY = node.getCords().getY();
				}
				if (node.getCords().getY() > maxY) {
					maxY = node.getCords().getY();
				}

				if (node.getCords().getZ() < minZ) {
					minZ = node.getCords().getZ();
				}
				if (node.getCords().getZ() > maxZ) {
					maxZ = node.getCords().getZ();
				}
			}

			minPropLabel.setText("Minimum property = " + minProp);
			maxPropLabel.setText("Maximum property = " + maxProp);

			//group nodes into corresponding elements
			Node[][] nodesLoader = new Node[listOfElements.size()][8];
			int counter = 0;
			for (Element element : listOfElements) {

				int counter2 = 0;
				for (int el : element.getElementNodes()) {
					nodesLoader[counter][counter2++] = hm.get(el);
				}
				counter++;
			}

			//generate elements for 3D scene
			cubes = new Cube[listOfElements.size()];
			try {
				for (int i = 0; i < listOfElements.size(); i++) {
					cubes[i] = new Cube(nodesLoader[i]);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}


			//set texture to each element
			texturedMaterial = new PhongMaterial[listOfElements.size()];

			java.awt.Color[] cubeColors = new java.awt.Color[8];
			counter = 0;
			for (Cube cube : cubes) {
				setCubeColors(cubeColors, minProp, maxProp, cube);
				texturedMaterial[counter] = new PhongMaterial();
				texturedMaterial[counter].setDiffuseMap(ColorInterpolation.colorPalette(cubeColors[0], cubeColors[1], cubeColors[2], cubeColors[3], cubeColors[4], cubeColors[5], cubeColors[6], cubeColors[7]));
				cube.getMeshView().setMaterial(texturedMaterial[counter]);
				cube.getMeshView().setDrawMode(DrawMode.FILL);
				counter++;
			}

			double minCubeX, minCubeY, minCubeZ;
			for (Cube cube : cubes) {
				group.getChildren().add(cube.getMeshView());

				minCubeX = getCubeMinX(cube);
				if (minCubeX < ((minX+maxX)/2.0))
					listOfCubesX.add(cube);

				minCubeY = getCubeMinY(cube);
				if (minCubeY < ((minY+maxY)/2.0))
					listOfCubesY.add(cube);

				minCubeZ = getCubeMinZ(cube);
				if (minCubeZ > ((minZ+maxZ)/2.0))
					listOfCubesZ.add(cube);
			}

			group.getChildren().add(new AmbientLight());
			group.setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
			group.setTranslateY(VIEWPORT_SIZE / 2 * 9.0 / 16 + MODEL_Y_OFFSET);
			group.setTranslateZ(VIEWPORT_SIZE / 2 + MODEL_Z_OFFSET);
			group.setScaleX(MODEL_SCALE_FACTOR);
			group.setScaleY(MODEL_SCALE_FACTOR);
			group.setScaleZ(MODEL_SCALE_FACTOR);
		}

		return group;
	}

	private double getCubeMinX(Cube cube) {
		double minX = cube.getNodes()[0].getCords().getX();

		for (Node node : cube.getNodes()){
			if (minX < node.getCords().getX()){
				minX = node.getCords().getX();
			}
		}

		return minX;
	}

	private double getCubeMinY(Cube cube) {
		double minY = cube.getNodes()[0].getCords().getY();

		for (Node node : cube.getNodes()){
			if (minY < node.getCords().getY()){
				minY = node.getCords().getY();
			}
		}

		return minY;
	}

	private double getCubeMinZ(Cube cube) {
		double minZ = cube.getNodes()[0].getCords().getZ();

		for (Node node : cube.getNodes()){
			if (minZ < node.getCords().getZ()){
				minZ = node.getCords().getZ();
			}
		}

		return minZ;
	}

	private void setCubeColors(java.awt.Color[] cubeColors, double min, double max, Cube cube) {
		for (int i = 0; i < 8; i++) {
			cubeColors[i] = setColor(min, max, cube.getNodes()[i].getProp());
		}
	}

	private java.awt.Color setColor(double min, double max, double prop) {
		java.awt.Color outColor = null;
		double fraction = 0.0;
		double s = (min + max) / 2.0;

		java.awt.Color minColor = new java.awt.Color(
				(float)minColorPicker.getValue().getRed(),
				(float)minColorPicker.getValue().getGreen(),
				(float)minColorPicker.getValue().getBlue(),
				(float)minColorPicker.getValue().getOpacity());

		java.awt.Color midColor = new java.awt.Color(
				(float)midColorPicker.getValue().getRed(),
				(float)midColorPicker.getValue().getGreen(),
				(float)midColorPicker.getValue().getBlue(),
				(float)midColorPicker.getValue().getOpacity());

		java.awt.Color maxColor = new java.awt.Color(
				(float)maxColorPicker.getValue().getRed(),
				(float)maxColorPicker.getValue().getGreen(),
				(float)maxColorPicker.getValue().getBlue(),
				(float)maxColorPicker.getValue().getOpacity());

		if (prop > s) {
			fraction = (prop - s) / (max - s);
			outColor = ColorInterpolation.interpolateColor(midColor, maxColor, (float) fraction);
		} else {
			fraction = (prop - min) / (s - min);
			outColor = ColorInterpolation.interpolateColor(minColor, midColor, (float) fraction);
		}

		return outColor;
	}

	@Override
	public void start(Stage stage) {
		RotateTransition rotate = rotate3dGroup(group);

		VBox layout = new VBox(
				createControls(rotate),
				createScene3D(group)
		);

		stage.setTitle("Model Viewer");
		layout.setPrefSize(1000, 630);
		Scene scene = new Scene(layout, Color.CORNSILK);
		stage.setScene(scene);

		stage.show();
	}

	private SubScene createScene3D(Group group) {
		SubScene scene3d = new SubScene(group, VIEWPORT_SIZE, VIEWPORT_SIZE * 9.0 / 16, true, SceneAntialiasing.BALANCED);
		scene3d.setFill(Color.rgb(146, 181, 174));
//		scene3d.setCamera(new PerspectiveCamera());

		camera = new PerspectiveCamera(false);
		scene3d.setCamera(camera);
		camera.setTranslateX(0);
		camera.setTranslateY(0);
		camera.setTranslateZ(0);
		camera.setNearClip(0.1);
		camera.setFarClip(1000.0);

		camera.getTransforms().addAll(rotateX, rotateY, new Translate(0, 0, 0));
		group.setRotationAxis(Rotate.Y_AXIS);
		group.setRotate(200);
		rotateX.setPivotX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
		rotateX.setPivotY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
		rotateX.setPivotZ(VIEWPORT_SIZE / 2);

		rotateY.setPivotX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
		rotateY.setPivotY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
		rotateY.setPivotZ(VIEWPORT_SIZE / 2);

		rotateZ.setPivotX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
		rotateZ.setPivotY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
		rotateZ.setPivotZ(VIEWPORT_SIZE / 2);

		group.setScaleX(MODEL_SCALE_FACTOR);
		group.setScaleY(MODEL_SCALE_FACTOR);
		group.setScaleZ(MODEL_SCALE_FACTOR);

		scene3d.setOnScroll(event -> {
			double zoomFactor = 1.05;
			double deltaY = event.getDeltaY();
			if (deltaY < 0) {
				zoomFactor = 2.0 - zoomFactor;
			}
			group.setScaleX(group.getScaleX() * zoomFactor);
			group.setScaleY(group.getScaleY() * zoomFactor);
			group.setScaleZ(group.getScaleZ() * zoomFactor);
			event.consume();
		});

		scene3d.setOnMousePressed(event -> {
			mouseOldX = event.getSceneX();
			mouseOldY = event.getSceneY();
		});

		scene3d.setOnMouseDragged(event -> {
			rotateX.setAngle(rotateX.getAngle() - (event.getSceneY() - mouseOldY));
			rotateY.setAngle(rotateY.getAngle() + (event.getSceneX() - mouseOldX));
			mouseOldX = event.getSceneX();
			mouseOldY = event.getSceneY();

		});

		return scene3d;
	}

	private VBox createControls(RotateTransition rotateTransition) {
		CheckBox cull = new CheckBox("Cull Back");
		CheckBox wireframe = new CheckBox("Wireframe");
		CheckBox rotate = new CheckBox("Rotate");

		CheckBox xView = new CheckBox("X");
		CheckBox yView = new CheckBox("Y");
		CheckBox zView = new CheckBox("Z");

		minColorPicker.setValue(Color.BLUE);
		midColorPicker.setValue(Color.YELLOW);
		maxColorPicker.setValue(Color.RED);

		minColorPicker.setMinHeight(25);
		midColorPicker.setMinHeight(25);
		maxColorPicker.setMinHeight(25);

		Label minColorLabel = new Label("Minimum Color");
		Label midColorLabel = new Label("Middle Color");
		Label maxColorLabel = new Label("Maximum Color");

		minColorLabel.setPrefHeight(25);
		midColorLabel.setPrefHeight(25);
		maxColorLabel.setPrefHeight(25);

		Button loadData = new Button("Load data");
		loadData.setOnAction(event -> {
			Loader loader = new Loader();
			String fileName = "";
			try {
				fileName = loader.getFileName();
				if (!fileName.equals("")) {
					xView.setSelected(false);
					yView.setSelected(false);
					zView.setSelected(false);
					cull.setSelected(false);
					wireframe.setSelected(false);
					rotate.setSelected(false);

					loader.loadDataFromFile(fileName);

					//convert nodes from list to map - for easy processing
					listOfElements = loader.getListOfElements();
					listOfNodes = loader.getListOfNodes();
					group.getChildren().removeAll(group.getChildren());
					buildScene();

					if (cubes != null && cubes.length > 0) {
						for (Cube cube : cubes) {
							cube.getMeshView().cullFaceProperty().bind(
									Bindings.when(
											cull.selectedProperty())
											.then(CullFace.BACK)
											.otherwise(CullFace.NONE)
							);
						}

						for (Cube cube : cubes) {
							cube.getMeshView().drawModeProperty().bind(
									Bindings.when(
											wireframe.selectedProperty())
											.then(DrawMode.LINE)
											.otherwise(DrawMode.FILL)
							);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		xView.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (listOfCubesX != null) {
				if (newValue) {
					yView.setDisable(true);
					zView.setDisable(true);
					for (Cube cube : listOfCubesX)
						group.getChildren().remove(cube.getMeshView());
				} else {
					yView.setDisable(false);
					zView.setDisable(false);
					for (Cube cube : listOfCubesX)
						group.getChildren().addAll(cube.getMeshView());
				}
			}
		});

		yView.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (listOfCubesY != null) {
				if (newValue) {
					xView.setDisable(true);
					zView.setDisable(true);
					for (Cube cube : listOfCubesY)
						group.getChildren().remove(cube.getMeshView());
				} else {
					xView.setDisable(false);
					zView.setDisable(false);
					for (Cube cube : listOfCubesY)
						group.getChildren().addAll(cube.getMeshView());
				}
			}
		});

		zView.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (listOfCubesZ != null) {
				if (newValue) {
					xView.setDisable(true);
					yView.setDisable(true);
					for (Cube cube : listOfCubesZ)
						group.getChildren().remove(cube.getMeshView());
				} else {
					xView.setDisable(false);
					yView.setDisable(false);
					for (Cube cube : listOfCubesZ)
						group.getChildren().addAll(cube.getMeshView());
				}
			}
		});

		rotate.selectedProperty().addListener(observable -> {
			if (rotate.isSelected()) {
				rotateTransition.play();
			} else {
				rotateTransition.pause();
			}
		});

		HBox controls1 = new HBox(10, loadData, minPropLabel, maxPropLabel);
		HBox controls2 = new HBox(10, minColorLabel, minColorPicker, midColorLabel, midColorPicker, maxColorLabel, maxColorPicker);
		HBox controls3 = new HBox(10, xView, yView, zView, rotate, cull, wireframe);
		VBox controls = new VBox(10, controls1, controls2, controls3);
		controls.setPadding(new Insets(10));
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