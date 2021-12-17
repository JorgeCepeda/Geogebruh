package motor_gráfico.menús;

import chunks_NoCeldas.Chunks;
import efectos.Iluminación;
import motor_gráfico.MotorGráfico;
import niveles.Niveles;
import operaciones.MyMath;

public class MenúPrincipal extends Menú {
	private static final Opción[][] opciones = {
		{Opción.ILUMINACIÓN, Opción.SOMBRAS, null, null},
		{Opción.CARGAR_SECCIONES, Opción.GUARDAR, Opción.CARGAR_NIVEL, null},
		{Opción.FOTO, null, null, null}
	};
	private final MenúNiveles submenú;

	public MenúPrincipal(MotorGráfico motor_gráfico) {
		super(motor_gráfico, true);
		tamaño_texto = 20;
		submenú = new MenúNiveles(motor_gráfico);
	}

	@Override
	protected void updateTexto() {
		StringBuilder sb = new StringBuilder(400);
		sb.append("<html><pre>");
		for (int i = 0; i < opciones.length; i++) {
			for (int j = 0; j < opciones[i].length; j++) {
				if (i == opción_actual[0] && j == opción_actual[1]) sb.append("> ");
				else if (opciones[i][j] != null) {
					sb.append("- ");
				}
				
				if (opciones[i][j] != null) {
					sb.append(opciones[i][j]);
				}
				sb.append("   ");
			}
			sb.append("<br>");
		}
		getTexto().setText(sb.append("</pre></html>").toString());
	}
	
	@Override
	public synchronized boolean ejecutarInput(char input) {
		if (submenú.estáMostrado()) {
			submenú.ejecutarInput(input);
			return false;
		}
		return super.ejecutarInput(input);
	}
	
	@Override
	public synchronized boolean ejecutarInput(int keyCode) {
		if (submenú.estáMostrado()) {
			submenú.ejecutarInput(keyCode);
			return false;
		}
		return super.ejecutarInput(keyCode);
	}

	@Override
	protected void ejecutarOpción() {
		Opción opción = opciones[opción_actual[0]][opción_actual[1]];
		if (opción != null) {
			System.out.println("Ejecutada opción: " + opción.nombre);
			switch (opción) {
			case CARGAR_NIVEL:
				submenú.setMostrado(true);
				break;
			case CARGAR_SECCIONES:
				Chunks.cargar(Niveles.cargado().getNombre() + ".chunks", true);
				break;
			case GUARDAR:
				Chunks.guardar(Niveles.cargado().getNombre() + ".chunks");
				break;
			case ILUMINACIÓN:
				Iluminación.setIluminación(!Iluminación.hayIluminación());
				break;
			case SOMBRAS:
				Iluminación.setSombras(!Iluminación.haySombras());
				break;
			case FOTO:
				motor_gráfico.getPantalla().exportarFrame();
				break;
			default:
				System.out.println("Opción inválida: " + opción);
			}
		}
	}

	@Override
	protected void desplazarOpción(int filas, int columnas) {
		if (MyMath.esVálido(opción_actual[0] + filas, opción_actual[1] + columnas, opciones, 0)) {
			opción_actual[0] += filas;
			opción_actual[1] += columnas;
			updateTexto();
		}
	}
}
