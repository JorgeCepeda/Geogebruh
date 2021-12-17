package objetos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import niveles.Niveles;
import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoCompuesto;
import operaciones.Vector;

public class Objetos {
	
	private Objetos() {
		throw new IllegalStateException("Clase no instanciable");
	}
	
	public static void init() {
		Niveles.cargar(Niveles.NIVEL_C�DIGO_1);
		//Niveles.cargar(Niveles.nivelArchivado(Niveles.NIVEL_C�DIGO_1.getNombre()));
		if (debugObjs()) {
			System.err.println("Cerrando programa debido a bugs en la lista de objetos.");
			System.exit(-1);
		}
	}
	
	public static LinkedHashMap<String, Objeto> objs() {
		return Niveles.cargado().objs();
	}
	
	public static Objeto objConcreto(Objeto obj, double[] pos) {
		if (obj instanceof ObjetoCompuesto) {
			Fot�n fot�n = new Fot�n();
			fot�n.setPos(pos, true);
			obj.colisi�n(fot�n);
			return fot�n.objCol();
		}
		return obj;
	}
	
	/**
	 * @see Vector#rotar(double[], double[], double[], double[])
	 */
	public static double[] rotarAlrededor(Fot�n fot�n, Objeto objeto) {
		return Vector.rotar(fot�n.getPos(), objeto.getPos(), objeto.getOrient(), objeto.getRotaci�n());
	}
	
	/**
	 * @see Vector#rotar(double[], double[], double[], double[])
	 */
	public static double[] rotarAlrededor(double[] punto_a_rotar, Objeto objeto) {
		return Vector.rotar(punto_a_rotar, objeto.getPos(), objeto.getOrient(), objeto.getRotaci�n());
	}
	
	public static boolean debugObjs() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("Comenzando debug de objetos compuestos, los siguientes objetos se contienen a s� mismos:\n");
		boolean swEnc = false;
		for (Objeto objeto : objs().values()) {
			if (objeto instanceof ObjetoCompuesto && buscar(objeto, ((ObjetoCompuesto) objeto).objs())) {
				sb.append(" -" + objeto + "\n");
				swEnc = true;
			}
		}
		sb.append("Finalizada fase 1 del debug. ");
		
		if (swEnc) sb.append("Al haber problemas jer�rquicos no se puede continuar el debug.\n");
		else {
			sb.append("Ninguno encontrado.\nLos siguientes objetos est�n en dos o m�s objetos distintos:\n");
			HashSet<Objeto> objetos = new HashSet<>();
			for (Objeto objeto : objs().values()) {
				if (!objetos.add(objeto)) {
					sb.append(" -" + objeto + "\n");
					swEnc = true;
				}
				if (objeto instanceof ObjetoCompuesto && !sinDuplicados(((ObjetoCompuesto) objeto).objs(), objetos)) {
					sb.append(" -Alg�n objeto de " + objeto + "\n");
					swEnc = true;
				}
			}
			sb.append("Finalizada fase 2 del debug. ");
			if (!swEnc) sb.append("Ninguno encontrado.\n");
		}
		System.out.println(sb.append("Debug completado.").toString());
		return swEnc;
	}
	
	private static boolean buscar(Objeto buscado, ArrayList<Objeto> lista_buscar) {
		for (Objeto objeto : lista_buscar) {
			if (objeto == buscado || objeto instanceof ObjetoCompuesto && buscar(buscado, ((ObjetoCompuesto) objeto).objs())) return true;
		}
		return false;
	}
	
	private static boolean sinDuplicados(ArrayList<Objeto> objetos, HashSet<Objeto> lista_total) {
		for (Objeto objeto : objetos) {
			if (!lista_total.add(objeto) || objeto instanceof ObjetoCompuesto && !sinDuplicados(((ObjetoCompuesto) objeto).objs(), lista_total)) return false;
		}
		return true;
	}
}