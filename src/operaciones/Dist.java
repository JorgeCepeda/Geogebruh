package operaciones;

public class Dist {
	
	private Dist() {
		throw new IllegalStateException("Clase no instanciable");
	}
	
	public static double puntoARecta(int[] pos_punto, int[] pos_recta, int[] orient_recta, double escalar) {
		double radicando = 0;
		for (int i = 0; i < 3; i++) {
			radicando += Math.pow(pos_recta[i] + orient_recta[i] * escalar - pos_punto[i],  2);
		}
		return MyMath.fix(Math.sqrt(radicando));
	}
	
	public static double puntoARecta(int[] pos_punto, int[] pos_recta, double[] orient_recta, double escalar) {
		double radicando = 0;
		for (int i = 0; i < 3; i++) {
			radicando += Math.pow(pos_recta[i] + orient_recta[i] * escalar - pos_punto[i],  2);
		}
		return MyMath.fix(Math.sqrt(radicando));
	}
	
	public static double puntoARecta(int[] pos_punto, double[] pos_recta, double[] orient_recta, double escalar) {
		double radicando = 0;
		for (int i = 0; i < 3; i++) {
			radicando += Math.pow(pos_recta[i] + orient_recta[i] * escalar - pos_punto[i],  2);
		}
		return MyMath.fix(Math.sqrt(radicando));
	}
	
	public static double puntoARecta(double[] pos_punto, double[] pos_recta, double[] orient_recta, double escalar) {
		double radicando = 0;
		for (int i = 0; i < 3; i++) {
			radicando += Math.pow(pos_recta[i] + orient_recta[i] * escalar - pos_punto[i],  2);
		}
		return MyMath.fix(Math.sqrt(radicando));
	}
	
	public static double puntoARecta(int[] pos_punto, int[] pos_recta, int[] orient_recta) {
		return puntoARecta(pos_punto, pos_recta, orient_recta, puntoAPlano(pos_punto, pos_recta, orient_recta));
	}
	
	public static double puntoARecta(int[] pos_punto, int[] pos_recta, double[] orient_recta) {
		return puntoARecta(pos_punto, pos_recta, orient_recta, puntoAPlano(pos_punto, pos_recta, orient_recta));
	}
	
	public static double puntoARecta(int[] pos_punto, double[] pos_recta, double[] orient_recta) {
		return puntoARecta(pos_punto, pos_recta, orient_recta, puntoAPlano(pos_punto, pos_recta, orient_recta));
	}
	
	public static double puntoARecta(double[] pos_punto, double[] pos_recta, double[] orient_recta) {
		return puntoARecta(pos_punto, pos_recta, orient_recta, puntoAPlano(pos_punto, pos_recta, orient_recta));
	}
	
	public static double puntoAPlano(int[] pos_punto, int[] pos_plano, int[] vec_normal) {
		double escalar = 0;
		for (int i = 0; i < 3; i++) {
			escalar += vec_normal[i] * (pos_punto[i] - pos_plano[i]);
		}
		return MyMath.fix(escalar);
	}
	
	public static double puntoAPlano(int[] pos_punto, int[] pos_plano, double[] vec_normal) {
		double escalar = 0;
		for (int i = 0; i < 3; i++) {
			escalar += vec_normal[i] * (pos_punto[i] - pos_plano[i]);
		}
		return MyMath.fix(escalar);
	}
	
	public static double puntoAPlano(int[] pos_punto, double[] pos_plano, double[] vec_normal) {
		double escalar = 0;
		for (int i = 0; i < 3; i++) {
			escalar += vec_normal[i] * (pos_punto[i] - pos_plano[i]);
		}
		return MyMath.fix(escalar);
	}
	
	public static double puntoAPlano(double[] pos_punto, double[] pos_plano, double[] vec_normal) {
		double escalar = 0;
		for (int i = 0; i < 3; i++) {
			escalar += vec_normal[i] * (pos_punto[i] - pos_plano[i]);
		}
		return MyMath.fix(escalar);
	}
	
	public static double rectaARecta(double[] pos_recta1, double[] orient_recta1, double[] pos_recta2, double[] orient_recta2) {
		if (MyMath.mismaDirec(orient_recta1, orient_recta2)) return puntoARecta(pos_recta1, pos_recta2, orient_recta2);
		return rectaARecta_NoParalelas(pos_recta1, orient_recta1, pos_recta2, orient_recta2);
	}
	
	public static double rectaARecta_NoParalelas(double[] pos_recta1, double[] orient_recta1, double[] pos_recta2, double[] orient_recta2) {
		double[] prod_vec = MyMath.prodVectorial(orient_recta1, orient_recta2), vec1 = MyMath.vector(pos_recta1, pos_recta2);
		double prod_mixto = 0;
		for (int i = 0; i < vec1.length; i++) {
			prod_mixto += vec1[i] * prod_vec[i];
		}
		prod_mixto = Math.abs(prod_mixto);
		
		return MyMath.fix(prod_mixto / MyMath.pitágoras(prod_vec));
	}
	
	public static double puntoAPunto(int[] punto1, int[] punto2) {
		return MyMath.fix(Math.sqrt(puntoAPuntoSinRaíz(punto1, punto2)));
	}
	
	public static double puntoAPunto(int[] punto1, double[] punto2) {
		return MyMath.fix(Math.sqrt(puntoAPuntoSinRaíz(punto1, punto2)));
	}
	
	public static double puntoAPunto(double[] punto1, double[] punto2) {
		return MyMath.fix(Math.sqrt(puntoAPuntoSinRaíz(punto1, punto2)));
	}
	
	public static int puntoAPuntoSinRaíz(int[] punto1, int[] punto2) {
		int radicando = 0;
		for (int i = 0; i < punto1.length; i++) {
			radicando += Math.pow(punto1[i] - punto2[i], 2);
		}
		return radicando;
	}
	
	public static double puntoAPuntoSinRaíz(int[] punto1, double[] punto2) {
		double radicando = 0;
		for (int i = 0; i < punto1.length; i++) {
			radicando += Math.pow(punto1[i] - punto2[i], 2);
		}
		return radicando;
	}
	
	public static double puntoAPuntoSinRaíz(double[] punto1, double[] punto2) {
		double radicando = 0;
		for (int i = 0; i < punto1.length; i++) {
			radicando += Math.pow(punto1[i] - punto2[i], 2);
		}
		return radicando;
	}
}