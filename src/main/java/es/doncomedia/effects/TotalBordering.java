package es.doncomedia.effects;

import es.doncomedia.graphics.Screen;
import es.doncomedia.objects.Objects;
import es.doncomedia.objects.abstracts.BaseObject;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.objects.properties.Border;
import es.doncomedia.operations.MyMath;

public class TotalBordering {
	private static final double totalMargin = 2, crossMargin = 1, lineMargin = 1; /*Made up margin values*/
	
	private TotalBordering() {
		throw new IllegalStateException("Can't instantiate class");
	}
	
	public static void drawBorde(Screen s) {
		ExtBordering bExt = new ExtBordering(s);
		drawBorde(s, bExt.drawBorde());
	}
	
	public static void drawBorde(Screen s, boolean[][] extBorders) {
		for (int i = s.extra(); i < s.colorTable().length - s.extra(); i++) { // Rows
			for (int j = s.extra(); j < s.colorTable()[0].length - s.extra(); j++) { // Columns
				if (!extBorders[i][j]) {
					GameObject obj = Objects.concreteObj(s.objTable()[i][j], s.posTable()[i][j]);
					Border border;
					if (obj != null && (border = obj.property(BaseObject.BORDER)) != null && hasBorder(i, j, s)) {
						s.colorTable()[i][j] = border.getColor().getString();
						s.charTable()[i][j] = border.getChar();
					}
				}
			}
		}
	}

	private static boolean hasBorder(int i, int j, Screen s) {
		// Laplace edge detection
		boolean equalVerticals = MyMath.areEqual(s.objTable()[i-1][j], s.objTable()[i][j], s.objTable()[i+1][j]),
			equalHorizontals = MyMath.areEqual(s.objTable()[i][j-1], s.objTable()[i][j], s.objTable()[i][j+1]);
		
		if (equalVerticals && equalHorizontals) {
			if (s.camera().isPrecise() && MyMath.areEqual(s.objTable()[i+1][j+1], s.objTable()[i-1][j+1], s.objTable()[i+1][j-1], s.objTable()[i-1][j-1], s.objTable()[i][j])) {
				return Math.abs(s.distTable()[i+1][j+1] + s.distTable()[i+1][j-1] + s.distTable()[i-1][j+1] + s.distTable()[i-1][j-1] +
					s.distTable()[i+1][j] + s.distTable()[i-1][j] + s.distTable()[i][j+1] + s.distTable()[i][j-1] - s.distTable()[i][j] * 8) >= totalMargin;
			}
			return Math.abs(s.distTable()[i+1][j] + s.distTable()[i-1][j] + s.distTable()[i][j+1] + s.distTable()[i][j-1] - s.distTable()[i][j] * 4) >= crossMargin;
		}
		return equalVerticals && Math.abs(s.distTable()[i+1][j] + s.distTable()[i-1][j] - s.distTable()[i][j] * 2) >= lineMargin ||
			equalHorizontals && Math.abs(s.distTable()[i][j+1] + s.distTable()[i][j-1] - s.distTable()[i][j] * 2) >= lineMargin;
	}
}