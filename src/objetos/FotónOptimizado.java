package objetos;

import java.util.Collection;
import collections.ExcludingCollection;
import objetos.abstracto.Objeto;

public class FotónOptimizado extends Fotón {
	private static final long serialVersionUID = 1L;
	private boolean optimizado = true;

	public FotónOptimizado(double[] pos, boolean optimizado) {
		super(pos);
		setOptimizado(optimizado);
	}
	
	public FotónOptimizado(double[] pos) {
		super(pos);
	}
	
	public FotónOptimizado() {}
	
	@Override
	public boolean colisión() {
		if (optimizado) {
			if (objs() == null) return false;
			
			// Si está en un espacio negativo no se considera válida la colisión con otros objetos
			for (Objeto objeto : vecObjCast().getExcludedReadOnly()) {
				if (objeto.colisión(this)) return false;
			}
			
			for (Objeto objeto : vecObjCast().getIncludedReadOnly()) {
				Object[] result = comprobarColisión(objeto);
				if ((boolean) result[0]) {
					setObjCol((Objeto) result[1]);
					setObjColContenedor(objeto);
					return true;
				}
			}
			return false;
		}
		return super.colisión();
	}
	
	@Override
	public synchronized void setObjs(Collection<Objeto> objetos) {
		try {
			if (objetos instanceof ExcludingCollection<?>) {
				((ExcludingCollection<Objeto>) objetos).setOptimized(optimizado);
				super.setObjs(objetos);
			}
			else if (objs() == null || objs() instanceof ExcludingCollection<?> && !vecObjCast().isCollection(objetos)) super.setObjs(new ExcludingCollection<>(objetos, optimizado, Objeto.class, EspacioNegativo.class));
		} catch (Exception e) {
			System.err.println("Collection incompatible, revirtiendo a modo normal");
			setOptimizado(false);
			super.setObjs(objetos);
			e.printStackTrace();
		}
	}
	
	@Override
	protected void crearSubfotón(double[] pos) {
		if (subfotón() == null && !isSub()) {
			setSubfotón(new FotónOptimizado(pos, optimizado));
			subfotón().setSub();
		}
	}
	
	public boolean isOptimizado() {
		return optimizado;
	}
	
	public void setOptimizado(boolean optimizado) {
		this.optimizado = optimizado;
		if (objs() != null) vecObjCast().setOptimized(optimizado);
	}
	
	private ExcludingCollection<Objeto> vecObjCast() {
		return ((ExcludingCollection<Objeto>) objs());
	}
}
