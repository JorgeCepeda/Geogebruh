package es.doncomedia.objects;

import es.doncomedia.objects.abstracts.GameObject;

public class DoubleCone extends GameObject {
	private static final long serialVersionUID = 1L;
	
	public DoubleCone(double[] pos, String color) {
		super(pos, color, true);
	}

	@Override
	public boolean collision(Photon photon) {
		double[] point = Objects.rotateAround(photon, this);
		return Math.pow(point[0] - getPos(0), 2) + Math.pow(point[2] - getPos(2), 2) <= Math.pow(point[1] - getPos(1), 2);
	}
}