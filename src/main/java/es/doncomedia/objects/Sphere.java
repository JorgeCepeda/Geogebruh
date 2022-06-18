package es.doncomedia.objects;

import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.operations.Dist;

public class Sphere extends GameObject {
	private static final long serialVersionUID = 1L;
	private double radius;
	
	public Sphere(double[] pos, double radius, String color) {
		super(pos, color, true);
		setRadius(radius);
	}
	
	public Sphere(double[] pos, double radius) {
		super(pos, true);
		setRadius(radius);
	}

	public void setRadius(double radius) {
		if (radius < 0) throw new IllegalArgumentException("Negative radius");
		this.radius = radius;
	}

	@Override
	public boolean collision(Photon photon) {
		return Dist.pointToPoint(photon.getPos(), pos) <= radius;
	}
}