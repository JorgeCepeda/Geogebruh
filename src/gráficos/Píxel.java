package gráficos;

import chunks_NoCeldas.*;
import objetos.Fotón;
import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoBase;
import objetos.propiedades.Color;
import objetos.propiedades.Textura;
import operaciones.Dist;
import operaciones.MyMath;

public class Píxel implements Runnable {
	private double vel_retroceso;
	private int i, j;
	private Pantalla p;
	private Fotón fotón;
	private double[] c_ini, orient_fotón;
	
	public Píxel(int i, int j, Pantalla p, double[] c_ini, double[] orient_fotón, double[] despl_plano, double vel_retroceso) {
		init(i, j, p, c_ini, orient_fotón, despl_plano, vel_retroceso);
		fotón = new Fotón();
	}
	
	public Píxel(int i, int j, Pantalla p, Fotón fotón, double[] c_ini, double[] orient_fotón, double[] despl_plano, double vel_retroceso) {
		init(i, j, p, c_ini, orient_fotón, despl_plano, vel_retroceso);
		this.fotón = fotón;
	}
	
	private void init(int i, int j, Pantalla p, double[] c_ini, double[] orient_fotón, double[] despl_plano, double vel_retroceso) {
		this.i = i;
		this.j = j;
		this.p = p;
		this.vel_retroceso = vel_retroceso;
		
		if (p.cámara().tieneCDV()) {
			double[] celda = p.calcularCelda(i, j, MyMath.sumar(p.cámara().getPos(), despl_plano));
			this.orient_fotón = MyMath.unitario(MyMath.vector(c_ini, celda));
			this.c_ini = c_ini;
		}
		else {
			this.orient_fotón = orient_fotón;
			this.c_ini = p.calcularCelda(i, j, c_ini);
		}
	}
	
	@Override
	public void run() {
		p.tabla_carác()[p.tabla_carác().length-1-i][j] = ' ';
		double[] coord = c_ini.clone();
		fotón.setPos(c_ini, true);
		
		double multiplicador, dist;
		while ((dist = MyMath.round(Dist.puntoAPunto(fotón.getPos(), c_ini), 12)) <= p.cámara().getRender()) { // Proyección del fotón
			double velocidad = 1;
			Chunk chunk = Chunks.getChunk(fotón);
			if (chunk.estáVacío()) {
				// Saltar chunk
				if ((velocidad = Chunks.saltarChunk(fotón.getPos(), orient_fotón, chunk)) == -1) break;
			}
			else {
				fotón.setObjs(chunk.getObjs());
				Malla malla_actual = chunk.getMalla(fotón.getPos(1));
				if (malla_actual == null) {
					// Saltar malla
					if ((velocidad = Chunks.saltarMalla(fotón.getPos(), orient_fotón, chunk)) == -1) break;
				}
				else if (fotón.colisión()) {
					// Retroceder
					Objeto obj = fotón.objCol();
					double[] coord_backup = coord.clone();
					while ((dist = MyMath.round(Dist.puntoAPunto(fotón.getPos(), c_ini), 12)) >= vel_retroceso * 0.6) {
						coord[0] -= orient_fotón[0]*vel_retroceso;
						coord[1] -= orient_fotón[1]*vel_retroceso;
						coord[2] -= orient_fotón[2]*vel_retroceso;
						fotón.setPos(MyMath.fix(coord), false);
						if (p.cámara().esPrecisa() && fotón.colisión() || obj.colisión(fotón) && !fotón.enEspacioNegativo()) coord_backup = coord.clone();
						else break;
					} 
					
					char carácter = '·';
					if (dist < 1) carácter = '×';
					else if (dist <= 2) carácter = '#';
					else if (dist <= 8) carácter = '+';
					else if (dist <= 14) carácter = '-';
					
					// Guardar datos
					Color color_píxel = Textura.color(obj, fotón);
					if (color_píxel == null) color_píxel = fotón.objCol().propiedad(ObjetoBase.COLOR);
					
					p.tabla_carác()[p.tabla_color().length-1-i][j] = carácter;
					p.tabla_obj()[p.tabla_color().length-1-i][j] = fotón.objColContenedor();
					p.tabla_color()[p.tabla_color().length-1-i][j] = color_píxel.getString();
					p.tabla_dist()[p.tabla_color().length-1-i][j] = dist;
					if (p.tieneBorde() || p.isIluminada()) p.tabla_pos()[p.tabla_color().length-1-i][j] = coord_backup;
					break;
				}
				else if (p.cámara().esPrecisa() && (multiplicador = 1 + dist*0.18 /*20 cuando dist es 100*/) < 20) { // Precisión del render
					velocidad = 0.05*multiplicador;
				}
			}
			
			if (p.cámara().esPrecisa()) coord = MyMath.sumar(c_ini, MyMath.multipl(orient_fotón, dist + velocidad));
			else {
				coord[0] += orient_fotón[0]*velocidad;
				coord[1] += orient_fotón[1]*velocidad;
				coord[2] += orient_fotón[2]*velocidad;
			}
			fotón.setPos(MyMath.fix(coord), false);
		}
	}
}