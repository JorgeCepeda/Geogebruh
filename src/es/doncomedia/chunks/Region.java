package es.doncomedia.chunks;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import es.doncomedia.objects.Photon;
import es.doncomedia.objects.abstracts.GameObject;

public class Region implements Serializable {
	private static final long serialVersionUID = 1L;
	private int[] pos, dimens;
	
	public Region(int longX, int longY, int longZ) {
		setDimensions(longX, longY, longZ);
	}

	public synchronized void fill(Set<GameObject> chunkObjs, Collection<GameObject> available) {
		Photon photon = new Photon();
		
		// Checkered 3D traverse for each object
		for (GameObject object : available) {
			if (!chunkObjs.contains(object)) {
				boolean swCollision = false;
				for (int x = 0; x < dimens[0]; x++) {
					checkInterruption(chunkObjs);
					for (int y = 0; y < dimens[1]; y++) {
						for (int z = (x + y) % 2; z < dimens[2]; z+=2) {
							photon.setPos(new double[] {pos[0]+x, pos[1]+y, pos[2]+z}, true);
							
							if (object.collision(photon)) {
								chunkObjs.add(object);
								swCollision = true;
								break;
							}
						}
						if (swCollision) break;
					}
					if (swCollision) break;
				}
			}
		}
	}

	private void checkInterruption(Object monitor) {
		if (Thread.currentThread().isInterrupted()) {
			synchronized (monitor) {
				Chunks.decrementLoad();
				Thread.interrupted();
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) { 
					e.printStackTrace();
				}
				Chunk.waitInQueue();
				Chunks.incrementLoad();
			}
		}
	}

	public int[] getDimensions() {
		return dimens.clone();
	}
	
	public void setDimensions(int longX, int longY, int longZ) {
		if (longX < 1 || longY < 1 || longZ < 1) throw new IllegalArgumentException("Invalid region dimensions");
		dimens = new int[] {longX, longY, longZ};
	}

	public int[] getPos() {
		return pos.clone();
	}
	
	public int getPos(int i) {
		return pos[i];
	}

	public void setPos(int[] pos) {
		this.pos = pos.clone();
	}
}