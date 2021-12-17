package objetos;

import java.io.Serializable;

import objetos.abstracto.Objeto;
import operaciones.Dist;
import operaciones.Vector;

public class Prisma extends Objeto {
	interface Base extends Serializable {
		
		/**
		 * @param coord - Las coordenadas x e y del plano
		 * @param datos - Los par�metros de la base (ej: alto y ancho de un rect�ngulo)
		 * @return si las coordenadas corresponden a un punto de la base
		 */
		public boolean colisi�n(double[] coord, Object[] datos);
	}

	private static final long serialVersionUID = 1L;
	private Base base;
	private Object[] datos;
	private double altura;
	private Ref ref;
	
	public Prisma(double[] pos, int altura, String color, Ref ref, Object[] datos, Base base) {
		super(pos, color, true);
		setBase(base);
		setAltura(altura);
		setDatos(datos);
		setRef(ref);
	}
	
	public Prisma(double[] pos, int altura, Ref ref, Object[] datos, Base base) {
		super(pos, false);
		setBase(base);
		setAltura(altura);
		setDatos(datos);
		setRef(ref);
	}
	
	public Prisma(Prisma p, boolean borde, boolean clonarDatos) {
		super(p.getPos(), borde);
		base = p.base;
		altura = p.altura;
		datos = clonarDatos ? p.datos.clone() : p.datos;
		ref = p.ref;
		setRotaci�nYOrient(p.getRotaci�n());
	}

	public double getAltura() {
		return altura;
	}

	public void setAltura(double altura) {
		this.altura = altura >= 1 ? altura : 1;
	}

	public void setBase(Base base) {
		this.base = base;
	}

	public Object[] getDatos() {
		return datos;
	}

	public void setDatos(Object[] datos) {
		this.datos = datos;
	}

	public Ref getRef() {
		return ref;
	}

	public void setRef(Ref ref) {
		this.ref = ref;
	}
	
	/**
	 * Comprueba la colisi�n con el fot�n y su proyecci�n sobre la base del prisma
	 * @return si hay colisi�n en el primer �ndice y las coordenadas 2D de la proyecci�n en el segundo
	 */
	public Object[] colisi�nYProyecci�n(Fot�n fot�n) {
		double[] pos_fot�n = fot�n.getPos(), coord = proyectarABase(pos_fot�n);
		return new Object[] {base.colisi�n(coord, datos) && rangoV�lido(Dist.puntoAPlano(pos_fot�n, getPos(), getOrient())), coord};
	}

	@Override
	public boolean colisi�n(Fot�n fot�n) {
		double[] pos_fot�n = fot�n.getPos();
		return base.colisi�n(proyectarABase(pos_fot�n), datos) && rangoV�lido(Dist.puntoAPlano(pos_fot�n, getPos(), getOrient()));
	}

	private boolean rangoV�lido(double escalar) {
		switch (ref) {
			case BASE1:
				return escalar >= 0 && escalar <= altura;
			case BASE2:
				return escalar >= -altura && escalar <= 0;
			case CENTRO:
				return escalar >= -altura / 2 && escalar <= altura / 2;
			default:
				return false;
		}
	}
	
	/**
	 * @see Vector#proyectar2D(double[], double[], double[], double[])
	 */
	public double[] proyectarABase(double[] punto) {
		return Vector.proyectar2D(punto, getPos(), getOrient(), getRotaci�n());
	}
	
	public static class Bases {
		
		private Bases() {
			throw new UnsupportedOperationException("Clase no instanciable");
		}
		
		/**
		 * El vector datos incluye el ancho y alto en los primeros �ndices
		 */
		public static final Base RECTANGULAR = (double[] coord, Object[] datos) -> {
			if (datos.length > 2) throw new IllegalArgumentException("Datos incorrectos");
			return coord[0] <= (double) datos[0]/2 && coord[0] >= - (double) datos[0]/2
				&& coord[1] <= (double) datos[1]/2 && coord[1] >= - (double) datos[1]/2;
		};
	}
}