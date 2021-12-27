package es.doncomedia.misc;

import java.util.HashSet;

public final class Key {
	private static final HashSet<Key> keys = new HashSet<>();
	private boolean possessed, deletable;
	
	public Key(boolean deletable) {
		keys.add(this);
		this.deletable = deletable;
	}
	
	public synchronized Key possess() throws IllegalAccessException {
		if (possessed || !keys.contains(this)) throw new IllegalAccessException("The key's already possessed or doesn't exist anymore");
		possessed = true;
		return this;
	}
	
	public synchronized Key liberar() {
		if (possessed && keys.contains(this)) {
			possessed = false;
			return this;
		}
		throw new NullPointerException("The key wasn't possessed or didn't exist anymore");
	}
	
	public synchronized void borrar() {
		if (deletable) keys.remove(this);
		else throw new IllegalArgumentException("Undeletable key");
	}
	
	public synchronized boolean isPossessed() {
		return possessed;
	}
	
	public synchronized boolean isDeletable() {
		return deletable;
	}
}