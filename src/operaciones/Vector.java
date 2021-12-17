package operaciones;

public class Vector {
	private static final double[] ROTACIÓN_STD = {0, 0, 0} /*teta_hori, teta_vert, teta_inclin*/, ORIENT_STD = orient(ROTACIÓN_STD);
	
	private Vector() {
		throw new IllegalStateException("Clase no instanciable");
	}
	
	public static double[] rotaciónSTD() {
		return ROTACIÓN_STD.clone();
	}
	
	public static double[] orientSTD() {
		return ORIENT_STD.clone();
	}
	
	public static double[] orient(double[] rotación) {
		return MyMath.fix(new double[] {
			Math.cos(rotación[0]) * Math.cos(rotación[1]),
			Math.sin(rotación[1]),
			Math.sin(rotación[0]) * Math.cos(rotación[1])
		});
	}

	public static double[] rotación(double[] orient) {
		if (MyMath.iguales(Math.abs(orient[1]), 1)) return new double[] {0, Math.PI/2.0 * Math.signum(orient[1]), 0};
		double rot_hori = Math.acos(orient[0] / MyMath.pitágoras(new double[] {orient[0], orient[2]})), rot_vert = Math.asin(orient[1]);
		if (orient[2] < 0) rot_hori *= -1;
		
		return MyMath.fix(new double[] {rot_hori - ROTACIÓN_STD[0], rot_vert - ROTACIÓN_STD[1], -ROTACIÓN_STD[2]});
	}
	
	/**
	 * @return las coordenadas x,y del punto proyectado sobre un plano definido por su posición, su vector normal, y su rotación, siendo (0,0) el centro
	 */
	public static double[] proyectar2D(double[] punto_a_proyectar, double[] punto_central, double[] vec_normal, double[] rotación) {
		if (rotación[2] == 0) return proyectar2D_Inclin0(punto_a_proyectar, punto_central, vec_normal, rotación);
		return new double[] {
			Dist.puntoAPlano(punto_a_proyectar, punto_central,
			/*Eje horizontal inclinado*/ vectorCelda_Unitario(0, vec_normal[1], rotación)),
			Dist.puntoAPlano(punto_a_proyectar, punto_central,
			/*Eje vertical inclinado*/ vectorCelda_Unitario(Math.PI/2.0, vec_normal[1], rotación))
		};
	}
	
	/**
	 * @see Vector#proyectar2D(double[], double[], double[], double[])
	 */
	public static double[] proyectar2D_Inclin0(double[] punto_a_proyectar, double[] punto_central, double[] vec_normal, double[] rotación) { // Supone inclinación = 0
		return new double[] {
			Dist.puntoAPlano(punto_a_proyectar, punto_central, 
			/*Eje horizontal*/ new double[] {Math.sin(rotación[0]), 0, - Math.cos(rotación[0])}),
			Dist.puntoAPlano(punto_a_proyectar, punto_central,
			/*Eje vertical*/ new double[] {- vec_normal[1] * Math.cos(rotación[0]), Math.cos(rotación[1]), - vec_normal[1] * Math.sin(rotación[0])})
		};
	}
	
	/**
	 * Rota el punto alrededor del central según su orientación y rotación, de manera que relativo a él fuese como si estuviese con orientación y rotación estándar
	 * @return el punto rotado sin modificar el original
	 */
	public static double[] rotar(double[] punto_a_rotar, double[] punto_central, double[] orient, double[] rotación) {
		if (MyMath.iguales(orient, ORIENT_STD) && MyMath.iguales(rotación, ROTACIÓN_STD)) return punto_a_rotar;
		
		// Proyecto el punto a rotar sobre el plano definido por la orientación y el centro
		double[] coord = proyectar2D_Inclin0(punto_a_rotar, punto_central, orient, rotación);
		
		// Roto el punto proyectado hacia la nueva orientación e inclinación
		double[] punto_rotado = MyMath.fix(centroACelda(coord[1], coord[0], punto_central, ORIENT_STD[1], new double[] {ROTACIÓN_STD[0], ROTACIÓN_STD[1], -rotación[2]}));
		
		// Saco el punto proyectado a su posición final
		return MyMath.fix(MyMath.sumar(punto_rotado, MyMath.multipl(ORIENT_STD, Dist.puntoAPlano(punto_a_rotar, punto_central, orient))));
	}
	
	public static double[] centroACelda(double fila, double columna, double[] pos_centro, double orient_vert_nueva, double[] nueva_rotación) {
		double[] celda = pos_centro.clone();
		if (fila != 0 || columna != 0) {
			double mag_vec_celda = MyMath.pitágoras(new double[] {fila, columna}), phi = Math.acos(columna / mag_vec_celda);
			if (fila < 0) phi *= -1;
			double cos_0 = Math.cos(nueva_rotación[0]), sin_0 = Math.sin(nueva_rotación[0]), cos_phi_2 = Math.cos(phi + nueva_rotación[2]), sin_phi_2 = Math.sin(phi + nueva_rotación[2]);
			celda[0] += (sin_0 * cos_phi_2 - orient_vert_nueva * cos_0 * sin_phi_2) * mag_vec_celda;
			celda[1] += Math.cos(nueva_rotación[1]) * sin_phi_2 * mag_vec_celda;
			celda[2] -= (cos_0 * cos_phi_2 + orient_vert_nueva * sin_0 * sin_phi_2) * mag_vec_celda;
		}
		return celda;
	}
	
	public static double[] vectorCelda_Unitario(double phi, double orient_vert_nueva, double[] nueva_rotación) {
		double cos_0 = Math.cos(nueva_rotación[0]), sin_0 = Math.sin(nueva_rotación[0]), cos_phi_2 = Math.cos(phi + nueva_rotación[2]), sin_phi_2 = Math.sin(phi + nueva_rotación[2]);
		return new double[] {
			sin_0 * cos_phi_2 - orient_vert_nueva * cos_0 * sin_phi_2,
			Math.cos(nueva_rotación[1]) * sin_phi_2,
			- cos_0 * cos_phi_2 - orient_vert_nueva * sin_0 * sin_phi_2
		};
	}
}