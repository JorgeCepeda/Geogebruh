package es.doncomedia.objects;

import es.doncomedia.objects.abstracts.CompoundObject;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.operations.MyMath;

public class Bowl extends CompoundObject {
	private static final long serialVersionUID = 1L;

	public Bowl(double[] pos, String color) {
		super(pos, color, true);
		init();
	}
	
	@SuppressWarnings("serial")
	private void init() {
		double[] displ = MyMath.multipl(orient, 10), currentPos = pos;
		
		objs().add(new Sphere(currentPos, 9));
		objs(0).changeProperty(COLOR, property(COLOR));
		Sphere sphere = new Sphere(MyMath.sum(currentPos, displ), 11, "yellow");
		
		objs().add(new NegativeSpace(sphere));
		
		for (GameObject obj : objs()) {
			objProperties.put("rotation_" + obj, obj.getRotation());
		}
		
		setUpdate(new Behaviour() {
			@Override
			public void run() {
				double[] pos = Bowl.this.pos;
				objs(0).setPos(pos, true);
				objs(0).changeProperty(COLOR, property(COLOR));
				updateOrientation();
				objs(1).setPos(MyMath.sum(pos, MyMath.multipl(orient, 11)), true);
			}

			private void updateOrientation() {
				double[] rot = rotation;
				objs(0).setRotationAndOrient(MyMath.sum(rot, (double[]) objProperties.get("rotation_" + objs(0))));
				objs(1).setRotationAndOrient(MyMath.sum(rot, (double[]) objProperties.get("rotation_" + objs(1))));
			}
		});
		setRotationAndOrient(new double[] {Math.sqrt(2)/2, Math.sqrt(2)/2, 0}, 0);
	}
}