package es.doncomedia.engine;

import es.doncomedia.graphics.Screen;
import es.doncomedia.levels.Listener;

public interface GFXEngine<E> extends Listener {
	
	Screen getScreen();
	E getOutput();
}
