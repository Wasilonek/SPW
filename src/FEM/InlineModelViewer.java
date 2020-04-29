package FEM;

import DataLoader.Loader;
import DataLoader.Node;
import DataLoader.Point;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.stream.IntStream;

public class InlineModelViewer extends Application {

    private static final int VIEWPORT_SIZE = 800;

    private static final double MODEL_SCALE_FACTOR = 40;
    private static final double MODEL_X_OFFSET = 0;
    private static final double MODEL_Y_OFFSET = 0;
    private static final double MODEL_Z_OFFSET = VIEWPORT_SIZE / 2;

    private PhongMaterial texturedMaterial = new PhongMaterial();
	private Group group;
	private Cube[] cubes;

	private Group buildScene() {

		Node[][] nodes = new Node[2][8];
		nodes[0][0] = new Node(1, new Point(0.0, 0.0, 0.0), 0);
		nodes[0][1] = new Node(1, new Point(5.0, 0.0, 0.0), 0);
		nodes[0][2] = new Node(1, new Point(0.0, 5.0, 0.0), 0);
		nodes[0][3] = new Node(1, new Point(5.0, 5.0, 0.0), 0);

		nodes[0][4] = new Node(1, new Point(0.0, 0.0, 5.0), 0);
		nodes[0][5] = new Node(1, new Point(5.0, 0.0, 5.0), 0);
		nodes[0][6] = new Node(1, new Point(0.0, 5.0, 5.0), 0);
		nodes[0][7] = new Node(1, new Point(5.0, 5.0, 5.0), 0);


		nodes[1][0] = new Node(1, new Point(6.0, 5.0, 0.0), 0);
		nodes[1][1] = new Node(1, new Point(15.0, 5.0, 0.0), 0);
		nodes[1][2] = new Node(1, new Point(6.0, 10.0, 0.0), 0);
		nodes[1][3] = new Node(1, new Point(15.0, 10.0, 0.0), 0);

		nodes[1][4] = new Node(1, new Point(11.0, 0.0, 5.0), 0);
		nodes[1][5] = new Node(1, new Point(16.0, 0.0, 5.0), 0);
		nodes[1][6] = new Node(1, new Point(11.0, 5.0, 5.0), 0);
		nodes[1][7] = new Node(1, new Point(16.0, 5.0, 5.0), 0);

		cubes = new Cube[2];
		try {
			cubes[0] = new Cube(nodes[0]);
			cubes[1] = new Cube(nodes[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		texturedMaterial.setDiffuseMap(colorPalette());

		for (Cube c : cubes) {
			c.getMeshView().setMaterial(texturedMaterial);
			c.getMeshView().setDrawMode(DrawMode.FILL);
		}

		group = new Group();
		group.getChildren().add(cubes[0].getMeshView());
		group.getChildren().add(cubes[1].getMeshView());

		group.setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
		group.setTranslateY(VIEWPORT_SIZE / 2 * 9.0 / 16 + MODEL_Y_OFFSET);
		group.setTranslateZ(VIEWPORT_SIZE / 2 + MODEL_Z_OFFSET);
		group.setScaleX(MODEL_SCALE_FACTOR);
		group.setScaleY(MODEL_SCALE_FACTOR);
		group.setScaleZ(MODEL_SCALE_FACTOR);

		return group;
	}


	static int TEXTURE_SIZE = 64;
	private Image colorPalette() {
		WritableImage img = new WritableImage(TEXTURE_SIZE, TEXTURE_SIZE);
		PixelWriter pw = img.getPixelWriter();

		//right
		IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
				.forEach(y -> IntStream.range((int)(0* TEXTURE_SIZE), (int)(0.25* TEXTURE_SIZE)).boxed()
						.forEach(x -> pw.setColor(x, y, getColor(x-(int)(0* TEXTURE_SIZE), y-(int)(0.25* TEXTURE_SIZE), java.awt.Color.RED, java.awt.Color.RED, java.awt.Color.BLUE, java.awt.Color.BLUE))));

		//top
		IntStream.range((int)(0* TEXTURE_SIZE), (int)(0.25* TEXTURE_SIZE)).boxed()
				.forEach(y -> IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
						.forEach(x -> pw.setColor(x, y, getColor(x-(int)(0.25* TEXTURE_SIZE), y-(int)(0* TEXTURE_SIZE), java.awt.Color.RED, java.awt.Color.RED, java.awt.Color.RED, java.awt.Color.RED))));
		//back
		IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
				.forEach(y -> IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
						.forEach(x -> pw.setColor(x, y, getColor(x-(int)(0.25* TEXTURE_SIZE), y-(int)(0.25* TEXTURE_SIZE), java.awt.Color.RED, java.awt.Color.RED, java.awt.Color.BLUE, java.awt.Color.BLUE))));
		//left
		IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
				.forEach(y -> IntStream.range((int)(0.5* TEXTURE_SIZE), (int)(0.75* TEXTURE_SIZE)).boxed()
						.forEach(x -> pw.setColor(x, y, getColor(x-(int)(0.5* TEXTURE_SIZE), y-(int)(0.25* TEXTURE_SIZE), java.awt.Color.RED, java.awt.Color.RED, java.awt.Color.BLUE, java.awt.Color.BLUE))));
		//front
		IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
				.forEach(y -> IntStream.range((int)(0.75* TEXTURE_SIZE), (int)(1* TEXTURE_SIZE)).boxed()
						.forEach(x -> pw.setColor(x, y, getColor(x-(int)(0.75* TEXTURE_SIZE), y-(int)(0.25* TEXTURE_SIZE), java.awt.Color.RED, java.awt.Color.RED, java.awt.Color.BLUE, java.awt.Color.BLUE))));

		//down
		IntStream.range((int)(0.5* TEXTURE_SIZE), (int)(0.75* TEXTURE_SIZE)).boxed()
				.forEach(y -> IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
						.forEach(x -> pw.setColor(x, y, getColor(x-(int)(0.25* TEXTURE_SIZE), y-(int)(0.5* TEXTURE_SIZE), java.awt.Color.BLUE, java.awt.Color.BLUE, java.awt.Color.BLUE, java.awt.Color.BLUE))));

		// save for testing purposes
//		try {
//			ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File("palette" + ".png"));
//
//		} catch (IOException ex) {
//		}
		return img;
	}

	private static final float INT_TO_FLOAT_CONST = 1f / 255f;
	private java.awt.Color interpolateColor(final java.awt.Color COLOR1, final java.awt.Color COLOR2, float fraction)
	{
		fraction = Math.min(fraction, 1f);
		fraction = Math.max(fraction, 0f);

		final float RED1 = COLOR1.getRed() * INT_TO_FLOAT_CONST;
		final float GREEN1 = COLOR1.getGreen() * INT_TO_FLOAT_CONST;
		final float BLUE1 = COLOR1.getBlue() * INT_TO_FLOAT_CONST;
		final float ALPHA1 = COLOR1.getAlpha() * INT_TO_FLOAT_CONST;

		final float RED2 = COLOR2.getRed() * INT_TO_FLOAT_CONST;
		final float GREEN2 = COLOR2.getGreen() * INT_TO_FLOAT_CONST;
		final float BLUE2 = COLOR2.getBlue() * INT_TO_FLOAT_CONST;
		final float ALPHA2 = COLOR2.getAlpha() * INT_TO_FLOAT_CONST;

		final float DELTA_RED = RED2 - RED1;
		final float DELTA_GREEN = GREEN2 - GREEN1;
		final float DELTA_BLUE = BLUE2 - BLUE1;
		final float DELTA_ALPHA = ALPHA2 - ALPHA1;

		float red = RED1 + (DELTA_RED * fraction);
		float green = GREEN1 + (DELTA_GREEN * fraction);
		float blue = BLUE1 + (DELTA_BLUE * fraction);
		float alpha = ALPHA1 + (DELTA_ALPHA * fraction);

		red = Math.min(red, 1f);
		red = Math.max(red, 0f);
		green = Math.min(green, 1f);
		green = Math.max(green, 0f);
		blue = Math.min(blue, 1f);
		blue = Math.max(blue, 0f);
		alpha = Math.min(alpha, 1f);
		alpha = Math.max(alpha, 0f);

		return new java.awt.Color(red, green, blue, alpha);
	}

	/**
	 * Returns the color calculated by a bilinear interpolation by the two fractions in x and y direction.
	 * To get the color of the point defined by FRACTION_X and FRACTION_Y with in the rectangle defined by the
	 * for given colors we first calculate the interpolated color between COLOR_00 and COLOR_10 (x-direction) with
	 * the given FRACTION_X. After that we calculate the interpolated color between COLOR_01 and COLOR_11 (x-direction)
	 * with the given FRACTION_X. Now we interpolate between the two results of the former calculations (y-direction)
	 * with the given FRACTION_Y.
	 * @param COLOR_00 The color on the lower left corner of the square
	 * @param COLOR_10 The color on the lower right corner of the square
	 * @param COLOR_01 The color on the upper left corner of the square
	 * @param COLOR_11 The color on the upper right corner of the square
	 * @param FRACTION_X The fraction of the point in x direction (between COLOR_00 and COLOR_10 or COLOR_01 and COLOR_11) range: 0.0f .. 1.0f
	 * @param FRACTION_Y The fraction of the point in y direction (between COLOR_00 and COLOR_01 or COLOR_10 and COLOR_11) range: 0.0f .. 1.0f
	 * @return the color of the point defined by fraction_x and fraction_y in the square defined by the for colors
	 */
	private java.awt.Color bilinearInterpolateColor(final java.awt.Color COLOR_00, final java.awt.Color COLOR_10, final java.awt.Color COLOR_01, final java.awt.Color COLOR_11, final float FRACTION_X, final float FRACTION_Y)
	{
		final java.awt.Color INTERPOLATED_COLOR_X1 = interpolateColor(COLOR_00, COLOR_10, FRACTION_X);
		final java.awt.Color INTERPOLATED_COLOR_X2 = interpolateColor(COLOR_01, COLOR_11, FRACTION_X);
		return interpolateColor(INTERPOLATED_COLOR_X1, INTERPOLATED_COLOR_X2, FRACTION_Y);
	}

	private Color getColor(int x, int y, java.awt.Color color1, java.awt.Color color2, java.awt.Color color3, java.awt.Color color4){
		float xFraction = (float)x/((float) TEXTURE_SIZE /4);
		float yFraction = (float)y/((float) TEXTURE_SIZE /4);

		java.awt.Color awtColor = bilinearInterpolateColor(color1, color2, color3, color4, xFraction, yFraction);
		int r = awtColor.getRed();
		int g = awtColor.getGreen();
		int b = awtColor.getBlue();
		int a = awtColor.getAlpha();
		double opacity = a / 255.0 ;

		javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(r, g, b, opacity);
		return fxColor;
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
        CheckBox cull      = new CheckBox("Cull Back");
        cubes[0].getMeshView().cullFaceProperty().bind(
                Bindings.when(
                        cull.selectedProperty())
                        .then(CullFace.BACK)
                        .otherwise(CullFace.NONE)
        );
        CheckBox wireframe = new CheckBox("Wireframe");
		cubes[0].getMeshView().drawModeProperty().bind(
                Bindings.when(
                        wireframe.selectedProperty())
                        .then(DrawMode.LINE)
                        .otherwise(DrawMode.FILL)
        );

        CheckBox rotate = new CheckBox("Rotate");
        rotate.selectedProperty().addListener(observable -> {
            if (rotate.isSelected()) {
                rotateTransition.play();
            } else {
                rotateTransition.pause();
            }
        });

//        CheckBox texture = new CheckBox("Texture");
//		cubes[0].getMeshView().materialProperty().bind(
//                Bindings.when(
//                        texture.selectedProperty())
//                        .then(texturedMaterial)
//                        .otherwise((PhongMaterial) null)
//        );

        //VBox controls = new VBox(10, rotate, texture, cull, wireframe);
		VBox controls = new VBox(10, rotate, cull, wireframe);
        controls.setPadding(new Insets(10));
        return controls;
    }

    private RotateTransition rotate3dGroup(Group group) {
        RotateTransition rotate = new RotateTransition(Duration.seconds(5), group);
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