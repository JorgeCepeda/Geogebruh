package es.doncomedia.objects;

import es.doncomedia.objects.abstracts.GameObject;

public class Cube extends GameObject {
	private static final long serialVersionUID = 1L;
	private double side;
	
	public Cube(double[] pos, double lado, String color) {
		super(pos, color, true);
		setSide(lado);
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
		double[] point = Objects.rotateAround(photon, this);
		double sideHalf = side/2;
		return point[0] <= getPos(0) + sideHalf && point[0] >= getPos(0) - sideHalf
			&& point[1] <= getPos(1) + sideHalf && point[1] >= getPos(1) - sideHalf
			&& point[2] <= getPos(2) + sideHalf && point[2] >= getPos(2) - sideHalf;
	}
}