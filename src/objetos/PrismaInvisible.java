package objetos;

import java.util.concurrent.ConcurrentHashMap;

import objetos.propiedades.Propiedad;

public final class PrismaInvisible extends Prisma {
	private static final long serialVersionUID = 1L;

	public PrismaInvisible(double[] pos, int altura, Ref ref, Object[] datos, Base base) {
		super(pos, altura, ref, datos, base);
	}
	
	public PrismaInvisible(PrismaInvisible p, boolean clonarDatos) {
		super(p, false, clonarDatos);
	}
	
	@Override
	public <T extends Propiedad> T añadirPropiedad(String nombre, Propiedad propiedad) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean cambiarPropiedad(String nombre, Propiedad propiedad) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cambiarPropiedades(ConcurrentHashMap<String, Propiedad> propiedades, boolean enlazar) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void numerar() {
		// No se puede numerar
	}
}