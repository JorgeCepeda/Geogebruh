package es.doncomedia.objects.properties;

import java.io.Serializable;

public interface Property extends Serializable {
	class Container<E> implements Property {
		private static final long serialVersionUID = 1L;
		public final E data;
		
		public Container(E data) {
			this.data = data;
		}
	}
}