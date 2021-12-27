package es.doncomedia.objects;

import es.doncomedia.objects.abstracts.Projection;

public class Camera extends Projection {
	private static final long serialVersionUID = 1L;
	private double fovTheta, render;
	private boolean swFOV, swPrecise;

	public Camera(double[] pos, double render, boolean swPrecise, double fovTheta) {
		super(pos);
		setRender(render);
		setFOVTheta(fovTheta);
		setPrecise(swPrecise);
	}
	
	public Camera(double[] pos, double render, boolean swPrecise) {
		super(pos);
		setRender(render);
		setPrecise(swPrecise);
	}
	
	public boolean hasFOV() {
		return swFOV;
	}

	public void setFOV(boolean swFOV) {
		this.swFOV = swFOV;
	}

	public double getRender() {
		return render;
	}

	public void setRender(double render) {
		if (render < 0) throw new IllegalArgumentException("Negative render distance");
		this.render = render;
	}

	public double getFOVTheta() {
		if (!swFOV) throw new IllegalArgumentException("There's no field of view");
		return fovTheta;
	}

	public void setFOVTheta(double fovTheta) {
		if (fovTheta < 0) throw new IllegalArgumentException("Negative field of view");
		this.fovTheta = fovTheta;
		swFOV = true;
	}
	
	public boolean isPrecise() {
		return swPrecise;
	}
	
	public void setPrecise(boolean swPrecise) {
		this.swPrecise = swPrecise;
	}
	
	@Override
	public boolean collision() {
		return false;
	}
}