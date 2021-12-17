package objetos;

import objetos.abstracto.Objeto;

public class Cubo extends Objeto {
	private static final long serialVersionUID = 1L;
	private double lado;
	
	public Cubo(double[] pos, double lado, String color) {
		super(pos, color, true);
		setLado(lado);
	}
	
	public double getLado() {
		return lado;
	}
	
	public void setLado(double lado) {
		if (lado <= 0) throw new IllegalArgumentException("Lado inválido: " + lado);
		this.lado = lado;
	}
	
	@Override
	public boolean colisión(Fotón fotón) {
		double[] punto = Objetos.rotarAlrededor(fotón, this);
		double mitad_lado = lado/2;
		return punto[0] <= getPos(0) + mitad_lado && punto[0] >= getPos(0) - mitad_lado
			&& punto[1] <= getPos(1) + mitad_lado && punto[1] >= getPos(1) - mitad_lado
			&& punto[2] <= getPos(2) + mitad_lado && punto[2] >= getPos(2) - mitad_lado;
	}
}