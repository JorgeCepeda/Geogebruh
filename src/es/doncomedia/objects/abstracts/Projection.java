package es.doncomedia.objects.abstracts;

public abstract class Projection extends BaseObject {
	private static final long serialVersionUID = 1L;
	
	protected Projection(double[] pos) {
		super(pos);
	}

	protected Projection() {}

	public abstract boolean collision();
}