package es.doncomedia.objects;

import es.doncomedia.objects.abstracts.GameObject;

public class Cube extends GameObject {
	private static final long serialVersionUID = 1L;
	private double side;
	
	public Cube(double[] pos, double side, String color) {
		super(pos, color, true);
		setSide(side);
	}
	
	public double getSide() {
		return side;
	}
	
	public void setSide(double side) {
		if (side <= 0) throw new IllegalArgumentException("Invalid side: " + side);
		this.side = side;
	}
	
	@Override
	public boolean collision(Photon photon) {
		double[] point = Objects.rotateAround(photon, this), pos = this.pos;
		double sideHalf = side/2;
		return point[0] <= pos[0] + sideHalf && point[0] >= pos[0] - sideHalf
			&& point[1] <= pos[1] + sideHalf && point[1] >= pos[1] - sideHalf
			&& point[2] <= pos[2] + sideHalf && point[2] >= pos[2] - sideHalf;
	}
}