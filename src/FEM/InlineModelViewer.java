package FEM;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class InlineModelViewer extends Application {

    private static final int VIEWPORT_SIZE = 800;

    private static final double MODEL_SCALE_FACTOR = 40;
    private static final double MODEL_X_OFFSET = 0;
    private static final double MODEL_Y_OFFSET = 0;
    private static final double MODEL_Z_OFFSET = VIEWPORT_SIZE / 2;

    private static final String textureLoc = "https://www.sketchuptextureclub.com/public/texture_f/slab-marble-emperador-cream-light-preview.jpg";
    public static final int SIZE = 5;

    private Image texture;
    private PhongMaterial texturedMaterial = new PhongMaterial();

    private MeshView meshView;

    private MeshView loadMeshView(Point3D[] points3D) {

        float[] points = {
                (float)points3D[0].getX(), (float)points3D[0].getY(), (float)points3D[0].getZ(),    //P0
                (float)points3D[1].getX(), (float)points3D[1].getY(), (float)points3D[1].getZ(),    //P1
                (float)points3D[2].getX(), (float)points3D[2].getY(), (float)points3D[2].getZ(),    //P2
                (float)points3D[3].getX(), (float)points3D[3].getY(), (float)points3D[3].getZ(),    //P3
                (float)points3D[4].getX(), (float)points3D[4].getY(), (float)points3D[4].getZ(),    //P4
                (float)points3D[5].getX(), (float)points3D[5].getY(), (float)points3D[5].getZ(),    //P5
                (float)points3D[6].getX(), (float)points3D[6].getY(), (float)points3D[6].getZ(),    //P6
                (float)points3D[7].getX(), (float)points3D[7].getY(), (float)points3D[7].getZ()     //P7
        };

        float[] texCoords = {
                0,0
        };

        int[] faces = {
                //top
                0,0,    4,0,    5,0,
                1,0,    0,0,    5,0,

                //left
                6,0,    4,0,    0,0,
                2,0,    6,0,    0,0,

                //front
                2,0,    0,0,    1,0,
                3,0,    2,0,    1,0,

                //right
                3,0,    1,0,    5,0,
                7,0,    3,0,    5,0,

                //back
                7,0,    5,0,    4,0,
                6,0,    7,0,    4,0,

                //down
                6,0,    2,0,    3,0,
                7,0,    6,0,    3,0
        };

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().setAll(points);
        mesh.getTexCoords().setAll(texCoords);
        mesh.getFaces().setAll(faces);

        return new MeshView(mesh);
    }

    private Group buildScene() {
        Point3D[] point3DS = new Point3D[8];
        point3DS[0] = new Point3D(0, 0, SIZE);
        point3DS[1] = new Point3D(SIZE, 0, SIZE);
        point3DS[2] = new Point3D(0, SIZE, SIZE);
        point3DS[3] = new Point3D(SIZE, SIZE, SIZE);
        point3DS[4] = new Point3D(0, 0, 0);
        point3DS[5] = new Point3D(SIZE, 0, 0);
        point3DS[6] = new Point3D(0, SIZE, 0);
        point3DS[7] = new Point3D(SIZE, SIZE, 0);
        meshView = loadMeshView(point3DS);

//        meshView.setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
//        meshView.setTranslateY(VIEWPORT_SIZE / 2 * 9.0 / 16 + MODEL_Y_OFFSET);
//        meshView.setTranslateZ(VIEWPORT_SIZE / 2 + MODEL_Z_OFFSET);
//        meshView.setScaleX(MODEL_SCALE_FACTOR);
//        meshView.setScaleY(MODEL_SCALE_FACTOR);
//        meshView.setScaleZ(MODEL_SCALE_FACTOR);

        Point3D[] point3DS_2 = new Point3D[8];
        point3DS_2[0] = new Point3D(0+SIZE, 0+SIZE, SIZE+SIZE);
        point3DS_2[1] = new Point3D(SIZE+SIZE, 0+SIZE, SIZE+SIZE);
        point3DS_2[2] = new Point3D(0+SIZE, SIZE+SIZE, SIZE+SIZE);
        point3DS_2[3] = new Point3D(SIZE+SIZE, SIZE+SIZE, SIZE+SIZE);
        point3DS_2[4] = new Point3D(0+SIZE, 0+SIZE, 0+SIZE);
        point3DS_2[5] = new Point3D(SIZE+SIZE, 0+SIZE, 0+SIZE);
        point3DS_2[6] = new Point3D(0+SIZE, SIZE+SIZE, 0+SIZE);
        point3DS_2[7] = new Point3D(SIZE+SIZE, SIZE+SIZE, 0+SIZE);
        MeshView meshView_2 = loadMeshView(point3DS_2);

        Group group = new Group();
        group.getChildren().add(meshView);
        group.getChildren().add(meshView_2);

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
        texture = new Image(textureLoc);
//        texturedMaterial.setDiffuseMap(texture);
        texturedMaterial.setDiffuseColor(Color.BLUE);

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
        scene3d.setFill(Color.rgb(122, 239, 231));
        scene3d.setCamera(new PerspectiveCamera());
        return scene3d;
    }

    private VBox createControls(RotateTransition rotateTransition) {
        CheckBox cull      = new CheckBox("Cull Back");
        meshView.cullFaceProperty().bind(
                Bindings.when(
                        cull.selectedProperty())
                        .then(CullFace.BACK)
                        .otherwise(CullFace.NONE)
        );
        CheckBox wireframe = new CheckBox("Wireframe");
        meshView.drawModeProperty().bind(
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

        CheckBox texture = new CheckBox("Texture");
        meshView.materialProperty().bind(
                Bindings.when(
                        texture.selectedProperty())
                        .then(texturedMaterial)
                        .otherwise((PhongMaterial) null)
        );

        VBox controls = new VBox(10, rotate, texture, cull, wireframe);
        controls.setPadding(new Insets(10));
        return controls;
    }

    private RotateTransition rotate3dGroup(Group group) {
        RotateTransition rotate = new RotateTransition(Duration.seconds(10), group);
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