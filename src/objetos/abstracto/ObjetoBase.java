package objetos.abstracto;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import objetos.propiedades.Propiedad;
import operaciones.Vector;

public abstract class ObjetoBase implements Serializable {
	public static final String COLOR = "color", BORDE = "borde";
	private static final long serialVersionUID = 1L;
	
	/**
	 * Los objetos de los objetos compuestos acceden a estas para obtener las suyas, actualizándose si se sustituyen.
	 * Mediante Decorators de cada tipo de propiedad en las que sea necesario, esos objetos pueden implementar un comportamiento
	 * más complejo (Ejemplo, color más claro que el color principal: para obtener el color accedería al de la propiedad y operaría sin modificarlo
	 */
	private ConcurrentHashMap<String, Propiedad> propiedades;
	private double[] pos, orient = Vector.orientSTD(), rotación = Vector.rotaciónSTD();

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
		if (orient.length != 3) throw new IllegalArgumentException("La orientación debe ser 3 componentes");
		this.orient = orient.clone();
	}

	public double[] getRotación() {
		return rotación.clone();
	}

	public double getRotación(int i) {
		return rotación[i];
	}

	public void setRotación(double[] rotación) {
		if (rotación.length != 3 && rotación.length != 2) throw new IllegalArgumentException("La rotación debe ser dos o tres componentes");
		if (rotación.length == 3) this.rotación = rotación.clone();
		else this.rotación = new double[] {rotación[0], rotación[1], 0};
	}

	public void setRotaciónYOrient(double[] orient, double inclinación) {
		if (orient.length != 3) throw new IllegalArgumentException("La orientación debe ser 3 componentes");
		this.orient = orient.clone();
		double[] nueva_rotación = Vector.rotación(orient);
		nueva_rotación[2] = inclinación;
		rotación = nueva_rotación;
	}

	public void setRotaciónYOrient(double[] rotación) {
		if (rotación.length != 3 && rotación.length != 2) throw new IllegalArgumentException("La rotación debe ser dos o tres componentes");
		if (rotación.length == 3) this.rotación = rotación.clone();
		else this.rotación = new double[] {rotación[0], rotación[1], 0};
		orient = Vector.orient(rotación);
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
	 * Añade una propiedad a la lista si no existe
	 * @return la propiedad ya existente en su caso o null
	 */
	@SuppressWarnings("unchecked")
	public <T extends Propiedad> T añadirPropiedad(String nombre, Propiedad propiedad) {
		if (propiedades == null) propiedades = new ConcurrentHashMap<>();
		return (T) propiedades.putIfAbsent(nombre, propiedad);
	}

	/**
	 * Añade una propiedad a la lista o la sobreescribe
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