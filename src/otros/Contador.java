package otros;

import java.util.LinkedHashMap;

import objetos.abstracto.Objeto;

public class Contador {
	private static final Llave acceso_contadores = new Llave(false);
	private static final LinkedHashMap<Class<?>, Contador> contadores = new LinkedHashMap<>();
	private int cont;
	private boolean listado; // Si est� en la lista de contadores puede contar objetos y asignarles un n�mero
	
	private Contador(boolean listar, int cont) {
		listado = listar;
		setCont(cont);
	}
	
	public Contador(int cont) {
		setCont(cont);
	}
	
	public synchronized int getCont() {
		return cont;
	}
	
	public synchronized void setCont(int cont) {
		this.cont = cont;
	}
	
	public synchronized void variar(int cantidad) {
		cont += cantidad;
	}
	
	public boolean isListado() {
		return listado;
	}
	
	public void contar(Objeto obj) {
		if (listado) obj.setCont(cont++);
		else throw new IllegalArgumentException("No se puede listar, el contador no est� en lista");
	}
	
	public static void listar(Objeto obj) { // Asigna el n�mero al objeto con el contador de su clase
		Contador nuevo = new Contador(true, 0), existente = contadores.putIfAbsent(obj.getClass(), nuevo);
		if (existente == null) nuevo.contar(obj);
		else existente.contar(obj);
	}
	
	public static void reset(Llave llave) {
		if (llave == acceso_contadores && llave.isPose�da()) {
			for (Contador contador : contadores.values()) {
				contador.setCont(0);
			}
		}
		else throw new IllegalArgumentException("Esa llave no est� pose�da o es incorrecta");
	}
	
	public static Contador getContador(Objeto obj, Llave llave) {
		return getContador(obj.getClass(), llave);
	}
	
	public static Contador getContador(Class<?> clase, Llave llave) {
		if (llave == acceso_contadores && llave.isPose�da()) return contadores.get(clase);
		throw new IllegalArgumentException("Esa llave no est� pose�da o es incorrecta");
	}
	
	public static Llave obtenerAcceso() throws IllegalAccessException {
		return acceso_contadores.poseer();
	}
}