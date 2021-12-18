package objetos.propiedades;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import objetos.Fot�n;
import objetos.Objetos;
import objetos.PrismaInvisible;
import objetos.abstracto.Objeto;
import operaciones.EngHexDec;
import operaciones.MyMath;

public class Textura implements Propiedad {
	private static final long serialVersionUID = 1L;
	
	private PrismaInvisible prisma_colisi�n;
	private String pathOrColor;
	private double escala_x = 1, escala_y = 1;
	private boolean hasPath;
	
	public Textura(PrismaInvisible caja_colisi�n, String pathOrColor, boolean isPath) {
		setPrisma(caja_colisi�n);
		setPathOrColor(pathOrColor, isPath);
	}
	
	public Textura(PrismaInvisible caja_colisi�n) {
		setPrisma(caja_colisi�n);
	}
	
	public Textura(Textura t, boolean clonarDatosPrisma) {
		prisma_colisi�n = new PrismaInvisible(t.prisma_colisi�n, clonarDatosPrisma);
		pathOrColor = t.pathOrColor;
		escala_x = t.escala_x;
		escala_y = t.escala_y;
		hasPath = t.hasPath;
	}
	
	public Textura() {}

	/**
	 * La posici�n, orientaci�n, y rotaci�n no son absolutas, sino relativas al objeto
	 * (suponiendo que se encuentra con datos est�ndar en (0,0,0)
	 */
	public PrismaInvisible getPrisma() {
		return prisma_colisi�n;
	}
	
	public void setPrisma(PrismaInvisible prisma_colisi�n) {
		this.prisma_colisi�n = prisma_colisi�n;
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
	 * Proyecta el fot�n sobre la textura (suele usarse con datos relativos al objeto que la contiene, no con datos absolutos)
	 * @return el color en el punto proyectado o null si no forma parte de ella o est� lejos
	 */
	public Color colorEnPunto(Fot�n fot�n) {
		Object[] resultado = prisma_colisi�n.colisi�nYProyecci�n(fot�n);
		if ((boolean) resultado[0]) return colorEnP�xel((double[]) resultado[1]);
		return null;
	}
	
	/**
	 * @param coord - Las coordenadas, x aumenta hacia la derecha, y aumenta hacia arriba, (0,0) est� en el centro de la textura
	 * @return el color en ese p�xel o null si no hay
	 */
	public Color colorEnP�xel(double[] coord) {
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
	
	public static void a�adir(Objeto obj, Textura tex) {
		Propiedad.Container<ConcurrentHashMap<String, Textura>> mapa = obj.propiedad(Objeto.TEXTURAS);
		if (mapa == null) {
			mapa = new Propiedad.Container<>(new ConcurrentHashMap<>());
			obj.cambiarPropiedad(Objeto.TEXTURAS, mapa);
		}
		mapa.dato.put("texture_" + mapa.dato.size(), tex);
	}
	
	/**
	 * @return el color de la textura que haya del objeto en la posici�n del fot�n, o null
	 */
	public static Color color(Objeto obj, Fot�n fot�n) {
		Propiedad.Container<ConcurrentHashMap<String, Textura>> mapa = obj.propiedad(Objeto.TEXTURAS);
		if (mapa != null && !mapa.dato.isEmpty()) {
			double[] pos_backup = fot�n.getPos();
			fot�n.setPos(MyMath.restar(Objetos.rotarAlrededor(fot�n, obj), obj.getPos()), true);
			
			Color color = null;
			for (Textura tex : mapa.dato.values()) {
				if ((color = tex.colorEnPunto(fot�n)) != null) break;
			}
			
			fot�n.setPos(pos_backup, true);
			return color;
		}
		return null;
	}
}