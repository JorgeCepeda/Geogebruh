package es.doncomedia.objects.abstracts;

import es.doncomedia.misc.Counter;
import es.doncomedia.objects.Photon;
import es.doncomedia.objects.properties.Border;
import es.doncomedia.objects.properties.Color;

public abstract class GameObject extends BaseObject { // Object codes can't be easily saved un a file, unless it exists as a java object in the program's code (i.e: rectangular base, compound objects' code)
	public enum Ref {CENTER,BASE1,BASE2}

	public static final String TEXTURES = "texture_map";
	private static final long serialVersionUID = 1L;
	private int id = -1;

	protected GameObject(double[] pos, String color, boolean border) {
		super(pos);
		changeProperty(COLOR, new Color(color, true));
		if (border) changeProperty(BORDER, Border.defaultBorder());
		numerate();
	}
	
	protected GameObject(double[] pos, boolean borde) {
		super(pos);
		if (borde) changeProperty(BORDER, Border.defaultBorder());
		numerate();
	}
	
	protected GameObject() {
		numerate();
	}

	public int getID() {
		return id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
	
	public void numerate() {
		Counter.register(this);
	}

	public abstract boolean collision(Photon photon);

	@Override
	public String toString() {
		return getClass().getSimpleName() + id;
	}
}