package niveles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Random;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

import chunks_NoCeldas.Chunks;
import efectos.Iluminación;
import objetos.*;
import objetos.Prisma.Bases;
import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoBase;
import objetos.abstracto.ObjetoCompuesto;
import objetos.abstracto.Objeto.Ref;
import objetos.propiedades.Borde;
import objetos.propiedades.Textura;
import otros.Contador;
import otros.Llave;
import otros.Tareas;

@SuppressWarnings("serial")
public class Niveles {
	public abstract static class Nivel implements Serializable {
		private static final long serialVersionUID = 1L;
		private LinkedHashMap<String, Objeto> objetos;
		private double[] pos, rotación;
		private String nombre;
		
		protected Nivel(double[] pos, double[] rotación, String nombre) {
			this.pos = pos.clone();
			this.rotación = rotación.clone();
			this.nombre = nombre;
		}
		
		protected Nivel(double[] pos, double[] rotación) {
			this.pos = pos.clone();
			this.rotación = rotación.clone();
		}

		public LinkedHashMap<String, Objeto> objs() {
			return objetos;
		}
		
		public void setObjs(LinkedHashMap<String, Objeto> objetos) {
			this.objetos = objetos;
		}
		
		public void borrar() {
			objetos = null;
		}
		
		public double[] getPos() {
			return pos.clone();
		}
		
		public double[] getRotación() {
			return rotación.clone();
		}
		
		public String getNombre() {
			return nombre;
		}
		
		public void añadir(Objeto obj) {
			objetos.put(obj.toString(), obj);
		}
		
		public abstract void cargar();
	}

	private static final ObjectContainer oc = Db4oEmbedded.openFile("niveles.niveles");
	private static Nivel cargado;
	private static Llave acceso_contadores;
	
	static {
		try {
			acceso_contadores = Contador.obtenerAcceso();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void main(String[] args) { //DEBUG
		long t = System.nanoTime();
		guardar(NIVEL_CÓDIGO_1);
		System.out.printf("Tiempo: %dns\n", System.nanoTime()-t);
	}
	
	public static Nivel cargado() {
		return cargado;
	}
	
	/**
	 * Descarga el nivel actual y carga el nivel especificado
	 */
	public static synchronized void cargar(Nivel nivel) {
		if (cargado != null) cargado.borrar();
		cargado = nivel;
		
		synchronized (Tareas.chunks) {
			Tareas.chunks.getES().shutdownNow();
			Tareas.chunks.init();
			Chunks.getChunks().clear();
		}
		
		Contador.reset(acceso_contadores);
		Iluminación.getLuces().clear();
		cargado.cargar();
	}
	
	/**
	 * Guarda un nivel, para ello tiene que descargar el actual, dejando cargado el que se quiere guardar
	 */
	public static synchronized void guardar(Nivel nivel) {
		Query q = oc.query();
		q.descend("nombre").constrain(nivel.getNombre());
		if (q.execute().isEmpty()) {
			cargar(nivel);
			oc.store(nivel);
			oc.commit();
			System.out.println("Nivel \"" + nivel.getNombre() + "\" guardado");
		}
		else System.err.println("El nivel \"" + nivel.getNombre() + "\" ya estaba guardado");
	}
	
	public static void borrar(Nivel nivel) {
		borrar(nivel.getNombre());
	}
	
	public static synchronized void borrar(String nombre) {
		oc.delete(nivelArchivadoIncompleto(nombre));
		oc.commit();
		System.out.println("Nivel \"" + nombre + "\" borrado de la bbdd");
	}
	
	public static void sobrescribir(Nivel nivel) {
		borrar(nivel);
		guardar(nivel);
	}
	
	public static Nivel nivelArchivado(String nombre) {
		Nivel incompleto = nivelArchivadoIncompleto(nombre);
		return new Nivel(incompleto.getPos(), incompleto.getRotación(), incompleto.getNombre()) {
			@Override
			public void cargar() { // Se desecha el código de carga del nivel archivado
				setObjs(incompleto.objs());
				
				// Asignación de los valores adecuados a los contadores de los objetos
				HashMap<Class<?>, Contador> clases = new HashMap<>();
				contarObjetos(incompleto.objs().values(), new HashSet<>(incompleto.objs().size()), clases);
				for (Entry<Class<?>, Contador> entry : clases.entrySet()) {
					Contador contador = Contador.getContador(entry.getKey(), acceso_contadores);
					if (contador != null) contador.setCont(entry.getValue().getCont());
					//System.out.println("Contador de " + entry.getKey().getSimpleName() + ": " + entry.getValue().getCont());
				}
			}
				
			private void contarObjetos(Collection<Objeto> objs_contar, HashSet<Objeto> objs_contados, HashMap<Class<?>, Contador> clases) {
				for (Objeto objeto : objs_contar) {
					Contador contador = clases.putIfAbsent(objeto.getClass(), new Contador(1));
					if (objs_contados.add(objeto) && contador != null) contador.variar(1);
					if (objeto instanceof ObjetoCompuesto) contarObjetos(((ObjetoCompuesto) objeto).objs(), objs_contados, clases);
				}
			}
		};
	}
	
	private static Nivel nivelArchivadoIncompleto(String nombre) { // No incluye el código de carga correcto si el nivel no existe en el código
		Query q = oc.query();
		q.descend("nombre").constrain(nombre);
		ObjectSet<Nivel> niveles = q.execute();
		
		if (niveles.size() > 1) throw new IllegalArgumentException("Hay más de un nivel con ese nombre");
		if (niveles.isEmpty()) throw new IllegalArgumentException("No hay ningún nivel con ese nombre");
		return niveles.get(0);
	}
	
	public static final Nivel NIVEL_CÓDIGO_1 = new Nivel(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "default") {
		@Override
		public void cargar() {
			setObjs(new LinkedHashMap<>(25));
			
			// Declaración de objetos
			Cilindro cilindro1 = new Cilindro(new double[] {45,20,0}, new double[] {Math.sqrt(2)/2, Math.sqrt(2)/2, 0}, 7, 20, Objeto.Ref.BASE2, "green");
			añadir(cilindro1);
			//ConoDoble cono = new ConoDoble(new double[] {0,15,20}, "red");
			//añadir(cono);
			//Elipsoide elip = new Elipsoide(new double[] {0,40,20}, new double[] {0,0,-1}, new double[] {20,30,9}, "yellow");
			//añadir(elip);
			Esfera esfera1 = new Esfera(new double[] {-15,15,0}, 5, "white");
			añadir(esfera1);
			Esfera esfera2 = new Esfera(new double[] {15,15,0}, 7, "#FFD400");
			añadir(esfera2);
			Esfera esfera3 = new Esfera(new double[] {-30,25,15}, 10, "blue");
			añadir(esfera3);
			Cubo cubo = new Cubo(new double[] {20,20,20}, 10, "red");
			Textura hierba = new Textura(
				new PrismaInvisible(new double[] {-5,0,0}, 5, Ref.CENTRO, new Object[] {11.0,11.0}, Bases.RECTANGULAR),
				"textures/grass.png",
				true
			);
			hierba.setEscala(10.0/16, 10.0/16);
			Textura.añadir(cubo, hierba);
			
			añadir(cubo);
			ObjCompTest objcomp1 = new ObjCompTest(new double[] {10,50,10}, "blue");
			añadir(objcomp1);
			Objeto prisma1 = new Prisma(new double[] {35,20,20}, 15, "purple", Ref.CENTRO, new Object[] {8.0 /*eje_vert*/, 5.0 /*eje_hori*/}, Bases.RECTANGULAR);
			añadir(prisma1);
			Luz luz1 = new Luz(new double[] {20,40,0}, "#33ee33", 6);
			añadir(luz1);
			Luz luz2 = new Luz(new double[] {40,40,10}, "pink", 5);
			añadir(luz2);
			luz2.setEncendida(false);
			Luz luz3 = new Luz(new double[] {40,40,-20}, "white", 10);
			añadir(luz3);
			
			esfera2.cambiarPropiedad(ObjetoBase.BORDE, new Borde("blue", 2, 'R'));
			prisma1.setRotaciónYOrient(new double[] {0,0,Math.PI/4});
		}
	}, NIVEL_CÓDIGO_2 = new Nivel(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_2") {
		@Override
		public void cargar() {
			setObjs(new LinkedHashMap<>(25));
			
			// Declaración de objetos
			Cilindro cilindro1 = new Cilindro(new double[] {45,20,0}, new double[] {Math.sqrt(2)/2, Math.sqrt(2)/2, 0}, 7, 20, Objeto.Ref.BASE2, "green");
			añadir(cilindro1);
			ConoDoble cono = new ConoDoble(new double[] {0,15,20}, "red");
			añadir(cono);
			Elipsoide elip = new Elipsoide(new double[] {0,40,20}, new double[] {0,0,-1}, new double[] {20,30,9}, "yellow");
			añadir(elip);
			Esfera esfera1 = new Esfera(new double[] {-15,15,0}, 5, "white");
			añadir(esfera1);
			Esfera esfera2 = new Esfera(new double[] {15,15,0}, 7, "#FFD400");
			añadir(esfera2);
			Esfera esfera3 = new Esfera(new double[] {-30,25,15}, 10, "blue");
			añadir(esfera3);
			Cubo cubo = new Cubo(new double[] {20,20,20}, 10, "red");
			añadir(cubo);
			ObjCompTest objcomp1 = new ObjCompTest(new double[] {10,50,10}, "blue");
			añadir(objcomp1);
			Objeto prisma1 = new Prisma(new double[] {35,20,20}, 15, "purple", Ref.CENTRO, new Object[] {8.0 /*eje_vert*/, 5.0 /*eje_hori*/}, Bases.RECTANGULAR);
			añadir(prisma1);
			
			esfera2.cambiarPropiedad(ObjetoBase.BORDE, new Borde("blue", 2, 'R'));
			prisma1.setRotaciónYOrient(new double[] {0,0,Math.PI/4});
			objcomp1.objs().add(esfera1);
			objcomp1.objs().add(esfera2);
			objcomp1.objs().add(esfera3);
			//objcomp1.objs().add(objcomp1);
		}
	}, NIVEL_CÓDIGO_3 = new Nivel(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_3") {
		@Override
		public void cargar() {
			setObjs(new LinkedHashMap<>(25));
			
			// Declaración de objetos
			Objeto prisma1 = new Prisma(new double[] {35,20,20}, 15, "purple", Ref.CENTRO, new Object[] {8.0 /*eje_vert*/, 5.0 /*eje_hori*/}, Bases.RECTANGULAR);
			añadir(prisma1);
			
			prisma1.setRotaciónYOrient(new double[] {0,0,Math.PI/4});
		}
	}, NIVEL_CÓDIGO_4 = new Nivel(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_4") {
		@Override
		public void cargar() {
			setObjs(new LinkedHashMap<>(5000));
			
			// Declaración de objetos
			Random rnd = new Random();
			for (int i = 0; i <= 500000; i++) {
				añadir(new Esfera(new double[] {rnd.nextFloat()*100, rnd.nextFloat()*100, rnd.nextFloat()*100}, rnd.nextFloat()*100, ""));
			}
			ArrayList<Objeto> l = new ArrayList<>(objs().values());
			for (Objeto obj : l) {
				ObjetoCompuesto p = new ObjetoCompuesto(obj.getPos(), true) {};
				ArrayList<Objeto> a = new ArrayList<>();
				a.add(obj);
				p.setObjs(a);
				añadir(p);
			}
		}
	}, NIVEL_CÓDIGO_5 = new Nivel(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_5") {
		@Override
		public void cargar() {
			setObjs(new LinkedHashMap<>(5000));
			
			// Declaración de objetos
			Esfera esfera2 = new Esfera(new double[] {15,15,0}, 7, "#FFD400");
			añadir(esfera2);
			esfera2.cambiarPropiedad(ObjetoBase.BORDE, new Borde("blue", 2, 'R'));
		}
	}, NIVEL_CÓDIGO_6 = new Nivel(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_6") {
		@Override
		public void cargar() {
			setObjs(new LinkedHashMap<>());
			
			// Declaración de objetos
			añadir(new Cuenco(new double[] {-15,30,0}, "white"));
		}
	}, NIVEL_CÓDIGO_7 = new Nivel(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_7") {
		@Override
		public void cargar() {
			setObjs(new LinkedHashMap<>(5000));
			
			// Declaración de objetos
			Random rnd = new Random();
			for (int i = 0; i <= 50; i++) {
				ObjetoCompuesto p = new ObjetoCompuesto(new double[] {rnd.nextFloat()*100, rnd.nextFloat()*100, rnd.nextFloat()*100}, true) {};
				ArrayList<Objeto> a = new ArrayList<>();
				for (int j = 0; j < 500; j++) {
					a.add(new Esfera(new double[] {rnd.nextFloat()*100, rnd.nextFloat()*100, rnd.nextFloat()*100}, rnd.nextFloat()*100, ""));
				}
				System.out.println("obj " + i);
				a.add(new EspacioNegativo(new Esfera(p.getPos(), 3000, "red")));
				p.setObjs(a);
				añadir(p);
			}
			añadir(new EspacioNegativo(new Esfera(getPos(), 30, "red")));
		}
	}, NIVEL_CÓDIGO_8 = new Nivel(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_8") {
		@Override
		public void cargar() {
			setObjs(new LinkedHashMap<>(75000));
			
			// Declaración de objetos
			Random rnd = new Random();
			ObjetoCompuesto p = new ObjetoCompuesto(new double[] {rnd.nextFloat()*100, rnd.nextFloat()*100, rnd.nextFloat()*100}, true) {};
			ArrayList<Objeto> a = new ArrayList<>();
			a.add(new Esfera(new double[] {rnd.nextFloat()*100, rnd.nextFloat()*100, rnd.nextFloat()*100}, rnd.nextFloat()*100, "red"));
			p.setObjs(a);
			añadir(p);
			for (int i = 0; i <= 5000; i++) {
				añadir(new Esfera(new double[] {rnd.nextFloat()*100, rnd.nextFloat()*100, rnd.nextFloat()*100}, rnd.nextFloat()*100, "blue"));
			}
			añadir(new EspacioNegativo(new Esfera(p.getPos(), 3000, "red")));
		}
	}, NOCHE_ESTRELLADA = new Nivel(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "noche_estrellada") {
		@Override
		public void cargar() {
			setObjs(new LinkedHashMap<>());
			
			// Declaración de objetos
			añadir(new Luna(new double[] {-15,30,0}, "white"));
		}
	}, NIVEL_CUBO_ESPAÑA = new Nivel(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "ESPAÑA") {
		@Override
		public void cargar() {
			setObjs(new LinkedHashMap<>());
			Cubo cubo = new Cubo(new double[] {20,20,20}, 10, "red");
			Textura tex_cubo1 = new Textura(
				new PrismaInvisible(new double[3], 30, Ref.CENTRO, new Object[] {20.0,5.0}, Bases.RECTANGULAR),
				"yellow",
				false
			);
			Textura.añadir(cubo, tex_cubo1);
			
			añadir(cubo);
		}
	};
	
	private Niveles() {
		throw new IllegalStateException("Clase no instanciable");
	}
}