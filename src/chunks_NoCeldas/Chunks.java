package chunks_NoCeldas;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import niveles.Niveles;
import objetos.Fot�n;
import objetos.Objetos;
import operaciones.Dist;
import operaciones.MyMath;
import otros.Tareas;

public class Chunks {
	public static final int PARALELOS = (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 4.0 + 1); /*Funci�n a mano*/
	private static final AtomicInteger cont_pausa = new AtomicInteger();
	private static final Semaphore en_carga = new Semaphore(PARALELOS);
	private static ConcurrentHashMap<String,Chunk> lista_chunks = new ConcurrentHashMap<>(100000);
	private static int alto_regi�n = 20;
	
	public static void main(String[] args) { // Test de rendimiento
		Objetos.init();
		System.out.println(PARALELOS);
		double[] pos = Niveles.cargado().getPos();
		
		boolean enserie = false;
		int mitad_cuadrado = 9, chunks_totales = (mitad_cuadrado*2+1)*(mitad_cuadrado*2+1);
		if (enserie) {
			System.out.println("Cargando chunks en serie...");
			int cont = 0;
			long start = System.nanoTime();
			for (int i = -mitad_cuadrado; i <= mitad_cuadrado; i++) {
				for (int j = -mitad_cuadrado; j <= mitad_cuadrado; j++) {
					a�adirChunk(new Chunk(alto_regi�n, Chunk.getPos(pos[0] + Chunk.dimensiones(0)*i, pos[2] + Chunk.dimensiones(0)*j), true));
					
					//if (++cont%10 == 0) System.out.println(cont + "/" + chunks_totales);
					System.out.println(++cont + "/" + chunks_totales);
				}
			}
			System.out.printf("Chunks cargados en %d nanosegundos.\n", System.nanoTime() - start);
		}
		else {
			System.out.println("Cargando chunks en paralelo...");
			long start = System.nanoTime();
			cargar(pos, mitad_cuadrado);
			System.out.printf("Chunks cargados en %d nanosegundos.\n", System.nanoTime() - start);
		}
		System.exit(0);
	}
	
	private Chunks() {
		throw new IllegalStateException("Clase no instanciable");
	}
	
	public static void cargar(double[] pos, int mitad_cuadrado) {
		HashSet<Future<?>> cargas = new HashSet<>((int) (mitad_cuadrado*mitad_cuadrado*1.5));
		for (int i = -mitad_cuadrado; i <= mitad_cuadrado; i++) {
			for (int j = -mitad_cuadrado; j <= mitad_cuadrado; j++) {
				cargas.add((Future<?>) a�adirChunk(new Chunk(alto_regi�n, Chunk.getPos(pos[0] + Chunk.dimensiones(0)*i, pos[2] + Chunk.dimensiones(0)*j), false))[1]);
			}
		}
		Tareas.esperar(cargas);
	}
	
	public static void cargar(String ruta, boolean a�adir) {
		cargar(new File(ruta), a�adir);
	}
	
	@SuppressWarnings("unchecked")
	public static void cargar(File archivo, boolean a�adir) {
		Tareas.archivos.getES().submit(() -> {
			synchronized (Tareas.archivos) {
				try {
					archivo.createNewFile();
				} catch (IOException e) {
					System.err.println("Error al crear archivo");
				}
				int tama�o = (int) archivo.length();
				System.out.println("Cargando...");
				pausar();
				
				boolean swErr = false;
				if (tama�o > 0) try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(archivo), tama�o))) {
					long start = System.nanoTime();
					
					ConcurrentHashMap<String, Chunk> chunks_le�dos = (ConcurrentHashMap<String, Chunk>) ois.readObject();
					if (a�adir) {
						for (Entry<String, Chunk> entry : chunks_le�dos.entrySet()) {
							a�adirChunk(entry.getKey(), entry.getValue());
						}
					}
					else {
						synchronized (Tareas.chunks) {
							Tareas.chunks.getES().shutdownNow();
							Tareas.chunks.init();
						}
						for (Chunk c : chunks_le�dos.values()) {
							c.cargarEnHilo();
						}
						setChunks(chunks_le�dos);
					}
					
					System.out.println("Cargados unos " + lista_chunks.size() + " chunks desde el archivo " + archivo.getPath() + " en " + (System.nanoTime()-start) + " nanosegundos");
				} catch (IOException e) {
					swErr = true;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				else swErr = true;
				
				if (swErr) {
					// Mostrar error y leer copia si no se est� haciendo ya
					System.err.println("Error de lectura de " + archivo.getPath());
					if (!archivo.getPath().endsWith(".bck")) cargar(archivo.getPath() + ".bck", a�adir);
				}
				reanudar();
			}
		});
	}
	
	public static void guardar(String ruta) {
		guardar(new File(ruta));
	}
	
	public static void guardar(File archivo) {
		Tareas.archivos.getES().submit(() -> {
			synchronized (Tareas.archivos) {
				File copia = new File(archivo.getPath() + ".bck");
				try {
					archivo.createNewFile();
					copia.createNewFile();
				} catch (IOException e) {
					System.err.println("Error al crear los archivos: " + e.getMessage());
				}
				int tama�o = (int) archivo.length(), nuevo_tama�o = 6 * 1024 * 1024;
				System.out.println("Guardando...");
				pausar();
				
				// Copiar archivo original si existe a la copia
				if (tama�o > 0) {
					nuevo_tama�o = tama�o;
					try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(archivo), tama�o);
						BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(copia), tama�o)) {
						
						byte[] le�do = new byte[tama�o];
						if (bis.read(le�do) != tama�o) throw new IOException("El archivo no se ha le�do correctamente");
						bos.write(le�do);
					} catch (IOException e) {
						System.err.println("No se ha podido hacer una copia de " + archivo.getPath() + ": " + e.getMessage());
					}
				}
				
				// Guardar en el archivo
				try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(archivo), nuevo_tama�o))) {
					long start = System.nanoTime();
					oos.writeObject(lista_chunks);
					System.out.println("Guardados unos " + lista_chunks.size() + " chunks en " + (System.nanoTime()-start) + " nanosegundos");
				} catch (IOException e) {
					System.err.println("No se ha podido guardar en " + archivo.getPath() + ": " + e.getMessage());
				}
				reanudar();
			}
		});
	}

	public static Chunk getChunk(Fot�n fot�n) {
		return getChunk(fot�n.getPos());
	}
	
	public static Chunk getChunk(double[] pos) {
		int[] chunk_pos = Chunk.getPos(pos);
		String key = Chunk.getChunkKey(chunk_pos, true);
		
		// Comprueba si ya existe
		Chunk chunk_actual = lista_chunks.get(key);
		if (chunk_actual != null) return chunk_actual;
		
		return (Chunk) a�adirChunk(key, new Chunk(alto_regi�n, chunk_pos, false))[0];
	}
	
	private static Object[] a�adirChunk(Chunk c) {
		return a�adirChunk(c.toString(), c);
	}
	
	private static Object[] a�adirChunk(String key, Chunk c) {
		// Compruebo si no est� ya a�adido
		Chunk chunk_existente = lista_chunks.putIfAbsent(key, c);
		if (chunk_existente == null) {
			return new Object[] {c, c.cargarEnHilo()};
		}
		return new Object[] {chunk_existente, null};
	}
	
	public static double saltarMalla(double[] pos, double[] orient) {
		return saltarMalla(pos, orient, getChunk(pos));
	}
	
	public static double saltarMalla(double[] pos, double[] orient, Chunk chunk_actual) {
		if (orient[1] == 0) return saltarChunk(pos, orient, chunk_actual);
		
		int nueva_malla_y = (int) (Math.floor(pos[1]/alto_regi�n) * alto_regi�n);
		if (orient[1] > 0) {
			nueva_malla_y += alto_regi�n;
			if (nueva_malla_y > Chunk.dimensiones(1)) return saltarChunk(pos, orient, chunk_actual);
			if (nueva_malla_y < 0) nueva_malla_y = 0;
		}
		else {
			if (nueva_malla_y < 0) return saltarChunk(pos, orient, chunk_actual);
			if (nueva_malla_y > Chunk.dimensiones(1)) nueva_malla_y = Chunk.dimensiones(1);
		}
		
		if (Math.abs(orient[1]) == 1) return MyMath.fix(Math.abs(nueva_malla_y - pos[1])) + 0.01;
		
		// Esquina 1: La esquina de la malla m�s cerca de la trayectoria de la orientaci�n; Esquinas 2 y 3 adyacentes a Esquina 1 en el plano horizontal
		int[] esquina1 = {chunk_actual.getX(), nueva_malla_y, chunk_actual.getZ()}, esquina2 = esquina1.clone(), esquina3 = esquina1.clone();
		
		if (orient[0] > 0) {
			esquina1[0] += Chunk.dimensiones(0);
			esquina3[0] += Chunk.dimensiones(0);
		}
		else esquina2[0] += Chunk.dimensiones(0);
		
		if (orient[2] > 0) {
			esquina1[2] += Chunk.dimensiones(2);
			esquina2[2] += Chunk.dimensiones(2);
		}
		else esquina3[2] += Chunk.dimensiones(2);
		
		if (orient[0] < 0 && esquina1[0] == pos[0] || orient[2] < 0 && esquina1[2] == pos[2]) return 0.1;
		
		// Elegir segunda esquina para el plano dependiendo de la orientaci�n
		boolean swEsquina2_Izq = orient[0] > 0 && orient[2] > 0 || orient[0] <= 0 && orient[2] <= 0;
		double dist = Dist.puntoAPlano(esquina1, pos, new double[] {orient[2], 0, -orient[0]});

		int[] esquina_elegida = esquina3;
		if (dist > 0 && swEsquina2_Izq || dist <= 0 && !swEsquina2_Izq) esquina_elegida = esquina2;
		
		double[] coefs = MyMath.planoPuntos(esquina1, esquina_elegida, pos),
			recta_plano = MyMath.prodVectorial(new double[] {coefs[0], coefs[1], coefs[2]}, new double[] {orient[2], 0, -orient[0]});
		
		double tan_recta_plano = MyMath.fix(recta_plano[1] / MyMath.pit�goras(new double[] {recta_plano[0], recta_plano[2]})),
			tan_trayec = MyMath.fix(orient[1] / MyMath.pit�goras(new double[] {orient[0], orient[2]}));
		
		if (Math.abs(tan_trayec) <= Math.abs(tan_recta_plano)) return saltarChunk(pos, orient, chunk_actual);
		return MyMath.fix((nueva_malla_y - pos[1]) / orient[1]) + 0.01;
	}
	
	public static double saltarChunk(double[] pos, double[] orient) {
		return saltarChunk(pos, orient, getChunk(pos));
	}
	
	public static double saltarChunk(double[] pos, double[] orient, Chunk chunk_actual) {
		if (Math.abs(orient[1]) == 1) return -1;
		
		double nuevo_chunk_z = chunk_actual.getZ();
		if (orient[2] > 0) nuevo_chunk_z += Chunk.dimensiones(2);
		else if (orient[2] < 0 && nuevo_chunk_z == pos[2]) return 0.1;
		if (orient[0] == 0) return MyMath.fix((nuevo_chunk_z - pos[2]) / orient[2]) + 0.01;
		
		double nuevo_chunk_x = chunk_actual.getX();
		if (orient[0] > 0) nuevo_chunk_x += Chunk.dimensiones(0);
		else if (nuevo_chunk_x == pos[0]) return 0.1;
		if (orient[2] == 0) return MyMath.fix((nuevo_chunk_x - pos[0]) / orient[0]) + 0.01;
		
		double tan_diagonal = MyMath.fix((nuevo_chunk_z - pos[2]) / (nuevo_chunk_x - pos[0]));
		
		if (Math.abs(MyMath.fix(orient[2]/orient[0])) <= Math.abs(tan_diagonal)) return MyMath.fix((nuevo_chunk_x - pos[0]) / orient[0]) + 0.01;
		return MyMath.fix((nuevo_chunk_z - pos[2]) / orient[2]) + 0.01;
	}
	
	public static boolean est�EnElChunk(double[] pos, Chunk chunk) {
		return pos[0] >= chunk.getX() && pos[0] < chunk.getX() + Chunk.dimensiones(0)
			&& pos[2] >= chunk.getZ() && pos[2] < chunk.getZ() + Chunk.dimensiones(2);
	}
	
	public static boolean est�EnElChunk(int[] pos, Chunk chunk) {
		return pos[0] >= chunk.getX() && pos[0] < chunk.getX() + Chunk.dimensiones(0)
			&& pos[2] >= chunk.getZ() && pos[2] < chunk.getZ() + Chunk.dimensiones(2);
	}
	
	public static Malla getMalla(double[] pos) {
		return getChunk(pos).getMalla(pos[1]);
	}
	
	public static synchronized ConcurrentHashMap<String,Chunk> getChunks() {
		return lista_chunks;
	}
	
	public static synchronized void setChunks(ConcurrentHashMap<String,Chunk> chunks) {
		lista_chunks = chunks;
	}
	
	public static int enCarga() {
		return PARALELOS - en_carga.availablePermits();
	}
	
	public static void aumentarCarga() {
		en_carga.acquireUninterruptibly();
	}
	
	public static void disminuirCarga() {
		en_carga.release();
	}
	
	public static int contPausados() {
		return cont_pausa.get();
	}
	
	public static boolean est�nPausados() {
		return cont_pausa.get() > 0;
	}
	
	public static void pausar() {
		cont_pausa.incrementAndGet();
	}
	
	public static void reanudar() {
		cont_pausa.decrementAndGet();
	}
}