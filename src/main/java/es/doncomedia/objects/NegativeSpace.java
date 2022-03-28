package es.doncomedia.objects;

import java.util.concurrent.ConcurrentHashMap;

import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.objects.properties.Property;

public class NegativeSpace extends GameObject {
	private static final long serialVersionUID = 1L;
	private GameObject object;
	
	public NegativeSpace(GameObject objeto) {
		setObject(objeto);
	}
	
	public GameObject getObject() {
		return object;
	}
	
	private void setObject(GameObject object) {
		this.object = object;
	}

	@Override
	public boolean collision(Photon photon) {
		return object.collision(photon);
	}
	
	@Override
	public double[] getPos() {
		return object.getPos();
	}
	
	@Override
	public double getPos(int i) {
		return object.getPos(i);
	}
	
	@Override
	public void setPos(double[] pos, boolean link) {
		object.setPos(pos, link);
	}
	
	@Override
	public double[] getOrient() {
		return object.getOrient();
	}
	
	@Override
	public double getOrient(int i) {
		return object.getOrient(i);
	}
	
	@Override
	public void setOrient(double[] orient) {
		object.setOrient(orient);
	}
	
	@Override
	public double[] getRotation() {
		return object.getRotation();
	}
	
	@Override
	public double getRotation(int i) {
		return object.getRotation(i);
	}
	
	@Override
	public void setRotation(double[] rotAngles) {
		object.setRotation(rotAngles);
	}
	
	@Override
	public void setRotationAndOrient(double[] orient, double incline) {
		object.setRotationAndOrient(orient, incline);
	}

	@Override
	public void setRotationAndOrient(double[] rotAngles) {
		object.setRotationAndOrient(rotAngles);
	}

	@Override
	public <T extends Property> T property(Class<T> type, String name) {
		return object.property(type, name);
	}

	@Override
	public <T extends Property> T property(String name) {
		return object.<T>property(name);
	}
	
	@Override
	public <T extends Property> T addProperty(String name, Property property) {
		return object.addProperty(name, property);
	}

	@Override
	public boolean changeProperty(String name, Property property) {
		return object.changeProperty(name, property);
	}

	@Override
	public void changeProperties(ConcurrentHashMap<String, Property> properties, boolean link) {
		object.changeProperties(properties, link);
	}
}