package es.doncomedia.objects;

import es.doncomedia.objects.abstracts.GameObject;

public class Orthohedron extends GameObject {
	private static final long serialVersionUID = 1L;
	private double longX, height, longZ;
	
	public Orthohedron(double[] pos, double longX, double height, double longZ, String color) {
		super(pos, color, true);
		setDimensions(longX, height, longZ);
	}
	
	public Orthohedron(double[] pos, String color) {
		super(pos, color, true);
	}
	
	public double[] getDimensions() {
		return new double[] {longX, height, longZ};
	}

	public void setDimensions(double longX, double height, double longZ) {
		if (longX <= 0 || height <= 0 || longZ <= 0) throw new IllegalArgumentException("Invalid dimensions");
		this.longX = longX;
		this.height = height;
		this.longZ = longZ;
	}

	@Override
	public boolean collision(Photon photon) {
		double[] point = Objects.rotateAround(photon, this), pos = this.pos;
		return point[0] <= pos[0] + longX/2 && point[0] >= pos[0] - longX/2
			&& point[1] <= pos[1] + height/2 && point[1] >= pos[1] - height/2
			&& point[2] <= pos[2] + longZ/2 && point[2] >= pos[2] - longZ/2;
	}
}