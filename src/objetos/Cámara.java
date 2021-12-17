package objetos;

import objetos.abstracto.Proyección;

public class Cámara extends Proyección {
	private static final long serialVersionUID = 1L;
	private double teta_cdv, render;
	private boolean swCDV, swPreciso;

	public Cámara(double[] pos, double render, boolean swPreciso, double teta_cdv) {
		super(pos);
		setRender(render);
		setTeta_CDV(teta_cdv);
		setPrecisión(swPreciso);
	}
	
	public Cámara(double[] pos, double render, boolean swPreciso) {
		super(pos);
		setRender(render);
		setPrecisión(swPreciso);
	}
	
	public boolean tieneCDV() {
		return swCDV;
	}

	public void setCDV(boolean swCDV) {
		this.swCDV = swCDV;
	}

	public double getRender() {
		return render;
	}

	public void setRender(double render) {
		if (render < 0) throw new IllegalArgumentException("Distancia de dibujado negativa");
		this.render = render;
	}

	public double getTeta_CDV() {
		if (!swCDV) throw new IllegalArgumentException("No hay campo de visión");
		return teta_cdv;
	}

	public void setTeta_CDV(double teta_cdv) {
		if (teta_cdv < 0) throw new IllegalArgumentException("Campo de visión negativo");
		this.teta_cdv = teta_cdv;
		swCDV = true;
	}
	
	public boolean esPrecisa() {
		return swPreciso;
	}
	
	public void setPrecisión(boolean swPreciso) {
		this.swPreciso = swPreciso;
	}
	
	@Override
	public boolean colisión() {
		return false;
	}
}