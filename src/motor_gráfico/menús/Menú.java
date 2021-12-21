package motor_gráfico.menús;

import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;

import motor_gráfico.MotorGráfico;

public abstract class Menú {
	protected final MotorGráfico<JLabel> motor_gráfico;
	private final boolean atajo_m;
	protected Font font_backup;
	protected String texto_backup;
	protected boolean mostrado;
	protected int tamaño_texto;
	protected int[] opción_actual = new int[2];
	
	protected Menú(MotorGráfico<JLabel> motor_gráfico, boolean atajo_m) {
		if (motor_gráfico == null) throw new IllegalArgumentException("El argumento no puede ser null");
		this.motor_gráfico = motor_gráfico;
		this.atajo_m = atajo_m;
	}
	
	public JLabel getTexto() {
		return motor_gráfico.getSalida();
	}
	
	public boolean estáMostrado() {
		return mostrado;
	}
	
	public void setMostrado(boolean mostrado) {
		if (this.mostrado = mostrado) mostrar();
		else resetTexto();
	}
	
	private void mostrar() {
		opción_actual = new int[2];
		font_backup = getTexto().getFont();
		texto_backup = getTexto().getText();
		getTexto().setFont(new Font(font_backup.getName(), font_backup.getStyle(), tamaño_texto));
		updateTexto();
	}
	
	private void resetTexto() {
		getTexto().setFont(new Font(font_backup.getName(), font_backup.getStyle(), font_backup.getSize()));
		getTexto().setText(texto_backup);
	}

	protected abstract void updateTexto();

	protected abstract void ejecutarOpción();

	/**
	 * Ejecuta el input
	 * @return si el menú se ha ocultado
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
			desplazarOpción(-1,0);
			break;
		case 'a':
			desplazarOpción(0,-1);
			break;
		case 's':
			desplazarOpción(1,0);
			break;
		case 'd':
			desplazarOpción(0,1);
			break;
		case ' ':
			ejecutarOpción();
			break;
		default:
			System.out.println("Input no reconocido");
		}
		return false;
	}
	
	/**
	 * Ejecuta el KeyEvent según su código
	 * @return si el menú se ha ocultado
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
			desplazarOpción(-1,0);
			break;
		case KeyEvent.VK_A:
			desplazarOpción(0,-1);
			break;
		case KeyEvent.VK_S:
			desplazarOpción(1,0);
			break;
		case KeyEvent.VK_D:
			desplazarOpción(0,1);
			break;
		case KeyEvent.VK_SPACE:
			ejecutarOpción();
			break;
		default:
			System.out.println("Input no reconocido");
		}
		return false;
	}

	protected abstract void desplazarOpción(int filas, int columnas);

	protected enum Opción {
		CARGAR_SECCIONES("Cargar chunks"),
		GUARDAR("Guardar chunks"),
		ILUMINACIÓN("Activar iluminación"),
		SOMBRAS("Activar sombras"),
		FOTO("Exportar imagen"),
		CARGAR_NIVEL("Cargar nivel"); //TODO campo de visión y otros?
		
		final String nombre;
		
		Opción(String nombre) {
			this.nombre = nombre;
		}
		
		@Override
		public String toString() {
			return nombre;
		}
	}
}
