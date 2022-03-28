package es.doncomedia.chunks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import es.doncomedia.graphics.Screen;
import es.doncomedia.misc.Tasks;
import es.doncomedia.objects.Objects;
import es.doncomedia.objects.abstracts.GameObject;

public class Chunk implements Serializable {
	public static final ReentrantLock lock = new ReentrantLock();
	public static final boolean LIGHTWEIGHT_LOAD = true; // Allows load to pause periodically
	private static final long serialVersionUID = 1L;
	private static int[] dimens = {30,100,30};
	private LinkedHashSet<GameObject> objs = new LinkedHashSet<>(100);
	private Region[] regions;
	private int regionHeight, posX, posZ;
	private boolean loaded;
	private transient Future<?> loadTask;
	private transient boolean loading, parallelLoading;
	
	public Chunk(int regionHeight, int posX, int posZ, boolean load) {
		setPos(posX, posZ);
		setRegionHeight(regionHeight);
		init(load);
	}
	
	public Chunk(int regionHeight, double[] pos, boolean load) {
		int[] chunkPos = getPos(pos);
		setPos(chunkPos[0], chunkPos[1]);
		setRegionHeight(regionHeight);
		init(load);
	}
	
	public Chunk(int regionHeight, int[] pos, boolean load) {
		int[] chunkPos = getPos(pos);
		setPos(chunkPos[0], chunkPos[1]);
		setRegionHeight(regionHeight);
		init(load);
	}
	
	private void init(boolean load) {
		initRegions();
		if (load) load(); // Load
	}

	private synchronized void parallelLoad() {
		parallelLoading = true;
		load();
	}
	
	public synchronized void load() { // Only one thread at a time can load each chunk
		if (!loaded) {
			waitInQueue();
			Chunks.incrementLoad();
			if (regions == null) {
				objs = new LinkedHashSet<>(100);
				initRegions();
			}
			
			ScheduledFuture<?> future = null;
			AtomicBoolean condition = null;
			if (LIGHTWEIGHT_LOAD) {
				/*if (carga_paralela) future = Tasks.monitor(loadingTask, 1500, condition = new AtomicBoolean(), objs); //FIXME see method
				else*/ future = Tasks.monitor(Thread.currentThread(), 1500, condition = new AtomicBoolean(), objs);
			}
			
			for (Region region : regions) {
				region.fill(objs, Objects.objs().values());
			}
			
			if (future != null) {
				Tasks.endMonitoring(future, condition, objs);
				loadTask = null;
			}
			loaded = true;
			Chunks.decrementLoad();
			parallelLoading = false;
		}
	}
	
	/**
	 * Asynchronous chunk load
	 * @return the task associated to the load
	 */
	public synchronized Future<?> loadInParallel() {
		if (!loaded && !loading) {
			loading = true;
			Future<?> newTask = Tasks.chunks.getES().submit(this::parallelLoad);
			loadTask = newTask;
			return newTask;
		}
		return null;
	}
	
	public synchronized void unload() {
		if (loaded) {
			loaded = false;
			loading = false;
			regions = null;
			objs = null;
		}
	}

	/**
	 * Limits how many chunks can be loaded at the same time
	 */
	public static void waitInQueue() {
		while (Chunks.loadingChunks() >= loadLimit()) {
			lock.lock();
			lock.unlock();
		}
	}
	
	private static int loadLimit() {
		if (Chunks.arePaused()) return 0;
		int limit = Chunks.PARALLEL - Screen.threadsRunning();
		return limit > 0 ? limit : 1;
	}
	
	public static int[] dimensions() {
		return dimens.clone();
	}
	
	public static int dimensions(int i) {
		return dimens[i];
	}

	public static void setDimensions(int[] dimens) {
		if (dimens.length != 3 || dimens[0] < 1 || dimens[1] < 1 || dimens[2] < 1) throw new IllegalArgumentException("Invalid chunk dimensions");
		Chunk.dimens = dimens;
	}
	
	private void initRegions() {
		regions = new Region[(int) Math.ceil((double) dimens[1]/regionHeight)];
		
		for (int i = 0; i < regions.length; i++) {
			regions[i] = new Region(dimens[0], regionHeight, dimens[2]);
			regions[i].setPos(new int[] {posX, regionHeight*i, posZ});
		}
	}
	
	public Region[] getRegions() {
		return regions;
	}
	
	public Region getRegion(int i) {
		if (i < 0 || i >= regions.length) return null;
		return regions[i];
	}
	
	public Region getRegion(double height) {
		return getRegion((int) Math.floor(height/regionHeight));
	}

	public int[] getPos() {
		return new int[] {posX, posZ};
	}

	public static int[] getPos(double[] pos) {
		if (pos.length == 2) return getPos(pos[0], pos[1]);
		return getPos(pos[0], pos[2]);
	}

	public static int[] getPos(int[] pos) {
		if (pos.length == 2) return getPos(pos[0], pos[1]);
		return getPos(pos[0], pos[2]);
	}
	
	public static int[] getPos(double posX, double posZ) {
		return new int[] {(int) Math.floor(posX/dimens[0]) * dimens[0], (int) Math.floor(posZ/dimens[2]) * dimens[2]};
	}

	public int getX() {
		return posX;
	}
	
	public int getZ() {
		return posZ;
	}
	
	public void setPos(int x, int z) {
		posX = (int) Math.floor((double) x / dimens[0]) * dimens[0];
		posZ = (int) Math.floor((double) z / dimens[2]) * dimens[2];
	}
	
	public int getRegionHeight() {
		return regionHeight;
	}

	public void setRegionHeight(int height) {
		if (height < 1) throw new IllegalArgumentException("Invalid height (" + height + ")");
		regionHeight = height;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public boolean isEmpty() {
		return !loaded || objs.isEmpty();
	}

	public LinkedHashSet<GameObject> getObjs() {
		return objs;
	}
	
	public static String getChunkKey(double[] pos) {
		return getChunkKey(getPos(pos), true);
	}
	
	public static String getChunkKey(int[] pos, boolean swChunkPos) {
		if (swChunkPos) {
			if (pos.length != 2) throw new IllegalArgumentException("Invalid vector dimensions");
			return pos[0] + "_" + pos[1];
		}
		return getChunkKey(getPos(pos), true);
	}
	
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		objs = new LinkedHashSet<>(100);
		regionHeight = ois.readInt();
		posX = ois.readInt();
		posZ = ois.readInt();

		if (loaded = ois.readBoolean()) {
			regions = (Region[]) ois.readObject();

			// Read saved objects' IDs
			int objCount = ois.readInt();
			for (int i = 0; i < objCount; i++) {
				objs.add(Objects.objs().get(ois.readObject()));
			}
		}
		else initRegions();
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeInt(regionHeight);
		oos.writeInt(posX);
		oos.writeInt(posZ);
		oos.writeBoolean(loaded);
		
		if (loaded) {
			oos.writeObject(regions);
			
			oos.writeInt(objs.size());
			for (GameObject obj : objs) {
				oos.writeObject(obj.toString());
			}
		}
	}
	
	@Override
	public String toString() {
		return posX + "_" + posZ;
	}
}