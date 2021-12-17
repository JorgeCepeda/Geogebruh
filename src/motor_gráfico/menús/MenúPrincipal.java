package motor_gr�fico.men�s;

import chunks_NoCeldas.Chunks;
import efectos.Iluminaci�n;
import motor_gr�fico.MotorGr�fico;
import niveles.Niveles;
import operaciones.MyMath;

public class Men�Principal extends Men� {
	private static final Opci�n[][] opciones = {
		{Opci�n.ILUMINACI�N, Opci�n.SOMBRAS, null, null},
		{Opci�n.CARGAR_SECCIONES, Opci�n.GUARDAR, Opci�n.CARGAR_NIVEL, null},
		{Opci�n.FOTO, null, null, null}
	};
	private final Men�Niveles submen�;

	public Men�Principal(MotorGr�fico motor_gr�fico) {
		super(motor_gr�fico, true);
		tama�o_texto = 20;
		submen� = new Men�Niveles(motor_gr�fico);
	}

	@Override
	protected void updateTexto() {
		StringBuilder sb = new StringBuilder(400);
		sb.append("<html><pre>");
		for (int i = 0; i < opciones.length; i++) {
			for (int j = 0; j < opciones[i].length; j++) {
				if (i == opci�n_actual[0] && j == opci�n_actual[1]) sb.append("> ");
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
		if (submen�.est�Mostrado()) {
			submen�.ejecutarInput(input);
			return false;
		}
		return super.ejecutarInput(input);
	}
	
	@Override
	public synchronized boolean ejecutarInput(int keyCode) {
		if (submen�.est�Mostrado()) {
			submen�.ejecutarInput(keyCode);
			return false;
		}
		return super.ejecutarInput(keyCode);
	}

	@Override
	protected void ejecutarOpci�n() {
		Opci�n opci�n = opciones[opci�n_actual[0]][opci�n_actual[1]];
		if (opci�n != null) {
			System.out.println("Ejecutada opci�n: " + opci�n.nombre);
			switch (opci�n) {
			case CARGAR_NIVEL:
				submen�.setMostrado(true);
				break;
			case CARGAR_SECCIONES:
				Chunks.cargar(Niveles.cargado().getNombre() + ".chunks", true);
				break;
			case GUARDAR:
				Chunks.guardar(Niveles.cargado().getNombre() + ".chunks");
				break;
			case ILUMINACI�N:
				Iluminaci�n.setIluminaci�n(!Iluminaci�n.hayIluminaci�n());
				break;
			case SOMBRAS:
				Iluminaci�n.setSombras(!Iluminaci�n.haySombras());
				break;
			case FOTO:
				motor_gr�fico.getPantalla().exportarFrame();
				break;
			default:
				System.out.println("Opci�n inv�lida: " + opci�n);
			}
		}
	}

	@Override
	protected void desplazarOpci�n(int filas, int columnas) {
		if (MyMath.esV�lido(opci�n_actual[0] + filas, opci�n_actual[1] + columnas, opciones, 0)) {
			opci�n_actual[0] += filas;
			opci�n_actual[1] += columnas;
			updateTexto();
		}
	}
}
