package objetos;

import objetos.abstracto.Objeto;

public class Elipsoide extends Objeto {
	private static final long serialVersionUID = 1L;
	private double[] semiejes;
	
	public Elipsoide(double[] pos, double[] orient, double[] semiejes, String color) {
		super(pos, color, true);
		setRotaciónYOrient(orient, 0);
		setSemiejes(semiejes);
	}

	@Override
	public boolean colisión(Fotón fotón) {
		double[] punto = Objetos.rotarAlrededor(fotón, this);
		return Math.pow((punto[0] - getPos(0)) / semiejes[0], 2) +
			Math.pow((punto[1] - getPos(1)) / semiejes[1], 2) +
			Math.pow((punto[2] - getPos(2)) / semiejes[2], 2) <= 1;
	}

	public double[] getSemiejes() {
		return semiejes.clone();
	}

	public void setSemiejes(double[] semiejes) {
		this.semiejes = semiejes.clone();
	}
}