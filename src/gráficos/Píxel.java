package gr�ficos;

import chunks_NoCeldas.*;
import objetos.Fot�n;
import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoBase;
import objetos.propiedades.Color;
import objetos.propiedades.Textura;
import operaciones.Dist;
import operaciones.MyMath;

public class P�xel implements Runnable {
	private double vel_retroceso;
	private int i, j;
	private Pantalla p;
	private Fot�n fot�n;
	private double[] c_ini, orient_fot�n;
	
	public P�xel(int i, int j, Pantalla p, double[] c_ini, double[] orient_fot�n, double[] despl_plano, double vel_retroceso) {
		init(i, j, p, c_ini, orient_fot�n, despl_plano, vel_retroceso);
		fot�n = new Fot�n();
	}
	
	public P�xel(int i, int j, Pantalla p, Fot�n fot�n, double[] c_ini, double[] orient_fot�n, double[] despl_plano, double vel_retroceso) {
		init(i, j, p, c_ini, orient_fot�n, despl_plano, vel_retroceso);
		this.fot�n = fot�n;
	}
	
	private void init(int i, int j, Pantalla p, double[] c_ini, double[] orient_fot�n, double[] despl_plano, double vel_retroceso) {
		this.i = i;
		this.j = j;
		this.p = p;
		this.vel_retroceso = vel_retroceso;
		
		if (p.c�mara().tieneCDV()) {
			double[] celda = p.calcularCelda(i, j, MyMath.sumar(p.c�mara().getPos(), despl_plano));
			this.orient_fot�n = MyMath.unitario(MyMath.vector(c_ini, celda));
			this.c_ini = c_ini;
		}
		else {
			this.orient_fot�n = orient_fot�n;
			this.c_ini = p.calcularCelda(i, j, c_ini);
		}
	}
	
	@Override
	public void run() {
		p.tabla_car�c()[p.tabla_car�c().length-1-i][j] = ' ';
		double[] coord = c_ini.clone();
		fot�n.setPos(c_ini, true);
		
		double multiplicador, dist;
		while ((dist = MyMath.round(Dist.puntoAPunto(fot�n.getPos(), c_ini), 12)) <= p.c�mara().getRender()) { // Proyecci�n del fot�n
			double velocidad = 1;
			Chunk chunk = Chunks.getChunk(fot�n);
			if (chunk.est�Vac�o()) {
				// Saltar chunk
				if ((velocidad = Chunks.saltarChunk(fot�n.getPos(), orient_fot�n, chunk)) == -1) break;
			}
			else {
				fot�n.setObjs(chunk.getObjs());
				Malla malla_actual = chunk.getMalla(fot�n.getPos(1));
				if (malla_actual == null) {
					// Saltar malla
					if ((velocidad = Chunks.saltarMalla(fot�n.getPos(), orient_fot�n, chunk)) == -1) break;
				}
				else if (fot�n.colisi�n()) {
					// Retroceder
					Objeto obj = fot�n.objCol();
					double[] coord_backup = coord.clone();
					while ((dist = MyMath.round(Dist.puntoAPunto(fot�n.getPos(), c_ini), 12)) >= vel_retroceso * 0.6) {
						coord[0] -= orient_fot�n[0]*vel_retroceso;
						coord[1] -= orient_fot�n[1]*vel_retroceso;
						coord[2] -= orient_fot�n[2]*vel_retroceso;
						fot�n.setPos(MyMath.fix(coord), false);
						if (p.c�mara().esPrecisa() && fot�n.colisi�n() || obj.colisi�n(fot�n) && !fot�n.enEspacioNegativo()) coord_backup = coord.clone();
						else break;
					} 
					
					char car�cter = '�';
					if (dist < 1) car�cter = '�';
					else if (dist <= 2) car�cter = '#';
					else if (dist <= 8) car�cter = '+';
					else if (dist <= 14) car�cter = '-';
					
					// Guardar datos
					Color color_p�xel = Textura.color(obj, fot�n);
					if (color_p�xel == null) color_p�xel = fot�n.objCol().propiedad(ObjetoBase.COLOR);
					
					p.tabla_car�c()[p.tabla_color().length-1-i][j] = car�cter;
					p.tabla_obj()[p.tabla_color().length-1-i][j] = fot�n.objColContenedor();
					p.tabla_color()[p.tabla_color().length-1-i][j] = color_p�xel.getString();
					p.tabla_dist()[p.tabla_color().length-1-i][j] = dist;
					if (p.tieneBorde() || p.isIluminada()) p.tabla_pos()[p.tabla_color().length-1-i][j] = coord_backup;
					break;
				}
				else if (p.c�mara().esPrecisa() && (multiplicador = 1 + dist*0.18 /*20 cuando dist es 100*/) < 20) { // Precisi�n del render
					velocidad = 0.05*multiplicador;
				}
			}
			
			if (p.c�mara().esPrecisa()) coord = MyMath.sumar(c_ini, MyMath.multipl(orient_fot�n, dist + velocidad));
			else {
				coord[0] += orient_fot�n[0]*velocidad;
				coord[1] += orient_fot�n[1]*velocidad;
				coord[2] += orient_fot�n[2]*velocidad;
			}
			fot�n.setPos(MyMath.fix(coord), false);
		}
	}
}