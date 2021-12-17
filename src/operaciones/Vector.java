package operaciones;

public class Vector {
	private static final double[] ROTACI�N_STD = {0, 0, 0} /*teta_hori, teta_vert, teta_inclin*/, ORIENT_STD = orient(ROTACI�N_STD);
	
	private Vector() {
		throw new IllegalStateException("Clase no instanciable");
	}
	
	public static double[] rotaci�nSTD() {
		return ROTACI�N_STD.clone();
	}
	
	public static double[] orientSTD() {
		return ORIENT_STD.clone();
	}
	
	public static double[] orient(double[] rotaci�n) {
		return MyMath.fix(new double[] {
			Math.cos(rotaci�n[0]) * Math.cos(rotaci�n[1]),
			Math.sin(rotaci�n[1]),
			Math.sin(rotaci�n[0]) * Math.cos(rotaci�n[1])
		});
	}

	public static double[] rotaci�n(double[] orient) {
		if (MyMath.iguales(Math.abs(orient[1]), 1)) return new double[] {0, Math.PI/2.0 * Math.signum(orient[1]), 0};
		double rot_hori = Math.acos(orient[0] / MyMath.pit�goras(new double[] {orient[0], orient[2]})), rot_vert = Math.asin(orient[1]);
		if (orient[2] < 0) rot_hori *= -1;
		
		return MyMath.fix(new double[] {rot_hori - ROTACI�N_STD[0], rot_vert - ROTACI�N_STD[1], -ROTACI�N_STD[2]});
	}
	
	/**
	 * @return las coordenadas x,y del punto proyectado sobre un plano definido por su posici�n, su vector normal, y su rotaci�n, siendo (0,0) el centro
	 */
	public static double[] proyectar2D(double[] punto_a_proyectar, double[] punto_central, double[] vec_normal, double[] rotaci�n) {
		if (rotaci�n[2] == 0) return proyectar2D_Inclin0(punto_a_proyectar, punto_central, vec_normal, rotaci�n);
		return new double[] {
			Dist.puntoAPlano(punto_a_proyectar, punto_central,
			/*Eje horizontal inclinado*/ vectorCelda_Unitario(0, vec_normal[1], rotaci�n)),
			Dist.puntoAPlano(punto_a_proyectar, punto_central,
			/*Eje vertical inclinado*/ vectorCelda_Unitario(Math.PI/2.0, vec_normal[1], rotaci�n))
		};
	}
	
	/**
	 * @see Vector#proyectar2D(double[], double[], double[], double[])
	 */
	public static double[] proyectar2D_Inclin0(double[] punto_a_proyectar, double[] punto_central, double[] vec_normal, double[] rotaci�n) { // Supone inclinaci�n = 0
		return new double[] {
			Dist.puntoAPlano(punto_a_proyectar, punto_central, 
			/*Eje horizontal*/ new double[] {Math.sin(rotaci�n[0]), 0, - Math.cos(rotaci�n[0])}),
			Dist.puntoAPlano(punto_a_proyectar, punto_central,
			/*Eje vertical*/ new double[] {- vec_normal[1] * Math.cos(rotaci�n[0]), Math.cos(rotaci�n[1]), - vec_normal[1] * Math.sin(rotaci�n[0])})
		};
	}
	
	/**
	 * Rota el punto alrededor del central seg�n su orientaci�n y rotaci�n, de manera que relativo a �l fuese como si estuviese con orientaci�n y rotaci�n est�ndar
	 * @return el punto rotado sin modificar el original
	 */
	public static double[] rotar(double[] punto_a_rotar, double[] punto_central, double[] orient, double[] rotaci�n) {
		if (MyMath.iguales(orient, ORIENT_STD) && MyMath.iguales(rotaci�n, ROTACI�N_STD)) return punto_a_rotar;
		
		// Proyecto el punto a rotar sobre el plano definido por la orientaci�n y el centro
		double[] coord = proyectar2D_Inclin0(punto_a_rotar, punto_central, orient, rotaci�n);
		
		// Roto el punto proyectado hacia la nueva orientaci�n e inclinaci�n
		double[] punto_rotado = MyMath.fix(centroACelda(coord[1], coord[0], punto_central, ORIENT_STD[1], new double[] {ROTACI�N_STD[0], ROTACI�N_STD[1], -rotaci�n[2]}));
		
		// Saco el punto proyectado a su posici�n final
		return MyMath.fix(MyMath.sumar(punto_rotado, MyMath.multipl(ORIENT_STD, Dist.puntoAPlano(punto_a_rotar, punto_central, orient))));
	}
	
	public static double[] centroACelda(double fila, double columna, double[] pos_centro, double orient_vert_nueva, double[] nueva_rotaci�n) {
		double[] celda = pos_centro.clone();
		if (fila != 0 || columna != 0) {
			double mag_vec_celda = MyMath.pit�goras(new double[] {fila, columna}), phi = Math.acos(columna / mag_vec_celda);
			if (fila < 0) phi *= -1;
			double cos_0 = Math.cos(nueva_rotaci�n[0]), sin_0 = Math.sin(nueva_rotaci�n[0]), cos_phi_2 = Math.cos(phi + nueva_rotaci�n[2]), sin_phi_2 = Math.sin(phi + nueva_rotaci�n[2]);
			celda[0] += (sin_0 * cos_phi_2 - orient_vert_nueva * cos_0 * sin_phi_2) * mag_vec_celda;
			celda[1] += Math.cos(nueva_rotaci�n[1]) * sin_phi_2 * mag_vec_celda;
			celda[2] -= (cos_0 * cos_phi_2 + orient_vert_nueva * sin_0 * sin_phi_2) * mag_vec_celda;
		}
		return celda;
	}
	
	public static double[] vectorCelda_Unitario(double phi, double orient_vert_nueva, double[] nueva_rotaci�n) {
		double cos_0 = Math.cos(nueva_rotaci�n[0]), sin_0 = Math.sin(nueva_rotaci�n[0]), cos_phi_2 = Math.cos(phi + nueva_rotaci�n[2]), sin_phi_2 = Math.sin(phi + nueva_rotaci�n[2]);
		return new double[] {
			sin_0 * cos_phi_2 - orient_vert_nueva * cos_0 * sin_phi_2,
			Math.cos(nueva_rotaci�n[1]) * sin_phi_2,
			- cos_0 * cos_phi_2 - orient_vert_nueva * sin_0 * sin_phi_2
		};
	}
}