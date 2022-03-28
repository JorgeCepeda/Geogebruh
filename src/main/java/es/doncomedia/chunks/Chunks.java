package es.doncomedia.chunks;

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

import es.doncomedia.levels.Levels;
import es.doncomedia.misc.Tasks;
import es.doncomedia.objects.Photon;
import es.doncomedia.objects.Objects;
import es.doncomedia.operations.Dist;
import es.doncomedia.operations.MyMath;

public class Chunks {
	public static final int PARALLEL = (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 4.0 + 1); /*Made up function*/
	private static final AtomicInteger paused = new AtomicInteger();
	private static final Semaphore loading = new Semaphore(PARALLEL);
	private static ConcurrentHashMap<String,Chunk> chunkMap = new ConcurrentHashMap<>(100000);
	private static int regionHeight = 20;
	
	public static void main(String[] args) { // Performance test
		Objects.init();
		System.out.println(PARALLEL);
		double[] pos = Levels.loaded().getPos();
		
		boolean inSeries = false;
		int sideHalf = 9, totalChunks = (sideHalf*2+1)*(sideHalf*2+1);
		if (inSeries) {
			System.out.println("Cargando chunks en serie...");
			int count = 0;
			long start = System.nanoTime();
			for (int i = -sideHalf; i <= sideHalf; i++) {
				for (int j = -sideHalf; j <= sideHalf; j++) {
					addChunk(new Chunk(regionHeight, Chunk.getPos(pos[0] + Chunk.dimensions(0)*i, pos[2] + Chunk.dimensions(0)*j), true));
					
					//if (++count%10 == 0) System.out.println(count + "/" + totalChunks);
					System.out.println(++count + "/" + totalChunks);
				}
			}
			System.out.printf("Chunks cargados en %d nanosegundos.\n", System.nanoTime() - start);
		}
		else {
			System.out.println("Cargando chunks en paralelo...");
			long start = System.nanoTime();
			load(pos, sideHalf);
			System.out.printf("Chunks cargados en %d nanosegundos.\n", System.nanoTime() - start);
		}
		System.exit(0);
	}
	
	private Chunks() {
		throw new IllegalStateException("Can't instantiate class");
	}
	
	public static void load(double[] pos, int sideHalf) {
		HashSet<Future<?>> loads = new HashSet<>((int) (sideHalf*sideHalf*1.5));
		for (int i = -sideHalf; i <= sideHalf; i++) {
			for (int j = -sideHalf; j <= sideHalf; j++) {
				loads.add((Future<?>) addChunk(new Chunk(regionHeight, Chunk.getPos(pos[0] + Chunk.dimensions(0)*i, pos[2] + Chunk.dimensions(0)*j), false))[1]);
			}
		}
		Tasks.await(loads);
	}
	
	public static void load(String path, boolean add) {
		load(new File(path), add);
	}
	
	@SuppressWarnings("unchecked")
	public static void load(File file, boolean add) {
		Tasks.files.getES().submit(() -> {
			synchronized (Tasks.files) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					System.err.println("Error al crear archivo");
				}
				int size = (int) file.length();
				System.out.println("Cargando...");
				pause();
				
				boolean swErr = false;
				if (size > 0) try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file), size))) {
					long start = System.nanoTime();
					
					ConcurrentHashMap<String, Chunk> chunksRead = (ConcurrentHashMap<String, Chunk>) ois.readObject();
					if (add) {
						for (Entry<String, Chunk> entry : chunksRead.entrySet()) {
							addChunk(entry.getKey(), entry.getValue());
						}
					}
					else {
						synchronized (Tasks.chunks) {
							Tasks.chunks.getES().shutdownNow();
							Tasks.chunks.init();
						}
						for (Chunk c : chunksRead.values()) {
							c.loadInParallel();
						}
						setChunks(chunksRead);
					}
					
					System.out.println("Cargados unos " + chunkMap.size() + " chunks desde el archivo " + file.getPath() + " en " + (System.nanoTime()-start) + " nanosegundos");
				} catch (IOException e) {
					swErr = true;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				else swErr = true;
				
				if (swErr) {
					// Show error and read backup if it isn't reading it already
					System.err.println("Error de lectura de " + file.getPath());
					if (!file.getPath().endsWith(".bck")) load(file.getPath() + ".bck", add);
				}
				resume();
			}
		});
	}
	
	public static void save(String path) {
		save(new File(path));
	}
	
	public static void save(File file) {
		Tasks.files.getES().submit(() -> {
			synchronized (Tasks.files) {
				File copy = new File(file.getPath() + ".bck");
				try {
					file.createNewFile();
					copy.createNewFile();
				} catch (IOException e) {
					System.err.println("Error al crear los archivos: " + e.getMessage());
				}
				int size = (int) file.length(), newSize = 6 * 1024 * 1024;
				System.out.println("Guardando...");
				pause();
				
				// Copy original file if it exists to backup
				if (size > 0) {
					newSize = size;
					try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file), size);
						BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(copy), size)) {
						
						byte[] read = new byte[size];
						if (bis.read(read) != size) throw new IOException("El archivo no se ha leído correctamente");
						bos.write(read);
					} catch (IOException e) {
						System.err.println("No se ha podido hacer una copia de " + file.getPath() + ": " + e.getMessage());
					}
				}
				
				// Save chunks in file
				try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file), newSize))) {
					long start = System.nanoTime();
					oos.writeObject(chunkMap);
					System.out.println("Guardados unos " + chunkMap.size() + " chunks en " + (System.nanoTime()-start) + " nanosegundos");
				} catch (IOException e) {
					System.err.println("No se ha podido guardar en " + file.getPath() + ": " + e.getMessage());
				}
				resume();
			}
		});
	}

	public static Chunk getChunk(Photon photon) {
		return getChunk(photon.getPos());
	}
	
	public static Chunk getChunk(double[] pos) {
		int[] chunkPos = Chunk.getPos(pos);
		String key = Chunk.getChunkKey(chunkPos, true);
		
		// Check if it exists
		Chunk currentChunk = chunkMap.get(key);
		if (currentChunk != null) return currentChunk;
		
		return (Chunk) addChunk(key, new Chunk(regionHeight, chunkPos, false))[0];
	}
	
	private static Object[] addChunk(Chunk c) {
		return addChunk(c.toString(), c);
	}
	
	private static Object[] addChunk(String key, Chunk c) {
		// Check if it isn't already added
		Chunk existingChunk = chunkMap.putIfAbsent(key, c);
		if (existingChunk == null) {
			return new Object[] {c, c.loadInParallel()};
		}
		return new Object[] {existingChunk, null};
	}
	
	public static double skipRegion(double[] pos, double[] orient) {
		return skipRegion(pos, orient, getChunk(pos));
	}
	
	public static double skipRegion(double[] pos, double[] orient, Chunk currentChunk) {
		if (orient[1] == 0) return skipChunk(pos, orient, currentChunk);
		
		int newRegionY = (int) (Math.floor(pos[1]/regionHeight) * regionHeight);
		if (orient[1] > 0) {
			newRegionY += regionHeight;
			if (newRegionY > Chunk.dimensions(1)) return skipChunk(pos, orient, currentChunk);
			if (newRegionY < 0) newRegionY = 0;
		}
		else {
			if (newRegionY < 0) return skipChunk(pos, orient, currentChunk);
			if (newRegionY > Chunk.dimensions(1)) newRegionY = Chunk.dimensions(1);
		}
		
		if (Math.abs(orient[1]) == 1) return MyMath.fix(Math.abs(newRegionY - pos[1])) + 0.01;
		
		// Corner 1: The region's corner nearest to the orientation's trayectory; Corners 2 and 3 adjacent to Corner 1 in the horizontal plane
		int[] corner1 = {currentChunk.getX(), newRegionY, currentChunk.getZ()}, corner2 = corner1.clone(), corner3 = corner1.clone();
		
		if (orient[0] > 0) {
			corner1[0] += Chunk.dimensions(0);
			corner3[0] += Chunk.dimensions(0);
		}
		else corner2[0] += Chunk.dimensions(0);
		
		if (orient[2] > 0) {
			corner1[2] += Chunk.dimensions(2);
			corner2[2] += Chunk.dimensions(2);
		}
		else corner3[2] += Chunk.dimensions(2);
		
		if (orient[0] < 0 && corner1[0] == pos[0] || orient[2] < 0 && corner1[2] == pos[2]) return 0.1;
		
		// Choose second corner for the plane based on the orientation
		boolean isCorner2Left = orient[0] > 0 && orient[2] > 0 || orient[0] <= 0 && orient[2] <= 0;
		double dist = Dist.pointToPlane(corner1, pos, new double[] {orient[2], 0, -orient[0]});

		int[] chosenCorner = corner3;
		if (dist > 0 && isCorner2Left || dist <= 0 && !isCorner2Left) chosenCorner = corner2;
		
		double[] coefs = MyMath.planePoints(corner1, chosenCorner, pos),
			planeLineVec = MyMath.vectorProduct(new double[] {coefs[0], coefs[1], coefs[2]}, new double[] {orient[2], 0, -orient[0]});
		
		double planeLineTan = MyMath.fix(planeLineVec[1] / MyMath.pythagoras(new double[] {planeLineVec[0], planeLineVec[2]})),
			trayectoryTan = MyMath.fix(orient[1] / MyMath.pythagoras(new double[] {orient[0], orient[2]}));
		
		if (Math.abs(trayectoryTan) <= Math.abs(planeLineTan)) return skipChunk(pos, orient, currentChunk);
		return MyMath.fix((newRegionY - pos[1]) / orient[1]) + 0.01;
	}
	
	public static double skipChunk(double[] pos, double[] orient) {
		return skipChunk(pos, orient, getChunk(pos));
	}
	
	public static double skipChunk(double[] pos, double[] orient, Chunk currentChunk) {
		if (Math.abs(orient[1]) == 1) return -1;
		
		double newChunkZ = currentChunk.getZ();
		if (orient[2] > 0) newChunkZ += Chunk.dimensions(2);
		else if (orient[2] < 0 && newChunkZ == pos[2]) return 0.1;
		if (orient[0] == 0) return MyMath.fix((newChunkZ - pos[2]) / orient[2]) + 0.01;
		
		double newChunkX = currentChunk.getX();
		if (orient[0] > 0) newChunkX += Chunk.dimensions(0);
		else if (newChunkX == pos[0]) return 0.1;
		if (orient[2] == 0) return MyMath.fix((newChunkX - pos[0]) / orient[0]) + 0.01;
		
		double diagonalTan = MyMath.fix((newChunkZ - pos[2]) / (newChunkX - pos[0]));
		
		if (Math.abs(MyMath.fix(orient[2]/orient[0])) <= Math.abs(diagonalTan)) return MyMath.fix((newChunkX - pos[0]) / orient[0]) + 0.01;
		return MyMath.fix((newChunkZ - pos[2]) / orient[2]) + 0.01;
	}
	
	public static boolean isInChunk(double[] pos, Chunk chunk) {
		return pos[0] >= chunk.getX() && pos[0] < chunk.getX() + Chunk.dimensions(0)
			&& pos[2] >= chunk.getZ() && pos[2] < chunk.getZ() + Chunk.dimensions(2);
	}
	
	public static boolean isInChunk(int[] pos, Chunk chunk) {
		return pos[0] >= chunk.getX() && pos[0] < chunk.getX() + Chunk.dimensions(0)
			&& pos[2] >= chunk.getZ() && pos[2] < chunk.getZ() + Chunk.dimensions(2);
	}
	
	public static Region getRegion(double[] pos) {
		return getChunk(pos).getRegion(pos[1]);
	}
	
	public static synchronized ConcurrentHashMap<String,Chunk> getChunks() {
		return chunkMap;
	}
	
	public static synchronized void setChunks(ConcurrentHashMap<String,Chunk> chunks) {
		chunkMap = chunks;
	}
	
	public static int loadingChunks() {
		return PARALLEL - loading.availablePermits();
	}
	
	public static void incrementLoad() {
		loading.acquireUninterruptibly();
	}
	
	public static void decrementLoad() {
		loading.release();
	}
	
	public static int pausedCount() {
		return paused.get();
	}
	
	public static boolean arePaused() {
		return paused.get() > 0;
	}
	
	public static void pause() {
		paused.incrementAndGet();
	}
	
	public static void resume() {
		paused.decrementAndGet();
	}
}