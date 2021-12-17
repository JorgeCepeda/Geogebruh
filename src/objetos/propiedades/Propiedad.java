package objetos.propiedades;

import java.io.Serializable;

public interface Propiedad extends Serializable {
	class Container<E> implements Propiedad {
		private static final long serialVersionUID = 1L;
		public final E dato;
		
		public Container(E dato) {
			this.dato = dato;
		}
		
	}
	
}
