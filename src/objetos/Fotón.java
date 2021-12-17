package objetos;

import java.util.Collection;
import java.util.function.BooleanSupplier;

import chunks_NoCeldas.*;
import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoCompuesto;
import objetos.abstracto.Proyección;
import operaciones.Dist;
import operaciones.MyMath;

public class Fotón extends Proyección {
	private static final long serialVersionUID = 1L;
	private Collection<Objeto> objetos;
	private Objeto obj_coli, obj_coli_contenedor;
	private Fotón subfotón;
	private boolean isSub;
	
	public Fotón(double[] pos) {
		super(pos);
	}
	
	public Fotón() {}
	
	@Override
	public void setPos(double[] pos, boolean enlazar) {
		super.setPos(pos, enlazar);
		if (subfotón != null) subfotón.setPos(pos, enlazar);
	}
	
	public synchronized Collection<Objeto> objs() {
		return objetos;
	}
	
	public synchronized void setObjs(Collection<Objeto> objetos) {
		this.objetos = objetos;
	}
	
	public int tamañoObjs() {
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
//	public boolean colisión() {
//		if (objetos == null) return false;
//		
//		boolean swColisión = false;
//		Objeto obj_coli_posible = null, obj_coli_contenedor_posible = null;
//		for (Objeto objeto : objetos) {
//			if (objeto instanceof EspacioNegativo) {
//				if (objeto.colisión(this)) return false; // Si está en un espacio negativo no se considera válida la colisión con otros objetos
//			}
//			else if (!swColisión) {
//				// Si colisiona con un objeto compuesto se asegura de que hay un subfotón, se lo pasa y recoge los datos de colisión.
//				// En caso contrario guarda los datos de colisión necesarios
//				if (objeto instanceof ObjetoCompuesto) {
//					if (!isSub) {
//						crearSubfotón(getPos());
//						if (objeto.colisión(subfotón)) {
//							obj_coli_posible = subfotón.objCol();
//							obj_coli_contenedor_posible = objeto;
//							swColisión = true;
//						}
//					}
//					else if (objeto.colisión(this)) {
//						obj_coli_posible = obj_coli;
//						swColisión = true;
//					}
//				}
//				else if (objeto.colisión(this)) {
//					obj_coli_posible = objeto;
//					if (!isSub) obj_coli_contenedor_posible = objeto;
//					swColisión = true;
//				}
//			}
//		}
//		if (swColisión) {
//			obj_coli = obj_coli_posible;
//			obj_coli_contenedor = obj_coli_contenedor_posible;
//			return true;
//		}
//		return false;
//	}
	
	@Override
	public boolean colisión() {
		if (objetos == null) return false;
		
		boolean swColisión = false;
		Objeto obj_coli_posible = null, obj_coli_contenedor_posible = null;
		for (Objeto objeto : objetos) {
			if (objeto instanceof EspacioNegativo) {
				if (objeto.colisión(this)) return false; // Si está en un espacio negativo no se considera válida la colisión con otros objetos
			}
			else if (!swColisión) {
				Object[] resultado = comprobarColisión(objeto);
				if ((boolean) resultado[0]) {
					swColisión = true;
					obj_coli_posible = (Objeto) resultado[1];
					obj_coli_contenedor_posible = objeto;
				}
			}
		}
		if (swColisión) {
			obj_coli = obj_coli_posible;
			obj_coli_contenedor = obj_coli_contenedor_posible;
			return true;
		}
		return false;
	}
	
	/**
	 * Si colisiona con un objeto compuesto se asegura de que hay un subfotón, se lo pasa y recoge los datos de colisión.
	 * En caso contrario guarda los datos de colisión necesarios
	 * @return si colisiona en el primer índice, y en tal caso el objeto concreto con el que colisiona en el segundo índice
	 */
	protected Object[] comprobarColisión(Objeto objeto) {
		if (objeto instanceof ObjetoCompuesto) {
			if (isSub) {
				if (objeto.colisión(this)) return new Object[] {true, obj_coli};
			}
			else {
				crearSubfotón(getPos());
				if (objeto.colisión(subfotón)) return new Object[] {true, subfotón.objCol()};
			}
		}
		else if (objeto.colisión(this)) return new Object[] {true, objeto};
		
		return new Object[] {false};
	}
	
	public boolean enEspacioNegativo() {
		return comprobarEspacioNegativo(objetos);
	}
	
	private boolean comprobarEspacioNegativo(Collection<Objeto> objetos) {
		for (Objeto objeto : objetos) {
			if (objeto instanceof EspacioNegativo && objeto.colisión(this) ||
				objeto instanceof ObjetoCompuesto && comprobarEspacioNegativo(((ObjetoCompuesto) objeto).objs())) return true;
		}
		return false;
	}
	
	protected void crearSubfotón(double[] pos) {
		if (subfotón == null && !isSub) {
			subfotón = new Fotón(pos);
			subfotón.setSub();
		}
	}
	
	public boolean isSub() {
		return isSub;
	}
	
	protected void setSub() {
		isSub = true;
	}

	public Fotón subfotón() {
		return subfotón;
	}
	
	protected void setSubfotón(Fotón subfotón) {
		this.subfotón = subfotón;
	}

	/**
	 * Proyecta un fotón como indica la sobrecarga del método, creando un nuevo fotón
	 * @see Fotón#proyectar(double[], double[], double, boolean, Fotón, BooleanSupplier)
	 */
	public static boolean proyectar(double[] pos_ini, double[] pos_destino, double salto_inicial, boolean swPrecisión, BooleanSupplier detención) {
		return proyectar(pos_ini, pos_destino, salto_inicial, swPrecisión, new Fotón(), detención);
	}
	
	/**
	 * @see Fotón#proyectar(double[], double, double, boolean, Fotón, BooleanSupplier)
	 * @param pos_destino - El punto final de la proyección
	 */
	public static boolean proyectar(double[] pos_ini, double[] pos_destino, double salto_inicial, boolean swPrecisión, Fotón fotón, BooleanSupplier detención) {
		fotón.setOrient(MyMath.unitario(MyMath.vector(pos_ini, pos_destino)));
		return proyectar(pos_ini, Dist.puntoAPunto(pos_ini, pos_destino), salto_inicial, swPrecisión, fotón, detención);
	}
	
	/**
	 * Permite proyectar un fotón ya creado hasta una distancia máxima o hasta que se cumpla la condición que lo detiene
	 * @param pos_ini - El punto inicial de la proyección
	 * @param dist_máx - El límite de unidades que puede proyectarse
	 * @param salto_inicial - La cantidad de unidades que se salta hacia su proyección antes de comprobar si debe detenerse
	 * @param swPrecisión - El modo de proyección, con menos errores de redondeo si se activa
	 * @param fotón - Fotón a proyectar, con una orientación preasignada
	 * @param detención - La lógica de detención del fotón (ej: dejar de colisionar con un objeto, o por el contrario empezar a colisionar)
	 * @return si ha sido detenido sin pasar la distancia máxima
	 */
	public static boolean proyectar(double[] pos_ini, double dist_máx, double salto_inicial, boolean swPrecisión, Fotón fotón, BooleanSupplier detención) { //FIXME más lento, al menos en java 8
		double[] orient = fotón.getOrient(), coord = MyMath.multipl(orient, salto_inicial);
		double multiplicador, dist;

		// Proyección del fotón
		fotón.setPos(coord, false);
		while ((dist = MyMath.round(Dist.puntoAPunto(fotón.getPos(), pos_ini), 12)) <= dist_máx) {
			double velocidad = 1;
			Chunk chunk = Chunks.getChunk(fotón);
			if (chunk.estáVacío()) {
				// Saltar chunk
				if ((velocidad = Chunks.saltarChunk(fotón.getPos(), orient, chunk)) == -1) break;
			}
			else {
				fotón.setObjs(chunk.getObjs());
				Malla malla_actual = chunk.getMalla(fotón.getPos(1));
				if (malla_actual == null) {
					// Saltar malla
					if ((velocidad = Chunks.saltarMalla(fotón.getPos(), orient, chunk)) == -1) break;
				}
				else if (detención.getAsBoolean()) return true;
				
				if ((multiplicador = 1 + dist*0.18 /*20 cuando dist es 100*/) < 20) { // Precisión de la proyección
					velocidad = 0.05*multiplicador;
				}
			}
			
			if (swPrecisión) coord = MyMath.sumar(pos_ini, MyMath.multipl(orient, dist + velocidad));
			else for (int i = 0; i < coord.length; i++) {
				coord[i] += orient[i]*velocidad;
			}
			fotón.setPos(MyMath.fix(coord), false);
		}
		return false;
	}
}