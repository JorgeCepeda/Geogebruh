package es.doncomedia.objects;

import es.doncomedia.objects.abstracts.GameObject;

public class Ellipsoid extends GameObject {
	private static final long serialVersionUID = 1L;
	private double[] semiaxes;
	
	public Ellipsoid(double[] pos, double[] orient, double[] semiaxes, String color) {
		super(pos, color, true);
		setRotationAndOrient(orient, 0);
		setSemiaxes(semiaxes);
	}

	@Override
	public boolean collision(Photon photon) {
		double[] point = Objects.rotateAround(photon, this), pos = this.pos;
		return Math.pow((point[0] - pos[0]) / semiaxes[0], 2) +
			Math.pow((point[1] - pos[1]) / semiaxes[1], 2) +
			Math.pow((point[2] - pos[2]) / semiaxes[2], 2) <= 1;
	}

	public double[] getSemiaxes() {
		return semiaxes.clone();
	}

	public void setSemiaxes(double[] semiaxes) {
		this.semiaxes = semiaxes.clone();
	}
}