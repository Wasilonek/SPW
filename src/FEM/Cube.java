package FEM;

import DataLoader.Node;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

public class Cube {

	private Node[] nodes;
	private TriangleMesh mesh;
	private MeshView meshView;

	public Cube(Node[] nodes) throws Exception {
		if (nodes.length != 8) throw new Exception("List of nodes should contain 8 elements!");

		this.nodes = nodes;
		createCube();
	}

	public TriangleMesh getMesh() {
		return mesh;
	}

	public MeshView getMeshView() {
		return meshView;
	}

	public void createCube() {
		float[] points = {
				(float) nodes[0].getCords().getX(), (float) nodes[0].getCords().getY(), (float) nodes[0].getCords().getZ(),    //P0
				(float) nodes[1].getCords().getX(), (float) nodes[1].getCords().getY(), (float) nodes[1].getCords().getZ(),    //P1
				(float) nodes[2].getCords().getX(), (float) nodes[2].getCords().getY(), (float) nodes[2].getCords().getZ(),    //P2
				(float) nodes[3].getCords().getX(), (float) nodes[3].getCords().getY(), (float) nodes[3].getCords().getZ(),    //P3
				(float) nodes[4].getCords().getX(), (float) nodes[4].getCords().getY(), (float) nodes[4].getCords().getZ(),    //P4
				(float) nodes[5].getCords().getX(), (float) nodes[5].getCords().getY(), (float) nodes[5].getCords().getZ(),    //P5
				(float) nodes[6].getCords().getX(), (float) nodes[6].getCords().getY(), (float) nodes[6].getCords().getZ(),    //P0
				(float) nodes[7].getCords().getX(), (float) nodes[7].getCords().getY(), (float) nodes[7].getCords().getZ(),    //P0
		};

		float[] texCoords = {
				0.25f, 0,       //T0
				0.5f, 0,        //T1
				0, 0.25f,       //T2
				0.25f, 0.25f,   //T3
				0.5f, 0.25f,    //T4
				0.75f, 0.25f,   //T5
				1, 0.25f,       //T6
				0, 0.5f,        //T7
				0.25f, 0.5f,    //T8
				0.5f, 0.5f,     //T9
				0.75f, 0.5f,    //T10
				1, 0.5f,        //T11
				0.25f, 0.75f,   //T12
				0.5f, 0.75f     //T13
		};

		int[] faces = {
				//top
				5,1,	4,0,	0,3,
				5,1,	0,3,	1,4,

				//left
				0,3,	4,2,	6,7,
				0,3,	6,7,	2,8,

				//front
				1,4,	0,3,	2,8,
				1,4,	2,8,	3,9,

				//right
				5,5,	1,4,	3,9,
				5,5,	3,9,	7,10,

				//back
				4,6,	5,5,	7,10,
				4,6,	7,10,	6,11,

				//down
				3,9,	2,8,	6,12,
				3,9,	6,12,	7,13
		};

		mesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);
		mesh.getPoints().setAll(points);
		mesh.getTexCoords().setAll(texCoords);
		mesh.getFaces().setAll(faces);

		this.meshView = new MeshView(mesh);
	}


}
