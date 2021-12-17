package objetos;

import java.util.concurrent.ConcurrentHashMap;

import objetos.abstracto.Objeto;
import objetos.propiedades.Propiedad;

public class EspacioNegativo extends Objeto {
	private static final long serialVersionUID = 1L;
	private Objeto objeto;
	
	public EspacioNegativo(Objeto objeto) {
		setObjeto(objeto);
	}
	
	public Objeto getObjeto() {
		return objeto;
	}
	
	private void setObjeto(Objeto objeto) {
		this.objeto = objeto;
	}

	@Override
	public boolean colisi�n(Fot�n fot�n) {
		return objeto.colisi�n(fot�n);
	}
	
	@Override
	public double[] getPos() {
		return objeto.getPos();
	}
	
	@Override
	public double getPos(int i) {
		return objeto.getPos(i);
	}
	
	@Override
	public void setPos(double[] pos, boolean enlazar) {
		objeto.setPos(pos, enlazar);
	}
	
	@Override
	public double[] getOrient() {
		return objeto.getOrient();
	}
	
	@Override
	public double getOrient(int i) {
		return objeto.getOrient(i);
	}
	
	@Override
	public void setOrient(double[] orient) {
		objeto.setOrient(orient);
	}
	
	@Override
	public double[] getRotaci�n() {
		return objeto.getRotaci�n();
	}
	
	@Override
	public double getRotaci�n(int i) {
		return objeto.getRotaci�n(i);
	}
	
	@Override
	public void setRotaci�n(double[] �ngulos) {
		objeto.setRotaci�n(�ngulos);
	}
	
	@Override
	public void setRotaci�nYOrient(double[] orient, double inclinaci�n) {
		objeto.setRotaci�nYOrient(orient, inclinaci�n);
	}

	@Override
	public void setRotaci�nYOrient(double[] �ngulos) {
		objeto.setRotaci�nYOrient(�ngulos);
	}

	@Override
	public <T extends Propiedad> T propiedad(Class<T> tipo, String nombre) {
		return objeto.propiedad(tipo, nombre);
	}

	@Override
	public <T extends Propiedad> T propiedad(String nombre) {
		return objeto.<T>propiedad(nombre);
	}
	
	@Override
	public <T extends Propiedad> T a�adirPropiedad(String nombre, Propiedad propiedad) {
		return objeto.a�adirPropiedad(nombre, propiedad);
	}

	@Override
	public boolean cambiarPropiedad(String nombre, Propiedad propiedad) {
		return objeto.cambiarPropiedad(nombre, propiedad);
	}

	@Override
	public void cambiarPropiedades(ConcurrentHashMap<String, Propiedad> propiedades, boolean enlazar) {
		objeto.cambiarPropiedades(propiedades, enlazar);
	}
}