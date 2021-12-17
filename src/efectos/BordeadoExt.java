package efectos;

import gráficos.Pantalla;
import objetos.Objetos;
import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoBase;
import objetos.propiedades.Borde;
import operaciones.MyMath;

public class BordeadoExt {
	
	private BordeadoExt() {
		throw new IllegalStateException("Clase no instanciable");
	}
	
	public static boolean[][] drawBorde(Pantalla p) {
		boolean[][] bordeado_ext = new boolean[p.tabla_carác().length][p.tabla_carác()[0].length];
		double[][] tabla_dist_borde = new double[p.tabla_carác().length][p.tabla_carác()[0].length];
		for (int i = p.tabla_carác().length-1; i > -1; i--) { // Filas
			for (int j = p.tabla_carác()[0].length-1; j > -1; j--) { // Columnas
				if (p.tabla_obj()[i][j] != null) {
					// Comprobar celdas adyacentes mediante bucle
					Objeto obj = Objetos.objConcreto(p.tabla_obj()[i][j], p.tabla_pos()[i][j]);
					Borde borde = obj.propiedad(ObjetoBase.BORDE);
					if (borde != null) {
						int grosor = borde.getGrosor();
						for (int k = -grosor; k <= grosor; k++) { // Filas
							for (int l = -grosor; l <= grosor; l++) { // Columnas
								// Si hay borde o no será en función de con qué objeto colisiona y de la distancia de colisión
								if ((k != 0 || l != 0) && MyMath.esVálido(i+k, j+l, p.tabla_color(), p.extra()) &&
										
								(p.tabla_obj()[i+k][j+l] == null || Objetos.objConcreto(p.tabla_obj()[i+k][j+l], p.tabla_pos()[i+k][j+l]) != obj &&
										
								p.tabla_dist()[i+k][j+l] > p.tabla_dist()[i][j]) && k*k+l*l <= grosor*grosor &&
										
								(!bordeado_ext[i+k][j+l] || tabla_dist_borde[i+k][j+l] > p.tabla_dist()[i][j])) {
									
									p.tabla_color()[i+k][j+l] = borde.getColor().getString();
									p.tabla_carác()[i+k][j+l] = borde.getCarác();
									tabla_dist_borde[i+k][j+l] = p.tabla_dist()[i][j]; // La distancia del borde puede ser algo imprecisa por cómo funciona el recorrido
									bordeado_ext[i+k][j+l] = true;
								}
							}
						}
					}
				}
			}
		}
		return bordeado_ext;
	}
}