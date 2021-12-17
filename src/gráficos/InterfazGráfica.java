package gr�ficos;

import chunks_NoCeldas.Chunks;
import objetos.Objetos;
import objetos.abstracto.Objeto;
import objetos.abstracto.ObjetoCompuesto;

public class InterfazGr�fica {
	
	private InterfazGr�fica() {
		throw new IllegalStateException("Clase no instanciable");
	}
	
	public static char[] modificarFila(char[] fila_car�c, String datos, int alineaci�n) {
		char[] nueva_fila = fila_car�c.clone();
		switch (alineaci�n) {
			case 1:
				for (int i = 0; i < datos.length(); i++) {
					nueva_fila[i] = datos.charAt(i);
				}
				break;
			case -1:
				for (int i = 0; i < datos.length(); i++) {
					nueva_fila[nueva_fila.length - datos.length() + i] = datos.charAt(i);
				}
				break;
			default:
				throw new IllegalArgumentException("Alineaci�n incorrecta");
		}
		return nueva_fila;
	}
	
	public static Object[] modificarFilaYColor(Pantalla p, int fila, String datos, String nuevo_color, int alineaci�n) {
		char[] nueva_fila_car�c = p.tabla_car�c()[fila].clone();
		String[] nueva_fila_color = p.tabla_color()[fila].clone();
		switch (alineaci�n) {
			case 1:
				for (int i = 0; i < datos.length(); i++) {
					nueva_fila_car�c[i + p.extra()] = datos.charAt(i);
					nueva_fila_color[i + p.extra()] = nuevo_color;
				}
				break;
			case -1:
				for (int i = 0; i < datos.length(); i++) {
					nueva_fila_car�c[nueva_fila_car�c.length - p.extra() - datos.length() + i] = datos.charAt(i);
					nueva_fila_color[nueva_fila_car�c.length - p.extra() - datos.length() + i] = nuevo_color;
				}
				break;
			default:
				throw new IllegalArgumentException("Alineaci�n incorrecta");
		}
		return new Object[] {nueva_fila_car�c, nueva_fila_color};
	}
	
	public static String[] modificarColor(String[] fila, String nuevo_color, int pos_inicio, int pos_final) {
		if (pos_final < pos_inicio) throw new IllegalArgumentException("La posici�n final no puede ser menor a la de inicio");
		String[] nueva_fila = fila.clone();
		for (int i = pos_inicio; i <= pos_final; i++) {
			nueva_fila[i] = nuevo_color;
		}
		return nueva_fila;
	}
	
	public static void cursor(Pantalla p) { // Se adapta a diferentes dimensione
		p.tabla_color()[p.tabla_color().length/2][p.tabla_color()[0].length/2] = "#FF0000";
		p.tabla_car�c()[p.tabla_color().length/2][p.tabla_color()[0].length/2] = 'o';
		
		boolean ancho_par = false;
		if (p.tabla_color()[0].length % 2 == 0) {
			p.tabla_color()[p.tabla_color().length/2][p.tabla_color()[0].length/2 - 1] = "#FF0000";
			p.tabla_car�c()[p.tabla_color().length/2][p.tabla_color()[0].length/2 - 1] = 'o';
			ancho_par = true;
		}
		if (p.tabla_color().length % 2 == 0) {
			p.tabla_color()[p.tabla_color().length/2 - 1][p.tabla_color()[0].length/2] = "#FF0000";
			p.tabla_car�c()[p.tabla_color().length/2 - 1][p.tabla_color()[0].length/2] = 'o';
			
			if (ancho_par) {
				p.tabla_color()[p.tabla_color().length/2 - 1][p.tabla_color()[0].length/2 - 1] = "#FF0000";
				p.tabla_car�c()[p.tabla_color().length/2 - 1][p.tabla_color()[0].length/2 - 1] = 'o';
			}
		}
	}
	
	public static void objetoApuntado(Pantalla p) {
		Objeto obj = p.tabla_obj()[p.tabla_obj().length/2][p.tabla_obj()[0].length/2];
		if (obj != null) {
			String texto = obj.toString();
			if (p.tabla_pos() != null && obj instanceof ObjetoCompuesto) texto += " - " + Objetos.objConcreto(obj, p.tabla_pos()[p.tabla_pos().length/2][p.tabla_pos()[0].length/2]);
			Object[] datos = modificarFilaYColor(p, p.extra(), texto, "#FFFFFF", 1);
			p.tabla_car�c()[p.extra()] = (char[]) datos[0];
			p.tabla_color()[p.extra()] = (String[]) datos[1];
		}
	}
	
	public static void orientaci�n(Pantalla p) {
		Object[] datos = modificarFilaYColor(p, p.extra(), String.format("Orient: {%.3f,%.3f,%.3f}", p.orientC�m(0), p.orientC�m(1), p.orientC�m(2)), "#FFFFFF", -1);
		p.tabla_car�c()[p.extra()] = (char[]) datos[0];
		p.tabla_color()[p.extra()] = (String[]) datos[1];
	}
	
	public static void chunksCargados(Pantalla p) {
		Object[] datos = modificarFilaYColor(p, p.extra()+1, "Chunks: " + Chunks.getChunks().size(), "#FFFFFF", 1);
		p.tabla_car�c()[p.extra()+1] = (char[]) datos[0];
		p.tabla_color()[p.extra()+1] = (String[]) datos[1];
	}
}