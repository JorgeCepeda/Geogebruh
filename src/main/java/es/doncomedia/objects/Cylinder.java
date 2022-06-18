package es.doncomedia.objects;

import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.operations.Dist;

public class Cylinder extends GameObject {
	private static final long serialVersionUID = 1L;
	private double radius, height;
	private Ref ref;
	
	public Cylinder(double[] pos, double[] orient, double radius, double height, Ref ref, String color) {
		super(pos, color, true);
		setRotationAndOrient(orient, 0);
		setRef(ref);
		setHeight(height);
		setRadius(radius);
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius(double radius) {
		this.radius = radius >= 1 ? radius : 1;
	}
	
	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height >= 1 ? height : 1;
	}
	
	public Ref getRef() {
		return ref;
	}

	public void setRef(Ref ref) {
		this.ref = ref;
	}

	@Override
	public boolean collision(Photon photon) {
		double[] phPos = photon.getPos(), pos = this.pos, orient = this.orient;
		double scalar = Dist.pointToPlane(phPos, pos, orient);
		return Dist.pointToLine(phPos, pos, orient, scalar) <= radius && validRange(scalar);
	}
	
	private boolean validRange(double scalar) {
		switch (ref) {
			case BASE1:
				return scalar >= 0 && scalar <= height;
			case BASE2:
				return scalar >= -height && scalar <= 0;
			case CENTER:
				return scalar >= -height / 2 && scalar <= height / 2;
			default:
				return false;
		}
	}
}