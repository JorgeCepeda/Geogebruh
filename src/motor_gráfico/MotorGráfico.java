package motor_gr�fico;

import javax.swing.JLabel;

import gr�ficos.Pantalla;
import niveles.Listener;

public interface MotorGr�fico extends Listener {
	
	Pantalla getPantalla();
	JLabel getJLabel();
}
