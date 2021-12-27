package es.doncomedia.objects.abstracts;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import es.doncomedia.objects.properties.Property;
import es.doncomedia.operations.Vector;

public abstract class BaseObject implements Serializable { //TODO traducir desde aquí
	public static final String COLOR = "color", BORDER = "borde";
	private static final long serialVersionUID = 1L;
	
	/**
	 * Compound object's objects access properties to obtain theirs, updating them if they are modified.
	 * Using Decorators of each type of property where it's needed, those objects can implement a more complex behaviour
	 * (Example: color lighter than the main color: to obtain the color it would access the property and would operate on it without modifying it)
	 */
	private ConcurrentHashMap<String, Property> properties;
	private double[] pos, orient = Vector.orientSTD(), rotation = Vector.rotationSTD();

	protected BaseObject(double[] pos) {
		setPos(pos, false);
	}
	
	protected BaseObject() {}

	public double[] getPos() {
		return pos.clone();
	}

	public double getPos(int i) {
		return pos[i];
	}

	public void setPos(double[] pos, boolean link) {
		this.pos = link ? pos : pos.clone();
	}

	public double[] getOrient() {
		return orient.clone();
	}

	public double getOrient(int i) {
		return orient[i];
	}

	public void setOrient(double[] orient) {
		if (orient.length != 3) throw new IllegalArgumentException("Orientation must be 3 components");
		this.orient = orient.clone();
	}

	public double[] getRotation() {
		return rotation.clone();
	}

	public double getRotation(int i) {
		return rotation[i];
	}

	public void setRotation(double[] rotAngles) {
		if (rotAngles.length != 3 && rotAngles.length != 2) throw new IllegalArgumentException("Rotation must contain 2 or 3 components");
		if (rotAngles.length == 3) this.rotation = rotAngles.clone();
		else this.rotation = new double[] {rotAngles[0], rotAngles[1], 0};
	}

	public void setRotationAndOrient(double[] orient, double incline) {
		if (orient.length != 3) throw new IllegalArgumentException("Orientation must be 3 components");
		this.orient = orient.clone();
		double[] newRotation = Vector.rotation(orient);
		newRotation[2] = incline;
		rotation = newRotation;
	}

	public void setRotationAndOrient(double[] rotAngles) {
		if (rotAngles.length != 3 && rotAngles.length != 2) throw new IllegalArgumentException("Rotation must contain 2 or 3 components");
		if (rotAngles.length == 3) this.rotation = rotAngles.clone();
		else this.rotation = new double[] {rotAngles[0], rotAngles[1], 0};
		orient = Vector.orient(rotAngles);
	}

	/**
	 * @param type - The required type of the property
	 * @return the property with that name and specified type if it exists, otherwise it returns null
	 */
	@SuppressWarnings("unchecked")
	public <T extends Property> T property(Class<T> type, String name) {
		if (properties == null) return null;
		return (T) properties.get(name);
	}
	
	/**
	 * @return the property with that name and specified type if it exists, otherwise it returns null
	 */
	@SuppressWarnings("unchecked")
	public <T extends Property> T property(String name) {
		if (properties == null) return null;
		return (T) properties.get(name);
	}
	
	/**
	 * Adds a property to the list if it doesn't exist
	 * @return the existing property or null
	 */
	@SuppressWarnings("unchecked")
	public <T extends Property> T addProperty(String name, Property property) {
		if (properties == null) properties = new ConcurrentHashMap<>();
		return (T) properties.putIfAbsent(name, property);
	}

	/**
	 * Adds a property to the list or overwrites it
	 * @return whether it has been overwritten
	 */
	public boolean changeProperty(String name, Property property) {
		if (properties == null) properties = new ConcurrentHashMap<>();
		return properties.put(name, property) != null;
	}

	public void changeProperties(ConcurrentHashMap<String, Property> properties, boolean link) {
		this.properties = link ? properties : new ConcurrentHashMap<>(properties);
	}
}