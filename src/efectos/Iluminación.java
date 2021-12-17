package efectos;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import gráficos.Pantalla;
import objetos.Fotón;
import objetos.Luz;
import objetos.Objetos;
import objetos.abstracto.Objeto;
import operaciones.EngHexDec;

public class Iluminación {
	private static LinkedHashSet<Luz> luces = new LinkedHashSet<>();
	private static boolean iluminación_general = true, sombras = true;

	private Iluminación() {
		throw new IllegalStateException("Clase no instanciable");
	}

	public static LinkedHashSet<Luz> getLuces() {
		return luces;
	}

	public static void setLuces(LinkedHashSet<Luz> luces) {
		if (luces == null) throw new IllegalArgumentException("El set de luces no puede ser null");
		Iluminación.luces = luces;
	}
	
	public static boolean haySombras() {
		return sombras;
	}
	
	public static void setSombras(boolean sombras) {
		Iluminación.sombras = sombras;
	}

	public static boolean hayIluminación() {
		return iluminación_general;
	}

	public static void setIluminación(boolean iluminación_general) {
		Iluminación.iluminación_general = iluminación_general;
	}

	public static void iluminar(Pantalla p) { //TODO reflejos
		for (int i = p.extra(); i < p.tabla_pos().length - p.extra(); i++) { // Filas
			for (int j = p.extra(); j < p.tabla_pos()[0].length - p.extra(); j++) { // Columnas
				Objeto obj = Objetos.objConcreto(p.tabla_obj()[i][j], p.tabla_pos()[i][j]);
				if (obj != null) {
					if (obj instanceof Luz && ((Luz) obj).isEncendida()) p.tabla_carác()[i][j] = '@';
					else p.tabla_color()[i][j] = EngHexDec.rgbToHex6(Luz.aplicarLuz(iluminar(obj, p.tabla_pos()[i][j]), EngHexDec.getRGB(p.tabla_color()[i][j])));
				}
			}
		}
	}
	
	/**
	 * Aplica todas las luces disponibles sobre un punto relacionado con un objeto
	 * @return la luz total aplicada sobre blanco con saturación incluida
	 */
	public static int[] iluminar(Objeto obj, double[] punto) {
		ArrayList<int[]> iluminaciones = new ArrayList<>(luces.size());
		Fotón fotón = new Fotón();
		for (Luz luz : luces) {
			if (luz != obj && luz.isEncendida() && (sombras && luz.puedeIluminar(punto, fotón) || !sombras && luz.puedeIluminar_SinOclusión(punto, obj, fotón))) {
				iluminaciones.add(luz.iluminarBlanco(punto));
			}
		}
		
		int[] iluminado = new int[3];
		for (int[] colorLuz : iluminaciones) {
			for (int i = 0; i < iluminado.length; i++) {
				iluminado[i] += colorLuz[i];
			}
		}
		
		// Calcular saturación
		double saturación_máx = 1;
		for (int i : iluminado) {
			double saturación = i / 255.0;
			if (saturación > saturación_máx) saturación_máx = saturación;
		}
		
		for (int i = 0; i < iluminado.length; i++) {
			int nuevo_valor = (int) (iluminado[i] / saturación_máx);
			iluminado[i] = nuevo_valor > 255 ? 255 : nuevo_valor;
		}
		return iluminado;
	}
}
