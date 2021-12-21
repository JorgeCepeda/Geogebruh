package motor_gráfico;

import gráficos.Pantalla;
import niveles.Listener;

public interface MotorGráfico<E> extends Listener {
	
	Pantalla getPantalla();
	E getSalida();
}
