package objetos.abstracto;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import objetos.propiedades.Propiedad;
import operaciones.Vector;

public abstract class ObjetoBase implements Serializable {
	public static final String COLOR = "color", BORDE = "borde";
	private static final long serialVersionUID = 1L;
	
	/**
	 * Los objetos de los objetos compuestos acceden a estas para obtener las suyas, actualiz�ndose si se sustituyen.
	 * Mediante Decorators de cada tipo de propiedad en las que sea necesario, esos objetos pueden implementar un comportamiento
	 * m�s complejo (Ejemplo, color m�s claro que el color principal: para obtener el color acceder�a al de la propiedad y operar�a sin modificarlo
	 */
	private ConcurrentHashMap<String, Propiedad> propiedades;
	private double[] pos, orient = Vector.orientSTD(), rotaci�n = Vector.rotaci�nSTD();

	protected ObjetoBase(double[] pos) {
		setPos(pos, false);
	}
	
	protected ObjetoBase() {}

	public double[] getPos() {
		return pos.clone();
	}

	public double getPos(int i) {
		return pos[i];
	}

	public void setPos(double[] pos, boolean enlazar) {
		this.pos = enlazar ? pos : pos.clone();
	}

	public double[] getOrient() {
		return orient.clone();
	}

	public double getOrient(int i) {
		return orient[i];
	}

	public void setOrient(double[] orient) {
		if (orient.length != 3) throw new IllegalArgumentException("La orientaci�n debe ser 3 componentes");
		this.orient = orient.clone();
	}

	public double[] getRotaci�n() {
		return rotaci�n.clone();
	}

	public double getRotaci�n(int i) {
		return rotaci�n[i];
	}

	public void setRotaci�n(double[] rotaci�n) {
		if (rotaci�n.length != 3 && rotaci�n.length != 2) throw new IllegalArgumentException("La rotaci�n debe ser dos o tres componentes");
		if (rotaci�n.length == 3) this.rotaci�n = rotaci�n.clone();
		else this.rotaci�n = new double[] {rotaci�n[0], rotaci�n[1], 0};
	}

	public void setRotaci�nYOrient(double[] orient, double inclinaci�n) {
		if (orient.length != 3) throw new IllegalArgumentException("La orientaci�n debe ser 3 componentes");
		this.orient = orient.clone();
		double[] nueva_rotaci�n = Vector.rotaci�n(orient);
		nueva_rotaci�n[2] = inclinaci�n;
		rotaci�n = nueva_rotaci�n;
	}

	public void setRotaci�nYOrient(double[] rotaci�n) {
		if (rotaci�n.length != 3 && rotaci�n.length != 2) throw new IllegalArgumentException("La rotaci�n debe ser dos o tres componentes");
		if (rotaci�n.length == 3) this.rotaci�n = rotaci�n.clone();
		else this.rotaci�n = new double[] {rotaci�n[0], rotaci�n[1], 0};
		orient = Vector.orient(rotaci�n);
	}

	/**
	 * @param tipo - El tipo de propiedad requerido
	 * @return la propiedad con ese nombre y tipo especificada si la hay, si no devuelve null
	 */
	@SuppressWarnings("unchecked")
	public <T extends Propiedad> T propiedad(Class<T> tipo, String nombre) {
		if (propiedades == null) return null;
		return (T) propiedades.get(nombre);
	}
	
	/**
	 * @return la propiedad con ese nombre y tipo especificada si la hay, si no devuelve null
	 */
	@SuppressWarnings("unchecked")
	public <T extends Propiedad> T propiedad(String nombre) {
		if (propiedades == null) return null;
		return (T) propiedades.get(nombre);
	}
	
	/**
	 * A�ade una propiedad a la lista si no existe
	 * @return la propiedad ya existente en su caso o null
	 */
	@SuppressWarnings("unchecked")
	public <T extends Propiedad> T a�adirPropiedad(String nombre, Propiedad propiedad) {
		if (propiedades == null) propiedades = new ConcurrentHashMap<>();
		return (T) propiedades.putIfAbsent(nombre, propiedad);
	}

	/**
	 * A�ade una propiedad a la lista o la sobreescribe
	 * @return si ha sido sobreescrita
	 */
	public boolean cambiarPropiedad(String nombre, Propiedad propiedad) {
		if (propiedades == null) propiedades = new ConcurrentHashMap<>();
		return propiedades.put(nombre, propiedad) != null;
	}

	public void cambiarPropiedades(ConcurrentHashMap<String, Propiedad> propiedades, boolean enlazar) {
		this.propiedades = enlazar ? propiedades : new ConcurrentHashMap<>(propiedades);
	}
}