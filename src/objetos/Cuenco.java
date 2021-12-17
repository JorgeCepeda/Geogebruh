package objetos;

import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoCompuesto;
import operaciones.MyMath;

public class Cuenco extends ObjetoCompuesto {
	private static final long serialVersionUID = 1L;

	public Cuenco (double[] pos, String color) {
		super(pos, color, true);
		init();
	}
	
	@SuppressWarnings("serial")
	private void init() {
		double[] despl = MyMath.multipl(getOrient(), 10);
		
		objs().add(new Esfera(getPos(), 9));
		objs(0).cambiarPropiedad(COLOR, propiedad(COLOR));
		Esfera esfera = new Esfera(MyMath.sumar(getPos(), despl), 11, "yellow");
		
		objs().add(new EspacioNegativo(esfera));
		
		for (Objeto obj : objs()) {
			propiedades_objs.put("rotaci�n_" + obj, obj.getRotaci�n());
		}
		
		setActualizar(new Comportamiento() {
			@Override
			public void run() {
				objs(0).setPos(getPos(), true);
				objs(0).cambiarPropiedad(COLOR, propiedad(COLOR));
				actualizarOrientaci�n();
				objs(1).setPos(MyMath.sumar(getPos(), MyMath.multipl(getOrient(), 11)), true);
			}

			private void actualizarOrientaci�n() {
				objs(0).setRotaci�nYOrient(MyMath.sumar(getRotaci�n(), (double[]) propiedades_objs.get("rotaci�n_" + objs(0))));
				objs(1).setRotaci�nYOrient(MyMath.sumar(getRotaci�n(), (double[]) propiedades_objs.get("rotaci�n_" + objs(1))));
			}
		});
		setRotaci�nYOrient(new double[] {Math.sqrt(2)/2, Math.sqrt(2)/2, 0}, 0);
	}
}