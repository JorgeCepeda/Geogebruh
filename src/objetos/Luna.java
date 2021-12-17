package objetos;

import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoCompuesto;
import operaciones.MyMath;

/*
 * ¿No es cierto, ángel de amor,
 * que en esta apartada orilla
 * más puro el debugger brilla
 * y se programa mejor?
 */
public class Luna extends ObjetoCompuesto {
	private static final long serialVersionUID = 1L;

	public Luna (double[] pos, String color) {
		super(pos, color, true);
		init();
	}
	
	@SuppressWarnings("serial")
	private void init() {
		double[] despl = MyMath.multipl(getOrient(), 8);
		
		objs().add(new Esfera(getPos(), 9));
		objs(0).cambiarPropiedad(COLOR, propiedad(COLOR));
		Cilindro cilindro = new Cilindro(MyMath.sumar(getPos(), despl), new double[] {0,0,1}, 11, 24, Ref.CENTRO, "yellow");
		
		objs().add(new EspacioNegativo(cilindro));
		
		for (Objeto obj : objs()) {
			propiedades_objs.put("rotación_" + obj, obj.getRotación());
		}
		
		setActualizar(new Comportamiento() {
			@Override
			public void run() {
				objs(0).setPos(getPos(), true);
				objs(0).cambiarPropiedad(COLOR, propiedad(COLOR));
				actualizarOrientación();
				objs(1).setPos(MyMath.sumar(getPos(), MyMath.multipl(getOrient(), 8)), true);
			}

			private void actualizarOrientación() {
				objs(0).setRotaciónYOrient(MyMath.sumar(getRotación(), (double[]) propiedades_objs.get("rotación_" + objs(0))));
				objs(1).setRotaciónYOrient(new double[] {Math.PI/2.0 + getRotación(0), -getRotación(2), getRotación(1)}); // Vector perpendicular
			}
		});
		setRotaciónYOrient(new double[] {0, 1, 0}, 0);
	}
}