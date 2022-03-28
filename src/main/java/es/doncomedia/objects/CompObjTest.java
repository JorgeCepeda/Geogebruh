package es.doncomedia.objects;

import es.doncomedia.objects.Prism.Bases;
import es.doncomedia.objects.abstracts.CompoundObject;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.objects.properties.Border;
import es.doncomedia.objects.properties.Texture;
import es.doncomedia.operations.MyMath;

public class CompObjTest extends CompoundObject {
	private static final long serialVersionUID = 1L;
	
	public CompObjTest(double[] pos, String color) {
		super(pos, color, true);
		init();
	}
	
	@SuppressWarnings("serial")
	private void init() {
		/*Test*/
		double[] displ = MyMath.multipl(getOrient(), 11);
		objs().add(new Sphere(getPos(), 5));
		objs().add(new Cube(MyMath.sum(getPos(), displ), 10, "wheat"));
		
		Border b = new Border("green", 2, 'X');
		objs(0).changeProperty(BORDER, b);
		objs(0).changeProperty(COLOR, property(COLOR));

		objs(1).setRotationAndOrient(new double[] {Math.PI/4, Math.PI/4, 0});
		objProperties.put("border_" + objs(0), b);
		for (GameObject obj : objs()) {
			objProperties.put("rotation_" + obj, obj.getRotation());
		}
		
		setUpdate(new Behaviour() {
			@Override
			public void run() {
				objs(0).setPos(getPos(), true);
				objs(0).changeProperty(COLOR, property(COLOR));
				updateOrientation();
				objs(1).setPos(MyMath.sum(getPos(), MyMath.multipl(getOrient(), 11)), true);
			}

			private void updateOrientation() {
				objs(0).setRotationAndOrient(MyMath.sum(getRotation(), (double[]) objProperties.get("rotation_" + objs(0))));
				objs(1).setRotationAndOrient(MyMath.sum(getRotation(), (double[]) objProperties.get("rotation_" + objs(1))));
			}
		});
		setRotationAndOrient(new double[] {0, 1, 0}, Math.PI/4);
		
		Texture grass = new Texture(
			new InvisiblePrism(new double[] {-5,0,0}, 5, Ref.CENTER, new Object[] {11.0,11.0}, Bases.RECTANGULAR),
			"/grass.png",
			true
		);
		grass.setScale(10.0/16, 10.0/16);
		Texture.add(objs(0), grass);
		Texture.add(objs(1), grass);
	}
}