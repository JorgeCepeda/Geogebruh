package motor_gráfico.menús;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;

import motor_gráfico.MotorGráfico;
import niveles.Niveles;
import niveles.Niveles.Nivel;

public class MenúNiveles extends Menú {
	private static final ArrayList<Nivel> niveles = new ArrayList<>();
	private static final ArrayList<String> consejos = new ArrayList<>();
	private final Random rnd = new Random();
	
	static {
		niveles.add(Niveles.NIVEL_CÓDIGO_1);
		niveles.add(Niveles.NIVEL_CÓDIGO_2);
		niveles.add(Niveles.NIVEL_CÓDIGO_3);
		niveles.add(Niveles.NIVEL_CÓDIGO_4);
		niveles.add(Niveles.NIVEL_CÓDIGO_5);
		niveles.add(Niveles.NIVEL_CÓDIGO_6);
		niveles.add(Niveles.NIVEL_CÓDIGO_7);
		niveles.add(Niveles.NIVEL_CÓDIGO_8);
		niveles.add(Niveles.NIVEL_CUBO_ESPAÑA);
		niveles.add(Niveles.NOCHE_ESTRELLADA);
	}
	
	static {
		consejos.add("Puedes hacer EJERCICIO para mantenerte EN FORMA");
		consejos.add("Algo peor que las oraciones incompletas es");
		consejos.add("Si te quedas sin memoria, compra más");
		consejos.add("Baterías no incluidas");
		consejos.add("El Konami Code no está implementado");
		consejos.add("Esto es demasiado difícil para periodistas");
		consejos.add("test7");
		consejos.add("No, es verdad, me habías dicho que no lo sabías");
		consejos.add("Shakespeare una vez dijo: \"An SSL error has occurred and a secure connection to the server cannot be made\"");
	}
	
	public MenúNiveles(MotorGráfico<JLabel> motor_gráfico) {
		super(motor_gráfico, false);
		tamaño_texto = 20;
	}

	@Override
	protected void updateTexto() {
		int tamaño = niveles.size();
		StringBuilder sb = new StringBuilder(400);
		sb.append("<html>");
		for (int i = 0; i <= tamaño; i++) {
			if (i == opción_actual[0]) sb.append("> ");
			else sb.append("- ");
			
			if (i < tamaño) sb.append(niveles.get(i).getNombre());
			else sb.append("Volver");
			sb.append("<br>");
		}
		getTexto().setText(sb.append("</html>").toString());
	}

	@Override
	protected void ejecutarOpción() {
		if (opción_actual[0] == niveles.size()) setMostrado(false);
		else {
			Nivel nivel = niveles.get(opción_actual[0]);
			StringBuilder sb = new StringBuilder(150);
			sb.append("Cargando nivel: ").append(nivel.getNombre());
			sb.append(".\n¿Sabías que...? ");
			
			sb.append(consejos.get(rnd.nextInt(consejos.size())));
			System.out.println(sb.toString());
			Niveles.cargar(nivel);
		}
	}

	@Override
	protected void desplazarOpción(int filas, int columnas) {
		int nueva_opción = opción_actual[0] + filas;
		if (nueva_opción >= 0 && nueva_opción <= niveles.size()) {
			opción_actual[0] += filas;
			updateTexto();
		}
	}
}
