package objetos.abstracto;

import objetos.Fotón;
import objetos.propiedades.Borde;
import objetos.propiedades.Color;
import otros.Contador;

public abstract class Objeto extends ObjetoBase { // El código de los objetos no se puede guardar en un archivo fácilmente, a no ser que exista como objeto en el código del programa. Ej: base rectangular / propiedades objcomp
	public enum Ref {CENTRO,BASE1,BASE2}

	public static final String TEXTURAS = "texture_map";
	private static final long serialVersionUID = 1L;
	private int cont = -1;

	protected Objeto(double[] pos, String color, boolean borde) {
		super(pos);
		cambiarPropiedad(COLOR, new Color(color, true));
		if (borde) cambiarPropiedad(BORDE, Borde.predeterminado());
		numerar();
	}
	
	protected Objeto(double[] pos, boolean borde) {
		super(pos);
		if (borde) cambiarPropiedad(BORDE, Borde.predeterminado());
		numerar();
	}
	
	protected Objeto() {
		numerar();
	}

	public int getCont() {
		return cont;
	}
	
	public void setCont(int cont) {
		this.cont = cont;
	}
	
	public void numerar() {
		Contador.listar(this);
	}

	public abstract boolean colisión(Fotón fotón);

	@Override
	public String toString() {
		return getClass().getSimpleName() + cont;
	}
}