package otros;

import java.util.HashSet;

public final class Llave {
	private static final HashSet<Llave> llaves = new HashSet<>();
	private boolean pose�da, borrable;
	
	public Llave(boolean borrable) {
		llaves.add(this);
		this.borrable = borrable;
	}
	
	public synchronized Llave poseer() throws IllegalAccessException {
		if (pose�da || !llaves.contains(this)) throw new IllegalAccessException("La llave ya tiene poseedor o ya no existe");
		pose�da = true;
		return this;
	}
	
	public synchronized Llave liberar() {
		if (pose�da && llaves.contains(this)) {
			pose�da = false;
			return this;
		}
		throw new NullPointerException("La llave no ten�a poseedor o ya no existe");
	}
	
	public synchronized void borrar() {
		if (borrable) llaves.remove(this);
		else throw new IllegalArgumentException("Llave no borrable");
	}
	
	public synchronized boolean isPose�da() {
		return pose�da;
	}
	
	public synchronized boolean isBorrable() {
		return borrable;
	}
}