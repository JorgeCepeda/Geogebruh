package es.doncomedia.graphics;

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

import es.doncomedia.chunks.*;
import es.doncomedia.effects.Lighting;
import es.doncomedia.effects.TotalBordering;
import es.doncomedia.misc.Tasks;
import es.doncomedia.objects.Camera;
import es.doncomedia.objects.Photon;
import es.doncomedia.objects.abstracts.BaseObject;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.objects.properties.Color;
import es.doncomedia.objects.properties.Texture;
import es.doncomedia.operations.*;

public class Screen implements Serializable {
	private static final long serialVersionUID = 1L;
	private static AtomicInteger threadsRunning = new AtomicInteger();
	private Camera camera;
	private double[][][] posTable;
	private double[][] distTable;
	private String[][] colorTable;
	private char[][] charTable;
	private GameObject[][] objTable;
	private boolean rendered;
	private boolean swBorders;
	private boolean swLighting;
	private int height, width, extra = 0;
	private ScheduledFuture<?> timer;
	private AtomicBoolean executedResSaving; // Lets you know if resource saving has been executed
	
	public Screen(int height, int width, boolean swBorders, Camera camera) {
		setBorders(swBorders);
		setDimensions(height, width);
		setCamera(camera);
	}
	
	/**
	 * @return height and width in that order
	 */
	public int[] getDimensions() {
		return new int[] {height, width};
	}

	public void setDimensions(int height, int width) {
		if (height < 1 || width < 1) throw new IllegalArgumentException("Invalid screen dimensions");
		this.width = width;
		this.height = height;
	}
	
	public synchronized void render() {
		Photon photon = new Photon();
		init();
			
		double[] coord, iniCoord = camera.getPos(), phOrient = orientCam(), planeDispl = null;
		double backwardsSpeed = camera.isPrecise() ? 0.001 : 0.1;
		
		if (camera.hasFOV()) planeDispl = MyMath.multipl(phOrient, MyMath.fix(width / 2.0 / Math.tan(camera.getFOVTheta() / 2)));
		
		countThread();
		for (int i = height + 2*extra - 1; i >= 0; i--) { // Plane rows
			for (int j = 0; j < width + 2*extra; j++) { // Plane columns
				if (camera.hasFOV()) {
					double[] cell = computeCell(i, j, MyMath.sum(camera.getPos(), planeDispl));
					phOrient = MyMath.unitary(MyMath.vector(iniCoord, cell));
				}
				else iniCoord = computeCell(i, j, camera.getPos());

				int row = colorTable.length-1-i;
				charTable[row][j] = ' ';
				coord = iniCoord.clone();
				photon.setPos(iniCoord, true);
				
				double spdMutiplier, dist;
				while ((dist = MyMath.round(Dist.pointToPoint(photon.getPos(), iniCoord), 12)) <= camera.getRender()) { // Photon projection
					double speed = 1;
					Chunk chunk = Chunks.getChunk(photon);
					if (chunk.isEmpty()) {
						// Skip chunk
						if ((speed = Chunks.skipChunk(photon.getPos(), phOrient, chunk)) == -1) break;
					}
					else {
						photon.setObjs(chunk.getObjs());
						Region region = chunk.getRegion(photon.getPos(1));
						if (region == null) {
							// Skip region
							if ((speed = Chunks.skipRegion(photon.getPos(), phOrient, chunk)) == -1) break;
						}
						else if (photon.collision()) {
							// Go backwards
							GameObject obj = photon.collObj();
							double[] coordBackup = coord.clone();
							while ((dist = MyMath.round(Dist.pointToPoint(photon.getPos(), iniCoord), 12)) >= backwardsSpeed * 0.6) {
								coord[0] -= phOrient[0]*backwardsSpeed;
								coord[1] -= phOrient[1]*backwardsSpeed;
								coord[2] -= phOrient[2]*backwardsSpeed;
								photon.setPos(MyMath.fix(coord), false);
								if (camera.isPrecise() && photon.collision() || obj.collision(photon) && !photon.inNegativeSpace()) coordBackup = coord.clone();
								else break;
							} 
							
							char character = '·';
							if (dist < 1) character = '×';
							else if (dist <= 2) character = '#';
							else if (dist <= 8) character = '+';
							else if (dist <= 14) character = '-';
							
							// Save pixel data
							Color pixelColor = Texture.color(obj, photon);
							if (pixelColor == null) pixelColor = photon.collObj().property(BaseObject.COLOR);
							
							charTable[row][j] = character;
							objTable[row][j] = photon.collObjContainer();
							colorTable[row][j] = pixelColor.getString();
							distTable[row][j] = dist;
							if (swBorders || swLighting) posTable[row][j] = coordBackup;
							break;
						}
						else if (camera.isPrecise() && (spdMutiplier = 1 + dist*0.18 /*20 when dist is 100*/) < 20) { // Render precision
							speed = 0.05*spdMutiplier;
						}
					}
					
					if (camera.isPrecise()) coord = MyMath.sum(iniCoord, MyMath.multipl(phOrient, dist + speed));
					else {
						coord[0] += phOrient[0]*speed;
						coord[1] += phOrient[1]*speed;
						coord[2] += phOrient[2]*speed;
					}
					photon.setPos(MyMath.fix(coord), false);
				}
			}
		}
		uncountThread();
		postProcessing();
		endRender();
	}

	protected void postProcessing() {
		if (swLighting) Lighting.lightUp(this);
		if (swBorders) TotalBordering.drawBorde(this);
		addGUI();
	}

	protected void init() {
		initTables();
		startTimer();
	}
	
	protected void endRender() {
		setRendered(true);
		stopTimer();
	}

	private void initTables() {
		int totalHeight = height + 2*extra, totalWidth = width + 2*extra;
		
		setRendered(false);
		if (swBorders || swLighting) posTable = new double[totalHeight][totalWidth][3];
		distTable = new double[totalHeight][totalWidth];
		colorTable = new String[totalHeight][totalWidth];
		charTable = new char[totalHeight][totalWidth];
		objTable = new GameObject[totalHeight][totalWidth];
	}
	
	public String getRenderText() {
		String color = "";
		StringBuilder sb = new StringBuilder(height*width*8);
		sb.append("<html><pre>");
		for (int i = extra; i < colorTable.length - extra; i++) {
			for (int j = extra; j < colorTable[0].length - extra; j++) {
				if (colorTable[i][j] != null && !colorTable[i][j].equals(color)) {
					if (charTable[i][j] != ' ' && !"".equals(color)) sb.append("</span>");
					color = colorTable[i][j];
					sb.append("<span style=\"color:" + color + ";\">");
				}
				sb.append(charTable[i][j]);
				//sb.append(j % 10); //DEBUG
				if (j != colorTable[0].length - extra - 1) sb.append(" ");
			}
			sb.append("<br>");
		}
		return sb.append("</span></pre></html>").toString();
	}

	/**
	 * Obtain render image and export to a file
	 * @return the image
	 */
	public BufferedImage getFrame(boolean export) {
		int[] pixelsInt = new int[height*width*3];
		for (int i = extra; i < height + extra; i++) {
	        for (int j = extra; j < width + extra; j++) {
	            int index = 3 * ((i-extra) * width + (j-extra));
	            int[] color = new int[3];
	            if (colorTable[i][j] != null) color = EngHexDec.hex6ToRGB(colorTable[i][j]);
	            pixelsInt[index] = color[0]; //R
	            pixelsInt[index + 1] = color[1]; //G
	            pixelsInt[index + 2] = color[2]; //B
	        }
	    }
		
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = (WritableRaster) image.getData();
        raster.setPixels(0, 0, width, height, pixelsInt);
        image.setData(raster);
        
        if (export) {
	        try {
				ImageIO.write(image, "jpg", new File("Render.jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		return image;
	}

	public void addGUI() {
		GUI.cursor(this);
		GUI.loadedChunks(this);
		GUI.aimedObject(this);
		GUI.orientation(this);
	}

	public double[] computeCell(double y, double x, double[] centralCell) {
		// Move (0,0) to the center
		y -= (height + 2*extra - 1) / 2.0;
		x -= (width + 2*extra - 1) / 2.0;
		
		return MyMath.fix(Vector.centerToCell(y, x, centralCell, orientCam(1), camera.getRotation()));
	}
	
	private void startTimer() {
		AtomicBoolean executedResSaving2 = new AtomicBoolean();
		executedResSaving = executedResSaving2;
		timer = Tasks.timers.getES().schedule(() -> saveResources(true, executedResSaving2), 1, TimeUnit.SECONDS);
	}
	
	private void stopTimer() {
		timer.cancel(true);
		saveResources(false, executedResSaving);
	}

	/**
	 * Stops or resumes chunk loading based on the save parameter and the state of the AtomicBoolean, and registers this method's execution on the AtomicBoolean
	 */
	private void saveResources(boolean save, AtomicBoolean executedResSaving) {
		synchronized (executedResSaving) {
			if (executedResSaving.get()) {
				if (!save) Chunks.resume();
			}
			else {
				executedResSaving.set(true);
				if (save) Chunks.pause();
			}
		}
	}
	
	public int extra() {
		return extra;
	}

	public boolean hasBorders() {
		return swBorders;
	}

	public void setBorders(boolean swBorders) {
		this.swBorders = swBorders;
		extra = swBorders ? 5 : 0;
	}
	
	public Camera camera() {
		return camera;
	}
	
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public double[] orientCam() {
		return camera.getOrient();
	}
	
	public double orientCam(int i) {
		return camera.getOrient(i);
	}
	
	public double[][][] posTable() {
		return posTable;
	}
	
	public double[][] distTable() {
		return distTable;
	}
	
	public String[][] colorTable() {
		return colorTable;
	}
	
	public char[][] charTable() {
		return charTable;
	}
	
	public GameObject[][] objTable() {
		return objTable;
	}
	
	public static int threadsRunning() {
		return threadsRunning.get();
	}
	
	public static void countThread() {
		threadsRunning.incrementAndGet();
	}
	
	public static void uncountThread() {
		threadsRunning.decrementAndGet();
	}
	
	public boolean isRendered() {
		return rendered;
	}
	
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}

	public boolean isLightUp() {
		return swLighting;
	}

	public void setLightUp(boolean lighting) {
		swLighting = lighting;
	}
}