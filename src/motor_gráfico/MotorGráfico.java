package motor_gr�fico;

import gr�ficos.Pantalla;
import niveles.Listener;

public interface MotorGr�fico<E> extends Listener {
	
	Pantalla getPantalla();
	E getSalida();
}
