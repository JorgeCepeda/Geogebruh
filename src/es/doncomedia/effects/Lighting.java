package es.doncomedia.effects;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import es.doncomedia.graphics.Screen;
import es.doncomedia.objects.Photon;
import es.doncomedia.objects.Light;
import es.doncomedia.objects.Objects;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.operations.EngHexDec;

public class Lighting {
	private static LinkedHashSet<Light> lights = new LinkedHashSet<>();
	private static boolean generalLighting = true, shades = true;

	private Lighting() {
		throw new IllegalStateException("Can't instantiate class");
	}

	public static LinkedHashSet<Light> getLights() {
		return lights;
	}

	public static void setLights(LinkedHashSet<Light> lights) {
		if (lights == null) throw new IllegalArgumentException("Light set can't be null");
		Lighting.lights = lights;
	}
	
	public static boolean areShadesOn() {
		return shades;
	}
	
	public static void setShades(boolean shades) {
		Lighting.shades = shades;
	}

	public static boolean isLightingOn() {
		return generalLighting;
	}

	public static void setLighting(boolean generalLighting) {
		Lighting.generalLighting = generalLighting;
	}

	public static void lightUp(Screen s) { //TODO reflections
		for (int i = s.extra(); i < s.posTable().length - s.extra(); i++) { // Rows
			for (int j = s.extra(); j < s.posTable()[0].length - s.extra(); j++) { // Columns
				GameObject obj = Objects.concreteObj(s.objTable()[i][j], s.posTable()[i][j]);
				if (obj != null) {
					if (obj instanceof Light && ((Light) obj).isTurnedOn()) s.charTable()[i][j] = '@';
					else s.colorTable()[i][j] = EngHexDec.rgbToHex6(Light.applyLight(lightUp(obj, s.posTable()[i][j]), EngHexDec.getRGB(s.colorTable()[i][j])));
				}
			}
		}
	}
	
	/**
	 * Applies available lights on a point related to an object
	 * @return total light applied on a white surface with saturation included
	 */
	public static int[] lightUp(GameObject obj, double[] point) {
		ArrayList<int[]> appliedLights = new ArrayList<>(lights.size());
		Photon photon = new Photon();
		for (Light light : lights) {
			if (light != obj && light.isTurnedOn() && (shades && light.canLightUp(point, photon) || !shades && light.canLightUpNoOcclusion(point, obj, photon))) {
				appliedLights.add(light.lightUpWhite(point));
			}
		}
		
		int[] totalLight = new int[3];
		for (int[] lightColor : appliedLights) {
			for (int i = 0; i < totalLight.length; i++) {
				totalLight[i] += lightColor[i];
			}
		}
		
		// Compute saturation
		double maxSaturation = 1;
		for (int i : totalLight) {
			double saturation = i / 255.0;
			if (saturation > maxSaturation) maxSaturation = saturation;
		}
		
		for (int i = 0; i < totalLight.length; i++) {
			int newValue = (int) (totalLight[i] / maxSaturation);
			totalLight[i] = newValue > 255 ? 255 : newValue;
		}
		return totalLight;
	}
}
