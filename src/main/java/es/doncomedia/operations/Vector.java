package es.doncomedia.operations;

public class Vector {
	private static final double[] ROTATION_STD = {0, 0, 0} /*theta_hori, theta_vert, theta_inclin*/, ORIENT_STD = orient(ROTATION_STD);
	
	private Vector() {
		throw new IllegalStateException("Can't instantiate class");
	}
	
	public static double[] rotationSTD() {
		return ROTATION_STD.clone();
	}
	
	public static double[] orientSTD() {
		return ORIENT_STD.clone();
	}
	
	public static double[] orient(double[] rotation) {
		return MyMath.fix(new double[] {
			Math.cos(rotation[0]) * Math.cos(rotation[1]),
			Math.sin(rotation[1]),
			Math.sin(rotation[0]) * Math.cos(rotation[1])
		});
	}

	public static double[] rotation(double[] orient) {
		if (MyMath.areEqual(Math.abs(orient[1]), 1)) return new double[] {0, Math.PI/2.0 * Math.signum(orient[1]), 0};
		double rotHori = Math.acos(orient[0] / MyMath.pythagoras(new double[] {orient[0], orient[2]})), rotVert = Math.asin(orient[1]);
		if (orient[2] < 0) rotHori *= -1;
		
		return MyMath.fix(new double[] {rotHori - ROTATION_STD[0], rotVert - ROTATION_STD[1], -ROTATION_STD[2]});
	}
	
	/**
	 * @return the x,y coordinates of the point projected onto a plane defined by its position, its normal vector, and its rotation, being (0,0) its center
	 */
	public static double[] project2D(double[] pointToProject, double[] centralPoint, double[] normalVec, double[] rotation) {
		if (rotation[2] == 0) return project2DNoInclin(pointToProject, centralPoint, normalVec, rotation);
		return new double[] {
			Dist.pointToPlane(pointToProject, centralPoint,
			/*Inclined horizontal axis*/ unitaryCellVector(0, normalVec[1], rotation)),
			Dist.pointToPlane(pointToProject, centralPoint,
			/*Inclined vertical axis*/ unitaryCellVector(Math.PI/2.0, normalVec[1], rotation))
		};
	}
	
	/**
	 * @see Vector#project2D(double[], double[], double[], double[])
	 */
	public static double[] project2DNoInclin(double[] pointToProject, double[] centralPoint, double[] normalVec, double[] rotation) { // Assumes incline = 0
		return new double[] {
			Dist.pointToPlane(pointToProject, centralPoint, 
			/*Horizontal axis*/ new double[] {Math.sin(rotation[0]), 0, - Math.cos(rotation[0])}),
			Dist.pointToPlane(pointToProject, centralPoint,
			/*Vertical axis*/ new double[] {- normalVec[1] * Math.cos(rotation[0]), Math.cos(rotation[1]), - normalVec[1] * Math.sin(rotation[0])})
		};
	}
	
	/**
	 * Rotates the point around the central one based on its orientation and rotation, so that relative to it it's like it were with standard orientation and rotation
	 * @return the point rotated without modifying the original
	 */
	public static double[] rotate(double[] pointToRotate, double[] centralPoint, double[] orient, double[] rotation) {
		if (MyMath.areEqual(orient, ORIENT_STD) && MyMath.areEqual(rotation, ROTATION_STD)) return pointToRotate;
		
		// Project the point to rotate onto the plane defined by its orientation and the central point
		double[] coord = project2DNoInclin(pointToRotate, centralPoint, orient, rotation);
		
		// Rotate the projected point towards the new orientation and incline
		double[] rotatedPoint = MyMath.fix(centerToCell(coord[1], coord[0], centralPoint, ORIENT_STD[1], new double[] {ROTATION_STD[0], ROTATION_STD[1], -rotation[2]}));
		
		// Project the point outwards to its final position
		return MyMath.fix(MyMath.sum(rotatedPoint, MyMath.multipl(ORIENT_STD, Dist.pointToPlane(pointToRotate, centralPoint, orient))));
	}
	
	public static double[] centerToCell(double row, double column, double[] posCenter, double newVertOrient, double[] newRotation) {
		double[] celda = posCenter.clone();
		if (row != 0 || column != 0) {
			double cellVecMag = MyMath.pythagoras(new double[] {row, column}), phi = Math.acos(column / cellVecMag);
			if (row < 0) phi *= -1;
			double cos0 = Math.cos(newRotation[0]), sin0 = Math.sin(newRotation[0]), cosPhi2 = Math.cos(phi + newRotation[2]), sinPhi2 = Math.sin(phi + newRotation[2]);
			celda[0] += (sin0 * cosPhi2 - newVertOrient * cos0 * sinPhi2) * cellVecMag;
			celda[1] += Math.cos(newRotation[1]) * sinPhi2 * cellVecMag;
			celda[2] -= (cos0 * cosPhi2 + newVertOrient * sin0 * sinPhi2) * cellVecMag;
		}
		return celda;
	}
	
	public static double[] unitaryCellVector(double phi, double newVertOrient, double[] newRotation) {
		double cos0 = Math.cos(newRotation[0]), sin0 = Math.sin(newRotation[0]), cosPhi2 = Math.cos(phi + newRotation[2]), sinPhi2 = Math.sin(phi + newRotation[2]);
		return new double[] {
			sin0 * cosPhi2 - newVertOrient * cos0 * sinPhi2,
			Math.cos(newRotation[1]) * sinPhi2,
			- cos0 * cosPhi2 - newVertOrient * sin0 * sinPhi2
		};
	}
}