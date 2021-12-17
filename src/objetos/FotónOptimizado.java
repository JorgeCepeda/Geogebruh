package objetos;

import java.util.Collection;
import collections.ExcludingCollection;
import objetos.abstracto.Objeto;

public class Fot�nOptimizado extends Fot�n {
	private static final long serialVersionUID = 1L;
	private boolean optimizado = true;

	public Fot�nOptimizado(double[] pos, boolean optimizado) {
		super(pos);
		setOptimizado(optimizado);
	}
	
	public Fot�nOptimizado(double[] pos) {
		super(pos);
	}
	
	public Fot�nOptimizado() {}
	
	@Override
	public boolean colisi�n() {
		if (optimizado) {
			if (objs() == null) return false;
			
			// Si est� en un espacio negativo no se considera v�lida la colisi�n con otros objetos
			for (Objeto objeto : vecObjCast().getExcludedReadOnly()) {
				if (objeto.colisi�n(this)) return false;
			}
			
			for (Objeto objeto : vecObjCast().getIncludedReadOnly()) {
				Object[] result = comprobarColisi�n(objeto);
				if ((boolean) result[0]) {
					setObjCol((Objeto) result[1]);
					setObjColContenedor(objeto);
					return true;
				}
			}
			return false;
		}
		return super.colisi�n();
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
	protected void crearSubfot�n(double[] pos) {
		if (subfot�n() == null && !isSub()) {
			setSubfot�n(new Fot�nOptimizado(pos, optimizado));
			subfot�n().setSub();
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
