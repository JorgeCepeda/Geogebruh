package es.doncomedia.objects;

import java.util.Collection;

import es.doncomedia.collections.ExcludingCollection;
import es.doncomedia.objects.abstracts.GameObject;

public class OptimizedPhoton extends Photon {
	private static final long serialVersionUID = 1L;
	private boolean optimized = true;

	public OptimizedPhoton(double[] pos, boolean optimized) {
		super(pos);
		setOptimized(optimized);
	}
	
	public OptimizedPhoton(double[] pos) {
		super(pos);
	}
	
	public OptimizedPhoton() {}
	
	@Override
	public boolean collision() {
		if (optimized) {
			if (objs() == null) return false;
			
			// If it's in a negative space the collision with other objects isn't considered valid
			for (GameObject object : objsCast().getExcludedReadOnly()) {
				if (object.collision(this)) return false;
			}
			
			for (GameObject object : objsCast().getIncludedReadOnly()) {
				Object[] result = checkCollision(object);
				if ((boolean) result[0]) {
					setCollObj((GameObject) result[1]);
					setCollObjContainer(object);
					return true;
				}
			}
			return false;
		}
		return super.collision();
	}
	
	@Override
	public synchronized void setObjs(Collection<GameObject> objects) {
		try {
			if (objects instanceof ExcludingCollection<?>) {
				((ExcludingCollection<GameObject>) objects).setOptimized(optimized);
				super.setObjs(objects);
			}
			else if (objs() == null || objs() instanceof ExcludingCollection<?> && !objsCast().isCollection(objects)) super.setObjs(new ExcludingCollection<>(objects, optimized, GameObject.class, NegativeSpace.class));
		} catch (Exception e) {
			System.err.println("Incompatible collection, reverting to normal mode");
			setOptimized(false);
			super.setObjs(objects);
			e.printStackTrace();
		}
	}
	
	@Override
	protected void createSubphoton(double[] pos) {
		if (subphoton() == null && !isSub()) {
			setSubphoton(new OptimizedPhoton(pos, optimized));
			subphoton().setSub();
		}
	}
	
	public boolean isOptimized() {
		return optimized;
	}
	
	public void setOptimized(boolean optimized) {
		this.optimized = optimized;
		if (objs() != null) objsCast().setOptimized(optimized);
	}
	
	private ExcludingCollection<GameObject> objsCast() {
		return ((ExcludingCollection<GameObject>) objs());
	}
}
