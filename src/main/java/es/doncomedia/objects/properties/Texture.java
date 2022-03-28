package es.doncomedia.objects.properties;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import es.doncomedia.graphics.Screen;
import es.doncomedia.objects.Photon;
import es.doncomedia.objects.Objects;
import es.doncomedia.objects.InvisiblePrism;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.operations.EngHexDec;
import es.doncomedia.operations.MyMath;

public class Texture implements Property {
	private static final long serialVersionUID = 1L;
	
	private InvisiblePrism collPrism;
	private String pathOrColor;
	private Screen screen;
	private double scaleX = 1, scaleY = 1;
	private boolean hasPath;
	
	public Texture(InvisiblePrism collPrism, String pathOrColor, boolean isPath) {
		setPrism(collPrism);
		setPathOrColor(pathOrColor, isPath);
	}
	
	public Texture(InvisiblePrism collPrism, Screen screen) {
		setPrism(collPrism);
		setScreen(screen);
	}

	public Texture(InvisiblePrism collPrism) {
		setPrism(collPrism);
	}
	
	public Texture(Texture t, boolean clonePrismData) {
		collPrism = new InvisiblePrism(t.collPrism, clonePrismData);
		pathOrColor = t.pathOrColor;
		scaleX = t.scaleX;
		scaleY = t.scaleY;
		hasPath = t.hasPath;
		screen = t.screen;
	}
	
	public Texture() {}

	/**
	 * Position, orientation, and rotation aren't absolute, they are relative to the object
	 * (assuming it has standard values at (0,0,0))
	 */
	public InvisiblePrism getPrism() {
		return collPrism;
	}
	
	public void setPrism(InvisiblePrism collPrism) {
		this.collPrism = collPrism;
	}
	
	public Screen getScreen() {
		return screen;
	}
	
	public void setScreen(Screen screen) {
		this.screen = screen;
		pathOrColor = null;
		hasPath = false;
	}
	
	public double getScaleX() {
		return scaleX;
	}
	
	public double getScaleY() {
		return scaleY;
	}
	
	public void setScale(double scaleX, double scaleY) {
		if (scaleX == 0 || scaleY == 0) throw new IllegalArgumentException("Texture scale can't be 0");
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	public boolean hasPath() {
		return hasPath;
	}
	
	public String getPath() {
		return hasPath ? pathOrColor : null;
	}
	
	public String getColorString() {
		return hasPath ? null : pathOrColor;
	}
	
	public void setPathOrColor(String pathOrColor, boolean isPath) {
		this.pathOrColor = isPath ? pathOrColor : EngHexDec.getHex6(pathOrColor);
		hasPath = isPath;
		screen = null;
	}

	/**
	 * Projects the photon onto the texture (it's used with data relative to the object instead of absolute)
	 * @return the color in the projected point or null if it's far away from the texture or not on it
	 */
	public Color colorAtPoint(Photon photon) {
		Object[] result = collPrism.collisionAndProjection(photon);
		if ((boolean) result[0]) return colorAtPixel((double[]) result[1]);
		return null;
	}
	
	/**
	 * @param coord - The coordinates on the plane, x grows to the right, y grows to the top, (0,0) is in the texture's center
	 * @return the color in that pixel or null if there's none
	 */
	public Color colorAtPixel(double[] coord) {
		if (hasPath) {
			try (InputStream texStream = Texture.class.getResourceAsStream(pathOrColor)) {
				BufferedImage img = ImageIO.read(texStream);
				int x = (int) (coord[0] / scaleX + img.getWidth() / 2.0), y = (int) (-coord[1] / scaleY + img.getHeight() / 2.0);
				
				if (x >= img.getWidth() || y >= img.getHeight() || x < 0 || y < 0) return null;
				
				int color = img.getRGB(x, y);
				return new Color(new int[] {(color>>16) & 0xff, (color>>8) & 0xff, color & 0xff}, true);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		if (screen != null) {
			if (screen.colorTable() != null) {
				int[] dimens = screen.getDimensions();
				int extra = screen.extra(), row = (int) (-coord[1] / scaleY + dimens[0] / 2.0) + extra, column = (int) (coord[0] / scaleX + dimens[1] / 2.0) + extra;
				String color;
				
				if (MyMath.isInside(row, column, screen.colorTable(), extra) && (color = screen.colorTable()[row][column]) != null) {
					return new Color(color, true);
				}
			}
			return new Color("#000000", true); // Assuming background is black
		}
		return new Color(pathOrColor, true);
	}
	
	public static void add(GameObject obj, Texture tex) {
		Property.Container<ConcurrentHashMap<String, Texture>> map = obj.property(GameObject.TEXTURES);
		if (map == null) {
			map = new Property.Container<>(new ConcurrentHashMap<>());
			obj.changeProperty(GameObject.TEXTURES, map);
		}
		map.data.put("texture_" + map.data.size(), tex);
	}
	
	/**
	 * @return the color of the object's texture colliding with the photon, or null otherwise
	 */
	public static Color color(GameObject obj, Photon photon) {
		Property.Container<ConcurrentHashMap<String, Texture>> map = obj.property(GameObject.TEXTURES);
		if (map != null && !map.data.isEmpty()) {
			double[] posBackup = photon.getPos();
			photon.setPos(MyMath.subtract(Objects.rotateAround(photon, obj), obj.getPos()), true);
			
			Color color = null;
			for (Texture tex : map.data.values()) {
				if ((color = tex.colorAtPoint(photon)) != null) break;
			}
			
			photon.setPos(posBackup, true);
			return color;
		}
		return null;
	}
}