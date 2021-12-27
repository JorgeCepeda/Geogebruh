package es.doncomedia.objects;

import es.doncomedia.objects.abstracts.CompoundObject;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.operations.MyMath;

public class Bowl extends CompoundObject {
	private static final long serialVersionUID = 1L;

	public Bowl (double[] pos, String color) {
		super(pos, color, true);
		init();
	}
	
	@SuppressWarnings("serial")
	private void init() {
		double[] displ = MyMath.multipl(getOrient(), 10);
		
		objs().add(new Sphere(getPos(), 9));
		objs(0).changeProperty(COLOR, property(COLOR));
		Sphere sphere = new Sphere(MyMath.sum(getPos(), displ), 11, "yellow");
		
		objs().add(new NegativeSpace(sphere));
		
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
		setRotationAndOrient(new double[] {Math.sqrt(2)/2, Math.sqrt(2)/2, 0}, 0);
	}
}