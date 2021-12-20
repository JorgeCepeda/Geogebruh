package gr�ficos;

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
import efectos.Iluminaci�n;
import objetos.C�mara;
import objetos.Fot�n;
import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoBase;
import objetos.propiedades.Color;
import objetos.propiedades.Textura;
import operaciones.*;
import otros.Tareas;

public class Pantalla implements Serializable {
	private static final long serialVersionUID = 1L;
	private static AtomicInteger hilos_corriendo = new AtomicInteger();
	private C�mara c�mara;
	private double[][][] tabla_pos;
	private double[][] tabla_dist;
	private String[][] tabla_color;
	private char[][] tabla_car�c;
	private Objeto[][] tabla_obj;
	private boolean dibujada;
	private boolean swBorde;
	private boolean swIluminaci�n;
	private int alto, ancho, extra = 0;
	private ScheduledFuture<?> timer;
	private AtomicBoolean ahorroEjecutado; // Permite saber si se llega a procesar el ahorro de recursos
	
	public Pantalla(int alto, int ancho, boolean swBorde, C�mara c�mara) {
		setBorde(swBorde);
		setDimensiones(alto, ancho);
		setC�mara(c�mara);
	}
	
	/**
	 * @return alto y ancho en ese orden
	 */
	public int[] getDimensiones() {
		return new int[] {alto, ancho};
	}

	public void setDimensiones(int alto, int ancho) {
		if (alto < 1 || ancho < 1) throw new IllegalArgumentException("Dimensiones de pantalla inv�lidas");
		this.ancho = ancho;
		this.alto = alto;
	}
	
	public synchronized void renderizar() {
		Fot�n fot�n = new Fot�n();
		init();
			
		double[] coord, c_ini = c�mara.getPos(), orient_fot�n = orientC�m(), despl_plano = null;
		double vel_retroceso = c�mara.esPrecisa() ? 0.001 : 0.1;
		
		if (c�mara.tieneCDV()) despl_plano = MyMath.multipl(orient_fot�n, MyMath.fix(ancho / 2.0 / Math.tan(c�mara.getTeta_CDV() / 2)));
		
		sumarHilo();
		for (int i = alto + 2*extra - 1; i >= 0; i--) { // Filas del plano
			for (int j = 0; j < ancho + 2*extra; j++) { // Columnas del plano
				if (c�mara.tieneCDV()) {
					double[] celda = calcularCelda(i, j, MyMath.sumar(c�mara.getPos(), despl_plano));
					orient_fot�n = MyMath.unitario(MyMath.vector(c_ini, celda));
				}
				else c_ini = calcularCelda(i, j, c�mara.getPos());
				
				tabla_car�c[tabla_color.length-1-i][j] = ' ';
				coord = c_ini.clone();
				fot�n.setPos(c_ini, true);
				
				double multiplicador, dist;
				while ((dist = MyMath.round(Dist.puntoAPunto(fot�n.getPos(), c_ini), 12)) <= c�mara.getRender()) { // Proyecci�n del fot�n
					double velocidad = 1;
					Chunk chunk = Chunks.getChunk(fot�n);
					if (chunk.est�Vac�o()) {
						// Saltar chunk
						if ((velocidad = Chunks.saltarChunk(fot�n.getPos(), orient_fot�n, chunk)) == -1) break;
					}
					else {
						fot�n.setObjs(chunk.getObjs());
						Malla malla_actual = chunk.getMalla(fot�n.getPos(1));
						if (malla_actual == null) {
							// Saltar malla
							if ((velocidad = Chunks.saltarMalla(fot�n.getPos(), orient_fot�n, chunk)) == -1) break;
						}
						else if (fot�n.colisi�n()) {
							// Retroceder
							Objeto obj = fot�n.objCol();
							double[] coord_backup = coord.clone();
							while ((dist = MyMath.round(Dist.puntoAPunto(fot�n.getPos(), c_ini), 12)) >= vel_retroceso * 0.6) {
								coord[0] -= orient_fot�n[0]*vel_retroceso;
								coord[1] -= orient_fot�n[1]*vel_retroceso;
								coord[2] -= orient_fot�n[2]*vel_retroceso;
								fot�n.setPos(MyMath.fix(coord), false);
								if (c�mara.esPrecisa() && fot�n.colisi�n() || obj.colisi�n(fot�n) && !fot�n.enEspacioNegativo()) coord_backup = coord.clone();
								else break;
							} 
							
							char car�cter = '�';
							if (dist < 1) car�cter = '�';
							else if (dist <= 2) car�cter = '#';
							else if (dist <= 8) car�cter = '+';
							else if (dist <= 14) car�cter = '-';
							
							// Guardar datos
							Color color_p�xel = Textura.color(obj, fot�n);
							if (color_p�xel == null) color_p�xel = fot�n.objCol().propiedad(ObjetoBase.COLOR);
							
							tabla_car�c[tabla_color.length-1-i][j] = car�cter;
							tabla_obj[tabla_color.length-1-i][j] = fot�n.objColContenedor();
							tabla_color[tabla_color.length-1-i][j] = color_p�xel.getString();
							tabla_dist[tabla_color.length-1-i][j] = dist;
							if (swBorde || swIluminaci�n) tabla_pos[tabla_color.length-1-i][j] = coord_backup;
							break;
						}
						else if (c�mara.esPrecisa() && (multiplicador = 1 + dist*0.18 /*20 cuando dist es 100*/) < 20) { // Precisi�n del render
							velocidad = 0.05*multiplicador;
						}
					}
					
					if (c�mara.esPrecisa()) coord = MyMath.sumar(c_ini, MyMath.multipl(orient_fot�n, dist + velocidad));
					else {
						coord[0] += orient_fot�n[0]*velocidad;
						coord[1] += orient_fot�n[1]*velocidad;
						coord[2] += orient_fot�n[2]*velocidad;
					}
					fot�n.setPos(MyMath.fix(coord), false);
				}
			}
		}
		restarHilo();
		postProcesado();
		endRender();
	}

	protected void postProcesado() {
		if (swIluminaci�n) Iluminaci�n.iluminar(this);
		if (swBorde) BordeadoTotal.drawBorde(this);
		a�adirInterfaz();
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
		if (swBorde || swIluminaci�n) tabla_pos = new double[alto_total][ancho_total][3];
		tabla_dist = new double[alto_total][ancho_total];
		tabla_color = new String[alto_total][ancho_total];
		tabla_car�c = new char[alto_total][ancho_total];
		tabla_obj = new Objeto[alto_total][ancho_total];
	}
	
	public String textoRender() {
		String color_actual = "";
		StringBuilder sb = new StringBuilder(alto*ancho*8);
		sb.append("<html><pre>");
		for (int i = extra; i < tabla_color.length - extra; i++) {
			for (int j = extra; j < tabla_color[0].length - extra; j++) {
				if (tabla_color[i][j] != null && !tabla_color[i][j].equals(color_actual)) {
					if (tabla_car�c[i][j] != ' ' && !"".equals(color_actual)) sb.append("</span>");
					color_actual = tabla_color[i][j];
					sb.append("<span style=\"color:" + color_actual + ";\">");
				}
				sb.append(tabla_car�c[i][j]);
				//sb.append(j % 10); //DEBUG
				if (j != tabla_color[0].length - extra - 1) sb.append(" ");
			}
			sb.append("<br>");
		}
		return sb.append("</span></pre></html>").toString();
	}

	/**
	 * Obtener imagen en p�xeles y exportar a un archivo
	 * @return la imagen exportada
	 */
	public BufferedImage exportarFrame() {
		int[] p�xeles_int = new int[alto*ancho*3];
		for (int i = extra; i < alto + extra; i++) {
	        for (int j = extra; j < ancho + extra; j++) {
	            int �ndice = (i-extra) * ancho + (j-extra);
	            int[] color = new int[3];
	            if (tabla_color[i][j] != null) color = EngHexDec.hex6ToRGB(tabla_color[i][j]);
	            p�xeles_int[�ndice * 3] = color[0]; //R
	            p�xeles_int[�ndice * 3 + 1] = color[1]; //G
	            p�xeles_int[�ndice * 3 + 2] = color[2]; //B
	        }
	    }
		
        BufferedImage image = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = (WritableRaster) image.getData();
        raster.setPixels(0, 0, ancho, alto, p�xeles_int);
        
        image.setData(raster);
        try {
			ImageIO.write(image, "jpg", new File("Render.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	public void a�adirInterfaz() {
		InterfazGr�fica.cursor(this);
		InterfazGr�fica.chunksCargados(this);
		InterfazGr�fica.objetoApuntado(this);
		InterfazGr�fica.orientaci�n(this);
	}

	public double[] calcularCelda(double y, double x, double[] celda_central) {
		// Desplazar (0,0) al centro
		y -= (alto + 2*extra - 1) / 2.0;
		x -= (ancho + 2*extra - 1) / 2.0;
		
		return MyMath.fix(Vector.centroACelda(y, x, celda_central, orientC�m(1), c�mara.getRotaci�n()));
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
	 * Pausa o reanuda la carga de secciones en funci�n del par�metro ahorrar y el estado del AtomicBoolean, y deja registrada la ejecuci�n de este m�todo en el AtomicBoolean
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
	
	public C�mara c�mara() {
		return c�mara;
	}
	
	public void setC�mara(C�mara c�mara) {
		this.c�mara = c�mara;
	}
	
	public double[] orientC�m() {
		return c�mara.getOrient();
	}
	
	public double orientC�m(int i) {
		return c�mara.getOrient(i);
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
	
	public char[][] tabla_car�c() {
		return tabla_car�c;
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
		return swIluminaci�n;
	}

	public void setIluminada(boolean iluminada) {
		swIluminaci�n = iluminada;
	}
}