package objetos.propiedades;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import objetos.Fotón;
import objetos.Objetos;
import objetos.PrismaInvisible;
import objetos.abstracto.Objeto;
import operaciones.EngHexDec;
import operaciones.MyMath;

public class Textura implements Propiedad {
	private static final long serialVersionUID = 1L;
	
	private PrismaInvisible prisma_colisión;
	private String pathOrColor;
	private double escala_x = 1, escala_y = 1;
	private boolean hasPath;
	
	public Textura(PrismaInvisible caja_colisión, String pathOrColor, boolean isPath) {
		setPrisma(caja_colisión);
		setPathOrColor(pathOrColor, isPath);
	}
	
	public Textura(PrismaInvisible caja_colisión) {
		setPrisma(caja_colisión);
	}
	
	public Textura(Textura t, boolean clonarDatosPrisma) {
		prisma_colisión = new PrismaInvisible(t.prisma_colisión, clonarDatosPrisma);
		pathOrColor = t.pathOrColor;
		escala_x = t.escala_x;
		escala_y = t.escala_y;
		hasPath = t.hasPath;
	}
	
	public Textura() {}

	/**
	 * La posición, orientación, y rotación no son absolutas, sino relativas al objeto
	 * (suponiendo que se encuentra con datos estándar en (0,0,0)
	 */
	public PrismaInvisible getPrisma() {
		return prisma_colisión;
	}
	
	public void setPrisma(PrismaInvisible prisma_colisión) {
		this.prisma_colisión = prisma_colisión;
	}
	
	public double getEscalaX() {
		return escala_x;
	}
	
	public double getEscalaY() {
		return escala_y;
	}
	
	public void setEscala(double escala_x, double escala_y) {
		if (escala_x == 0 || escala_y == 0) throw new IllegalArgumentException("La escala de la textura no puede ser 0");
		this.escala_x = escala_x;
		this.escala_y = escala_y;
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
	}

	/**
	 * Proyecta el fotón sobre la textura (suele usarse con datos relativos al objeto que la contiene, no con datos absolutos)
	 * @return el color en el punto proyectado o null si no forma parte de ella o está lejos
	 */
	public Color colorEnPunto(Fotón fotón) {
		Object[] resultado = prisma_colisión.colisiónYProyección(fotón);
		if ((boolean) resultado[0]) return colorEnPíxel((double[]) resultado[1]);
		return null;
	}
	
	/**
	 * @param coord - Las coordenadas, x aumenta hacia la derecha, y aumenta hacia arriba, (0,0) está en el centro de la textura
	 * @return el color en ese píxel o null si no hay
	 */
	public Color colorEnPíxel(double[] coord) {
		if (hasPath) {
			try {
				BufferedImage img = ImageIO.read(new File(pathOrColor));
				int x = (int) (coord[0] / escala_x + img.getWidth() / 2.0), y = (int) (-coord[1] / escala_y + img.getHeight() / 2.0);
				
				if (x >= img.getWidth() || y >= img.getHeight() || x < 0 || y < 0) return null;
				
				int color = img.getRGB(x, y);
				return new Color(new int[] {(color>>16) & 0xff, (color>>8) & 0xff, color & 0xff}, true);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return new Color(pathOrColor, true);
	}
	
	public static void añadir(Objeto obj, Textura tex) {
		Propiedad.Container<ConcurrentHashMap<String, Textura>> mapa = obj.propiedad(Objeto.TEXTURAS);
		if (mapa == null) {
			mapa = new Propiedad.Container<>(new ConcurrentHashMap<>());
			obj.cambiarPropiedad(Objeto.TEXTURAS, mapa);
		}
		mapa.dato.put("texture_" + mapa.dato.size(), tex);
	}
	
	/**
	 * @return el color de la textura que haya del objeto en la posición del fotón, o null
	 */
	public static Color color(Objeto obj, Fotón fotón) {
		Propiedad.Container<ConcurrentHashMap<String, Textura>> mapa = obj.propiedad(Objeto.TEXTURAS);
		if (mapa != null && !mapa.dato.isEmpty()) {
			double[] pos_backup = fotón.getPos();
			fotón.setPos(MyMath.restar(Objetos.rotarAlrededor(fotón, obj), obj.getPos()), true);
			
			Color color = null;
			for (Textura tex : mapa.dato.values()) {
				if ((color = tex.colorEnPunto(fotón)) != null) break;
			}
			
			fotón.setPos(pos_backup, true);
			return color;
		}
		return null;
	}
}