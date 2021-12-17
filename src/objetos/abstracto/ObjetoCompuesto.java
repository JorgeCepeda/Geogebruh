package objetos.abstracto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import objetos.Fotón;
import objetos.propiedades.Propiedad;

public abstract class ObjetoCompuesto extends Objeto {
	protected interface Comportamiento extends Serializable, Runnable {}
	
	private static final long serialVersionUID = 1L;
	public final ConcurrentHashMap<String, Object> propiedades_objs = new ConcurrentHashMap<>(); // Ejemplos: cualquier propiedad que no venga de métodos o se quiera conservar antes de cambiarla (ej: rotación)
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
	public boolean colisión(Fotón fotón) {
		fotón.setObjs(objetos);
		return fotón.colisión();
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
	
	protected void actualizar() { // La gestión de propiedades heredadas por todos sus objetos como la orientación se hace en cada objeto compuesto para dar flexibilidad
		if (actualizar != null) actualizar.run();
	}

	public void setActualizar(Comportamiento código) {
		actualizar = código;
	}
	
	@Override
	public void setOrient(double[] orient) {
		super.setOrient(orient);
		actualizar();
	}
	
	@Override
	public void setRotación(double[] ángulos) {
		super.setRotación(ángulos);
		actualizar();
	}

	@Override
	public void setRotaciónYOrient(double[] orient, double inclinación) {
		super.setRotaciónYOrient(orient, inclinación);
		actualizar();
	}

	@Override
	public void setRotaciónYOrient(double[] ángulos) {
		super.setRotaciónYOrient(ángulos);
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