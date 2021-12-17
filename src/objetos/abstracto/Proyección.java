package objetos.abstracto;

public abstract class Proyección extends ObjetoBase {
	private static final long serialVersionUID = 1L;
	
	protected Proyección(double[] pos) {
		super(pos);
	}

	protected Proyección() {}

	public abstract boolean colisión();
}