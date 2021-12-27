package es.doncomedia.graphics;

import java.util.HashSet;
import java.util.concurrent.Future;

import es.doncomedia.misc.Tasks;
import es.doncomedia.objects.Camera;
import es.doncomedia.objects.Photon;
import es.doncomedia.operations.MyMath;

public class MultithreadedScreen extends Screen { //TODO puede ser útil pasar la creación de secciones a la inicialización y tener un método para pasarle datos, para cuando se reutilice una pantalla, hasta entonces no hace falta
	private static final long serialVersionUID = 1L;

	private class Section implements Runnable {
		private Object[] pixelData;
		private MultithreadedScreen s;
		private int i, j, sectionHeight, sectionWidth;
		
		public Section(MultithreadedScreen s, int topCornerRow, int topCornerCol, int sectionHeight, int sectionWidth, Object[] pixelData) {
			this.s = s;
			i = topCornerRow;
			j = topCornerCol;
			this.sectionHeight = sectionHeight;
			this.sectionWidth = sectionWidth;
			this.pixelData = pixelData;
		}
		
		@Override
		public void run() {
			countThread();
			Photon photon = new Photon();
			for (int k = 0; k < sectionHeight; k++) {
				for (int l = 0; l < sectionWidth; l++) {
					new Pixel(i-k, j+l, s, photon, (double[]) pixelData[0], (double[]) pixelData[1], (double[]) pixelData[2], (double) pixelData[3]).run();
				}
			}
			uncountThread();
		}
	}
	
	private int sectionsHori = 1, sectionsVert = 1;
	
	public MultithreadedScreen(int height, int width, int sections, boolean swBorders, Camera camera) {
		super(height, width, swBorders, camera);
		setSectionsCount(sections);
	}
	
	public MultithreadedScreen(int height, int width, boolean swBorders, Camera camera) {
		super(height, width, swBorders, camera);
	}
	
	public void setSectionsCount(int sections) {
		if (sections < 1) throw new IllegalArgumentException("Invalid sections amount (" + sections + ")");
		if (sections % 2 == 1) setSectionsCount(1, sections);
		else setSectionsCount(2, sections / 2);
	}
	
	public void setSectionsCount(int sectionsVert, int sectionsHori) {
		if (sectionsVert < 1 || sectionsHori < 1) throw new IllegalArgumentException("Invalid sections amount (" + sectionsHori + "x" + sectionsVert +")");
		this.sectionsVert = sectionsVert;
		this.sectionsHori = sectionsHori;
	}
	
	@Override
	public synchronized void render() {
		init();

		double[] iniCoord = camera().getPos(), phOrient = orientCam(), planeDispl = null;
		int[] dimens = getDimensions();
		double backwardsSpeed = 0.1;
		if (camera().isPrecise()) backwardsSpeed = 0.001;
		if (camera().hasFOV()) planeDispl = MyMath.multipl(phOrient, MyMath.fix((double) dimens[1] / 2 / Math.tan(camera().getFOVTheta() / 2)));
		
		HashSet<Future<?>> sections = new HashSet<>((int) (sectionsVert*sectionsHori*1.3));
		// Screen division in independent parallel parts
		int height = dimens[0] + 2*extra(), width = dimens[1] + 2*extra(), remainderV = height % sectionsVert, vOffset = height - 1;
		for (int i = 0; i < sectionsVert; i++) {
			int remainderH = width % sectionsHori, sectionHeight = height / sectionsVert + Integer.signum(remainderV), hOffset = 0;
			if (remainderV > 0) remainderV--;
			for (int j = 0; j < sectionsHori; j++) {
				int sectionWidth = width / sectionsHori + Integer.signum(remainderH);
				if (remainderH > 0) remainderH--;
				sections.add(Tasks.pixels.getES().submit(new Section(this, vOffset, hOffset, sectionHeight, sectionWidth, new Object[] {
					iniCoord, phOrient, planeDispl, backwardsSpeed})));
				hOffset += sectionWidth;
			}
			vOffset -= sectionHeight;
		}
		Tasks.await(sections);
		
		postProcessing();
		endRender();
	}
}