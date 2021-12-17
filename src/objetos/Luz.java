package objetos;

import java.util.concurrent.ConcurrentHashMap;

import chunks_NoCeldas.*;
import efectos.Iluminaci�n;
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
		Iluminaci�n.getLuces().add(this);
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
	public <T extends Propiedad> T a�adirPropiedad(String nombre, Propiedad propiedad) {
		return cuerpo.a�adirPropiedad(nombre, propiedad);
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
	 * La intensidad de la luz decrece con el cuadrado de la distancia a la posici�n a iluminar, siendo 100 iluminaci�n total
	 * @param colorObj - El color del punto a iluminar, sin iluminaci�n previa
	 * @return el color que corresponde a la iluminaci�n de esta luz sobre esa posici�n, suponiendo oscuridad antes de iluminar
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
	 * @return la iluminaci�n sobre un punto blanco, sirve para poder combinar posteriormente varias luces sobre un punto de cualquier color
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
	 * @return la iluminaci�n combinada sobre una superficie
	 */
	public static int[] aplicarLuz(int[] luz, int[] colorSuperficie) {
		int[] iluminado = new int[3];
		for (int i = 0; i < iluminado.length; i++) {
			iluminado[i] = luz[i] * colorSuperficie[i] / 255;
		}
		return iluminado;
	}

	@Override
	public boolean colisi�n(Fot�n fot�n) {
		return cuerpo.colisi�n(fot�n);
	}

	/**
	 * Crea un fot�n y ejecuta la sobrecarga del m�todo
	 * @see Luz#puedeIluminar(double[], Fot�n)
	 */
	public boolean puedeIluminar(double[] punto) {
		return puedeIluminar(punto, new Fot�n());
	}
	
	/**
	 * Proyecta el fot�n desde el punto a la luz, comprobando la colisi�n con los elementos del nivel
	 * @return si puede iluminar ese punto
	 */
	public boolean puedeIluminar(double[] punto, Fot�n fot�n) {
		double[] pos_luz = getPos(), orient_fot�n = MyMath.unitario(MyMath.vector(punto, pos_luz)), coord = MyMath.sumar(punto, MyMath.multipl(orient_fot�n, 0.3));
		double dist_luz = Dist.puntoAPunto(punto, pos_luz), multiplicador, dist;

		// Proyecci�n del fot�n
		fot�n.setPos(coord, false);
		while ((dist = MyMath.round(Dist.puntoAPunto(fot�n.getPos(), punto), 12)) <= dist_luz) {
			double velocidad = 1;
			Chunk chunk = Chunks.getChunk(fot�n);
			if (chunk.est�Vac�o()) {
				// Saltar chunk
				if ((velocidad = Chunks.saltarChunk(fot�n.getPos(), orient_fot�n, chunk)) == -1) break;
			}
			else {
				fot�n.setObjs(chunk.getObjs());
				Malla malla_actual = chunk.getMalla(fot�n.getPos(1));
				if (malla_actual == null) {
					// Saltar malla
					if ((velocidad = Chunks.saltarMalla(fot�n.getPos(), orient_fot�n, chunk)) == -1) break;
				}
				else if (fot�n.colisi�n()) {
					return fot�n.objCol() == this;
				}
				if ((multiplicador = 1 + dist*0.18 /*20 cuando dist es 100*/) < 20) { // Precisi�n del render
					velocidad = 0.05*multiplicador;
				}
			}
			
			coord[0] += orient_fot�n[0]*velocidad;
			coord[1] += orient_fot�n[1]*velocidad;
			coord[2] += orient_fot�n[2]*velocidad;
			fot�n.setPos(MyMath.fix(coord), false);
		}
		return true;
	}
	
	/**
	 * Coloca el fot�n en ese punto y comprueba si se puede iluminar ignorando los obst�culos entre la luz y ese punto
	 * @param obj - El objeto en el punto a iluminar
	 * @return si puede iluminar ese punto
	 */
	public boolean puedeIluminar_SinOclusi�n(double[] punto, Objeto obj, Fot�n fot�n) {
		double[] punto2 = MyMath.sumar(punto, MyMath.unitario(MyMath.vector(punto, getPos())));
		Chunk chunk = Chunks.getChunk(punto2);
		if (chunk != null && !chunk.est�Vac�o()) {
			fot�n.setPos(punto2, true);
			fot�n.setObjs(chunk.getObjs());
			if (fot�n.colisi�n()) return fot�n.objColContenedor() != obj;
		}
		return true;
		
	}
	
//	/**
//	 * Proyecta el fot�n desde el punto a la luz, comprobando la colisi�n con los elementos del nivel
//	 * @return si puede iluminar ese punto
//	 */
//	public boolean puedeIluminar(double[] punto, Fot�n fot�n) {
//		if (Fot�n.proyectar(punto, getPos(), 0.3, true, fot�n, fot�n::colisi�n)) return fot�n.objCol() == this;
//		return true;
//	}
}
