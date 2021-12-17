package chunks_NoCeldas;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import objetos.Fotón;
import objetos.abstracto.Objeto;

public class Malla implements Serializable {
	private static final long serialVersionUID = 1L;
	private int[] pos, dimens;
	
	public Malla(int long_x, int long_y, int long_z) {
		setDimensiones(long_x, long_y, long_z);
	}

	public synchronized void rellenar(Set<Objeto> objs_sección, Collection<Objeto> objs_disponibles) {
		Fotón fotón = new Fotón();
		
		// Recorrido a cuadros en 3D para cada objeto
		for (Objeto objeto : objs_disponibles) {
			if (!objs_sección.contains(objeto)) {
				boolean swColisión = false;
				for (int x = 0; x < dimens[0]; x++) {
					checkInterrupción(objs_sección);
					for (int y = 0; y < dimens[1]; y++) {
						for (int z = (x + y) % 2; z < dimens[2]; z+=2) {
							fotón.setPos(new double[] {pos[0]+x, pos[1]+y, pos[2]+z}, true);
							
							if (objeto.colisión(fotón)) {
								objs_sección.add(objeto);
								swColisión = true;
								break;
							}
						}
						if (swColisión) break;
					}
					if (swColisión) break;
				}
			}
		}
	}

	private void checkInterrupción(Object testigo) {
		if (Thread.currentThread().isInterrupted()) {
			synchronized (testigo) {
				Chunks.disminuirCarga();
				Thread.interrupted();
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) { 
					e.printStackTrace();
				}
				Chunk.esperarEnCola();
				Chunks.aumentarCarga();
			}
		}
	}

	public int[] getDimensiones() {
		return dimens.clone();
	}
	
	public void setDimensiones(int long_x, int long_y, int long_z) {
		if (long_x < 1 || long_y < 1 || long_z < 1) throw new IllegalArgumentException("Dimensiones de malla inválidas");
		dimens = new int[] {long_x, long_y, long_z};
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