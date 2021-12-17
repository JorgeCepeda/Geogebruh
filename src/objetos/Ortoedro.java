package objetos;

import objetos.abstracto.Objeto;

public class Ortoedro extends Objeto {
	private static final long serialVersionUID = 1L;
	private double largo_x, alto, largo_z;
	
	public Ortoedro(double[] pos, double largo_x, double alto, double largo_z, String color) {
		super(pos, color, true);
		setDimensiones(largo_x, alto, largo_z);
	}
	
	public Ortoedro(double[] pos, String color) {
		super(pos, color, true);
	}
	
	public double[] getDimensiones() {
		return new double[] {largo_x, alto, largo_z};
	}

	public void setDimensiones(double largo_x, double alto, double largo_z) {
		if (largo_x <= 0 || alto <= 0 || largo_z <= 0) throw new IllegalArgumentException("Dimensiones inválidas");
		this.largo_x = largo_x;
		this.alto = alto;
		this.largo_z = largo_z;
	}

	@Override
	public boolean colisión(Fotón fotón) {
		double[] punto = Objetos.rotarAlrededor(fotón, this);
		return punto[0] <= getPos(0) + largo_x/2 && punto[0] >= getPos(0) - largo_x/2
			&& punto[1] <= getPos(1) + alto/2 && punto[1] >= getPos(1) - alto/2
			&& punto[2] <= getPos(2) + largo_z/2 && punto[2] >= getPos(2) - largo_z/2;
	}
}