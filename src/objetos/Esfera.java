package objetos;

import objetos.abstracto.Objeto;
import operaciones.Dist;

public class Esfera extends Objeto {
	private static final long serialVersionUID = 1L;
	private double radio;
	
	public Esfera(double[] pos, double radio, String color) {
		super(pos, color, true);
		setRadio(radio);
	}
	
	public Esfera(double[] pos, double radio) {
		super(pos, true);
		setRadio(radio);
	}

	public void setRadio(double radio) {
		if (radio < 0) throw new IllegalArgumentException("Radio negativo");
		this.radio = radio;
	}

	@Override
	public boolean colisión(Fotón fotón) {
		return Dist.puntoAPunto(fotón.getPos(), getPos()) <= radio;
	}
}