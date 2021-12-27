package es.doncomedia.operations;

public class MyMath {
	
	private MyMath() {
		throw new IllegalStateException("Can't instantiate class");
	}
	
	public static double round(double num, int decimals) {
		return Math.round(num * Math.pow(10, decimals)) / Math.pow(10, decimals);
	}
	
	public static double[] fix(double[] vec) {
		double[] vecFix = new double[vec.length];
		for (int i = 0; i < vec.length; i++) {
			vecFix[i] = fix(vec[i]);
		}
		return vecFix;
	}
	
	public static double fix(double num) {
		double rounded = round(num, 8);
		if (areEqual(num, rounded)) return rounded;
		return num;
	}
	
	public static boolean sameDirec(int[] vec1, int[] vec2) {
		double[] unitary1 = unitary(vec1), unitary2 = unitary(vec2);
		return areEqual(unitary1, unitary2) || areEqual(unitary1, opposite(unitary2));
	}
	
	public static boolean sameDirec(int[] vec1, double[] vec2) {
		double[] unitary1 = unitary(vec1), unitary2 = unitary(vec2);
		return areEqual(unitary1, unitary2) || areEqual(unitary1, opposite(unitary2));
	}
	
	public static boolean sameDirec(double[] vec1, double[] vec2) {
		double[] unitary1 = unitary(vec1), unitary2 = unitary(vec2);
		return areEqual(unitary1, unitary2) || areEqual(unitary1, opposite(unitary2));
	}
	
	public static boolean areEqual(int[] vec1, int[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Uncomparable vectors");
		for (int i = 0; i < vec1.length; i++) {
			if (!areEqual(vec1[i], vec2[i])) return false;
		}
		return true;
	}
	
	public static boolean areEqual(int[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Uncomparable vectors");
		for (int i = 0; i < vec1.length; i++) {
			if (!areEqual(vec1[i], vec2[i])) return false;
		}
		return true;
	}
	
	public static boolean areEqual(double[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Uncomparable vectors");
		for (int i = 0; i < vec1.length; i++) {
			if (!areEqual(vec1[i], vec2[i])) return false;
		}
		return true;
	}
	
	public static boolean areEqual(double num1, double num2) {
		return Math.abs(num1 - num2) <= 1e-11;
	}
	
	public static int[] opposite(int[] vec) {
		int[] oppVec = new int[vec.length];
		for (int i = 0; i < vec.length; i++) {
			oppVec[i] = -vec[i];
		}
		return oppVec;
	}
	
	public static double[] opposite(double[] vec) {
		double[] oppVec = new double[vec.length];
		for (int i = 0; i < vec.length; i++) {
			oppVec[i] = -vec[i];
		}
		return oppVec;
	}

	public static double[] unitary(int[] vec) {
		double[] uniVec = new double[vec.length];
		double mag = pythagoras(vec);
		for (int i = 0; i < vec.length; i++) {
			uniVec[i] = vec[i] / mag;
		}
		return fix(uniVec);
	}
	
	public static double[] unitary(double[] vec) {
		double[] uniVec = vec.clone();
		double mag = pythagoras(vec);
		for (int i = 0; i < vec.length; i++) {
			uniVec[i] /= mag;
		}
		return fix(uniVec);
	}
	
	public static int[] sum(int[] vec1, int[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Different dimension vectors");
		int[] sum = vec2.clone();
		for (int i = 0; i < vec2.length; i++) {
			sum[i] += vec1[i];
		}
		return sum;
	}
	
	public static double[] sum(int[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Different dimension vectors");
		double[] sum = vec2.clone();
		for (int i = 0; i < vec2.length; i++) {
			sum[i] += vec1[i];
		}
		return sum;
	}
	
	public static double[] sum(double[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Different dimension vectors");
		double[] sum = vec2.clone();
		for (int i = 0; i < vec2.length; i++) {
			sum[i] += vec1[i];
		}
		return sum;
	}
	
	public static int[] subtract(int[] vec1, int[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Different dimension vectors");
		int[] subtraction = vec1.clone();
		for (int i = 0; i < vec2.length; i++) {
			subtraction[i] -= vec2[i];
		}
		return subtraction;
	}
	
	public static double[] subtract(int[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Different dimension vectors");
		double[] subtraction = toDouble(vec1.clone());
		for (int i = 0; i < vec2.length; i++) {
			subtraction[i] -= vec2[i];
		}
		return subtraction;
	}
	
	public static double[] subtract(double[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Different dimension vectors");
		double[] subtraction = vec1.clone();
		for (int i = 0; i < vec2.length; i++) {
			subtraction[i] -= vec2[i];
		}
		return subtraction;
	}
	
	public static int[] multipl(int[] vec1, int[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Different dimension vectors");
		int[] mult = vec2.clone();
		for (int i = 0; i < vec2.length; i++) {
			mult[i] *= vec1[i];
		}
		return mult;
	}
	
	public static double[] multipl(int[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Different dimension vectors");
		double[] mult = vec2.clone();
		for (int i = 0; i < vec2.length; i++) {
			mult[i] *= vec1[i];
		}
		return fix(mult);
	}
	
	public static double[] multipl(double[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Different dimension vectors");
		double[] mult = vec2.clone();
		for (int i = 0; i < vec2.length; i++) {
			mult[i] *= vec1[i];
		}
		return fix(mult);
	}
	
	public static int[] multipl(int[] vec, double num) {
		int[] mult = vec.clone();
		for (int i = 0; i < vec.length; i++) {
			mult[i] *= num;
		}
		return mult;
	}
	
	public static double[] multipl(double[] vec, double num) {
		double[] mult = vec.clone();
		for (int i = 0; i < vec.length; i++) {
			mult[i] *= num;
		}
		return fix(mult);
	}
	
	public static double pythagoras(int[] point) {
		return fix(Math.sqrt(pythagorasRootless(point)));
	}
	
	public static double pythagoras(double[] point) {
		return fix(Math.sqrt(pythagorasRootless(point)));
	}
	
	public static int pythagorasRootless(int[] point) {
		int radicand = 0;
		for (int d : point) {
			radicand += Math.pow(d, 2);
		}
		return radicand;
	}
	
	public static double pythagorasRootless(double[] point) {
		double radicand = 0;
		for (double d : point) {
			radicand += Math.pow(d, 2);
		}
		return radicand;
	}

	/**
	 * @return a vector with non-negative ints
	 */
	public static int[] toInt(byte[] vec) {
		int[] ints = new int[vec.length];
		for (int i = 0; i < vec.length; i++) {
			ints[i] = vec[i] & 0xff;
		}
		return ints;
	}

	public static double[] toDouble(int[] vec) {
		double[] doubleVec = new double[vec.length];
		System.arraycopy(vec, 0, doubleVec, 0, vec.length);
		return doubleVec;
	}
	
	public static int[] vector(int[] start, int[] end) {
		if (start.length != end.length) throw new IllegalArgumentException("Different dimension points");
		int[] vec = end.clone();
		for (int i = 0; i < end.length; i++) {
			vec[i] -= start[i];
		}
		return vec;
	}
	
	public static double[] vector(double[] start, int[] end) {
		if (start.length != end.length) throw new IllegalArgumentException("Different dimension points");
		double[] vec = new double[end.length];
		for (int i = 0; i < end.length; i++) {
			vec[i] = end[i] - start[i];
		}
		return vec;
	}
	
	public static double[] vector(int[] start, double[] end) {
		if (start.length != end.length) throw new IllegalArgumentException("Different dimension points");
		double[] vec = end.clone();
		for (int i = 0; i < end.length; i++) {
			vec[i] -= start[i];
		}
		return vec;
	}
	
	public static double[] vector(double[] start, double[] end) {
		if (start.length != end.length) throw new IllegalArgumentException("Different dimension points");
		double[] vec = end.clone();
		for (int i = 0; i < end.length; i++) {
			vec[i] -= start[i];
		}
		return vec;
	}
	
	public static int[] vectorProduct(int[] vec1, int[] vec2) {
		if (vec1.length != 3 || vec2.length != 3) throw new IllegalArgumentException("Invalid vectors");
		return new int[] {
    		vec1[1] * vec2[2] - vec1[2] * vec2[1],
    		vec1[2] * vec2[0] - vec1[0] * vec2[2],
    		vec1[0] * vec2[1] - vec1[1] * vec2[0]};
    }
	
	public static double[] vectorProduct(int[] vec1, double[] vec2) {
		if (vec1.length != 3 || vec2.length != 3) throw new IllegalArgumentException("Invalid vectors");
		return new double[] {
    		vec1[1] * vec2[2] - vec1[2] * vec2[1],
    		vec1[2] * vec2[0] - vec1[0] * vec2[2],
    		vec1[0] * vec2[1] - vec1[1] * vec2[0]};
    }
	
	public static double[] vectorProduct(double[] vec1, double[] vec2) {
		if (vec1.length != 3 || vec2.length != 3) throw new IllegalArgumentException("Invalid vectors");
		return new double[] {
    		vec1[1] * vec2[2] - vec1[2] * vec2[1],
    		vec1[2] * vec2[0] - vec1[0] * vec2[2],
    		vec1[0] * vec2[1] - vec1[1] * vec2[0]};
    }
	
	public static int[] planePoints(int[] p1, int[] p2, int[] p3) {
		if (p1.length != 3) throw new IllegalArgumentException("Invalid point");
		int[] vec1 = vector(p1, p2), vec2 = vector(p1, p3);
		return planeVectors(p1, vec1, vec2);
	}
	
	public static double[] planePoints(int[] p1, int[] p2, double[] p3) {
		if (p1.length != 3) throw new IllegalArgumentException("Invalid point");
		int[] vec1 = vector(p1, p2);
		double[] vec2 = vector(p1, p3);
		return planeVectors(p1, vec1, vec2);
	}
	
	public static double[] planePoints(int[] p1, double[] p2, double[] p3) {
		if (p1.length != 3) throw new IllegalArgumentException("Invalid point");
		double[] vec1 = vector(p1, p2), vec2 = vector(p1, p3);
		return planeVectors(p1, vec1, vec2);
	}
	
	public static double[] planePoints(double[] p1, double[] p2, double[] p3) {
		if (p1.length != 3) throw new IllegalArgumentException("Invalid point");
		double[] vec1 = vector(p1, p2), vec2 = vector(p1, p3);
		return planeVectors(p1, vec1, vec2);
	}
	
	public static int[] planeVectors(int[] point, int[] vec1, int[] vec2) {
		if (point.length != 3) throw new IllegalArgumentException("Invalid point");
		int[] normalVec = vectorProduct(vec1, vec2); //A,B,C
		int constant = - (normalVec[0]*point[0] + normalVec[1]*point[1] + normalVec[2]*point[2]); //D
		return new int[] {normalVec[0], normalVec[1], normalVec[2], constant}; // Ax+By+Cz+D=0
	}
	
	public static double[] planeVectors(int[] point, int[] vec1, double[] vec2) {
		if (point.length != 3) throw new IllegalArgumentException("Invalid point");
		double[] normalVec = vectorProduct(vec1, vec2); //A,B,C
		double constant = - (normalVec[0]*point[0] + normalVec[1]*point[1] + normalVec[2]*point[2]); //D
		return fix(new double[] {normalVec[0], normalVec[1], normalVec[2], constant}); // Ax+By+Cz+D=0
	}
	
	public static double[] planeVectors(int[] point, double[] vec1, double[] vec2) {
		if (point.length != 3) throw new IllegalArgumentException("Invalid point");
		double[] normalVec = vectorProduct(vec1, vec2); //A,B,C
		double constant = - (normalVec[0]*point[0] + normalVec[1]*point[1] + normalVec[2]*point[2]); //D
		return fix(new double[] {normalVec[0], normalVec[1], normalVec[2], constant}); // Ax+By+Cz+D=0
	}
	
	public static double[] planeVectors(double[] point, double[] vec1, double[] vec2) {
		if (point.length != 3) throw new IllegalArgumentException("Invalid point");
		double[] normalVec = vectorProduct(vec1, vec2); //A,B,C
		double constant = - (normalVec[0]*point[0] + normalVec[1]*point[1] + normalVec[2]*point[2]); //D
		return fix(new double[] {normalVec[0], normalVec[1], normalVec[2], constant}); // Ax+By+Cz+D=0
	}
	
	public static boolean isInside(int i, int j, Object[][] table, int margin) {
		return margin <= i && i < table.length - margin && margin <= j && j < table[0].length - margin;
	}
	
	public static boolean areEqual(Object... objects) {
		if (objects.length < 2) throw new IllegalArgumentException("Needs more arguments");
		for (int i = 1; i < objects.length; i++) {
			if (objects[i] != objects[i-1]) return false;
		}
		return true;
	}
}