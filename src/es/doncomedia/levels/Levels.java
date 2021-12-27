package es.doncomedia.levels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;

import es.doncomedia.chunks.Chunks;
import es.doncomedia.effects.Lighting;
import es.doncomedia.graphics.MultithreadedScreen;
import es.doncomedia.graphics.Screen;
import es.doncomedia.misc.Counter;
import es.doncomedia.misc.Key;
import es.doncomedia.misc.Tasks;
import es.doncomedia.objects.*;
import es.doncomedia.objects.Prism.Bases;
import es.doncomedia.objects.abstracts.BaseObject;
import es.doncomedia.objects.abstracts.CompoundObject;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.objects.abstracts.GameObject.Ref;
import es.doncomedia.objects.properties.Border;
import es.doncomedia.objects.properties.Texture;
import es.doncomedia.operations.MyMath;

@SuppressWarnings("serial")
public class Levels {
	public abstract static class Level implements Serializable {
		private static final long serialVersionUID = 1L;
		private LinkedHashMap<String, GameObject> objects;
		private double[] pos, rotation;
		private String name;
		
		protected Level(double[] pos, double[] rotation, String name) {
			this.pos = pos.clone();
			this.rotation = rotation.clone();
			this.name = name;
		}
		
		protected Level(double[] pos, double[] rotation) {
			this.pos = pos.clone();
			this.rotation = rotation.clone();
		}

		public LinkedHashMap<String, GameObject> objs() {
			return objects;
		}
		
		public void setObjs(LinkedHashMap<String, GameObject> objects) {
			this.objects = objects;
		}
		
		public void delete() {
			objects = null;
		}
		
		public double[] getPos() {
			return pos.clone();
		}
		
		public double[] getRotation() {
			return rotation.clone();
		}
		
		public String getName() {
			return name;
		}
		
		public void add(GameObject obj) {
			objects.put(obj.toString(), obj);
		}
		
		public abstract void load();
	}
	
	private static final HashSet<Listener> listeners = new HashSet<>();
	private static final ObjectContainer oc = Db4oEmbedded.openFile("levels.levels");
	private static Level loadedLevel;
	private static Key countersAccess;
	
	static {
		try {
			countersAccess = Counter.getAccess();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void main(String[] args) { //DEBUG
		long t = System.nanoTime();
		save(LEVEL_1);
		System.out.printf("Tiempo: %d ns\n", System.nanoTime()-t);
	}
	
	public static void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public static void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	public static void removeListeners() {
		listeners.clear();
	}
	
	public static Level loaded() {
		return loadedLevel;
	}
	
	/**
	 * Unloads the current level and loads the specified level
	 */
	public static synchronized void load(Level level) {
		if (loadedLevel != null) loadedLevel.delete();
		loadedLevel = level;
		
		synchronized (Tasks.chunks) {
			Tasks.chunks.getES().shutdownNow();
			Tasks.chunks.init();
			Chunks.getChunks().clear();
		}
		
		Counter.reset(countersAccess);
		Lighting.getLights().clear();
		loadedLevel.load();
		for (Listener listener : listeners) {
			listener.react();
		}
	}
	
	/**
	 * Saves a level, first unloads the current one, leaving the one being saved loaded
	 */
	public static synchronized void save(Level level) {
		Query q = oc.query();
		q.descend("name").constrain(level.getName());
		if (q.execute().isEmpty()) {
			load(level);
			oc.store(level);
			oc.commit();
			System.out.println("Nivel \"" + level.getName() + "\" guardado");
		}
		else System.err.println("El nivel \"" + level.getName() + "\" ya estaba guardado");
	}
	
	public static void delete(Level level) {
		delete(level.getName());
	}
	
	public static synchronized void delete(String name) {
		oc.delete(incompleteArchivedLevel(name));
		oc.commit();
		System.out.println("Nivel \"" + name + "\" borrado de la bbdd");
	}
	
	public static void overwrite(Level level) {
		delete(level);
		save(level);
	}
	
	public static Level archivedLevel(String name) {
		Level incomplete = incompleteArchivedLevel(name);
		return new Level(incomplete.getPos(), incomplete.getRotation(), incomplete.getName()) {
			@Override
			public void load() { // Discards loading code from archived level
				setObjs(incomplete.objs());
				
				// Assing correct values to object counters
				HashMap<Class<?>, Counter> classes = new HashMap<>();
				countObjects(incomplete.objs().values(), new HashSet<>(incomplete.objs().size()), classes);
				for (Entry<Class<?>, Counter> entry : classes.entrySet()) {
					Counter counter = Counter.getCounter(entry.getKey(), countersAccess);
					if (counter != null) counter.setCount(entry.getValue().getCount());
					//System.out.println("Contador de " + entry.getKey().getSimpleName() + ": " + entry.getValue().getCount());
				}
			}
				
			private void countObjects(Collection<GameObject> objsToCount, HashSet<GameObject> objsCounted, HashMap<Class<?>, Counter> classes) {
				for (GameObject object : objsToCount) {
					Counter counter = classes.putIfAbsent(object.getClass(), new Counter(1));
					if (objsCounted.add(object) && counter != null) counter.change(1);
					if (object instanceof CompoundObject) countObjects(((CompoundObject) object).objs(), objsCounted, classes);
				}
			}
		};
	}
	
	private static Level incompleteArchivedLevel(String name) { // Doesn't include the correct loading code if it can't be found in the source code
		Query q = oc.query();
		q.descend("nombre").constrain(name);
		ObjectSet<Level> levels = q.execute();
		
		if (levels.size() > 1) throw new IllegalArgumentException("There's more than one level with that name");
		if (levels.isEmpty()) throw new IllegalArgumentException("There's no levels with that name");
		return levels.get(0);
	}
	
	public static final Level LEVEL_1 = new Level(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "default") {
		@Override
		public void load() {
			setObjs(new LinkedHashMap<>(25));
			
			// Object declarations
			Cylinder cylinder1 = new Cylinder(new double[] {45,20,0}, new double[] {Math.sqrt(2)/2, Math.sqrt(2)/2, 0}, 7, 20, GameObject.Ref.BASE2, "green");
			add(cylinder1);
			Sphere sphere1 = new Sphere(new double[] {-15,15,0}, 5, "white");
			add(sphere1);
			Sphere sphere2 = new Sphere(new double[] {15,15,0}, 7, "#FFD400");
			add(sphere2);
			Sphere sphere3 = new Sphere(new double[] {-30,25,15}, 10, "blue");
			add(sphere3);
			Cube cube = new Cube(new double[] {20,20,20}, 10, "red");
			Texture grass = new Texture(
				new InvisiblePrism(new double[] {-5,0,0}, 5, Ref.CENTER, new Object[] {11.0,11.0}, Bases.RECTANGULAR),
				"textures/grass.png",
				true
			);
			grass.setScale(10.0/16, 10.0/16);
			Texture.add(cube, grass);
			
			add(cube);
			CompObjTest compObj1 = new CompObjTest(new double[] {10,50,10}, "blue");
			add(compObj1);
			Prism prism1 = new Prism(new double[] {35,20,20}, 15, "purple", Ref.CENTER, new Object[] {8.0 /*vert_axis*/, 5.0 /*hori_axis*/}, Bases.RECTANGULAR);
			add(prism1);
			Light light1 = new Light(new double[] {20,40,0}, "#33ee33", 6);
			add(light1);
			Light light2 = new Light(new double[] {40,40,10}, "pink", 5);
			add(light2);
			light2.setTurnedOn(false);
			Light light3 = new Light(new double[] {40,40,-20}, "white", 10);
			add(light3);
			
			sphere2.changeProperty(BaseObject.BORDER, new Border("blue", 2, 'R'));
			prism1.setRotationAndOrient(new double[] {0,0,Math.PI/4});
		}
	}, LEVEL_2 = new Level(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_2") {
		@Override
		public void load() {
			setObjs(new LinkedHashMap<>(25));
			
			// Object declarations
			Cylinder cylinder1 = new Cylinder(new double[] {45,20,0}, new double[] {Math.sqrt(2)/2, Math.sqrt(2)/2, 0}, 7, 20, GameObject.Ref.BASE2, "green");
			add(cylinder1);
			DoubleCone cone = new DoubleCone(new double[] {0,15,20}, "red");
			add(cone);
			Ellipsoid elip = new Ellipsoid(new double[] {0,40,20}, new double[] {0,0,-1}, new double[] {20,30,9}, "yellow");
			add(elip);
			Sphere sphere1 = new Sphere(new double[] {-15,15,0}, 5, "white");
			add(sphere1);
			Sphere sphere2 = new Sphere(new double[] {15,15,0}, 7, "#FFD400");
			add(sphere2);
			Sphere sphere3 = new Sphere(new double[] {-30,25,15}, 10, "blue");
			add(sphere3);
			Cube cube = new Cube(new double[] {20,20,20}, 10, "red");
			Texture grass = new Texture(
				new InvisiblePrism(new double[] {-5,0,0}, 5, Ref.CENTER, new Object[] {11.0,11.0}, Bases.RECTANGULAR),
				"textures/grass.png",
				true
			);
			grass.setScale(10.0/16, 10.0/16);
			Texture.add(cube, grass);
			
			add(cube);
			CompObjTest compObj1 = new CompObjTest(new double[] {10,50,10}, "blue");
			add(compObj1);
			Prism prism1 = new Prism(new double[] {35,20,20}, 15, "purple", Ref.CENTER, new Object[] {8.0 /*vert_axis*/, 5.0 /*hori_axis*/}, Bases.RECTANGULAR);
			add(prism1);
			Light light1 = new Light(new double[] {20,40,0}, "#33ee33", 6);
			add(light1);
			Light light2 = new Light(new double[] {40,40,10}, "pink", 5);
			add(light2);
			light2.setTurnedOn(false);
			Light light3 = new Light(new double[] {40,40,-20}, "white", 10);
			add(light3);
			
			sphere2.changeProperty(BaseObject.BORDER, new Border("blue", 2, 'R'));
			prism1.setRotationAndOrient(new double[] {0,0,Math.PI/4});
			
			Camera camera = new Camera(new double[] {3.8, 10, -14.95}, 30, true);
			camera.setRotationAndOrient(new double[] {0.5, 0.707, 0.5}, Math.PI/8);
			camera.setFOVTheta(MyMath.fix(45 * Math.PI / 180));
			Screen screen = new MultithreadedScreen(30, 30, 4, false, camera);
			screen.setLightUp(true);
			
			InvisiblePrism invPrism = new InvisiblePrism(new double[] {0,0,-5}, 5, Ref.CENTER, new Object[] {11.0,11.0}, Bases.RECTANGULAR);
			invPrism.setRotationAndOrient(new double[] {0,0,1}, 0);
			Texture screenTex = new Texture(invPrism, screen);
			screenTex.setScale(10.0/30, 10.0/30);
			Texture.add(cube, screenTex);
			Tasks.timers.getES().schedule(screen::render, 10, TimeUnit.SECONDS); // Delay render, assuming chunks are loaded then
		}
	}, LEVEL_3 = new Level(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_3") {
		@Override
		public void load() {
			setObjs(new LinkedHashMap<>(25));
			
			// Object declarations
			Prism prism1 = new Prism(new double[] {35,20,20}, 15, "purple", Ref.CENTER, new Object[] {8.0 /*vert_axis*/, 5.0 /*hori_axis*/}, Bases.RECTANGULAR);
			add(prism1);
			
			prism1.setRotationAndOrient(new double[] {0,0,Math.PI/4});
		}
	}, LEVEL_4 = new Level(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_4") {
		@Override
		public void load() {
			setObjs(new LinkedHashMap<>(5000));
			
			// Object declarations
			Random rnd = new Random();
			for (int i = 0; i <= 500000; i++) {
				add(new Sphere(new double[] {rnd.nextFloat()*100, rnd.nextFloat()*100, rnd.nextFloat()*100}, rnd.nextFloat()*100, ""));
			}
			ArrayList<GameObject> l = new ArrayList<>(objs().values());
			for (GameObject obj : l) {
				CompoundObject p = new CompoundObject(obj.getPos(), true) {};
				ArrayList<GameObject> a = new ArrayList<>();
				a.add(obj);
				p.setObjs(a);
				add(p);
			}
		}
	}, LEVEL_5 = new Level(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_5") {
		@Override
		public void load() {
			setObjs(new LinkedHashMap<>(5000));
			
			// Object declarations
			Sphere sphere = new Sphere(new double[] {15,15,0}, 7, "#FFD400");
			add(sphere);
			sphere.changeProperty(BaseObject.BORDER, new Border("blue", 2, 'R'));
		}
	}, LEVEL_6 = new Level(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_6") {
		@Override
		public void load() {
			setObjs(new LinkedHashMap<>());
			
			// Object declarations
			add(new Bowl(new double[] {-15,30,0}, "white"));
		}
	}, LEVEL_7 = new Level(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_7") {
		@Override
		public void load() {
			setObjs(new LinkedHashMap<>(5000));
			
			// Object declarations
			Random rnd = new Random();
			for (int i = 0; i <= 50; i++) {
				CompoundObject p = new CompoundObject(new double[] {rnd.nextFloat()*100, rnd.nextFloat()*100, rnd.nextFloat()*100}, true) {};
				ArrayList<GameObject> a = new ArrayList<>();
				for (int j = 0; j < 500; j++) {
					a.add(new Sphere(new double[] {rnd.nextFloat()*100, rnd.nextFloat()*100, rnd.nextFloat()*100}, rnd.nextFloat()*100, ""));
				}
				System.out.println("obj " + i);
				a.add(new NegativeSpace(new Sphere(p.getPos(), 3000, "red")));
				p.setObjs(a);
				add(p);
			}
			add(new NegativeSpace(new Sphere(getPos(), 30, "red")));
		}
	}, LEVEL_8 = new Level(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "nivel_8") {
		@Override
		public void load() {
			setObjs(new LinkedHashMap<>(75000));
			
			// Object declarations
			Random rnd = new Random();
			CompoundObject p = new CompoundObject(new double[] {rnd.nextFloat()*100, rnd.nextFloat()*100, rnd.nextFloat()*100}, true) {};
			ArrayList<GameObject> a = new ArrayList<>();
			a.add(new Sphere(new double[] {rnd.nextFloat()*100, rnd.nextFloat()*100, rnd.nextFloat()*100}, rnd.nextFloat()*100, "red"));
			p.setObjs(a);
			add(p);
			for (int i = 0; i <= 5000; i++) {
				add(new Sphere(new double[] {rnd.nextFloat()*100, rnd.nextFloat()*100, rnd.nextFloat()*100}, rnd.nextFloat()*100, "blue"));
			}
			add(new NegativeSpace(new Sphere(p.getPos(), 3000, "red")));
		}
	}, STARRY_NIGHT = new Level(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "noche_estrellada") {
		@Override
		public void load() {
			setObjs(new LinkedHashMap<>());
			
			// Object declarations
			add(new Moon(new double[] {-15,30,0}, "white"));
		}
	}, SPAIN_CUBE = new Level(new double[] {10,15,-10}, new double[] {Math.PI/2,0,0}, "ESPAÑA") {
		@Override
		public void load() {
			setObjs(new LinkedHashMap<>());
			Cube cube = new Cube(new double[] {20,20,20}, 10, "red");
			Texture cubeTex = new Texture(
				new InvisiblePrism(new double[3], 30, Ref.CENTER, new Object[] {20.0,5.0}, Bases.RECTANGULAR),
				"yellow",
				false
			);
			Texture.add(cube, cubeTex);
			
			add(cube);
		}
	};
	
	private Levels() {
		throw new IllegalStateException("Can't instantiate class");
	}
}