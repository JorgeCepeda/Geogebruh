package es.doncomedia.graphics;

import es.doncomedia.chunks.Chunks;
import es.doncomedia.objects.Objects;
import es.doncomedia.objects.abstracts.CompoundObject;
import es.doncomedia.objects.abstracts.GameObject;

public class GUI {
	
	private GUI() {
		throw new IllegalStateException("Can't instantiate class");
	}
	
	public static char[] modifyRow(char[] rowChars, String data, int alignment) {
		char[] newChars = rowChars.clone();
		switch (alignment) {
			case 1:
				for (int i = 0; i < data.length(); i++) {
					newChars[i] = data.charAt(i);
				}
				break;
			case -1:
				for (int i = 0; i < data.length(); i++) {
					newChars[newChars.length - data.length() + i] = data.charAt(i);
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid alignment");
		}
		return newChars;
	}
	
	public static Object[] modifyRowAndColor(Screen s, int row, String data, String newColor, int alignment) {
		char[] newChars = s.charTable()[row].clone();
		String[] newColors = s.colorTable()[row].clone();
		switch (alignment) {
			case 1:
				for (int i = 0; i < data.length(); i++) {
					newChars[i + s.extra()] = data.charAt(i);
					newColors[i + s.extra()] = newColor;
				}
				break;
			case -1:
				for (int i = 0; i < data.length(); i++) {
					newChars[newChars.length - s.extra() - data.length() + i] = data.charAt(i);
					newColors[newChars.length - s.extra() - data.length() + i] = newColor;
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid alignment");
		}
		return new Object[] {newChars, newColors};
	}
	
	public static String[] modifyColor(String[] row, String newColor, int startIndex, int endIndex) {
		if (endIndex < startIndex) throw new IllegalArgumentException("End index can't be less than start index");
		String[] newColors = row.clone();
		for (int i = startIndex; i <= endIndex; i++) {
			newColors[i] = newColor;
		}
		return newColors;
	}
	
	public static void cursor(Screen s) { // Adapts to different dimensions
		s.colorTable()[s.colorTable().length/2][s.colorTable()[0].length/2] = "#FF0000";
		s.charTable()[s.colorTable().length/2][s.colorTable()[0].length/2] = 'o';
		
		boolean evenWidth = false;
		if (s.colorTable()[0].length % 2 == 0) {
			s.colorTable()[s.colorTable().length/2][s.colorTable()[0].length/2 - 1] = "#FF0000";
			s.charTable()[s.colorTable().length/2][s.colorTable()[0].length/2 - 1] = 'o';
			evenWidth = true;
		}
		if (s.colorTable().length % 2 == 0) {
			s.colorTable()[s.colorTable().length/2 - 1][s.colorTable()[0].length/2] = "#FF0000";
			s.charTable()[s.colorTable().length/2 - 1][s.colorTable()[0].length/2] = 'o';
			
			if (evenWidth) {
				s.colorTable()[s.colorTable().length/2 - 1][s.colorTable()[0].length/2 - 1] = "#FF0000";
				s.charTable()[s.colorTable().length/2 - 1][s.colorTable()[0].length/2 - 1] = 'o';
			}
		}
	}
	
	public static void aimedObject(Screen s) {
		GameObject obj = s.objTable()[s.objTable().length/2][s.objTable()[0].length/2];
		if (obj != null) {
			String text = obj.toString();
			if (s.posTable() != null && obj instanceof CompoundObject) text += " - " + Objects.concreteObj(obj, s.posTable()[s.posTable().length/2][s.posTable()[0].length/2]);
			Object[] data = modifyRowAndColor(s, s.extra(), text, "#FFFFFF", 1);
			s.charTable()[s.extra()] = (char[]) data[0];
			s.colorTable()[s.extra()] = (String[]) data[1];
		}
	}
	
	public static void orientation(Screen s) {
		Object[] data = modifyRowAndColor(s, s.extra(), String.format("Orient: {%.3f,%.3f,%.3f}", s.camOrient(0), s.camOrient(1), s.camOrient(2)), "#FFFFFF", -1);
		s.charTable()[s.extra()] = (char[]) data[0];
		s.colorTable()[s.extra()] = (String[]) data[1];
	}
	
	public static void loadedChunks(Screen s) {
		Object[] data = modifyRowAndColor(s, s.extra()+1, "Chunks: " + Chunks.getChunks().size(), "#FFFFFF", 1);
		s.charTable()[s.extra()+1] = (char[]) data[0];
		s.colorTable()[s.extra()+1] = (String[]) data[1];
	}
}