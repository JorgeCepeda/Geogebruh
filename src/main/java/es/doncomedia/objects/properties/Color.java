package es.doncomedia.objects.properties;

import java.util.Arrays;

import es.doncomedia.operations.EngHexDec;

public class Color implements Property {
	private static final long serialVersionUID = 1L;
	private int[] rgb;
	private String text;

	public Color(String color, boolean changeRGB) {
		setColor(color, changeRGB);
	}

	public Color(int[] rgb, boolean changeString) {
		setColor(rgb, changeString);
	}

	public Color(Color color) {
		if (color.rgb != null) rgb = color.rgb.clone();
		text = color.text;
	}

	public int[] getRGB() {
		return rgb.clone();
	}

	public int getRGB(int component) {
		return rgb[component];
	}

	public String getString() {
		return text;
	}

	public synchronized void updateString() {
		text = EngHexDec.rgbToHex6(rgb);
	}

	public synchronized void updateRGB() {
		rgb = EngHexDec.getRGB(text);
	}

	public synchronized void setColor(String color, boolean changeRGB) {
		text = EngHexDec.getHex6(color);
		if (changeRGB) updateRGB();
	}

	public synchronized void setColor(int[] rgb, boolean changeString) {
		if (isValid(rgb)) this.rgb = rgb.clone();
		if (changeString) updateString();
	}

	public static boolean isValid(int[] rgb) {
		if (rgb.length != 3) throw new IllegalArgumentException("Invalid vector");
		for (int i : rgb) {
			if (i < 0 || i > 255) return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Color [rgb=" + Arrays.toString(rgb) + ", text=" + text + "]";
	}
}