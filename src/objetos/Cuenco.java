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
			propiedades_objs.put("rotación_" + obj, obj.getRotación());
		}
		
		setActualizar(new Comportamiento() {
			@Override
			public void run() {
				objs(0).setPos(getPos(), true);
				objs(0).cambiarPropiedad(COLOR, propiedad(COLOR));
				actualizarOrientación();
				objs(1).setPos(MyMath.sumar(getPos(), MyMath.multipl(getOrient(), 11)), true);
			}

			private void actualizarOrientación() {
				objs(0).setRotaciónYOrient(MyMath.sumar(getRotación(), (double[]) propiedades_objs.get("rotación_" + objs(0))));
				objs(1).setRotaciónYOrient(MyMath.sumar(getRotación(), (double[]) propiedades_objs.get("rotación_" + objs(1))));
			}
		});
		setRotaciónYOrient(new double[] {Math.sqrt(2)/2, Math.sqrt(2)/2, 0}, 0);
	}
}