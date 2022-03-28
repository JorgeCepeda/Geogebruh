package es.doncomedia.operations;

public class Dist {
	
	private Dist() {
		throw new IllegalStateException("Can't instantiate class");
	}
	
	public static double pointToLine(int[] pointPos, int[] linePos, int[] lineOrient, double scalar) {
		double radicand = 0;
		for (int i = 0; i < 3; i++) {
			radicand += Math.pow(linePos[i] + lineOrient[i] * scalar - pointPos[i],  2);
		}
		return MyMath.fix(Math.sqrt(radicand));
	}
	
	public static double pointToLine(int[] pointPos, int[] linePos, double[] lineOrient, double scalar) {
		double radicand = 0;
		for (int i = 0; i < 3; i++) {
			radicand += Math.pow(linePos[i] + lineOrient[i] * scalar - pointPos[i],  2);
		}
		return MyMath.fix(Math.sqrt(radicand));
	}
	
	public static double pointToLine(int[] pointPos, double[] linePos, double[] lineOrient, double scalar) {
		double radicand = 0;
		for (int i = 0; i < 3; i++) {
			radicand += Math.pow(linePos[i] + lineOrient[i] * scalar - pointPos[i],  2);
		}
		return MyMath.fix(Math.sqrt(radicand));
	}
	
	public static double pointToLine(double[] pointPos, double[] linePos, double[] lineOrient, double scalar) {
		double radicand = 0;
		for (int i = 0; i < 3; i++) {
			radicand += Math.pow(linePos[i] + lineOrient[i] * scalar - pointPos[i],  2);
		}
		return MyMath.fix(Math.sqrt(radicand));
	}
	
	public static double pointToLine(int[] pointPos, int[] linePos, int[] lineOrient) {
		return pointToLine(pointPos, linePos, lineOrient, pointToPlane(pointPos, linePos, lineOrient));
	}
	
	public static double pointToLine(int[] pointPos, int[] linePos, double[] lineOrient) {
		return pointToLine(pointPos, linePos, lineOrient, pointToPlane(pointPos, linePos, lineOrient));
	}
	
	public static double pointToLine(int[] pointPos, double[] linePos, double[] lineOrient) {
		return pointToLine(pointPos, linePos, lineOrient, pointToPlane(pointPos, linePos, lineOrient));
	}
	
	public static double pointToLine(double[] pointPos, double[] linePos, double[] lineOrient) {
		return pointToLine(pointPos, linePos, lineOrient, pointToPlane(pointPos, linePos, lineOrient));
	}
	
	public static double pointToPlane(int[] pointPos, int[] planePos, int[] planeNormal) {
		double scalar = 0;
		for (int i = 0; i < 3; i++) {
			scalar += planeNormal[i] * (pointPos[i] - planePos[i]);
		}
		return MyMath.fix(scalar);
	}
	
	public static double pointToPlane(int[] pointPos, int[] planePos, double[] planeNormal) {
		double scalar = 0;
		for (int i = 0; i < 3; i++) {
			scalar += planeNormal[i] * (pointPos[i] - planePos[i]);
		}
		return MyMath.fix(scalar);
	}
	
	public static double pointToPlane(int[] pointPos, double[] planePos, double[] planeNormal) {
		double scalar = 0;
		for (int i = 0; i < 3; i++) {
			scalar += planeNormal[i] * (pointPos[i] - planePos[i]);
		}
		return MyMath.fix(scalar);
	}
	
	public static double pointToPlane(double[] pointPos, double[] planePos, double[] planeNormal) {
		double scalar = 0;
		for (int i = 0; i < 3; i++) {
			scalar += planeNormal[i] * (pointPos[i] - planePos[i]);
		}
		return MyMath.fix(scalar);
	}
	
	public static double lineToLine(double[] linePos1, double[] lineOrient1, double[] linePos2, double[] lineOrient2) {
		if (MyMath.sameDirec(lineOrient1, lineOrient2)) return pointToLine(linePos1, linePos2, lineOrient2);
		return lineToLineNotParallel(linePos1, lineOrient1, linePos2, lineOrient2);
	}
	
	public static double lineToLineNotParallel(double[] linePos1, double[] lineOrient1, double[] linePos2, double[] lineOrient2) {
		double[] vecProduct = MyMath.vectorProduct(lineOrient1, lineOrient2), vec1 = MyMath.vector(linePos1, linePos2);
		double mixedProduct = 0;
		for (int i = 0; i < vec1.length; i++) {
			mixedProduct += vec1[i] * vecProduct[i];
		}
		mixedProduct = Math.abs(mixedProduct);
		
		return MyMath.fix(mixedProduct / MyMath.pythagoras(vecProduct));
	}
	
	public static double pointToPoint(int[] point1, int[] point2) {
		return MyMath.fix(Math.sqrt(pointToPointRootless(point1, point2)));
	}
	
	public static double pointToPoint(int[] point1, double[] point2) {
		return MyMath.fix(Math.sqrt(pointToPointRootless(point1, point2)));
	}
	
	public static double pointToPoint(double[] point1, double[] point2) {
		return MyMath.fix(Math.sqrt(pointToPointRootless(point1, point2)));
	}
	
	public static int pointToPointRootless(int[] point1, int[] point2) {
		int radicand = 0;
		for (int i = 0; i < point1.length; i++) {
			radicand += Math.pow(point1[i] - point2[i], 2);
		}
		return radicand;
	}
	
	public static double pointToPointRootless(int[] point1, double[] point2) {
		double radicand = 0;
		for (int i = 0; i < point1.length; i++) {
			radicand += Math.pow(point1[i] - point2[i], 2);
		}
		return radicand;
	}
	
	public static double pointToPointRootless(double[] point1, double[] point2) {
		double radicand = 0;
		for (int i = 0; i < point1.length; i++) {
			radicand += Math.pow(point1[i] - point2[i], 2);
		}
		return radicand;
	}
}