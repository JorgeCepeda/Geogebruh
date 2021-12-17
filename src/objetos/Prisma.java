package objetos;

import java.io.Serializable;

import objetos.abstracto.Objeto;
import operaciones.Dist;
import operaciones.Vector;

public class Prisma extends Objeto {
	interface Base extends Serializable {
		
		/**
		 * @param coord - Las coordenadas x e y del plano
		 * @param datos - Los parámetros de la base (ej: alto y ancho de un rectángulo)
		 * @return si las coordenadas corresponden a un punto de la base
		 */
		public boolean colisión(double[] coord, Object[] datos);
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
		setRotaciónYOrient(p.getRotación());
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
	 * Comprueba la colisión con el fotón y su proyección sobre la base del prisma
	 * @return si hay colisión en el primer índice y las coordenadas 2D de la proyección en el segundo
	 */
	public Object[] colisiónYProyección(Fotón fotón) {
		double[] pos_fotón = fotón.getPos(), coord = proyectarABase(pos_fotón);
		return new Object[] {base.colisión(coord, datos) && rangoVálido(Dist.puntoAPlano(pos_fotón, getPos(), getOrient())), coord};
	}

	@Override
	public boolean colisión(Fotón fotón) {
		double[] pos_fotón = fotón.getPos();
		return base.colisión(proyectarABase(pos_fotón), datos) && rangoVálido(Dist.puntoAPlano(pos_fotón, getPos(), getOrient()));
	}

	private boolean rangoVálido(double escalar) {
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
		return Vector.proyectar2D(punto, getPos(), getOrient(), getRotación());
	}
	
	public static class Bases {
		
		private Bases() {
			throw new UnsupportedOperationException("Clase no instanciable");
		}
		
		/**
		 * El vector datos incluye el ancho y alto en los primeros índices
		 */
		public static final Base RECTANGULAR = (double[] coord, Object[] datos) -> {
			if (datos.length > 2) throw new IllegalArgumentException("Datos incorrectos");
			return coord[0] <= (double) datos[0]/2 && coord[0] >= - (double) datos[0]/2
				&& coord[1] <= (double) datos[1]/2 && coord[1] >= - (double) datos[1]/2;
		};
	}
}