package chunks_NoCeldas;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import gráficos.Pantalla;
import objetos.Objetos;
import objetos.abstracto.Objeto;
import otros.Tareas;

public class Chunk implements Serializable {
	public static final ReentrantLock lock = new ReentrantLock();
	public static final boolean CARGA_LIGERA = true; // Permite cambiar el modo de carga a uno intermitente
	private static final long serialVersionUID = 1L;
	private static int[] dimens = {30,100,30};
	private LinkedHashSet<Objeto> objs = new LinkedHashSet<>(100);
	private Malla[] regiones;
	private int alto_región, pos_x, pos_z;
	private boolean cargado;
	private transient Future<?> tarea;
	private transient boolean cargando, carga_paralela;
	
	public Chunk(int alto_región, int pos_x, int pos_z, boolean cargar) {
		setPos(pos_x, pos_z);
		setAltoRegión(alto_región);
		init(cargar);
	}
	
	public Chunk(int alto_región, double[] pos, boolean cargar) {
		int[] chunk_pos = getPos(pos);
		setPos(chunk_pos[0], chunk_pos[1]);
		setAltoRegión(alto_región);
		init(cargar);
	}
	
	public Chunk(int alto_región, int[] pos, boolean cargar) {
		int[] chunk_pos = getPos(pos);
		setPos(chunk_pos[0], chunk_pos[1]);
		setAltoRegión(alto_región);
		init(cargar);
	}
	
	private void init(boolean cargar) {
		initMallas();
		if (cargar) cargar(); // Cargar
	}

	private synchronized void cargarParalelo() {
		carga_paralela = true;
		cargar();
	}
	
	public synchronized void cargar() { // Solo un hilo puede cargar el mismo chunk
		if (!cargado) {
			esperarEnCola();
			Chunks.aumentarCarga();
			if (regiones == null) {
				objs = new LinkedHashSet<>(100);
				initMallas();
			}
			
			ScheduledFuture<?> future = null;
			AtomicBoolean condición = null;
			if (CARGA_LIGERA) {
				/*if (carga_paralela) future = Tareas.monitorizar(tarea, 1500, condición = new AtomicBoolean(), objs); //FIXME ver método
				else*/ future = Tareas.monitorizar(Thread.currentThread(), 1500, condición = new AtomicBoolean(), objs);
			}
			
			for (Malla malla : regiones) {
				malla.rellenar(objs, Objetos.objs().values());
			}
			
			if (future != null) {
				Tareas.endMonitorizar(future, condición, objs);
				tarea = null;
			}
			cargado = true;
			Chunks.disminuirCarga();
			carga_paralela = false;
		}
	}
	
	/**
	 * Carga asíncrona de la sección
	 * @return la tarea asociada a la carga
	 */
	public synchronized Future<?> cargarEnHilo() {
		if (!cargado && !cargando) {
			cargando = true;
			Future<?> nueva_tarea = Tareas.chunks.getES().submit(this::cargarParalelo);
			tarea = nueva_tarea;
			return nueva_tarea;
		}
		return null;
	}
	
	public synchronized void descargar() {
		if (cargado) {
			cargado = false;
			cargando = false;
			regiones = null;
			objs = null;
		}
	}

	/**
	 * Limita cuántos chunks se pueden cargar a la vez mediante el paso de un "testigo" entre hilos
	 */
	public static void esperarEnCola() {
		while (Chunks.enCarga() >= límiteCarga()) {
			lock.lock();
			lock.unlock();
		}
	}
	
	private static int límiteCarga() {
		if (Chunks.estánPausados()) return 0;
		int límite = Chunks.PARALELOS - Pantalla.hilosCorriendo();
		return límite > 0 ? límite : 1;
	}
	
	public static int[] dimensiones() {
		return dimens.clone();
	}
	
	public static int dimensiones(int i) {
		return dimens[i];
	}

	public static void setDimensiones(int[] dimens) {
		if (dimens.length != 3 || dimens[0] < 1 || dimens[1] < 1 || dimens[2] < 1) throw new IllegalArgumentException("Dimensiones de chunk inválidas");
		Chunk.dimens = dimens;
	}
	
	private void initMallas() {
		regiones = new Malla[(int) Math.ceil((double) dimens[1]/alto_región)];
		
		for (int i = 0; i < regiones.length; i++) {
			regiones[i] = new Malla(dimens[0], alto_región, dimens[2]);
			regiones[i].setPos(new int[] {pos_x, alto_región*i, pos_z});
		}
	}
	
	public Malla[] getRegiones() {
		return regiones;
	}
	
	public Malla getRegión(int i) {
		if (i < 0 || i >= regiones.length) return null;
		return regiones[i];
	}
	
	public Malla getMalla(double altura) {
		return getRegión((int) Math.floor(altura/alto_región));
	}

	public int[] getPos() {
		return new int[] {pos_x, pos_z};
	}

	public static int[] getPos(double[] pos) {
		if (pos.length == 2) return getPos(pos[0], pos[1]);
		return getPos(pos[0], pos[2]);
	}

	public static int[] getPos(int[] pos) {
		if (pos.length == 2) return getPos(pos[0], pos[1]);
		return getPos(pos[0], pos[2]);
	}
	
	public static int[] getPos(double pos_x, double pos_z) {
		return new int[] {(int) Math.floor(pos_x/dimens[0]) * dimens[0], (int) Math.floor(pos_z/dimens[2]) * dimens[2]};
	}

	public int getX() {
		return pos_x;
	}
	
	public int getZ() {
		return pos_z;
	}
	
	public void setPos(int x, int z) {
		pos_x = (int) Math.floor((double) x / dimens[0]) * dimens[0];
		pos_z = (int) Math.floor((double) z / dimens[2]) * dimens[2];
	}
	
	public int getAltoRegión() {
		return alto_región;
	}

	public void setAltoRegión(int alto) {
		if (alto < 1) throw new IllegalArgumentException("Alto inválido (" + alto + ")");
		alto_región = alto;
	}
	
	public boolean estáCargado() {
		return cargado;
	}
	
	public boolean estáVacío() {
		return !cargado || objs.isEmpty();
	}

	public LinkedHashSet<Objeto> getObjs() {
		return objs;
	}
	
	public static String getChunkKey(double[] pos) {
		return getChunkKey(getPos(pos), true);
	}
	
	public static String getChunkKey(int[] pos, boolean swChunkPos) {
		if (swChunkPos) {
			if (pos.length != 2) throw new IllegalArgumentException("Vector de dimensiones inválido");
			return pos[0] + "_" + pos[1];
		}
		return getChunkKey(getPos(pos), true);
	}
	
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		objs = new LinkedHashSet<>(100);
		alto_región = ois.readInt();
		pos_x = ois.readInt();
		pos_z = ois.readInt();

		if (cargado = ois.readBoolean()) {
			regiones = (Malla[]) ois.readObject();

			// Lectura de IDs de los objetos guardados
			int núm_objs = ois.readInt();
			for (int i = 0; i < núm_objs; i++) {
				objs.add(Objetos.objs().get(ois.readObject()));
			}
		}
		else initMallas();
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeInt(alto_región);
		oos.writeInt(pos_x);
		oos.writeInt(pos_z);
		oos.writeBoolean(cargado);
		
		if (cargado) {
			oos.writeObject(regiones);
			
			oos.writeInt(objs.size());
			for (Objeto obj : objs) {
				oos.writeObject(obj.toString());
			}
		}
	}
	
	@Override
	public String toString() {
		return pos_x + "_" + pos_z;
	}
}