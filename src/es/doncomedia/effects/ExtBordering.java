package es.doncomedia.effects;

import es.doncomedia.graphics.Screen;
import es.doncomedia.objects.Objects;
import es.doncomedia.objects.abstracts.BaseObject;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.objects.properties.Border;
import es.doncomedia.operations.MyMath;

public class ExtBordering {
	private double[][] borderDistTable;
	private boolean[][] extBorders;
	private Screen s;
	
	public ExtBordering(Screen s) {
		this.s = s;
	}
	
	public synchronized boolean[][] drawBorde() {
		extBorders = new boolean[s.charTable().length][s.charTable()[0].length];
		borderDistTable = new double[s.charTable().length][s.charTable()[0].length];
		for (int i = s.charTable().length-1; i >= 0; i--) { // Rows
			for (int j = s.charTable()[0].length-1; j >= 0; j--) { // Columns
				if (s.objTable()[i][j] != null) borderSurroundings(i, j);
			}
		}
		return extBorders;
	}

	/**
	 * Check adjacent cells and border them
	 * @param i - row
	 * @param j - column
	 */
	private void borderSurroundings(int i, int j) {
		GameObject obj = Objects.concreteObj(s.objTable()[i][j], s.posTable()[i][j]);
		Border border = obj.property(BaseObject.BORDER);
		if (border != null) {
			int thickness = border.getThickness();
			for (int k = -thickness; k <= thickness; k++) { // Rows
				for (int l = -thickness; l <= thickness; l++) { // Columns
					if (hasBorder(i, j, k, l, thickness, obj)) {
						int row = i+k, col = j+l;
						
						s.colorTable()[row][col] = border.getColor().getString();
						s.charTable()[row][col] = border.getChar();
						borderDistTable[row][col] = s.distTable()[i][j]; // Border distance may not be precise because of how it traverses the table
						extBorders[row][col] = true;
					}
				}
			}
		}
	}

	/**
	 * Border depends on current checked cell's object and how far it is compared to main cell's object
	 */
	private boolean hasBorder(int i, int j, int k, int l, int thickness, GameObject obj) {
		int row = i+k, col = j+l;
		boolean validObj = s.objTable()[row][col] == null
			|| Objects.concreteObj(s.objTable()[row][col], s.posTable()[row][col]) != obj && s.distTable()[row][col] > s.distTable()[i][j];
			
		boolean validCell = (k != 0 || l != 0) && MyMath.isInside(row, col, s.colorTable(), s.extra());
		
		return validCell && validObj && k*k+l*l <= thickness*thickness &&
			(!extBorders[row][col] || borderDistTable[row][col] > s.distTable()[i][j]);
	}
}