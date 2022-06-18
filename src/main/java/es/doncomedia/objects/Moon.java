package es.doncomedia.objects;

import es.doncomedia.objects.abstracts.CompoundObject;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.operations.MyMath;

/*
 * ¿No es cierto, ángel de amor,
 * que en esta apartada orilla
 * más puro el debugger brilla
 * y se programa mejor?
 */
public class Moon extends CompoundObject {
	private static final long serialVersionUID = 1L;

	public Moon (double[] pos, String color) {
		super(pos, color, true);
		init();
	}
	
	@SuppressWarnings("serial")
	private void init() {
		double[] displ = MyMath.multipl(getOrient(), 8), currentPos = pos;
		
		objs().add(new Sphere(currentPos, 9));
		objs(0).changeProperty(COLOR, property(COLOR));
		Cylinder cylinder = new Cylinder(MyMath.sum(currentPos, displ), new double[] {0,0,1}, 11, 24, Ref.CENTER, "yellow");
		
		objs().add(new NegativeSpace(cylinder));
		
		for (GameObject obj : objs()) {
			objProperties.put("rotation_" + obj, obj.getRotation());
		}
		
		setUpdate(new Behaviour() {
			@Override
			public void run() {
				double[] pos = Moon.this.pos;
				objs(0).setPos(pos, true);
				objs(0).changeProperty(COLOR, property(COLOR));
				updateOrientation();
				objs(1).setPos(MyMath.sum(pos, MyMath.multipl(orient, 8)), true);
			}

			private void updateOrientation() {
				double[] rot = rotation;
				objs(0).setRotationAndOrient(MyMath.sum(rot, (double[]) objProperties.get("rotation_" + objs(0))));
				objs(1).setRotationAndOrient(new double[] {Math.PI/2.0 + rot[0], -rot[2], rot[1]}); // Perpendicular vector
			}
		});
		setRotationAndOrient(new double[] {0, 1, 0}, 0);
	}
}