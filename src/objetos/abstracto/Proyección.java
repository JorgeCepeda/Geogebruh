package objetos.abstracto;

public abstract class Proyecci�n extends ObjetoBase {
	private static final long serialVersionUID = 1L;
	
	protected Proyecci�n(double[] pos) {
		super(pos);
	}

	protected Proyecci�n() {}

	public abstract boolean colisi�n();
}