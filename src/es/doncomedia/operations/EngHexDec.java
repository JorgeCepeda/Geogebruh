package es.doncomedia.operations;

import java.text.Collator;
import java.util.Arrays;

public class EngHexDec {
	private static final Collator comp = Collator.getInstance();

	static {
		comp.setStrength(Collator.PRIMARY);
	}
	
	private EngHexDec() {
		throw new IllegalStateException("Can't instantiate class");
	}
	
	/**
	 * Vector ordered alphabetically
	 */
	private static final String[] colors = {"AliceBlue", "AntiqueWhite", "Aqua", "Aquamarine", "Azure", "Beige", "Bisque", "Black", "BlanchedAlmond", "Blue", "BlueViolet", "Brown",
			"Burlywood", "CadetBlue", "Chartreuse", "Chocolate", "Coral", "CornflowerBlue", "Cornsilk", "Crimson", "Cyan", "DarkBlue", "DarkCyan", "DarkGoldenrod",
			"DarkGray", "DarkGreen", "DarkKhaki", "DarkMagenta", "DarkOliveGreen", "DarkOrange", "DarkOrchid", "DarkRed", "DarkSalmon", "DarkSeaGreen", "DarkSlateBlue",
			"DarkSlateGray", "DarkTurquoise", "DarkViolet", "DeepPink", "DeepSkyBlue", "DimGray", "DodgerBlue", "Firebrick", "FloralWhite", "ForestGreen", "Fuchsia",
			"Gainsboro", "GhostWhite", "Gold", "Goldenrod", "Gray", "Green", "GreenYellow", "Honeydew", "HotPink", "IndianRed", "Indigo", "Ivory", "Khaki", "Lavender",
			"LavenderBlush", "LawnGreen", "LemonChiffon", "LightBlue", "LightCoral", "LightCyan", "LightGoldenrodYellow", "LightGray", "LightGreen", "LightPink", "LightSalmon",
			"LightSeaGreen", "LightSkyBlue", "LightSlateGray", "LightSteelBlue", "LightYellow", "Lime", "LimeGreen", "Linen", "Magenta", "Maroon", "MediumAquamarine",
			"MediumBlue", "MediumOrchid", "MediumPurple", "MediumSeaGreen", "MediumSlateBlue", "MediumSpringGreen", "MediumTurquoise", "MediumVioletRed", "MidnightBlue",
			"MintCream", "MistyRose", "Moccasin", "NavajoWhite", "Navy", "OldLace", "Olive", "OliveDrab", "Orange", "OrangeRed", "Orchid", "PaleGoldenrod", "PaleGreen",
			"PaleTurquoise", "PaleVioletRed", "PapayaWhip", "PeachPuff", "Peru", "Pink", "Plum", "PowderBlue", "Purple", "RebeccaPurple", "Red", "RosyBrown", "RoyalBlue",
			"SaddleBrown", "Salmon", "SandyBrown", "SeaGreen", "Seashell", "Sienna", "Silver", "SkyBlue", "SlateBlue", "SlateGray", "Snow", "SpringGreen", "SteelBlue", "Tan",
			"Teal", "Thistle", "Tomato", "Turquoise", "Violet", "Wheat", "White", "WhiteSmoke", "Yellow", "YellowGreen"};
	
	private static final String[] hex = {"F0F8FF", "FAEBD7", "00FFFF", "7FFFD4", "F0FFFF", "F5F5DC", "FFE4C4", "000000", "FFEBCD", "0000FF", "8A2BE2", "A52A2A", "DEB887", "5F9EA0",
			"7FFF00", "D2691E", "FF7F50", "6495ED", "FFF8DC", "DC143C", "00FFFF", "00008B", "008B8B", "B8860B", "A9A9A9", "006400", "BDB76B", "8B008B", "556B2F", "FF8C00",
			"9932CC", "8B0000", "E9967A", "8FBC8F", "483D8B", "2F4F4F", "00CED1", "9400D3", "FF1493", "00BFFF", "696969", "1E90FF", "B22222", "FFFAF0", "228B22", "FF00FF",
			"DCDCDC", "F8F8FF", "FFD700", "DAA520", "808080", "008000", "ADFF2F", "F0FFF0", "FF69B4", "CD5C5C", "4B0082", "FFFFF0", "F0E68C", "E6E6FA", "FFF0F5", "7CFC00",
			"FFFACD", "ADD8E6", "F08080", "E0FFFF", "FAFAD2", "D3D3D3", "90EE90", "FFB6C1", "FFA07A", "20B2AA", "87CEFA", "778899", "B0C4DE", "FFFFE0", "00FF00", "32CD32",
			"FAF0E6", "FF00FF", "800000", "66CDAA", "0000CD", "BA55D3", "9370DB", "3CB371", "7B68EE", "00FA9A", "48D1CC", "C71585", "191970", "F5FFFA", "FFE4E1", "FFE4B5",
			"FFDEAD", "000080", "FDF5E6", "808000", "6B8E23", "FFA500", "FF4500", "DA70D6", "EEE8AA", "98FB98", "AFEEEE", "DB7093", "FFEFD5", "FFDAB9", "CD853F", "FFC0CB",
			"DDA0DD", "B0E0E6", "800080", "663399", "FF0000", "BC8F8F", "4169E1", "8B4513", "FA8072", "F4A460", "2E8B57", "FFF5EE", "A0522D", "C0C0C0", "87CEEB", "6A5ACD",
			"708090", "FFFAFA", "00FF7F", "4682B4", "D2B48C", "008080", "D8BFD8", "FF6347", "40E0D0", "EE82EE", "F5DEB3", "FFFFFF", "F5F5F5", "FFFF00", "9ACD32"};
	
	public static String rgbToHex6(int[] rgb) {
		String hexadecimal = "#";
		int[] colorDec = rgb.clone();
		for (int i = 0; i < colorDec.length; i++) {
			int count = 0;
			int[] remainders = new int[2];
			do {
				remainders[count] = colorDec[i] % 16;
				colorDec[i] /= 16;
				count++;
			} while (colorDec[i] / 16 >= 1);
			remainders[count] = colorDec[i] % 16;
			
			for (int j = remainders.length - 1; j >= 0; j--) {
				char character;
				switch (remainders[j]) {
					case 15:
						character = 'F';
						break;
					case 14:
						character = 'E';
						break;
					case 13:
						character = 'D';
						break;
					case 12:
						character = 'C';
						break;
					case 11:
						character = 'B';
						break;
					case 10:
						character = 'A';
						break;
					default:
						if (remainders[j] < 0) throw new IllegalArgumentException("Invalid RGB: " + Arrays.toString(rgb));
						character = (char) (remainders[j] + 48);
				}
				hexadecimal += character;
			}
		}
		return hexadecimal;
	}
	
	public static String hex3ToHex6(String hex3) {
		if (hex3.charAt(0) == '#' && hex3.length() == 4) return "#" + hex3.charAt(1) + hex3.charAt(1) + hex3.charAt(2) + hex3.charAt(2) + hex3.charAt(3) + hex3.charAt(3);
		throw new IllegalArgumentException("Invalid hexadecimal: " + hex3);
	}
	
	public static int[] hex3ToRGB(String hexadecimal) {
		return hex6ToRGB(hex3ToHex6(hexadecimal));
	}
	
	public static int[] hex6ToRGB(String hexadecimal) {
		if (hexadecimal.charAt(0) != '#') hexadecimal = '#' + hexadecimal;
		if (hexadecimal.length() != 7) throw new IllegalArgumentException("Invalid hexadecimal: " + hexadecimal);
		
		int[] decimal = new int[3];
		for (int i = 0; i < decimal.length; i++) {
			int dec = 0;
			for (int j = 2 * i + 1, exp = 1; j <= 2 * i + 2; j++, exp--) {
				switch(hexadecimal.toUpperCase().charAt(j)) {
					case 'F':
						dec += Math.pow(16, exp) * 15;
						break;
					case 'E':
						dec += Math.pow(16, exp) * 14;
						break;
					case 'D':
						dec += Math.pow(16, exp) * 13;
						break;
					case 'C':
						dec += Math.pow(16, exp) * 12;
						break;
					case 'B':
						dec += Math.pow(16, exp) * 11;
						break;
					case 'A':
						dec += Math.pow(16, exp) * 10;
						break;
					default:
						dec += Math.pow(16, exp) * (hexadecimal.charAt(j) - 48);
				}
			}
			decimal[i] = dec;
		}
		return decimal;
	}
	
	public static String engToHex6(String eng) {
		int left = 0, right = colors.length - 1, swPos = -1;
		while (left <= right) {
			int center = (left + right) / 2, res = comp.compare(colors[center], eng);
			
			if (res == 0) {
				swPos = center;
				break;
			}
			if (res < 0) left = center + 1;
			else right = center - 1;
		}
		if (swPos < 0) throw new IllegalArgumentException("Color not found: " + eng);
		return "#" + hex[swPos]; 
	}
	
	public static String getHex6(String color) {
		if (color.charAt(0) == '#') {
			if (color.length() == 7) return color;
			if (color.length() == 4) return hex3ToHex6(color);
		}
		return engToHex6(color);
	}
	
	public static int[] getRGB(String color) {
		if (color.charAt(0) == '#') {
			if (color.length() == 7) return hex6ToRGB(color);
			if (color.length() == 4) return hex3ToRGB(color);
		}
		return hex6ToRGB(engToHex6(color));
	}
}