package objetos;

import objetos.Prisma.Bases;
import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoCompuesto;
import objetos.propiedades.Borde;
import objetos.propiedades.Textura;
import operaciones.MyMath;

public class ObjCompTest extends ObjetoCompuesto {
	private static final long serialVersionUID = 1L;
	
	public ObjCompTest(double[] pos, String color) {
		super(pos, color, true);
		init();
	}
	
	@SuppressWarnings("serial")
	private void init() {
		/*Test*/
		double[] despl = MyMath.multipl(getOrient(), 11);
		objs().add(new Esfera(getPos(), 5));
		objs().add(new Cubo(MyMath.sumar(getPos(), despl), 10, "wheat"));
		
		Borde b = new Borde("green", 2, 'X');
		objs(0).cambiarPropiedad(BORDE, b);
		objs(0).cambiarPropiedad(COLOR, propiedad(COLOR));

		objs(1).setRotaci�nYOrient(new double[] {Math.PI/4, Math.PI/4, 0});
		propiedades_objs.put("borde_" + objs(0), b);
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
		setRotaci�nYOrient(new double[] {0, 1, 0}, Math.PI/4);
		
		Textura hierba = new Textura(
			new PrismaInvisible(new double[] {-5,0,0}, 5, Ref.CENTRO, new Object[] {11.0,11.0}, Bases.RECTANGULAR),
			"textures/grass.png",
			true
		);
		hierba.setEscala(10.0/16, 10.0/16);
		Textura.a�adir(objs(0), hierba);
		Textura.a�adir(objs(1), hierba);
	}
}