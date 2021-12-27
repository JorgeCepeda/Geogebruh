package es.doncomedia.graphics;

import es.doncomedia.chunks.*;
import es.doncomedia.objects.Photon;
import es.doncomedia.objects.abstracts.BaseObject;
import es.doncomedia.objects.abstracts.GameObject;
import es.doncomedia.objects.properties.Color;
import es.doncomedia.objects.properties.Texture;
import es.doncomedia.operations.Dist;
import es.doncomedia.operations.MyMath;

public class Pixel implements Runnable {
	private double backwardsSpeed;
	private int i, j;
	private Screen s;
	private Photon photon;
	private double[] iniCoord, phOrient;
	
	public Pixel(int i, int j, Screen s, double[] iniCoord, double[] orient_fotón, double[] despl_plano, double backwardsSpeed) {
		init(i, j, s, iniCoord, orient_fotón, despl_plano, backwardsSpeed);
		photon = new Photon();
	}
	
	public Pixel(int i, int j, Screen s, Photon photon, double[] iniCoord, double[] phOrient, double[] planeDispl, double backwardsSpeed) {
		init(i, j, s, iniCoord, phOrient, planeDispl, backwardsSpeed);
		this.photon = photon;
	}
	
	private void init(int i, int j, Screen s, double[] iniCoord, double[] phOrient, double[] planeDispl, double backwardsSpeed) {
		this.i = i;
		this.j = j;
		this.s = s;
		this.backwardsSpeed = backwardsSpeed;
		
		if (s.camera().hasFOV()) {
			double[] cell = s.computeCell(i, j, MyMath.sum(s.camera().getPos(), planeDispl));
			this.phOrient = MyMath.unitary(MyMath.vector(iniCoord, cell));
			this.iniCoord = iniCoord;
		}
		else {
			this.phOrient = phOrient;
			this.iniCoord = s.computeCell(i, j, iniCoord);
		}
	}
	
	@Override
	public void run() {
		int row = s.colorTable().length-1-i;
		s.charTable()[row][j] = ' ';
		double[] coord = iniCoord.clone();
		photon.setPos(iniCoord, true);
		
		double spdMultiplier, dist;
		while ((dist = MyMath.round(Dist.pointToPoint(photon.getPos(), iniCoord), 12)) <= s.camera().getRender()) { // Photon projection
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
					// Skip chunk region
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
						if (s.camera().isPrecise() && photon.collision() || obj.collision(photon) && !photon.inNegativeSpace()) coordBackup = coord.clone();
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
					
					s.charTable()[row][j] = character;
					s.objTable()[row][j] = photon.collObjContainer();
					s.colorTable()[row][j] = pixelColor.getString();
					s.distTable()[row][j] = dist;
					if (s.hasBorders() || s.isLightUp()) s.posTable()[row][j] = coordBackup;
					break;
				}
				else if (s.camera().isPrecise() && (spdMultiplier = 1 + dist*0.18 /*20 when dist is 100*/) < 20) { // Render precision
					speed = 0.05*spdMultiplier;
				}
			}
			
			if (s.camera().isPrecise()) coord = MyMath.sum(iniCoord, MyMath.multipl(phOrient, dist + speed));
			else {
				coord[0] += phOrient[0]*speed;
				coord[1] += phOrient[1]*speed;
				coord[2] += phOrient[2]*speed;
			}
			photon.setPos(MyMath.fix(coord), false);
		}
	}
}