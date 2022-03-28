package es.doncomedia.objects;

import java.util.concurrent.ConcurrentHashMap;

import es.doncomedia.chunks.*;
import es.doncomedia.effects.Lighting;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.objects.properties.Color;
import es.doncomedia.objects.properties.Property;
import es.doncomedia.operations.Dist;
import es.doncomedia.operations.MyMath;

public class Light extends GameObject {
	private static final long serialVersionUID = 1L;
	private double intensity;
	private boolean turnedOn = true;
	private GameObject body;

	public Light(double[] pos, String color, double intensity, GameObject body) {
		super(pos, false);
		setIntensity(intensity);
		setBody(body);
		body.changeProperty(COLOR, new Color(color, true));
		Lighting.getLights().add(this);
	}
	
	public Light(double[] pos, String color, double intensity) {
		this(pos, color, intensity, new Sphere(pos, 3, color));
	}

	public Light(double[] pos, String color) {
		this(pos, color, 1);
	}

	public Light(double[] pos) {
		this(pos, "white");
	}

	public double getIntensity() {
		return intensity;
	}

	public void setIntensity(double intensity) {
		if (intensity < 0) throw new IllegalArgumentException("Negative intensity");
		this.intensity = intensity;
	}

	public boolean isTurnedOn() {
		return turnedOn;
	}

	public void setTurnedOn(boolean turnedOn) {
		this.turnedOn = turnedOn;
	}

	public GameObject getBody() {
		return body;
	}

	public void setBody(GameObject body) {
		this.body = body;
	}

	@Override
	public <T extends Property> T property(Class<T> type, String name) {
		return body.property(type, name);
	}

	@Override
	public <T extends Property> T property(String name) {
		return body.<T>property(name);
	}

	@Override
	public <T extends Property> T addProperty(String name, Property property) {
		return body.addProperty(name, property);
	}

	@Override
	public boolean changeProperty(String name, Property properties) {
		return body.changeProperty(name, properties);
	}

	@Override
	public void changeProperties(ConcurrentHashMap<String, Property> properties, boolean link) {
		body.changeProperties(properties, link);
	}

	/**
	 * The light's intensity decreases inversely proportional to the squared distance to the lit point, with 100 being total lighting
	 * @param objColor - The color of the point to be lit up, without previous lighting
	 * @return the color corresponding to this light applied on this point, assuming darkness before
	 */
	public int[] lightUp(double[] point, int[] objColor) {
		if (!turnedOn || intensity < 1) return new int[3];
		double lux = intensity / Math.pow(Dist.pointToPoint(getPos(), point) / 100.0, 2);
		
		if (lux < 1) return new int[3];
		if (lux > 100) lux = 100;

		int[] light = new int[3], colorDec = property(Color.class, COLOR).getRGB();
		for (int i = 0; i < light.length; i++) {
			light[i] = (int) (colorDec[i] * lux * objColor[i] / 25500.0);
		}
		return light;
	}
	
	/**
	 * @see Light#lightUp(double[], int[])
	 * @return Lighting on a white point, it's used to combine several lights on a point of any color later
	 */
	public int[] lightUpWhite(double[] punto) {
		if (!turnedOn || intensity < 1) return new int[3];
		double lux = intensity / Math.pow(Dist.pointToPoint(getPos(), punto) / 100.0, 2);
		
		if (lux < 1) return new int[3];
		if (lux > 100) lux = 100;
		
		int[] light = new int[3], colorDec = property(Color.class, COLOR).getRGB();
		for (int i = 0; i < light.length; i++) {
			light[i] = (int) (colorDec[i] * lux / 100.0);
		}
		return light;
	}
	
	/**
	 * @param light - Light color to apply on a surface, being it individual or combined lights
	 * @return the combined lighting on a surface
	 */
	public static int[] applyLight(int[] light, int[] surfaceColor) {
		int[] iluminado = new int[3];
		for (int i = 0; i < iluminado.length; i++) {
			iluminado[i] = light[i] * surfaceColor[i] / 255;
		}
		return iluminado;
	}

	@Override
	public boolean collision(Photon photon) {
		return body.collision(photon);
	}

	/**
	 * Executes that method with a new photon
	 * @see Light#canLightUp(double[], Photon)
	 */
	public boolean canLightUp(double[] punto) {
		return canLightUp(punto, new Photon());
	}
	
	/**
	 * Projects the photon from the point to the light's position, checking collision with the level's elements
	 * @return whether it can light that point up
	 */
	public boolean canLightUp(double[] point, Photon photon) {
		double[] lightPos = getPos(), phOrient = MyMath.unitary(MyMath.vector(point, lightPos)), coord = MyMath.sum(point, MyMath.multipl(phOrient, 0.3));
		double lightDist = Dist.pointToPoint(point, lightPos), spdMultiplier, dist;

		// Photon projection
		photon.setPos(coord, false);
		while ((dist = MyMath.round(Dist.pointToPoint(photon.getPos(), point), 12)) <= lightDist) {
			double speed = 1;
			Chunk chunk = Chunks.getChunk(photon);
			if (chunk.isEmpty()) {
				// Skip chunk
				if ((speed = Chunks.skipChunk(photon.getPos(), phOrient, chunk)) == -1) break;
			}
			else {
				photon.setObjs(chunk.getObjs());
				Region region = chunk.getRegion(photon.getPos(1));
				if (region == null) {
					// Skip region
					if ((speed = Chunks.skipRegion(photon.getPos(), phOrient, chunk)) == -1) break;
				}
				else if (photon.collision()) {
					return photon.collObj() == this;
				}
				if ((spdMultiplier = 1 + dist*0.18 /*20 when dist is 100*/) < 20) { // Render precision
					speed = 0.05*spdMultiplier;
				}
			}
			
			coord[0] += phOrient[0]*speed;
			coord[1] += phOrient[1]*speed;
			coord[2] += phOrient[2]*speed;
			photon.setPos(MyMath.fix(coord), false);
		}
		return true;
	}
	
	/**
	 * Places the photon in that point and checks if it can be lit up ignoring the obstacles between the light and that point
	 * @param obj - The object in the point to be lit up
	 * @return whether it can light that point up
	 */
	public boolean canLightUpNoOcclusion(double[] point, GameObject obj, Photon photon) {
		double[] point2 = MyMath.sum(point, MyMath.unitary(MyMath.vector(point, getPos())));
		Chunk chunk = Chunks.getChunk(point2);
		if (chunk != null && !chunk.isEmpty()) {
			photon.setPos(point2, true);
			photon.setObjs(chunk.getObjs());
			if (photon.collision()) return photon.collObjContainer() != obj;
		}
		return true;
	}
	
//	/**
//	 * Projects the photon from the point to the light's position, checking collision with the level's elements
//	 * @return whether it can light that point up
//	 */
//	public boolean canLightUp(double[] point, Photon photon) {
//		if (Photon.proyect(point, getPos(), 0.3, true, photon, photon::collision)) return photon.collObj() == this;
//		return true;
//	}
}
