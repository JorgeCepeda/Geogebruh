package objetos.propiedades;

import java.util.Arrays;

import operaciones.EngHexDec;

public class Color implements Propiedad {
	private static final long serialVersionUID = 1L;
	private int[] rgb;
	private String texto;
	
	public Color(String color, boolean cambiarRGB) {
		setColor(color, cambiarRGB);
	}

	public Color(int[] rgb, boolean cambiarString) {
		setColor(rgb, cambiarString);
	}

	public Color(Color color) {
		if (color.rgb != null) rgb = color.rgb.clone();
		texto = color.texto;
	}

	public int[] getRGB() {
		return rgb.clone();
	}

	public int getRGB(int componente) {
		return rgb[componente];
	}
	
	public String getString() {
		return texto;
	}
	
	public synchronized void actualizarString() {
		texto = EngHexDec.rgbToHex6(rgb);
	}
	
	public synchronized void actualizarRGB() {
		rgb = EngHexDec.getRGB(texto);
	}

	public synchronized void setColor(String color, boolean cambiarRGB) {
		texto = EngHexDec.getHex6(color);
		if (cambiarRGB) actualizarRGB();
	}
	
	public synchronized void setColor(int[] rgb, boolean cambiarString) {
		if (esVálido(rgb)) this.rgb = rgb.clone();
		if (cambiarString) actualizarString();
	}
	
	public static boolean esVálido(int[] rgb) {
		if (rgb.length != 3) throw new IllegalArgumentException("Vector inválido");
		for (int i : rgb) {
			if (i < 0 || i > 255) return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Color [rgb=" + Arrays.toString(rgb) + ", texto=" + texto + "]";
	}
}