package es.doncomedia.engine.menus;

import javax.swing.JLabel;

import es.doncomedia.chunks.Chunks;
import es.doncomedia.effects.Lighting;
import es.doncomedia.engine.GFXEngine;
import es.doncomedia.levels.Levels;
import es.doncomedia.operations.MyMath;

public class MainMenu extends Menu {
	private static final Option[][] options = {
		{Option.LIGHTING, Option.SHADES, null, null},
		{Option.LOAD_CHUNKS, Option.SAVE_CHUNKS, Option.LOAD_LEVEL, null},
		{Option.SCREENSHOT, null, null, null}
	};
	private final LevelsMenu submenu;

	public MainMenu(GFXEngine<JLabel> gfxEngine) {
		super(gfxEngine, true);
		textSize = 20;
		submenu = new LevelsMenu(gfxEngine);
	}

	@Override
	protected void updateText() {
		StringBuilder sb = new StringBuilder(400);
		sb.append("<html><pre>");
		for (int i = 0; i < options.length; i++) {
			for (int j = 0; j < options[i].length; j++) {
				if (i == chosenOption[0] && j == chosenOption[1]) sb.append("> ");
				else if (options[i][j] != null) sb.append("- ");
				
				if (options[i][j] != null) sb.append(options[i][j]);
				sb.append("   ");
			}
			sb.append("<br>");
		}
		getText().setText(sb.append("</pre></html>").toString());
	}
	
	@Override
	public synchronized boolean executeInput(char input) {
		if (submenu.isDisplayed()) {
			submenu.executeInput(input);
			return false;
		}
		return super.executeInput(input);
	}
	
	@Override
	public synchronized boolean executeInput(int keyCode) {
		if (submenu.isDisplayed()) {
			submenu.executeInput(keyCode);
			return false;
		}
		return super.executeInput(keyCode);
	}

	@Override
	protected void executeOption() {
		Option option = options[chosenOption[0]][chosenOption[1]];
		if (option != null) {
			System.out.println("Ejecutada opción: " + option.name);
			switch (option) {
			case LOAD_LEVEL:
				submenu.setDisplayed(true);
				break;
			case LOAD_CHUNKS:
				Chunks.load(Levels.loaded().getName() + ".chunks", true);
				break;
			case SAVE_CHUNKS:
				Chunks.save(Levels.loaded().getName() + ".chunks");
				break;
			case LIGHTING:
				Lighting.setLighting(!Lighting.isLightingOn());
				break;
			case SHADES:
				Lighting.setShades(!Lighting.areShadesOn());
				break;
			case SCREENSHOT:
				gfxEngine.getScreen().getFrame(true);
				break;
			default:
				System.out.println("Invalid option: " + option);
			}
		}
	}

	@Override
	protected void moveOption(int rows, int columns) {
		if (MyMath.isInside(chosenOption[0] + rows, chosenOption[1] + columns, options, 0)) {
			chosenOption[0] += rows;
			chosenOption[1] += columns;
			updateText();
		}
	}
}
