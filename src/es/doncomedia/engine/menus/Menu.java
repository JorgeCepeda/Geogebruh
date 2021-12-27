package es.doncomedia.engine.menus;

import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;

import es.doncomedia.engine.GFXEngine;

public abstract class Menu {
	protected final GFXEngine<JLabel> gfxEngine;
	private final boolean mShortcut;
	protected Font fontBackup;
	protected String textBackup;
	protected boolean displayed;
	protected int textSize;
	protected int[] chosenOption = new int[2];
	
	protected Menu(GFXEngine<JLabel> gfxEngine, boolean mShortcut) {
		if (gfxEngine == null) throw new IllegalArgumentException("Graphics engine can't be null");
		this.gfxEngine = gfxEngine;
		this.mShortcut = mShortcut;
	}
	
	public JLabel getText() {
		return gfxEngine.getOutput();
	}
	
	public boolean isDisplayed() {
		return displayed;
	}
	
	public void setDisplayed(boolean displayed) {
		if (this.displayed = displayed) display();
		else resetText();
	}
	
	private void display() {
		chosenOption = new int[2];
		fontBackup = getText().getFont();
		textBackup = getText().getText();
		getText().setFont(new Font(fontBackup.getName(), fontBackup.getStyle(), textSize));
		updateText();
	}
	
	private void resetText() {
		getText().setFont(new Font(fontBackup.getName(), fontBackup.getStyle(), fontBackup.getSize()));
		getText().setText(textBackup);
	}

	protected abstract void updateText();

	protected abstract void executeOption();

	/**
	 * Executes the input
	 * @return whether the menu has been hidden
	 */
	public synchronized boolean executeInput(char input) {
		switch (input) {
		case 'm':
			if (mShortcut) {
				setDisplayed(!displayed);
				if (!displayed) return true;
			}
			break;
		case 'w':
			moveOption(-1,0);
			break;
		case 'a':
			moveOption(0,-1);
			break;
		case 's':
			moveOption(1,0);
			break;
		case 'd':
			moveOption(0,1);
			break;
		case ' ':
			executeOption();
			break;
		default:
			System.out.println("Input no reconocido");
		}
		return false;
	}
	
	/**
	 * Executes the KeyEvent based on its code
	 * @return whether the menu has been hidden
	 */
	public synchronized boolean executeInput(int keyCode) {
		switch (keyCode) {
		case KeyEvent.VK_M:
			if (mShortcut) {
				setDisplayed(!displayed);
				if (!displayed) return true;
			}
			break;
		case KeyEvent.VK_W:
			moveOption(-1,0);
			break;
		case KeyEvent.VK_A:
			moveOption(0,-1);
			break;
		case KeyEvent.VK_S:
			moveOption(1,0);
			break;
		case KeyEvent.VK_D:
			moveOption(0,1);
			break;
		case KeyEvent.VK_SPACE:
			executeOption();
			break;
		default:
			System.out.println("Input no reconocido");
		}
		return false;
	}

	protected abstract void moveOption(int rows, int columns);

	protected enum Option {
		LOAD_CHUNKS("Cargar chunks"),
		SAVE_CHUNKS("Guardar chunks"),
		LIGHTING("Activar iluminación"),
		SHADES("Activar sombras"),
		SCREENSHOT("Exportar imagen"),
		LOAD_LEVEL("Cargar nivel"); //TODO field of view and others?
		
		final String name;
		
		Option(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}
