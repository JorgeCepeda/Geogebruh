package es.doncomedia.objects;

import java.io.Serializable;

import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.operations.Dist;
import es.doncomedia.operations.Vector;

public class Prism extends GameObject {
	interface Base extends Serializable {
		
		/**
		 * @param coord - The x and y plane coordinates
		 * @param data - Base parameters (i.e.: width and height of a rectangle)
		 * @return whether the coordinates belong to the base
		 */
		public boolean collision(double[] coord, Object[] data);
	}

	private static final long serialVersionUID = 1L;
	private Base base;
	private Object[] data;
	private double height;
	private Ref ref;
	
	public Prism(double[] pos, int height, String color, Ref ref, Object[] data, Base base) {
		super(pos, color, true);
		setBase(base);
		setHeight(height);
		setData(data);
		setRef(ref);
	}
	
	public Prism(double[] pos, int height, Ref ref, Object[] data, Base base) {
		super(pos, false);
		setBase(base);
		setHeight(height);
		setData(data);
		setRef(ref);
	}
	
	public Prism(Prism p, boolean border, boolean cloneData) {
		super(p.pos, border);
		base = p.base;
		height = p.height;
		data = cloneData ? p.data.clone() : p.data;
		ref = p.ref;
		setRotationAndOrient(p.rotation);
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height >= 1 ? height : 1;
	}

	public void setBase(Base base) {
		this.base = base;
	}

	public Object[] getData() {
		return data;
	}

	public void setData(Object[] data) {
		this.data = data;
	}

	public Ref getRef() {
		return ref;
	}

	public void setRef(Ref ref) {
		this.ref = ref;
	}
	
	/**
	 * Checks collision with the photon and its projection to the prism's base
	 * @return whether it collides in the first index and the 2D coordinates of the projection in the second one
	 */
	public Object[] collisionAndProjection(Photon photon) {
		double[] phPos = photon.getPos(), coord = projectToBase(phPos);
		return new Object[] {base.collision(coord, data) && validRange(Dist.pointToPlane(phPos, pos, orient)), coord};
	}

	@Override
	public boolean collision(Photon photon) {
		double[] phPos = photon.getPos();
		return base.collision(projectToBase(phPos), data) && validRange(Dist.pointToPlane(phPos, pos, orient));
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
	
	/**
	 * @see Vector#project2D(double[], double[], double[], double[])
	 */
	public double[] projectToBase(double[] point) {
		return Vector.project2D(point, pos, orient, rotation);
	}
	
	public static class Bases {
		
		private Bases() {
			throw new UnsupportedOperationException("Can't instantiate class");
		}
		
		/**
		 * The data vector includes width and height in the first indexes
		 */
		public static final Base RECTANGULAR = (double[] coord, Object[] data) -> {
			if (data.length > 2) throw new IllegalArgumentException("Incorrect data");
			return coord[0] <= (double) data[0]/2 && coord[0] >= - (double) data[0]/2
				&& coord[1] <= (double) data[1]/2 && coord[1] >= - (double) data[1]/2;
		};
	}
}