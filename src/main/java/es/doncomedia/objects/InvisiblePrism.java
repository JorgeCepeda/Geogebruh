package es.doncomedia.objects;

import java.util.concurrent.ConcurrentHashMap;

import es.doncomedia.objects.properties.Property;

public final class InvisiblePrism extends Prism {
	private static final long serialVersionUID = 1L;

	public InvisiblePrism(double[] pos, int height, Ref ref, Object[] data, Base base) {
		super(pos, height, ref, data, base);
	}
	
	public InvisiblePrism(InvisiblePrism p, boolean cloneData) {
		super(p, false, cloneData);
	}
	
	@Override
	public <T extends Property> T addProperty(String name, Property property) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean changeProperty(String name, Property property) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void changeProperties(ConcurrentHashMap<String, Property> properties, boolean link) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void numerate() {
		// Can't be numerated
	}
}