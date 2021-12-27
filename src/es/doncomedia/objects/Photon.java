package es.doncomedia.objects;

import java.util.Collection;
import java.util.function.BooleanSupplier;

import es.doncomedia.chunks.*;
import es.doncomedia.objects.abstracts.CompoundObject;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.objects.abstracts.Projection;
import es.doncomedia.operations.Dist;
import es.doncomedia.operations.MyMath;

public class Photon extends Projection {
	private static final long serialVersionUID = 1L;
	private Collection<GameObject> objects;
	private GameObject collObj, collObjContainer;
	private Photon subphoton;
	private boolean isSub;
	
	public Photon(double[] pos) {
		super(pos);
	}
	
	public Photon() {}
	
	@Override
	public void setPos(double[] pos, boolean link) {
		super.setPos(pos, link);
		if (subphoton != null) subphoton.setPos(pos, link);
	}
	
	public synchronized Collection<GameObject> objs() {
		return objects;
	}
	
	public synchronized void setObjs(Collection<GameObject> objects) {
		this.objects = objects;
	}
	
	public int objsSize() {
		return objects.size();
	}

	public GameObject collObj() {
		return collObj;
	}
	
	protected void setCollObj(GameObject collObj) {
		this.collObj = collObj;
	}
	
	public GameObject collObjContainer() {
		return collObjContainer;
	}
	
	protected void setCollObjContainer(GameObject collObjContainer) {
		this.collObjContainer = collObjContainer;
	}
	
//	@Override
//	public boolean collision() {
//		if (objects == null) return false;
//		
//		boolean swCollision = false;
//		GameObject possibleCollObj = null, possibleCollObjContainer = null;
//		for (GameObject object : objects) {
//			if (object instanceof NegativeSpace) {
//				if (object.collision(this)) return false; // If it's in a negative space the collision with other objects isn't considered valid
//			}
//			else if (!swCollision) {
//				// If it collides with a compund object it makes sure there's a subphoton, uses it, and collects its collision data
//				// Otherwise it saves the necessary collision data
//				if (object instanceof CompoundObject) {
//					if (!isSub) {
//						createSubphoton(getPos());
//						if (object.collision(subphoton)) {
//							possibleCollObj = subphoton.collObj();
//							possibleCollObjContainer = object;
//							swCollision = true;
//						}
//					}
//					else if (object.collision(this)) {
//						possibleCollObj = collObj;
//						swCollision = true;
//					}
//				}
//				else if (object.collision(this)) {
//					possibleCollObj = object;
//					if (!isSub) possibleCollObjContainer = object;
//					swCollision = true;
//				}
//			}
//		}
//		if (swCollision) {
//			collObj = possibleCollObj;
//			collObjContainer = possibleCollObjContainer;
//			return true;
//		}
//		return false;
//	}
	
	@Override
	public boolean collision() {
		if (objects == null) return false;
		
		boolean swCollision = false;
		GameObject possibleCollObj = null, possibleCollObjContainer = null;
		for (GameObject object : objects) {
			if (object instanceof NegativeSpace) {
				if (object.collision(this)) return false; // If it's in a negative space the collision with other objects isn't considered valid
			}
			else if (!swCollision) {
				Object[] result = checkCollision(object);
				if ((boolean) result[0]) {
					swCollision = true;
					possibleCollObj = (GameObject) result[1];
					possibleCollObjContainer = object;
				}
			}
		}
		if (swCollision) {
			collObj = possibleCollObj;
			collObjContainer = possibleCollObjContainer;
			return true;
		}
		return false;
	}
	
	/**
	 * If it collides with a compund object it makes sure there's a subphoton, uses it, and collects its collision data.
	 * Otherwise it saves the necessary collision data
	 * @return whether it collides or not in the first index, and in that case the collision's concrete object in the second one
	 */
	protected Object[] checkCollision(GameObject object) {
		if (object instanceof CompoundObject) {
			if (isSub) {
				if (object.collision(this)) return new Object[] {true, collObj};
			}
			else {
				createSubphoton(getPos());
				if (object.collision(subphoton)) return new Object[] {true, subphoton.collObj()};
			}
		}
		else if (object.collision(this)) return new Object[] {true, object};
		
		return new Object[] {false};
	}
	
	public boolean inNegativeSpace() {
		return checkNegativeSpace(objects);
	}
	
	private boolean checkNegativeSpace(Collection<GameObject> objects) {
		for (GameObject object : objects) {
			if (object instanceof NegativeSpace && object.collision(this) ||
				object instanceof CompoundObject && checkNegativeSpace(((CompoundObject) object).objs())) return true;
		}
		return false;
	}
	
	protected void createSubphoton(double[] pos) {
		if (subphoton == null && !isSub) {
			subphoton = new Photon(pos);
			subphoton.setSub();
		}
	}
	
	public boolean isSub() {
		return isSub;
	}
	
	protected void setSub() {
		isSub = true;
	}

	public Photon subphoton() {
		return subphoton;
	}
	
	protected void setSubphoton(Photon subphoton) {
		this.subphoton = subphoton;
	}

	/**
	 * Projects a photon as the linked method says, creating a new photon
	 * @see Photon#project(double[], double[], double, boolean, Photon, BooleanSupplier)
	 */
	public static boolean project(double[] initialPos, double[] goalPos, double initialJump, boolean swPrecision, BooleanSupplier halt) {
		return project(initialPos, goalPos, initialJump, swPrecision, new Photon(), halt);
	}
	
	/**
	 * @see Photon#project(double[], double, double, boolean, Photon, BooleanSupplier)
	 * @param goalPos - The end point of the projection
	 */
	public static boolean project(double[] initialPos, double[] goalPos, double initialJump, boolean swPrecision, Photon photon, BooleanSupplier halt) {
		photon.setOrient(MyMath.unitary(MyMath.vector(initialPos, goalPos)));
		return project(initialPos, Dist.pointToPoint(initialPos, goalPos), initialJump, swPrecision, photon, halt);
	}
	
	/**
	 * Projects a photon already created to a max distance or until it has to halt based on a condition
	 * @param initialPos - The projection's starting point
	 * @param maxDist - Projection distance limit in units
	 * @param initialJump - The initial movement in units, skipping any checks
	 * @param swPrecision - Projection mode, with less rounding errors if precision's activated
	 * @param photon - Photon to be projected, with a preassigned orientation
	 * @param halt - Photon's halting logic (i.e.: not colling anymore with a given object, or starting to collide with any object)
	 * @return whether the projection has been halted without surpassing max distance
	 */
	public static boolean project(double[] initialPos, double maxDist, double initialJump, boolean swPrecision, Photon photon, BooleanSupplier halt) { //FIXME slower, at least in java 8
		double[] orient = photon.getOrient(), coord = MyMath.multipl(orient, initialJump);
		double spdMultiplier, dist;
		
		photon.setPos(coord, false);
		while ((dist = MyMath.round(Dist.pointToPoint(photon.getPos(), initialPos), 12)) <= maxDist) { // Photon projection
			double speed = 1;
			Chunk chunk = Chunks.getChunk(photon);
			if (chunk.isEmpty()) {
				// Skip chunk
				if ((speed = Chunks.skipChunk(photon.getPos(), orient, chunk)) == -1) break;
			}
			else {
				photon.setObjs(chunk.getObjs());
				Region region = chunk.getRegion(photon.getPos(1));
				if (region == null) {
					// Skip region
					if ((speed = Chunks.skipRegion(photon.getPos(), orient, chunk)) == -1) break;
				}
				else if (halt.getAsBoolean()) return true;
				
				if ((spdMultiplier = 1 + dist*0.18 /*20 when dist is 100*/) < 20) { // Projection precision
					speed = 0.05*spdMultiplier;
				}
			}
			
			if (swPrecision) coord = MyMath.sum(initialPos, MyMath.multipl(orient, dist + speed));
			else for (int i = 0; i < coord.length; i++) {
				coord[i] += orient[i]*speed;
			}
			photon.setPos(MyMath.fix(coord), false);
		}
		return false;
	}
}