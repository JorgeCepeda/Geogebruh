package gráficos;

import java.util.HashSet;
import java.util.concurrent.Future;

import objetos.Cámara;
import objetos.Fotón;
import operaciones.MyMath;
import otros.Tareas;

public class PantallaMultihilo extends Pantalla { //TODO puede ser útil pasar la creación de secciones a la inicialización y tener un método para pasarle datos, para cuando se reutilice una pantalla, hasta entonces no hace falta
	private static final long serialVersionUID = 1L;

	private class Sección implements Runnable {
		private Object[] datos_píxel;
		private PantallaMultihilo p;
		private int i, j, alto_sección, ancho_sección;
		
		public Sección(PantallaMultihilo p, int fila_esquina_superior, int columna_esquina_superior, int alto_sección, int ancho_sección, Object[] datos_píxel) {
			this.p = p;
			i = fila_esquina_superior;
			j = columna_esquina_superior;
			this.alto_sección = alto_sección;
			this.ancho_sección = ancho_sección;
			this.datos_píxel = datos_píxel;
		}
		
		@Override
		public void run() {
			sumarHilo();
			Fotón fotón = new Fotón();
			for (int k = 0; k < alto_sección; k++) {
				for (int l = 0; l < ancho_sección; l++) {
					new Píxel(i-k, j+l, p, fotón, (double[]) datos_píxel[0], (double[]) datos_píxel[1], (double[]) datos_píxel[2], (double) datos_píxel[3]).run();
				}
			}
			restarHilo();
		}
	}
	
	private int secciones_ancho = 1, secciones_alto = 1;
	
	public PantallaMultihilo(int alto, int ancho, int núm_secciones, boolean swBorde, Cámara cámara) {
		super(alto, ancho, swBorde, cámara);
		setNúmSecciones(núm_secciones);
	}
	
	public PantallaMultihilo(int alto, int ancho, boolean swBorde, Cámara cámara) {
		super(alto, ancho, swBorde, cámara);
	}
	
	public void setNúmSecciones(int núm_secciones) {
		if (núm_secciones < 1) throw new IllegalArgumentException("Número de secciones inválido (" + núm_secciones + ")");
		if (núm_secciones % 2 == 1) setNúmSecciones(1, núm_secciones);
		else setNúmSecciones(2, núm_secciones / 2);
	}
	
	public void setNúmSecciones(int secciones_alto, int secciones_ancho) {
		if (secciones_alto < 1 || secciones_ancho < 1) throw new IllegalArgumentException("Número de secciones inválido (" + secciones_ancho + "x" + secciones_alto +")");
		this.secciones_alto = secciones_alto;
		this.secciones_ancho = secciones_ancho;
	}
	
	@Override
	public synchronized void renderizar() {
		init();

		double[] c_ini = cámara().getPos(), orient_fotón = orientCám(), despl_plano = null;
		int[] dimens = getDimensiones();
		double vel_retroceso = 0.1;
		if (cámara().esPrecisa()) vel_retroceso = 0.001;
		if (cámara().tieneCDV()) despl_plano = MyMath.multipl(orient_fotón, MyMath.fix((double) dimens[1] / 2 / Math.tan(cámara().getTeta_CDV() / 2)));
		
		HashSet<Future<?>> secciones = new HashSet<>((int) (secciones_alto*secciones_ancho*1.3));
		// División de la pantalla en partes independientes paralelas
		int alto = dimens[0] + 2*extra(), ancho = dimens[1] + 2*extra(), resto_v = alto % secciones_alto, despl_v = alto - 1;
		for (int i = 0; i < secciones_alto; i++) {
			int resto_h = ancho % secciones_ancho, alto_sección = alto / secciones_alto + Integer.signum(resto_v), despl_h = 0;
			if (resto_v > 0) resto_v--;
			for (int j = 0; j < secciones_ancho; j++) {
				int ancho_sección = ancho / secciones_ancho + Integer.signum(resto_h);
				if (resto_h > 0) resto_h--;
				secciones.add(Tareas.píxeles.getES().submit(new Sección(this, despl_v, despl_h, alto_sección, ancho_sección, new Object[] {
					c_ini, orient_fotón, despl_plano, vel_retroceso})));
				despl_h += ancho_sección;
			}
			despl_v -= alto_sección;
		}
		Tareas.esperar(secciones);
		
		postProcesado();
		endRender();
	}
}