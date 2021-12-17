package objetos;

import objetos.abstracto.Objeto;
import operaciones.Dist;

public class Cilindro extends Objeto {
	private static final long serialVersionUID = 1L;
	private double radio, altura;
	private Ref ref;
	
	public Cilindro(double[] pos, double[] orient, double radio, double altura, Ref ref, String color) {
		super(pos, color, true);
		setRotaci�nYOrient(orient, 0);
		setRef(ref);
		setAltura(altura);
		setRadio(radio);
	}
	
	public double getRadio() {
		return radio;
	}
	
	public void setRadio(double radio) {
		this.radio = radio >= 1 ? radio : 1;
	}
	
	public double getAltura() {
		return altura;
	}

	public void setAltura(double altura) {
		this.altura = altura >= 1 ? altura : 1;
	}
	
	public Ref getRef() {
		return ref;
	}

	public void setRef(Ref ref) {
		this.ref = ref;
	}

	@Override
	public boolean colisi�n(Fot�n fot�n) {
		double[] pos_fot�n = fot�n.getPos(), pos = getPos(), orient = getOrient();
		double escalar = Dist.puntoAPlano(pos_fot�n, pos, orient);
		return Dist.puntoARecta(pos_fot�n, pos, orient, escalar) <= radio && rangoV�lido(escalar);
	}
	
	private boolean rangoV�lido(double escalar) {
		switch (ref) {
			case BASE1:
				return escalar >= 0 && escalar <= altura;
			case BASE2:
				return escalar >= -altura && escalar <= 0;
			case CENTRO:
				return escalar >= -altura / 2 && escalar <= altura / 2;
			default:
				return false;
		}
	}
}