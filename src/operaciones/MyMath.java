package operaciones;

public class MyMath {
	
	private MyMath() {
		throw new IllegalStateException("Clase no instanciable");
	}
	
	public static double round(double núm, int decimales) {
		return Math.round(núm * Math.pow(10, decimales)) / Math.pow(10, decimales);
	}
	
	public static double[] fix(double[] vec) {
		double[] vec_fix = new double[vec.length];
		for (int i = 0; i < vec.length; i++) {
			vec_fix[i] = fix(vec[i]);
		}
		return vec_fix;
	}
	
	public static double fix(double núm) {
		double redondeado = round(núm, 8);
		if (iguales(núm, redondeado)) return redondeado;
		return núm;
	}
	
	public static boolean mismaDirec(int[] vec1, int[] vec2) {
		double[] unitario1 = unitario(vec1), unitario2 = unitario(vec2);
		return iguales(unitario1, unitario2) || iguales(unitario1, opuesto(unitario2));
	}
	
	public static boolean mismaDirec(int[] vec1, double[] vec2) {
		double[] unitario1 = unitario(vec1), unitario2 = unitario(vec2);
		return iguales(unitario1, unitario2) || iguales(unitario1, opuesto(unitario2));
	}
	
	public static boolean mismaDirec(double[] vec1, double[] vec2) {
		double[] unitario1 = unitario(vec1), unitario2 = unitario(vec2);
		return iguales(unitario1, unitario2) || iguales(unitario1, opuesto(unitario2));
	}
	
	public static boolean iguales(int[] vec1, int[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectores no comparables");
		for (int i = 0; i < vec1.length; i++) {
			if (!iguales(vec1[i], vec2[i])) return false;
		}
		return true;
	}
	
	public static boolean iguales(int[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectores no comparables");
		for (int i = 0; i < vec1.length; i++) {
			if (!iguales(vec1[i], vec2[i])) return false;
		}
		return true;
	}
	
	public static boolean iguales(double[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectores no comparables");
		for (int i = 0; i < vec1.length; i++) {
			if (!iguales(vec1[i], vec2[i])) return false;
		}
		return true;
	}
	
	public static boolean iguales(double núm1, double núm2) {
		return Math.abs(núm1 - núm2) <= 0.00000000001;
	}
	
	public static int[] opuesto(int[] vec) {
		int[] vec_op = new int[vec.length];
		for (int i = 0; i < vec.length; i++) {
			vec_op[i] = -vec[i];
		}
		return vec_op;
	}
	
	public static double[] opuesto(double[] vec) {
		double[] vec_op = new double[vec.length];
		for (int i = 0; i < vec.length; i++) {
			vec_op[i] = -vec[i];
		}
		return vec_op;
	}

	public static double[] unitario(int[] vec) {
		double[] vec_uni = new double[vec.length];
		double mag = pitágoras(vec);
		for (int i = 0; i < vec.length; i++) {
			vec_uni[i] = vec[i] / mag;
		}
		return fix(vec_uni);
	}
	
	public static double[] unitario(double[] vec) {
		double[] vec_uni = vec.clone();
		double mag = pitágoras(vec);
		for (int i = 0; i < vec.length; i++) {
			vec_uni[i] /= mag;
		}
		return fix(vec_uni);
	}
	
	public static int[] sumar(int[] vec1, int[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectores de diferente dimensión");
		int[] vec_suma = vec2.clone();
		for (int i = 0; i < vec2.length; i++) {
			vec_suma[i] += vec1[i];
		}
		return vec_suma;
	}
	
	public static double[] sumar(int[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectores de diferente dimensión");
		double[] vec_suma = vec2.clone();
		for (int i = 0; i < vec2.length; i++) {
			vec_suma[i] += vec1[i];
		}
		return vec_suma;
	}
	
	public static double[] sumar(double[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectores de diferente dimensión");
		double[] vec_suma = vec2.clone();
		for (int i = 0; i < vec2.length; i++) {
			vec_suma[i] += vec1[i];
		}
		return vec_suma;
	}
	
	public static int[] restar(int[] vec1, int[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectores de diferente dimensión");
		int[] vec_resta = vec1.clone();
		for (int i = 0; i < vec2.length; i++) {
			vec_resta[i] -= vec2[i];
		}
		return vec_resta;
	}
	
	public static double[] restar(int[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectores de diferente dimensión");
		double[] vec_resta = to_double(vec1.clone());
		for (int i = 0; i < vec2.length; i++) {
			vec_resta[i] -= vec2[i];
		}
		return vec_resta;
	}
	
	public static double[] restar(double[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectores de diferente dimensión");
		double[] vec_resta = vec1.clone();
		for (int i = 0; i < vec2.length; i++) {
			vec_resta[i] -= vec2[i];
		}
		return vec_resta;
	}
	
	public static int[] multipl(int[] vec1, int[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectores de diferente dimensión");
		int[] vec_mult = vec2.clone();
		for (int i = 0; i < vec2.length; i++) {
			vec_mult[i] *= vec1[i];
		}
		return vec_mult;
	}
	
	public static double[] multipl(int[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectores de diferente dimensión");
		double[] vec_mult = vec2.clone();
		for (int i = 0; i < vec2.length; i++) {
			vec_mult[i] *= vec1[i];
		}
		return fix(vec_mult);
	}
	
	public static double[] multipl(double[] vec1, double[] vec2) {
		if (vec1.length != vec2.length) throw new IllegalArgumentException("Vectores de diferente dimensión");
		double[] vec_mult = vec2.clone();
		for (int i = 0; i < vec2.length; i++) {
			vec_mult[i] *= vec1[i];
		}
		return fix(vec_mult);
	}
	
	public static int[] multipl(int[] vec, double num) {
		int[] vec_mult = vec.clone();
		for (int i = 0; i < vec.length; i++) {
			vec_mult[i] *= num;
		}
		return vec_mult;
	}
	
	public static double[] multipl(double[] vec, double num) {
		double[] vec_mult = vec.clone();
		for (int i = 0; i < vec.length; i++) {
			vec_mult[i] *= num;
		}
		return fix(vec_mult);
	}
	
	public static double pitágoras(int[] punto) {
		return fix(Math.sqrt(pitágorasSinRaíz(punto)));
	}
	
	public static double pitágoras(double[] punto) {
		return fix(Math.sqrt(pitágorasSinRaíz(punto)));
	}
	
	public static int pitágorasSinRaíz(int[] punto) {
		int radicando = 0;
		for (int d : punto) {
			radicando += Math.pow(d, 2);
		}
		return radicando;
	}
	
	public static double pitágorasSinRaíz(double[] punto) {
		double radicando = 0;
		for (double d : punto) {
			radicando += Math.pow(d, 2);
		}
		return radicando;
	}

	/**
	 * @return un vector de ints no negativos
	 */
	public static int[] to_int(byte[] vec) {
		int[] vec_int = new int[vec.length];
		for (int i = 0; i < vec.length; i++) {
			vec_int[i] = vec[i] & 0xff;
		}
		return vec_int;
	}

	public static double[] to_double(int[] vec) {
		double[] vec_double = new double[vec.length];
		System.arraycopy(vec, 0, vec_double, 0, vec.length);
		return vec_double;
	}
	
	public static int[] vector(int[] inicio, int[] fin) {
		if (inicio.length != fin.length) throw new IllegalArgumentException("Puntos de diferente dimensión");
		int[] vec = fin.clone();
		for (int i = 0; i < fin.length; i++) {
			vec[i] -= inicio[i];
		}
		return vec;
	}
	
	public static double[] vector(double[] inicio, int[] fin) {
		if (inicio.length != fin.length) throw new IllegalArgumentException("Puntos de diferente dimensión");
		double[] vec = new double[fin.length];
		for (int i = 0; i < fin.length; i++) {
			vec[i] = fin[i] - inicio[i];
		}
		return vec;
	}
	
	public static double[] vector(int[] inicio, double[] fin) {
		if (inicio.length != fin.length) throw new IllegalArgumentException("Puntos de diferente dimensión");
		double[] vec = fin.clone();
		for (int i = 0; i < fin.length; i++) {
			vec[i] -= inicio[i];
		}
		return vec;
	}
	
	public static double[] vector(double[] inicio, double[] fin) {
		if (inicio.length != fin.length) throw new IllegalArgumentException("Puntos de diferente dimensión");
		double[] vec = fin.clone();
		for (int i = 0; i < fin.length; i++) {
			vec[i] -= inicio[i];
		}
		return vec;
	}
	
	public static int[] prodVectorial(int[] vec1, int[] vec2) {
		if (vec1.length != 3 || vec2.length != 3) throw new IllegalArgumentException("Vectores inválidos");
		return new int[] {
    		vec1[1] * vec2[2] - vec1[2] * vec2[1],
    		vec1[2] * vec2[0] - vec1[0] * vec2[2],
    		vec1[0] * vec2[1] - vec1[1] * vec2[0]};
    }
	
	public static double[] prodVectorial(int[] vec1, double[] vec2) {
		if (vec1.length != 3 || vec2.length != 3) throw new IllegalArgumentException("Vectores inválidos");
		return new double[] {
    		vec1[1] * vec2[2] - vec1[2] * vec2[1],
    		vec1[2] * vec2[0] - vec1[0] * vec2[2],
    		vec1[0] * vec2[1] - vec1[1] * vec2[0]};
    }
	
	public static double[] prodVectorial(double[] vec1, double[] vec2) {
		if (vec1.length != 3 || vec2.length != 3) throw new IllegalArgumentException("Vectores inválidos");
		return new double[] {
    		vec1[1] * vec2[2] - vec1[2] * vec2[1],
    		vec1[2] * vec2[0] - vec1[0] * vec2[2],
    		vec1[0] * vec2[1] - vec1[1] * vec2[0]};
    }
	
	public static int[] planoPuntos(int[] p1, int[] p2, int[] p3) {
		if (p1.length != 3) throw new IllegalArgumentException("Punto inválido");
		int[] vec1 = vector(p1, p2), vec2 = vector(p1, p3);
		return planoVectores(p1, vec1, vec2);
	}
	
	public static double[] planoPuntos(int[] p1, int[] p2, double[] p3) {
		if (p1.length != 3) throw new IllegalArgumentException("Punto inválido");
		int[] vec1 = vector(p1, p2);
		double[] vec2 = vector(p1, p3);
		return planoVectores(p1, vec1, vec2);
	}
	
	public static double[] planoPuntos(int[] p1, double[] p2, double[] p3) {
		if (p1.length != 3) throw new IllegalArgumentException("Punto inválido");
		double[] vec1 = vector(p1, p2), vec2 = vector(p1, p3);
		return planoVectores(p1, vec1, vec2);
	}
	
	public static double[] planoPuntos(double[] p1, double[] p2, double[] p3) {
		if (p1.length != 3) throw new IllegalArgumentException("Punto inválido");
		double[] vec1 = vector(p1, p2), vec2 = vector(p1, p3);
		return planoVectores(p1, vec1, vec2);
	}
	
	public static int[] planoVectores(int[] punto, int[] vec1, int[] vec2) {
		if (punto.length != 3) throw new IllegalArgumentException("Punto inválido");
		int[] vec_normal = prodVectorial(vec1, vec2); //A,B,C
		int constante = - (vec_normal[0]*punto[0] + vec_normal[1]*punto[1] + vec_normal[2]*punto[2]); //D
		return new int[] {vec_normal[0], vec_normal[1], vec_normal[2], constante}; // Ax+By+Cz+D=0
	}
	
	public static double[] planoVectores(int[] punto, int[] vec1, double[] vec2) {
		if (punto.length != 3) throw new IllegalArgumentException("Punto inválido");
		double[] vec_normal = prodVectorial(vec1, vec2); //A,B,C
		double constante = - (vec_normal[0]*punto[0] + vec_normal[1]*punto[1] + vec_normal[2]*punto[2]); //D
		return fix(new double[] {vec_normal[0], vec_normal[1], vec_normal[2], constante}); // Ax+By+Cz+D=0
	}
	
	public static double[] planoVectores(int[] punto, double[] vec1, double[] vec2) {
		if (punto.length != 3) throw new IllegalArgumentException("Punto inválido");
		double[] vec_normal = prodVectorial(vec1, vec2); //A,B,C
		double constante = - (vec_normal[0]*punto[0] + vec_normal[1]*punto[1] + vec_normal[2]*punto[2]); //D
		return fix(new double[] {vec_normal[0], vec_normal[1], vec_normal[2], constante}); // Ax+By+Cz+D=0
	}
	
	public static double[] planoVectores(double[] punto, double[] vec1, double[] vec2) {
		if (punto.length != 3) throw new IllegalArgumentException("Punto inválido");
		double[] vec_normal = prodVectorial(vec1, vec2); //A,B,C
		double constante = - (vec_normal[0]*punto[0] + vec_normal[1]*punto[1] + vec_normal[2]*punto[2]); //D
		return fix(new double[] {vec_normal[0], vec_normal[1], vec_normal[2], constante}); // Ax+By+Cz+D=0
	}
	
	public static boolean esVálido(int i, int j, Object[][] tabla, int margen) {
		return margen <= i && i < tabla.length - margen && margen <= j && j < tabla[0].length - margen;
	}
	
	public static boolean iguales(Object... objetos) {
		if (objetos.length < 2) throw new IllegalArgumentException("No hay varios argumentos");
		for (int i = 1; i < objetos.length; i++) {
			if (objetos[i] != objetos[i-1]) return false;
		}
		return true;
	}
}