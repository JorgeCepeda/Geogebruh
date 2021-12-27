package es.doncomedia.objects.properties;

public class Border implements Property {
	private static final long serialVersionUID = 1L;
	private Color color;
	private int thickness;
	private char character;

	public Border(String color, int thickness, char character) {
		setColor(new Color(color, false));
		setThickness(thickness);
		setChar(character);
	}

	public Border(Color color, int thickness, char character) {
		setColor(new Color(color));
		setThickness(thickness);
		setChar(character);
	}

	public Border(Border b) {
		color = new Color(b.color);
		thickness = b.thickness;
		character = b.character;
	}

	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	public int getThickness() {
		return thickness;
	}
	
	public void setThickness(int thickness) {
		this.thickness = Math.abs(thickness);
	}

	public char getChar() {
		return character;
	}

	public void setChar(char character) {
		this.character = character;
	}
	
	public static Border defaultBorder() {
		return new Border("white", 1, 'H');
	}
}