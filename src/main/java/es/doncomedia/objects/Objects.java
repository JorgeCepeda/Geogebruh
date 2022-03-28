package es.doncomedia.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import es.doncomedia.levels.Levels;
import es.doncomedia.objects.abstracts.CompoundObject;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.operations.Vector;

public class Objects {
	
	private Objects() {
		throw new IllegalStateException("Can't instantiate class");
	}
	
	public static void init() {
		Levels.load(Levels.LEVEL_1);
//		Levels.load(Levels.archivedLevel(Levels.LEVEL_1.getName()));
		if (debugObjs()) {
			System.err.println("Cerrando programa debido a bugs en la lista de objetos.");
			System.exit(-1);
		}
	}
	
	public static LinkedHashMap<String, GameObject> objs() {
		return Levels.loaded().objs();
	}
	
	public static GameObject concreteObj(GameObject obj, double[] pos) {
		if (obj instanceof CompoundObject) {
			Photon photon = new Photon();
			photon.setPos(pos, true);
			obj.collision(photon);
			return photon.collObj();
		}
		return obj;
	}
	
	/**
	 * @see Vector#rotate(double[], double[], double[], double[])
	 */
	public static double[] rotateAround(Photon photon, GameObject object) {
		return Vector.rotate(photon.getPos(), object.getPos(), object.getOrient(), object.getRotation());
	}
	
	/**
	 * @see Vector#rotate(double[], double[], double[], double[])
	 */
	public static double[] rotateAround(double[] pointToRotate, GameObject object) {
		return Vector.rotate(pointToRotate, object.getPos(), object.getOrient(), object.getRotation());
	}
	
	public static boolean debugObjs() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("Comenzando debug de objetos compuestos, los siguientes objetos se contienen a sí mismos:\n");
		boolean swFound = false;
		for (GameObject object : objs().values()) {
			if (object instanceof CompoundObject && search(object, ((CompoundObject) object).objs())) {
				sb.append(" -" + object + "\n");
				swFound = true;
			}
		}
		sb.append("Finalizada fase 1 del debug. ");
		
		if (swFound) sb.append("Al haber problemas jerárquicos no se puede continuar el debug.\n");
		else {
			sb.append("Ninguno encontrado.\nLos siguientes objetos están en dos o más objetos distintos:\n");
			HashSet<GameObject> objects = new HashSet<>();
			for (GameObject object : objs().values()) {
				if (!objects.add(object)) {
					sb.append(" -" + object + "\n");
					swFound = true;
				}
				if (object instanceof CompoundObject && !hasNoDuplicates(((CompoundObject) object).objs(), objects)) {
					sb.append(" -Algún objeto de " + object + "\n");
					swFound = true;
				}
			}
			sb.append("Finalizada fase 2 del debug. ");
			if (!swFound) sb.append("Ninguno encontrado.\n");
		}
		System.out.println(sb.append("Debug completado.").toString());
		return swFound;
	}
	
	private static boolean search(GameObject searched, ArrayList<GameObject> searchList) {
		for (GameObject object : searchList) {
			if (object == searched || object instanceof CompoundObject && search(searched, ((CompoundObject) object).objs())) return true;
		}
		return false;
	}
	
	private static boolean hasNoDuplicates(ArrayList<GameObject> objects, HashSet<GameObject> totalList) {
		for (GameObject object : objects) {
			if (!totalList.add(object) || object instanceof CompoundObject && !hasNoDuplicates(((CompoundObject) object).objs(), totalList)) return false;
		}
		return true;
	}
}