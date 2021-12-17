package objetos;

import objetos.abstracto.Objeto;

public class ConoDoble extends Objeto {
	private static final long serialVersionUID = 1L;
	
	public ConoDoble(double[] pos, String color) {
		super(pos, color, true);
	}

	@Override
	public boolean colisión(Fotón fotón) {
		double[] punto = Objetos.rotarAlrededor(fotón, this);
		return Math.pow(punto[0] - getPos(0), 2) + Math.pow(punto[2] - getPos(2), 2) <= Math.pow(punto[1] - getPos(1), 2);
	}
}