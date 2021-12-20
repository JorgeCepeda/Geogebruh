package motor_gráfico;

import javax.swing.JLabel;

import gráficos.Pantalla;
import niveles.Listener;

public interface MotorGráfico extends Listener {
	
	Pantalla getPantalla();
	JLabel getJLabel();
}
