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
	public boolean colisión(Fotón fotón) {
		return objeto.colisión(fotón);
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
	public double[] getRotación() {
		return objeto.getRotación();
	}
	
	@Override
	public double getRotación(int i) {
		return objeto.getRotación(i);
	}
	
	@Override
	public void setRotación(double[] ángulos) {
		objeto.setRotación(ángulos);
	}
	
	@Override
	public void setRotaciónYOrient(double[] orient, double inclinación) {
		objeto.setRotaciónYOrient(orient, inclinación);
	}

	@Override
	public void setRotaciónYOrient(double[] ángulos) {
		objeto.setRotaciónYOrient(ángulos);
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
	public <T extends Propiedad> T añadirPropiedad(String nombre, Propiedad propiedad) {
		return objeto.añadirPropiedad(nombre, propiedad);
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