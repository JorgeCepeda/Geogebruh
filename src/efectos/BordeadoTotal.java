package efectos;

import gráficos.Pantalla;
import objetos.Objetos;
import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoBase;
import objetos.propiedades.Borde;
import operaciones.MyMath;

public class BordeadoTotal {
	private static final double margen_total = 2, margen_cruz = 1, margen_línea = 1; /*Márgenes a mano*/
	
	private BordeadoTotal() {
		throw new IllegalStateException("Clase no instanciable");
	}
	
	public static void drawBorde(Pantalla p) {
		drawBorde(p, BordeadoExt.drawBorde(p));
	}
	
	public static void drawBorde(Pantalla p, boolean[][] bordeado_ext) {
		for (int i = p.extra(); i < p.tabla_color().length - p.extra(); i++) { // Filas
			for (int j = p.extra(); j < p.tabla_color()[0].length - p.extra(); j++) { // Columnas
				if (!bordeado_ext[i][j]) {
					Objeto obj = Objetos.objConcreto(p.tabla_obj()[i][j], p.tabla_pos()[i][j]);
					Borde borde;
					if (obj != null && (borde = obj.propiedad(ObjetoBase.BORDE)) != null && hayBorde(i, j, p)) {
						p.tabla_color()[i][j] = borde.getColor().getString();
						p.tabla_carác()[i][j] = borde.getCarác();
					}
				}
			}
		}
	}

	private static boolean hayBorde(int i, int j, Pantalla p) {
		// Detección de aristas de Laplace
		boolean verticales_iguales = MyMath.iguales(p.tabla_obj()[i-1][j], p.tabla_obj()[i][j], p.tabla_obj()[i+1][j]),
			horizontales_iguales = MyMath.iguales(p.tabla_obj()[i][j-1], p.tabla_obj()[i][j], p.tabla_obj()[i][j+1]);
		
		if (verticales_iguales && horizontales_iguales) {
			if (p.cámara().esPrecisa() && MyMath.iguales(p.tabla_obj()[i+1][j+1], p.tabla_obj()[i-1][j+1], p.tabla_obj()[i+1][j-1], p.tabla_obj()[i-1][j-1], p.tabla_obj()[i][j])) {
				return Math.abs(p.tabla_dist()[i+1][j+1] + p.tabla_dist()[i+1][j-1] + p.tabla_dist()[i-1][j+1] + p.tabla_dist()[i-1][j-1] +
					p.tabla_dist()[i+1][j] + p.tabla_dist()[i-1][j] + p.tabla_dist()[i][j+1] + p.tabla_dist()[i][j-1] - p.tabla_dist()[i][j] * 8) >= margen_total;
			}
			return Math.abs(p.tabla_dist()[i+1][j] + p.tabla_dist()[i-1][j] + p.tabla_dist()[i][j+1] + p.tabla_dist()[i][j-1] - p.tabla_dist()[i][j] * 4) >= margen_cruz;
		}
		return verticales_iguales && Math.abs(p.tabla_dist()[i+1][j] + p.tabla_dist()[i-1][j] - p.tabla_dist()[i][j] * 2) >= margen_línea ||
			horizontales_iguales && Math.abs(p.tabla_dist()[i][j+1] + p.tabla_dist()[i][j-1] - p.tabla_dist()[i][j] * 2) >= margen_línea;
	}
}