package es.doncomedia.engine.menus;

import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;

import es.doncomedia.engine.GFXEngine;
import es.doncomedia.levels.Levels;
import es.doncomedia.levels.Levels.Level;

public class LevelsMenu extends Menu {
	private static final ArrayList<Level> levels = new ArrayList<>();
	private static final ArrayList<String> tips = new ArrayList<>();
	private final Random rnd = new Random();
	
	static {
		levels.add(Levels.LEVEL_1);
		levels.add(Levels.LEVEL_2);
		levels.add(Levels.LEVEL_3);
		levels.add(Levels.LEVEL_4);
		levels.add(Levels.LEVEL_5);
		levels.add(Levels.LEVEL_6);
		levels.add(Levels.LEVEL_7);
		levels.add(Levels.LEVEL_8);
		levels.add(Levels.SPAIN_CUBE);
		levels.add(Levels.STARRY_NIGHT);
	}
	
	static {
		tips.add("Puedes hacer EJERCICIO para mantenerte EN FORMA");
		tips.add("Algo peor que las oraciones incompletas es");
		tips.add("Si te quedas sin memoria, compra más");
		tips.add("Baterías no incluidas");
		tips.add("El Konami Code no está implementado");
		tips.add("Esto es demasiado difícil para periodistas");
		tips.add("test7");
		tips.add("No, es verdad, me habías dicho que no lo sabías");
		tips.add("Shakespeare una vez dijo: \"An SSL error has occurred and a secure connection to the server cannot be made\"");
	}
	
	public LevelsMenu(GFXEngine<JLabel> gfxEngine) {
		super(gfxEngine, false);
		textSize = 20;
	}

	@Override
	protected void updateText() {
		int size = levels.size();
		StringBuilder sb = new StringBuilder(400);
		sb.append("<html>");
		for (int i = 0; i <= size; i++) {
			if (i == chosenOption[0]) sb.append("> ");
			else sb.append("- ");
			
			if (i < size) sb.append(levels.get(i).getName());
			else sb.append("Volver");
			sb.append("<br>");
		}
		getText().setText(sb.append("</html>").toString());
	}

	@Override
	protected void executeOption() {
		if (chosenOption[0] == levels.size()) setDisplayed(false);
		else {
			Level level = levels.get(chosenOption[0]);
			StringBuilder sb = new StringBuilder(150);
			sb.append("Cargando nivel: ").append(level.getName());
			sb.append(".\n¿Sabías que...? ");
			
			sb.append(tips.get(rnd.nextInt(tips.size())));
			System.out.println(sb.toString());
			Levels.load(level);
		}
	}

	@Override
	protected void moveOption(int rows, int columns) {
		int newOption = chosenOption[0] + rows;
		if (newOption >= 0 && newOption <= levels.size()) {
			chosenOption[0] += rows;
			updateText();
		}
	}
}