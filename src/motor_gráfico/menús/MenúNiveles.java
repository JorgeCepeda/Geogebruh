package motor_gr�fico.men�s;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;

import motor_gr�fico.MotorGr�fico;
import niveles.Niveles;
import niveles.Niveles.Nivel;

public class Men�Niveles extends Men� {
	private static final ArrayList<Nivel> niveles = new ArrayList<>();
	private static final ArrayList<String> consejos = new ArrayList<>();
	private final Random rnd = new Random();
	
	static {
		niveles.add(Niveles.NIVEL_C�DIGO_1);
		niveles.add(Niveles.NIVEL_C�DIGO_2);
		niveles.add(Niveles.NIVEL_C�DIGO_3);
		niveles.add(Niveles.NIVEL_C�DIGO_4);
		niveles.add(Niveles.NIVEL_C�DIGO_5);
		niveles.add(Niveles.NIVEL_C�DIGO_6);
		niveles.add(Niveles.NIVEL_C�DIGO_7);
		niveles.add(Niveles.NIVEL_C�DIGO_8);
		niveles.add(Niveles.NIVEL_CUBO_ESPA�A);
		niveles.add(Niveles.NOCHE_ESTRELLADA);
	}
	
	static {
		consejos.add("Puedes hacer EJERCICIO para mantenerte EN FORMA");
		consejos.add("Algo peor que las oraciones incompletas es");
		consejos.add("Si te quedas sin memoria, compra m�s");
		consejos.add("Bater�as no incluidas");
		consejos.add("El Konami Code no est� implementado");
		consejos.add("Esto es demasiado dif�cil para periodistas");
		consejos.add("test7");
		consejos.add("No, es verdad, me hab�as dicho que no lo sab�as");
		consejos.add("Shakespeare una vez dijo: \"An SSL error has occurred and a secure connection to the server cannot be made\"");
	}
	
	public Men�Niveles(MotorGr�fico<JLabel> motor_gr�fico) {
		super(motor_gr�fico, false);
		tama�o_texto = 20;
	}

	@Override
	protected void updateTexto() {
		int tama�o = niveles.size();
		StringBuilder sb = new StringBuilder(400);
		sb.append("<html>");
		for (int i = 0; i <= tama�o; i++) {
			if (i == opci�n_actual[0]) sb.append("> ");
			else sb.append("- ");
			
			if (i < tama�o) sb.append(niveles.get(i).getNombre());
			else sb.append("Volver");
			sb.append("<br>");
		}
		getTexto().setText(sb.append("</html>").toString());
	}

	@Override
	protected void ejecutarOpci�n() {
		if (opci�n_actual[0] == niveles.size()) setMostrado(false);
		else {
			Nivel nivel = niveles.get(opci�n_actual[0]);
			StringBuilder sb = new StringBuilder(150);
			sb.append("Cargando nivel: ").append(nivel.getNombre());
			sb.append(".\n�Sab�as que...? ");
			
			sb.append(consejos.get(rnd.nextInt(consejos.size())));
			System.out.println(sb.toString());
			Niveles.cargar(nivel);
		}
	}

	@Override
	protected void desplazarOpci�n(int filas, int columnas) {
		int nueva_opci�n = opci�n_actual[0] + filas;
		if (nueva_opci�n >= 0 && nueva_opci�n <= niveles.size()) {
			opci�n_actual[0] += filas;
			updateTexto();
		}
	}
}
