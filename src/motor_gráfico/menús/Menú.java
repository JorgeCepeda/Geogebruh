package motor_gr�fico.men�s;

import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;

import motor_gr�fico.MotorGr�fico;

public abstract class Men� {
	protected final MotorGr�fico<JLabel> motor_gr�fico;
	private final boolean atajo_m;
	protected Font font_backup;
	protected String texto_backup;
	protected boolean mostrado;
	protected int tama�o_texto;
	protected int[] opci�n_actual = new int[2];
	
	protected Men�(MotorGr�fico<JLabel> motor_gr�fico, boolean atajo_m) {
		if (motor_gr�fico == null) throw new IllegalArgumentException("El argumento no puede ser null");
		this.motor_gr�fico = motor_gr�fico;
		this.atajo_m = atajo_m;
	}
	
	public JLabel getTexto() {
		return motor_gr�fico.getSalida();
	}
	
	public boolean est�Mostrado() {
		return mostrado;
	}
	
	public void setMostrado(boolean mostrado) {
		if (this.mostrado = mostrado) mostrar();
		else resetTexto();
	}
	
	private void mostrar() {
		opci�n_actual = new int[2];
		font_backup = getTexto().getFont();
		texto_backup = getTexto().getText();
		getTexto().setFont(new Font(font_backup.getName(), font_backup.getStyle(), tama�o_texto));
		updateTexto();
	}
	
	private void resetTexto() {
		getTexto().setFont(new Font(font_backup.getName(), font_backup.getStyle(), font_backup.getSize()));
		getTexto().setText(texto_backup);
	}

	protected abstract void updateTexto();

	protected abstract void ejecutarOpci�n();

	/**
	 * Ejecuta el input
	 * @return si el men� se ha ocultado
	 */
	public synchronized boolean ejecutarInput(char input) {
		switch (input) {
		case 'm':
			if (atajo_m) {
				setMostrado(!mostrado);
				if (!mostrado) return true;
			}
			break;
		case 'w':
			desplazarOpci�n(-1,0);
			break;
		case 'a':
			desplazarOpci�n(0,-1);
			break;
		case 's':
			desplazarOpci�n(1,0);
			break;
		case 'd':
			desplazarOpci�n(0,1);
			break;
		case ' ':
			ejecutarOpci�n();
			break;
		default:
			System.out.println("Input no reconocido");
		}
		return false;
	}
	
	/**
	 * Ejecuta el KeyEvent seg�n su c�digo
	 * @return si el men� se ha ocultado
	 */
	public synchronized boolean ejecutarInput(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_M:
			if (atajo_m) {
				setMostrado(!mostrado);
				if (!mostrado) return true;
			}
			break;
		case KeyEvent.VK_W:
			desplazarOpci�n(-1,0);
			break;
		case KeyEvent.VK_A:
			desplazarOpci�n(0,-1);
			break;
		case KeyEvent.VK_S:
			desplazarOpci�n(1,0);
			break;
		case KeyEvent.VK_D:
			desplazarOpci�n(0,1);
			break;
		case KeyEvent.VK_SPACE:
			ejecutarOpci�n();
			break;
		default:
			System.out.println("Input no reconocido");
		}
		return false;
	}

	protected abstract void desplazarOpci�n(int filas, int columnas);

	protected enum Opci�n {
		CARGAR_SECCIONES("Cargar chunks"),
		GUARDAR("Guardar chunks"),
		ILUMINACI�N("Activar iluminaci�n"),
		SOMBRAS("Activar sombras"),
		FOTO("Exportar imagen"),
		CARGAR_NIVEL("Cargar nivel"); //TODO campo de visi�n y otros?
		
		final String nombre;
		
		Opci�n(String nombre) {
			this.nombre = nombre;
		}
		
		@Override
		public String toString() {
			return nombre;
		}
	}
}
