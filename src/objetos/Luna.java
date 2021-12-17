package objetos;

import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoCompuesto;
import operaciones.MyMath;

/*
 * �No es cierto, �ngel de amor,
 * que en esta apartada orilla
 * m�s puro el debugger brilla
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
			propiedades_objs.put("rotaci�n_" + obj, obj.getRotaci�n());
		}
		
		setActualizar(new Comportamiento() {
			@Override
			public void run() {
				objs(0).setPos(getPos(), true);
				objs(0).cambiarPropiedad(COLOR, propiedad(COLOR));
				actualizarOrientaci�n();
				objs(1).setPos(MyMath.sumar(getPos(), MyMath.multipl(getOrient(), 8)), true);
			}

			private void actualizarOrientaci�n() {
				objs(0).setRotaci�nYOrient(MyMath.sumar(getRotaci�n(), (double[]) propiedades_objs.get("rotaci�n_" + objs(0))));
				objs(1).setRotaci�nYOrient(new double[] {Math.PI/2.0 + getRotaci�n(0), -getRotaci�n(2), getRotaci�n(1)}); // Vector perpendicular
			}
		});
		setRotaci�nYOrient(new double[] {0, 1, 0}, 0);
	}
}