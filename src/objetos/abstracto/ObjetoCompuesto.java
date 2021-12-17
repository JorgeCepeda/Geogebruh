package objetos.abstracto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import objetos.Fot�n;
import objetos.propiedades.Propiedad;

public abstract class ObjetoCompuesto extends Objeto {
	protected interface Comportamiento extends Serializable, Runnable {}
	
	private static final long serialVersionUID = 1L;
	public final ConcurrentHashMap<String, Object> propiedades_objs = new ConcurrentHashMap<>(); // Ejemplos: cualquier propiedad que no venga de m�todos o se quiera conservar antes de cambiarla (ej: rotaci�n)
	private ArrayList<Objeto> objetos = new ArrayList<>();
	private Comportamiento actualizar;
	
	protected ObjetoCompuesto(double[] pos, String color, boolean borde) {
		super(pos, color, borde);
	}
	
	protected ObjetoCompuesto(double[] pos, boolean borde) {
		super(pos, borde);
	}
	
	protected ObjetoCompuesto() {}

	@Override
	public boolean colisi�n(Fot�n fot�n) {
		fot�n.setObjs(objetos);
		return fot�n.colisi�n();
	}

	public ArrayList<Objeto> objs() {
		return objetos;
	}
	
	public Objeto objs(int i) {
		return objetos.get(i);
	}
	
	public void setObjs(ArrayList<Objeto> objetos) {
		this.objetos = objetos;
		actualizar();
	}
	
	protected void actualizar() { // La gesti�n de propiedades heredadas por todos sus objetos como la orientaci�n se hace en cada objeto compuesto para dar flexibilidad
		if (actualizar != null) actualizar.run();
	}

	public void setActualizar(Comportamiento c�digo) {
		actualizar = c�digo;
	}
	
	@Override
	public void setOrient(double[] orient) {
		super.setOrient(orient);
		actualizar();
	}
	
	@Override
	public void setRotaci�n(double[] �ngulos) {
		super.setRotaci�n(�ngulos);
		actualizar();
	}

	@Override
	public void setRotaci�nYOrient(double[] orient, double inclinaci�n) {
		super.setRotaci�nYOrient(orient, inclinaci�n);
		actualizar();
	}

	@Override
	public void setRotaci�nYOrient(double[] �ngulos) {
		super.setRotaci�nYOrient(�ngulos);
		actualizar();
	}

	@Override
	public boolean cambiarPropiedad(String nombre, Propiedad propiedad) {
		boolean sobreescrita = super.cambiarPropiedad(nombre, propiedad);
		if (sobreescrita) actualizar();
		return sobreescrita;
	}

	@Override
	public void cambiarPropiedades(ConcurrentHashMap<String, Propiedad> propiedades, boolean enlazar) {
		super.cambiarPropiedades(propiedades, enlazar);
		actualizar();
	}
}