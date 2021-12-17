package objetos.propiedades;

public class Borde implements Propiedad {
	private static final long serialVersionUID = 1L;
	private Color color;
	private int grosor;
	private char car�c_borde = ' ';

	public Borde(String color, int grosor, char car�c_borde) {
		setColor(new Color(color, false));
		setGrosor(grosor);
		setCar�c(car�c_borde);
	}

	public Borde(Color color, int grosor, char car�c_borde) {
		setColor(new Color(color));
		setGrosor(grosor);
		setCar�c(car�c_borde);
	}

	public Borde(Borde b) {
		color = new Color(b.color);
		grosor = b.grosor;
		car�c_borde = b.car�c_borde;
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

	public char getCar�c() {
		return car�c_borde;
	}

	public void setCar�c(char car�c_borde) {
		this.car�c_borde = car�c_borde;
	}
	
	public static Borde predeterminado() {
		return new Borde("white", 1, 'H');
	}
}