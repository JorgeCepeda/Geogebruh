package gr�ficos;

import java.util.HashSet;
import java.util.concurrent.Future;

import objetos.C�mara;
import objetos.Fot�n;
import operaciones.MyMath;
import otros.Tareas;

public class PantallaMultihilo extends Pantalla { //TODO puede ser �til pasar la creaci�n de secciones a la inicializaci�n y tener un m�todo para pasarle datos, para cuando se reutilice una pantalla, hasta entonces no hace falta
	private static final long serialVersionUID = 1L;

	private class Secci�n implements Runnable {
		private Object[] datos_p�xel;
		private PantallaMultihilo p;
		private int i, j, alto_secci�n, ancho_secci�n;
		
		public Secci�n(PantallaMultihilo p, int fila_esquina_superior, int columna_esquina_superior, int alto_secci�n, int ancho_secci�n, Object[] datos_p�xel) {
			this.p = p;
			i = fila_esquina_superior;
			j = columna_esquina_superior;
			this.alto_secci�n = alto_secci�n;
			this.ancho_secci�n = ancho_secci�n;
			this.datos_p�xel = datos_p�xel;
		}
		
		@Override
		public void run() {
			sumarHilo();
			Fot�n fot�n = new Fot�n();
			for (int k = 0; k < alto_secci�n; k++) {
				for (int l = 0; l < ancho_secci�n; l++) {
					new P�xel(i-k, j+l, p, fot�n, (double[]) datos_p�xel[0], (double[]) datos_p�xel[1], (double[]) datos_p�xel[2], (double) datos_p�xel[3]).run();
				}
			}
			restarHilo();
		}
	}
	
	private int secciones_ancho = 1, secciones_alto = 1;
	
	public PantallaMultihilo(int alto, int ancho, int n�m_secciones, boolean swBorde, C�mara c�mara) {
		super(alto, ancho, swBorde, c�mara);
		setN�mSecciones(n�m_secciones);
	}
	
	public PantallaMultihilo(int alto, int ancho, boolean swBorde, C�mara c�mara) {
		super(alto, ancho, swBorde, c�mara);
	}
	
	public void setN�mSecciones(int n�m_secciones) {
		if (n�m_secciones < 1) throw new IllegalArgumentException("N�mero de secciones inv�lido (" + n�m_secciones + ")");
		if (n�m_secciones % 2 == 1) setN�mSecciones(1, n�m_secciones);
		else setN�mSecciones(2, n�m_secciones / 2);
	}
	
	public void setN�mSecciones(int secciones_alto, int secciones_ancho) {
		if (secciones_alto < 1 || secciones_ancho < 1) throw new IllegalArgumentException("N�mero de secciones inv�lido (" + secciones_ancho + "x" + secciones_alto +")");
		this.secciones_alto = secciones_alto;
		this.secciones_ancho = secciones_ancho;
	}
	
	@Override
	public synchronized void renderizar() {
		init();

		double[] c_ini = c�mara().getPos(), orient_fot�n = orientC�m(), despl_plano = null;
		int[] dimens = getDimensiones();
		double vel_retroceso = 0.1;
		if (c�mara().esPrecisa()) vel_retroceso = 0.001;
		if (c�mara().tieneCDV()) despl_plano = MyMath.multipl(orient_fot�n, MyMath.fix((double) dimens[1] / 2 / Math.tan(c�mara().getTeta_CDV() / 2)));
		
		HashSet<Future<?>> secciones = new HashSet<>((int) (secciones_alto*secciones_ancho*1.3));
		// Divisi�n de la pantalla en partes independientes paralelas
		int alto = dimens[0] + 2*extra(), ancho = dimens[1] + 2*extra(), resto_v = alto % secciones_alto, despl_v = alto - 1;
		for (int i = 0; i < secciones_alto; i++) {
			int resto_h = ancho % secciones_ancho, alto_secci�n = alto / secciones_alto + Integer.signum(resto_v), despl_h = 0;
			if (resto_v > 0) resto_v--;
			for (int j = 0; j < secciones_ancho; j++) {
				int ancho_secci�n = ancho / secciones_ancho + Integer.signum(resto_h);
				if (resto_h > 0) resto_h--;
				secciones.add(Tareas.p�xeles.getES().submit(new Secci�n(this, despl_v, despl_h, alto_secci�n, ancho_secci�n, new Object[] {
					c_ini, orient_fot�n, despl_plano, vel_retroceso})));
				despl_h += ancho_secci�n;
			}
			despl_v -= alto_secci�n;
		}
		Tareas.esperar(secciones);
		
		postProcesado();
		endRender();
	}
}