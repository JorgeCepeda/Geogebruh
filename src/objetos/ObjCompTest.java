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

		objs(1).setRotaciónYOrient(new double[] {Math.PI/4, Math.PI/4, 0});
		propiedades_objs.put("borde_" + objs(0), b);
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
		setRotaciónYOrient(new double[] {0, 1, 0}, Math.PI/4);
		
		Textura hierba = new Textura(
			new PrismaInvisible(new double[] {-5,0,0}, 5, Ref.CENTRO, new Object[] {11.0,11.0}, Bases.RECTANGULAR),
			"textures/grass.png",
			true
		);
		hierba.setEscala(10.0/16, 10.0/16);
		Textura.añadir(objs(0), hierba);
		Textura.añadir(objs(1), hierba);
	}
}