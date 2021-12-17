package objetos.propiedades;

public class Borde implements Propiedad {
	private static final long serialVersionUID = 1L;
	private Color color;
	private int grosor;
	private char carác_borde = ' ';

	public Borde(String color, int grosor, char carác_borde) {
		setColor(new Color(color, false));
		setGrosor(grosor);
		setCarác(carác_borde);
	}

	public Borde(Color color, int grosor, char carác_borde) {
		setColor(new Color(color));
		setGrosor(grosor);
		setCarác(carác_borde);
	}

	public Borde(Borde b) {
		color = new Color(b.color);
		grosor = b.grosor;
		carác_borde = b.carác_borde;
	}

	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	public int getGrosor() {
		return grosor;
	}
	
	public void setGrosor(int grosor) {
		this.grosor = Math.abs(grosor);
	}

	public char getCarác() {
		return carác_borde;
	}

	public void setCarác(char carác_borde) {
		this.carác_borde = carác_borde;
	}
	
	public static Borde predeterminado() {
		return new Borde("white", 1, 'H');
	}
}