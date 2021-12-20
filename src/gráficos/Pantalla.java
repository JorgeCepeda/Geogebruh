package gráficos;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import chunks_NoCeldas.*;
import efectos.BordeadoTotal;
import efectos.Iluminación;
import objetos.Cámara;
import objetos.Fotón;
import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoBase;
import objetos.propiedades.Color;
import objetos.propiedades.Textura;
import operaciones.*;
import otros.Tareas;

public class Pantalla implements Serializable {
	private static final long serialVersionUID = 1L;
	private static AtomicInteger hilos_corriendo = new AtomicInteger();
	private Cámara cámara;
	private double[][][] tabla_pos;
	private double[][] tabla_dist;
	private String[][] tabla_color;
	private char[][] tabla_carác;
	private Objeto[][] tabla_obj;
	private boolean dibujada;
	private boolean swBorde;
	private boolean swIluminación;
	private int alto, ancho, extra = 0;
	private ScheduledFuture<?> timer;
	private AtomicBoolean ahorroEjecutado; // Permite saber si se llega a procesar el ahorro de recursos
	
	public Pantalla(int alto, int ancho, boolean swBorde, Cámara cámara) {
		setBorde(swBorde);
		setDimensiones(alto, ancho);
		setCámara(cámara);
	}
	
	/**
	 * @return alto y ancho en ese orden
	 */
	public int[] getDimensiones() {
		return new int[] {alto, ancho};
	}

	public void setDimensiones(int alto, int ancho) {
		if (alto < 1 || ancho < 1) throw new IllegalArgumentException("Dimensiones de pantalla inválidas");
		this.ancho = ancho;
		this.alto = alto;
	}
	
	public synchronized void renderizar() {
		Fotón fotón = new Fotón();
		init();
			
		double[] coord, c_ini = cámara.getPos(), orient_fotón = orientCám(), despl_plano = null;
		double vel_retroceso = cámara.esPrecisa() ? 0.001 : 0.1;
		
		if (cámara.tieneCDV()) despl_plano = MyMath.multipl(orient_fotón, MyMath.fix(ancho / 2.0 / Math.tan(cámara.getTeta_CDV() / 2)));
		
		sumarHilo();
		for (int i = alto + 2*extra - 1; i >= 0; i--) { // Filas del plano
			for (int j = 0; j < ancho + 2*extra; j++) { // Columnas del plano
				if (cámara.tieneCDV()) {
					double[] celda = calcularCelda(i, j, MyMath.sumar(cámara.getPos(), despl_plano));
					orient_fotón = MyMath.unitario(MyMath.vector(c_ini, celda));
				}
				else c_ini = calcularCelda(i, j, cámara.getPos());
				
				tabla_carác[tabla_color.length-1-i][j] = ' ';
				coord = c_ini.clone();
				fotón.setPos(c_ini, true);
				
				double multiplicador, dist;
				while ((dist = MyMath.round(Dist.puntoAPunto(fotón.getPos(), c_ini), 12)) <= cámara.getRender()) { // Proyección del fotón
					double velocidad = 1;
					Chunk chunk = Chunks.getChunk(fotón);
					if (chunk.estáVacío()) {
						// Saltar chunk
						if ((velocidad = Chunks.saltarChunk(fotón.getPos(), orient_fotón, chunk)) == -1) break;
					}
					else {
						fotón.setObjs(chunk.getObjs());
						Malla malla_actual = chunk.getMalla(fotón.getPos(1));
						if (malla_actual == null) {
							// Saltar malla
							if ((velocidad = Chunks.saltarMalla(fotón.getPos(), orient_fotón, chunk)) == -1) break;
						}
						else if (fotón.colisión()) {
							// Retroceder
							Objeto obj = fotón.objCol();
							double[] coord_backup = coord.clone();
							while ((dist = MyMath.round(Dist.puntoAPunto(fotón.getPos(), c_ini), 12)) >= vel_retroceso * 0.6) {
								coord[0] -= orient_fotón[0]*vel_retroceso;
								coord[1] -= orient_fotón[1]*vel_retroceso;
								coord[2] -= orient_fotón[2]*vel_retroceso;
								fotón.setPos(MyMath.fix(coord), false);
								if (cámara.esPrecisa() && fotón.colisión() || obj.colisión(fotón) && !fotón.enEspacioNegativo()) coord_backup = coord.clone();
								else break;
							} 
							
							char carácter = '·';
							if (dist < 1) carácter = '×';
							else if (dist <= 2) carácter = '#';
							else if (dist <= 8) carácter = '+';
							else if (dist <= 14) carácter = '-';
							
							// Guardar datos
							Color color_píxel = Textura.color(obj, fotón);
							if (color_píxel == null) color_píxel = fotón.objCol().propiedad(ObjetoBase.COLOR);
							
							tabla_carác[tabla_color.length-1-i][j] = carácter;
							tabla_obj[tabla_color.length-1-i][j] = fotón.objColContenedor();
							tabla_color[tabla_color.length-1-i][j] = color_píxel.getString();
							tabla_dist[tabla_color.length-1-i][j] = dist;
							if (swBorde || swIluminación) tabla_pos[tabla_color.length-1-i][j] = coord_backup;
							break;
						}
						else if (cámara.esPrecisa() && (multiplicador = 1 + dist*0.18 /*20 cuando dist es 100*/) < 20) { // Precisión del render
							velocidad = 0.05*multiplicador;
						}
					}
					
					if (cámara.esPrecisa()) coord = MyMath.sumar(c_ini, MyMath.multipl(orient_fotón, dist + velocidad));
					else {
						coord[0] += orient_fotón[0]*velocidad;
						coord[1] += orient_fotón[1]*velocidad;
						coord[2] += orient_fotón[2]*velocidad;
					}
					fotón.setPos(MyMath.fix(coord), false);
				}
			}
		}
		restarHilo();
		postProcesado();
		endRender();
	}

	protected void postProcesado() {
		if (swIluminación) Iluminación.iluminar(this);
		if (swBorde) BordeadoTotal.drawBorde(this);
		añadirInterfaz();
	}

	protected void init() {
		initTablas();
		startTimer();
	}
	
	protected void endRender() {
		setDibujada(true);
		stopTimer();
	}

	private void initTablas() {
		int alto_total = alto + 2*extra, ancho_total = ancho + 2*extra;
		
		setDibujada(false);
		if (swBorde || swIluminación) tabla_pos = new double[alto_total][ancho_total][3];
		tabla_dist = new double[alto_total][ancho_total];
		tabla_color = new String[alto_total][ancho_total];
		tabla_carác = new char[alto_total][ancho_total];
		tabla_obj = new Objeto[alto_total][ancho_total];
	}
	
	public String textoRender() {
		String color_actual = "";
		StringBuilder sb = new StringBuilder(alto*ancho*8);
		sb.append("<html><pre>");
		for (int i = extra; i < tabla_color.length - extra; i++) {
			for (int j = extra; j < tabla_color[0].length - extra; j++) {
				if (tabla_color[i][j] != null && !tabla_color[i][j].equals(color_actual)) {
					if (tabla_carác[i][j] != ' ' && !"".equals(color_actual)) sb.append("</span>");
					color_actual = tabla_color[i][j];
					sb.append("<span style=\"color:" + color_actual + ";\">");
				}
				sb.append(tabla_carác[i][j]);
				//sb.append(j % 10); //DEBUG
				if (j != tabla_color[0].length - extra - 1) sb.append(" ");
			}
			sb.append("<br>");
		}
		return sb.append("</span></pre></html>").toString();
	}

	/**
	 * Obtener imagen en píxeles y exportar a un archivo
	 * @return la imagen exportada
	 */
	public BufferedImage exportarFrame() {
		int[] píxeles_int = new int[alto*ancho*3];
		for (int i = extra; i < alto + extra; i++) {
	        for (int j = extra; j < ancho + extra; j++) {
	            int índice = (i-extra) * ancho + (j-extra);
	            int[] color = new int[3];
	            if (tabla_color[i][j] != null) color = EngHexDec.hex6ToRGB(tabla_color[i][j]);
	            píxeles_int[índice * 3] = color[0]; //R
	            píxeles_int[índice * 3 + 1] = color[1]; //G
	            píxeles_int[índice * 3 + 2] = color[2]; //B
	        }
	    }
		
        BufferedImage image = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = (WritableRaster) image.getData();
        raster.setPixels(0, 0, ancho, alto, píxeles_int);
        
        image.setData(raster);
        try {
			ImageIO.write(image, "jpg", new File("Render.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public void añadirInterfaz() {
		InterfazGráfica.cursor(this);
		InterfazGráfica.chunksCargados(this);
		InterfazGráfica.objetoApuntado(this);
		InterfazGráfica.orientación(this);
	}

	public double[] calcularCelda(double y, double x, double[] celda_central) {
		// Desplazar (0,0) al centro
		y -= (alto + 2*extra - 1) / 2.0;
		x -= (ancho + 2*extra - 1) / 2.0;
		
		return MyMath.fix(Vector.centroACelda(y, x, celda_central, orientCám(1), cámara.getRotación()));
	}
	
	private void startTimer() {
		AtomicBoolean ahorroEjecutado2 = new AtomicBoolean();
		ahorroEjecutado = ahorroEjecutado2;
		timer = Tareas.timers.getES().schedule(() -> ahorrarRecursos(true, ahorroEjecutado2), 1, TimeUnit.SECONDS);
	}
	
	private void stopTimer() {
		timer.cancel(true);
		ahorrarRecursos(false, ahorroEjecutado);
	}

	/**
	 * Pausa o reanuda la carga de secciones en función del parámetro ahorrar y el estado del AtomicBoolean, y deja registrada la ejecución de este método en el AtomicBoolean
	 */
	private void ahorrarRecursos(boolean ahorrar, AtomicBoolean ahorroEjecutado) {
		synchronized (ahorroEjecutado) {
			if (ahorroEjecutado.get()) {
				if (!ahorrar) Chunks.reanudar();
			}
			else {
				ahorroEjecutado.set(true);
				if (ahorrar) Chunks.pausar();
			}
		}
	}
	
	public int extra() {
		return extra;
	}

	public boolean tieneBorde() {
		return swBorde;
	}

	public void setBorde(boolean swBorde) {
		this.swBorde = swBorde;
		extra = swBorde ? 5 : 0;
	}
	
	public Cámara cámara() {
		return cámara;
	}
	
	public void setCámara(Cámara cámara) {
		this.cámara = cámara;
	}
	
	public double[] orientCám() {
		return cámara.getOrient();
	}
	
	public double orientCám(int i) {
		return cámara.getOrient(i);
	}
	
	public double[][][] tabla_pos() {
		return tabla_pos;
	}
	
	public double[][] tabla_dist() {
		return tabla_dist;
	}
	
	public String[][] tabla_color() {
		return tabla_color;
	}
	
	public char[][] tabla_carác() {
		return tabla_carác;
	}
	
	public Objeto[][] tabla_obj() {
		return tabla_obj;
	}
	
	public static int hilosCorriendo() {
		return hilos_corriendo.get();
	}
	
	public static void sumarHilo() {
		hilos_corriendo.incrementAndGet();
	}
	
	public static void restarHilo() {
		hilos_corriendo.decrementAndGet();
	}
	
	public boolean isDibujada() {
		return dibujada;
	}
	
	public void setDibujada(boolean dibujada) {
		this.dibujada = dibujada;
	}

	public boolean isIluminada() {
		return swIluminación;
	}

	public void setIluminada(boolean iluminada) {
		swIluminación = iluminada;
	}
}