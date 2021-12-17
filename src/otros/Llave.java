package otros;

import java.util.HashSet;

public final class Llave {
	private static final HashSet<Llave> llaves = new HashSet<>();
	private boolean poseída, borrable;
	
	public Llave(boolean borrable) {
		llaves.add(this);
		this.borrable = borrable;
	}
	
	public synchronized Llave poseer() throws IllegalAccessException {
		if (poseída || !llaves.contains(this)) throw new IllegalAccessException("La llave ya tiene poseedor o ya no existe");
		poseída = true;
		return this;
	}
	
	public synchronized Llave liberar() {
		if (poseída && llaves.contains(this)) {
			poseída = false;
			return this;
		}
		throw new NullPointerException("La llave no tenía poseedor o ya no existe");
	}
	
	public synchronized void borrar() {
		if (borrable) llaves.remove(this);
		else throw new IllegalArgumentException("Llave no borrable");
	}
	
	public synchronized boolean isPoseída() {
		return poseída;
	}
	
	public synchronized boolean isBorrable() {
		return borrable;
	}
}