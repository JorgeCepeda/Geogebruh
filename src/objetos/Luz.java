package objetos;

import java.util.concurrent.ConcurrentHashMap;

import chunks_NoCeldas.*;
import efectos.Iluminación;
import objetos.abstracto.Objeto;
import objetos.propiedades.Color;
import objetos.propiedades.Propiedad;
import operaciones.Dist;
import operaciones.MyMath;

public class Luz extends Objeto {
	private static final long serialVersionUID = 1L;
	private double intensidad;
	private boolean encendida = true;
	private Objeto cuerpo;

	public Luz(double[] pos, String color, double intensidad) {
		super(pos, false);
		setIntensidad(intensidad);
		setCuerpo(new Esfera(pos, 3, color));
		Iluminación.getLuces().add(this);
	}

	public Luz(double[] pos, String color) {
		this(pos, color, 1);
	}

	public Luz(double[] pos) {
		this(pos, "white");
	}

	public double getIntensidad() {
		return intensidad;
	}

	public void setIntensidad(double intensidad) {
		if (intensidad < 0) throw new IllegalArgumentException("La intensidad no puede ser negativa");
		this.intensidad = intensidad;
	}

	public boolean isEncendida() {
		return encendida;
	}

	public void setEncendida(boolean encendida) {
		this.encendida = encendida;
	}

	public Objeto getCuerpo() {
		return cuerpo;
	}

	public void setCuerpo(Objeto cuerpo) {
		this.cuerpo = cuerpo;
	}

	@Override
	public <T extends Propiedad> T propiedad(Class<T> tipo, String nombre) {
		return cuerpo.propiedad(tipo, nombre);
	}

	@Override
	public <T extends Propiedad> T propiedad(String nombre) {
		return cuerpo.<T>propiedad(nombre);
	}

	@Override
	public <T extends Propiedad> T añadirPropiedad(String nombre, Propiedad propiedad) {
		return cuerpo.añadirPropiedad(nombre, propiedad);
	}

	@Override
	public boolean cambiarPropiedad(String nombre, Propiedad propiedad) {
		return cuerpo.cambiarPropiedad(nombre, propiedad);
	}

	@Override
	public void cambiarPropiedades(ConcurrentHashMap<String, Propiedad> propiedades, boolean enlazar) {
		cuerpo.cambiarPropiedades(propiedades, enlazar);
	}

	/**
	 * La intensidad de la luz decrece con el cuadrado de la distancia a la posición a iluminar, siendo 100 iluminación total
	 * @param colorObj - El color del punto a iluminar, sin iluminación previa
	 * @return el color que corresponde a la iluminación de esta luz sobre esa posición, suponiendo oscuridad antes de iluminar
	 */
	public int[] iluminar(double[] punto, int[] colorObj) {
		if (!encendida || intensidad < 1) return new int[3];
		double lux = intensidad / Math.pow(Dist.puntoAPunto(getPos(), punto) / 100.0, 2);
		
		if (lux < 1) return new int[3];
		if (lux > 100) lux = 100;

		int[] iluminado = new int[3], colorDec = propiedad(Color.class, COLOR).getRGB();
		for (int i = 0; i < iluminado.length; i++) {
			iluminado[i] = (int) (colorDec[i] * lux * colorObj[i] / 25500.0);
		}
		return iluminado;
	}
	
	/**
	 * @see Luz#iluminar(double[], int[])
	 * @return la iluminación sobre un punto blanco, sirve para poder combinar posteriormente varias luces sobre un punto de cualquier color
	 */
	public int[] iluminarBlanco(double[] punto) {
		if (!encendida || intensidad < 1) return new int[3];
		double lux = intensidad / Math.pow(Dist.puntoAPunto(getPos(), punto) / 100.0, 2);
		
		if (lux < 1) return new int[3];
		if (lux > 100) lux = 100;
		
		int[] iluminado = new int[3], colorDec = propiedad(Color.class, COLOR).getRGB();
		for (int i = 0; i < iluminado.length; i++) {
			iluminado[i] = (int) (colorDec[i] * lux / 100.0);
		}
		return iluminado;
	}
	
	/**
	 * @param luz - Color de la luz a aplicar en una superficie, individual o de varias luces combinadas
	 * @return la iluminación combinada sobre una superficie
	 */
	public static int[] aplicarLuz(int[] luz, int[] colorSuperficie) {
		int[] iluminado = new int[3];
		for (int i = 0; i < iluminado.length; i++) {
			iluminado[i] = luz[i] * colorSuperficie[i] / 255;
		}
		return iluminado;
	}

	@Override
	public boolean colisión(Fotón fotón) {
		return cuerpo.colisión(fotón);
	}

	/**
	 * Crea un fotón y ejecuta la sobrecarga del método
	 * @see Luz#puedeIluminar(double[], Fotón)
	 */
	public boolean puedeIluminar(double[] punto) {
		return puedeIluminar(punto, new Fotón());
	}
	
	/**
	 * Proyecta el fotón desde el punto a la luz, comprobando la colisión con los elementos del nivel
	 * @return si puede iluminar ese punto
	 */
	public boolean puedeIluminar(double[] punto, Fotón fotón) {
		double[] pos_luz = getPos(), orient_fotón = MyMath.unitario(MyMath.vector(punto, pos_luz)), coord = MyMath.sumar(punto, MyMath.multipl(orient_fotón, 0.3));
		double dist_luz = Dist.puntoAPunto(punto, pos_luz), multiplicador, dist;

		// Proyección del fotón
		fotón.setPos(coord, false);
		while ((dist = MyMath.round(Dist.puntoAPunto(fotón.getPos(), punto), 12)) <= dist_luz) {
			double velocidad = 1;
			Chunk chunk = Chunks.getChunk(fotón);
			if (chunk.estáVacío()) {
				// Saltar chunk
				if ((velocidad = Chunks.saltarChunk(fotón.getPos(), orient_fotón, chunk)) == -1) break;
			}
			else {
				fotón.setObjs(chunk.getObjs());
				Malla malla_actual = chunk.getMalla(fotón.getPos(1));
				if (malla_actual == null) {
					// Saltar malla
					if ((velocidad = Chunks.saltarMalla(fotón.getPos(), orient_fotón, chunk)) == -1) break;
				}
				else if (fotón.colisión()) {
					return fotón.objCol() == this;
				}
				if ((multiplicador = 1 + dist*0.18 /*20 cuando dist es 100*/) < 20) { // Precisión del render
					velocidad = 0.05*multiplicador;
				}
			}
			
			coord[0] += orient_fotón[0]*velocidad;
			coord[1] += orient_fotón[1]*velocidad;
			coord[2] += orient_fotón[2]*velocidad;
			fotón.setPos(MyMath.fix(coord), false);
		}
		return true;
	}
	
	/**
	 * Coloca el fotón en ese punto y comprueba si se puede iluminar ignorando los obstáculos entre la luz y ese punto
	 * @param obj - El objeto en el punto a iluminar
	 * @return si puede iluminar ese punto
	 */
	public boolean puedeIluminar_SinOclusión(double[] punto, Objeto obj, Fotón fotón) {
		double[] punto2 = MyMath.sumar(punto, MyMath.unitario(MyMath.vector(punto, getPos())));
		Chunk chunk = Chunks.getChunk(punto2);
		if (chunk != null && !chunk.estáVacío()) {
			fotón.setPos(punto2, true);
			fotón.setObjs(chunk.getObjs());
			if (fotón.colisión()) return fotón.objColContenedor() != obj;
		}
		return true;
		
	}
	
//	/**
//	 * Proyecta el fotón desde el punto a la luz, comprobando la colisión con los elementos del nivel
//	 * @return si puede iluminar ese punto
//	 */
//	public boolean puedeIluminar(double[] punto, Fotón fotón) {
//		if (Fotón.proyectar(punto, getPos(), 0.3, true, fotón, fotón::colisión)) return fotón.objCol() == this;
//		return true;
//	}
}
