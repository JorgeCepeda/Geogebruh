package es.doncomedia.misc;

import java.util.LinkedHashMap;

import es.doncomedia.objects.abstracts.GameObject;

public class Counter {
	private static final Key countersAccess = new Key(false);
	private static final LinkedHashMap<Class<?>, Counter> counters = new LinkedHashMap<>();
	private int count;
	private boolean listed; // If it's on the counter's list it can count objects and assign an ID
	
	private Counter(boolean list, int count) {
		listed = list;
		setCount(count);
	}
	
	public Counter(int count) {
		setCount(count);
	}
	
	public synchronized int getCount() {
		return count;
	}
	
	public synchronized void setCount(int count) {
		this.count = count;
	}
	
	public synchronized void change(int amount) {
		count += amount;
	}
	
	public boolean isListed() {
		return listed;
	}
	
	public void count(GameObject obj) {
		if (listed) obj.setID(count++);
		else throw new IllegalArgumentException("Counter isn't on the list so it can't count objects");
	}

	/**
	 * Asigns the object's ID with its counter, creating it if necessary
	 */
	public static void register(GameObject obj) {
		Counter newCounter = new Counter(true, 0), counter = counters.putIfAbsent(obj.getClass(), newCounter);
		if (counter == null) newCounter.count(obj);
		else counter.count(obj);
	}
	
	public static void reset(Key key) {
		if (key == countersAccess && key.isPossessed()) {
			for (Counter counter : counters.values()) {
				counter.setCount(0);
			}
		}
		else throw new IllegalArgumentException("That key isn't possessed or is incorrect");
	}
	
	public static Counter getCounter(GameObject obj, Key key) {
		return getCounter(obj.getClass(), key);
	}
	
	public static Counter getCounter(Class<?> objClass, Key key) {
		if (key == countersAccess && key.isPossessed()) return counters.get(objClass);
		throw new IllegalArgumentException("That key isn't possessed or is incorrect");
	}
	
	public static Key getAccess() throws IllegalAccessException {
		return countersAccess.possess();
	}
}