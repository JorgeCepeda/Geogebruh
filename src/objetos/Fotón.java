package objetos;

import java.util.Collection;
import java.util.function.BooleanSupplier;

import chunks_NoCeldas.*;
import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoCompuesto;
import objetos.abstracto.Proyecci�n;
import operaciones.Dist;
import operaciones.MyMath;

public class Fot�n extends Proyecci�n {
	private static final long serialVersionUID = 1L;
	private Collection<Objeto> objetos;
	private Objeto obj_coli, obj_coli_contenedor;
	private Fot�n subfot�n;
	private boolean isSub;
	
	public Fot�n(double[] pos) {
		super(pos);
	}
	
	public Fot�n() {}
	
	@Override
	public void setPos(double[] pos, boolean enlazar) {
		super.setPos(pos, enlazar);
		if (subfot�n != null) subfot�n.setPos(pos, enlazar);
	}
	
	public synchronized Collection<Objeto> objs() {
		return objetos;
	}
	
	public synchronized void setObjs(Collection<Objeto> objetos) {
		this.objetos = objetos;
	}
	
	public int tama�oObjs() {
		return objetos.size();
	}

	public Objeto objCol() {
		return obj_coli;
	}
	
	protected void setObjCol(Objeto obj_coli) {
		this.obj_coli = obj_coli;
	}
	
	public Objeto objColContenedor() {
		return obj_coli_contenedor;
	}
	
	protected void setObjColContenedor(Objeto obj_coli_contenedor) {
		this.obj_coli_contenedor = obj_coli_contenedor;
	}
	
//	@Override
//	public boolean colisi�n() {
//		if (objetos == null) return false;
//		
//		boolean swColisi�n = false;
//		Objeto obj_coli_posible = null, obj_coli_contenedor_posible = null;
//		for (Objeto objeto : objetos) {
//			if (objeto instanceof EspacioNegativo) {
//				if (objeto.colisi�n(this)) return false; // Si est� en un espacio negativo no se considera v�lida la colisi�n con otros objetos
//			}
//			else if (!swColisi�n) {
//				// Si colisiona con un objeto compuesto se asegura de que hay un subfot�n, se lo pasa y recoge los datos de colisi�n.
//				// En caso contrario guarda los datos de colisi�n necesarios
//				if (objeto instanceof ObjetoCompuesto) {
//					if (!isSub) {
//						crearSubfot�n(getPos());
//						if (objeto.colisi�n(subfot�n)) {
//							obj_coli_posible = subfot�n.objCol();
//							obj_coli_contenedor_posible = objeto;
//							swColisi�n = true;
//						}
//					}
//					else if (objeto.colisi�n(this)) {
//						obj_coli_posible = obj_coli;
//						swColisi�n = true;
//					}
//				}
//				else if (objeto.colisi�n(this)) {
//					obj_coli_posible = objeto;
//					if (!isSub) obj_coli_contenedor_posible = objeto;
//					swColisi�n = true;
//				}
//			}
//		}
//		if (swColisi�n) {
//			obj_coli = obj_coli_posible;
//			obj_coli_contenedor = obj_coli_contenedor_posible;
//			return true;
//		}
//		return false;
//	}
	
	@Override
	public boolean colisi�n() {
		if (objetos == null) return false;
		
		boolean swColisi�n = false;
		Objeto obj_coli_posible = null, obj_coli_contenedor_posible = null;
		for (Objeto objeto : objetos) {
			if (objeto instanceof EspacioNegativo) {
				if (objeto.colisi�n(this)) return false; // Si est� en un espacio negativo no se considera v�lida la colisi�n con otros objetos
			}
			else if (!swColisi�n) {
				Object[] resultado = comprobarColisi�n(objeto);
				if ((boolean) resultado[0]) {
					swColisi�n = true;
					obj_coli_posible = (Objeto) resultado[1];
					obj_coli_contenedor_posible = objeto;
				}
			}
		}
		if (swColisi�n) {
			obj_coli = obj_coli_posible;
			obj_coli_contenedor = obj_coli_contenedor_posible;
			return true;
		}
		return false;
	}
	
	/**
	 * Si colisiona con un objeto compuesto se asegura de que hay un subfot�n, se lo pasa y recoge los datos de colisi�n.
	 * En caso contrario guarda los datos de colisi�n necesarios
	 * @return si colisiona en el primer �ndice, y en tal caso el objeto concreto con el que colisiona en el segundo �ndice
	 */
	protected Object[] comprobarColisi�n(Objeto objeto) {
		if (objeto instanceof ObjetoCompuesto) {
			if (isSub) {
				if (objeto.colisi�n(this)) return new Object[] {true, obj_coli};
			}
			else {
				crearSubfot�n(getPos());
				if (objeto.colisi�n(subfot�n)) return new Object[] {true, subfot�n.objCol()};
			}
		}
		else if (objeto.colisi�n(this)) return new Object[] {true, objeto};
		
		return new Object[] {false};
	}
	
	public boolean enEspacioNegativo() {
		return comprobarEspacioNegativo(objetos);
	}
	
	private boolean comprobarEspacioNegativo(Collection<Objeto> objetos) {
		for (Objeto objeto : objetos) {
			if (objeto instanceof EspacioNegativo && objeto.colisi�n(this) ||
				objeto instanceof ObjetoCompuesto && comprobarEspacioNegativo(((ObjetoCompuesto) objeto).objs())) return true;
		}
		return false;
	}
	
	protected void crearSubfot�n(double[] pos) {
		if (subfot�n == null && !isSub) {
			subfot�n = new Fot�n(pos);
			subfot�n.setSub();
		}
	}
	
	public boolean isSub() {
		return isSub;
	}
	
	protected void setSub() {
		isSub = true;
	}

	public Fot�n subfot�n() {
		return subfot�n;
	}
	
	protected void setSubfot�n(Fot�n subfot�n) {
		this.subfot�n = subfot�n;
	}

	/**
	 * Proyecta un fot�n como indica la sobrecarga del m�todo, creando un nuevo fot�n
	 * @see Fot�n#proyectar(double[], double[], double, boolean, Fot�n, BooleanSupplier)
	 */
	public static boolean proyectar(double[] pos_ini, double[] pos_destino, double salto_inicial, boolean swPrecisi�n, BooleanSupplier detenci�n) {
		return proyectar(pos_ini, pos_destino, salto_inicial, swPrecisi�n, new Fot�n(), detenci�n);
	}
	
	/**
	 * @see Fot�n#proyectar(double[], double, double, boolean, Fot�n, BooleanSupplier)
	 * @param pos_destino - El punto final de la proyecci�n
	 */
	public static boolean proyectar(double[] pos_ini, double[] pos_destino, double salto_inicial, boolean swPrecisi�n, Fot�n fot�n, BooleanSupplier detenci�n) {
		fot�n.setOrient(MyMath.unitario(MyMath.vector(pos_ini, pos_destino)));
		return proyectar(pos_ini, Dist.puntoAPunto(pos_ini, pos_destino), salto_inicial, swPrecisi�n, fot�n, detenci�n);
	}
	
	/**
	 * Permite proyectar un fot�n ya creado hasta una distancia m�xima o hasta que se cumpla la condici�n que lo detiene
	 * @param pos_ini - El punto inicial de la proyecci�n
	 * @param dist_m�x - El l�mite de unidades que puede proyectarse
	 * @param salto_inicial - La cantidad de unidades que se salta hacia su proyecci�n antes de comprobar si debe detenerse
	 * @param swPrecisi�n - El modo de proyecci�n, con menos errores de redondeo si se activa
	 * @param fot�n - Fot�n a proyectar, con una orientaci�n preasignada
	 * @param detenci�n - La l�gica de detenci�n del fot�n (ej: dejar de colisionar con un objeto, o por el contrario empezar a colisionar)
	 * @return si ha sido detenido sin pasar la distancia m�xima
	 */
	public static boolean proyectar(double[] pos_ini, double dist_m�x, double salto_inicial, boolean swPrecisi�n, Fot�n fot�n, BooleanSupplier detenci�n) { //FIXME m�s lento, al menos en java 8
		double[] orient = fot�n.getOrient(), coord = MyMath.multipl(orient, salto_inicial);
		double multiplicador, dist;

		// Proyecci�n del fot�n
		fot�n.setPos(coord, false);
		while ((dist = MyMath.round(Dist.puntoAPunto(fot�n.getPos(), pos_ini), 12)) <= dist_m�x) {
			double velocidad = 1;
			Chunk chunk = Chunks.getChunk(fot�n);
			if (chunk.est�Vac�o()) {
				// Saltar chunk
				if ((velocidad = Chunks.saltarChunk(fot�n.getPos(), orient, chunk)) == -1) break;
			}
			else {
				fot�n.setObjs(chunk.getObjs());
				Malla malla_actual = chunk.getMalla(fot�n.getPos(1));
				if (malla_actual == null) {
					// Saltar malla
					if ((velocidad = Chunks.saltarMalla(fot�n.getPos(), orient, chunk)) == -1) break;
				}
				else if (detenci�n.getAsBoolean()) return true;
				
				if ((multiplicador = 1 + dist*0.18 /*20 cuando dist es 100*/) < 20) { // Precisi�n de la proyecci�n
					velocidad = 0.05*multiplicador;
				}
			}
			
			if (swPrecisi�n) coord = MyMath.sumar(pos_ini, MyMath.multipl(orient, dist + velocidad));
			else for (int i = 0; i < coord.length; i++) {
				coord[i] += orient[i]*velocidad;
			}
			fot�n.setPos(MyMath.fix(coord), false);
		}
		return false;
	}
}