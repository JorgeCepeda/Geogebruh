package es.doncomedia.objects.abstracts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import es.doncomedia.objects.Photon;
import es.doncomedia.objects.properties.Property;

public abstract class CompoundObject extends GameObject {
	protected interface Behaviour extends Serializable, Runnable {}
	
	private static final long serialVersionUID = 1L;
	public final ConcurrentHashMap<String, Object> objProperties = new ConcurrentHashMap<>(); // Examples: any property that doesn't come from methods or needs to be saved before updating it (like rotation)
	private ArrayList<GameObject> objects = new ArrayList<>();
	private Behaviour update;
	
	protected CompoundObject(double[] pos, String color, boolean border) {
		super(pos, color, border);
	}

	protected CompoundObject(double[] pos, boolean border) {
		super(pos, border);
	}

	protected CompoundObject() {}

	@Override
	public boolean collision(Photon photon) {
		photon.setObjs(objects);
		return photon.collision();
	}

	public ArrayList<GameObject> objs() {
		return objects;
	}
	
	public GameObject objs(int i) {
		return objects.get(i);
	}
	
	public void setObjs(ArrayList<GameObject> objects) {
		this.objects = objects;
		update();
	}
	
	protected void update() { // Inherited properties management by all its objects like orientation is done on each subclass to give more flexibility
		if (update != null) update.run();
	}

	public void setUpdate(Behaviour code) {
		update = code;
	}
	
	@Override
	public void setOrient(double[] orient) {
		super.setOrient(orient);
		update();
	}
	
	@Override
	public void setRotation(double[] rotAngles) {
		super.setRotation(rotAngles);
		update();
	}

	@Override
	public void setRotationAndOrient(double[] orient, double incline) {
		super.setRotationAndOrient(orient, incline);
		update();
	}

	@Override
	public void setRotationAndOrient(double[] rotAngles) {
		super.setRotationAndOrient(rotAngles);
		update();
	}

	@Override
	public boolean changeProperty(String name, Property property) {
		boolean overwritten = super.changeProperty(name, property);
		if (overwritten) update();
		return overwritten;
	}

	@Override
	public void changeProperties(ConcurrentHashMap<String, Property> properties, boolean link) {
		super.changeProperties(properties, link);
		update();
	}
}