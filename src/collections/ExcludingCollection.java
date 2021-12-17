package collections;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class ExcludingCollection<E> implements Collection<E>, Serializable {
	private static final long serialVersionUID = 1L;
	protected Collection<E> incluidos, excluidos, total;
	protected LinkedHashSet<Class<? extends E>> clases_excluidas;
	protected boolean optimizada, modificada;
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@SafeVarargs
	public ExcludingCollection(Collection<E> colección, boolean optimizada, Constructor<? extends Collection> constructor, Class<E> incluir,  Class<? extends E>... excluir)
		throws InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException {
		if (incluir == null) throw new NullPointerException("Clase a incluir nula");
		
		int capacidad = 16;
		if (excluir.length > 11) capacidad = (int) (excluir.length * 1.3);
		clases_excluidas = new LinkedHashSet<>(capacidad);
		for (Class<? extends E> c : excluir) {
			if (c == null) throw new NullPointerException("Clase a excluir nula");
			if (c == incluir) throw new IllegalArgumentException("La clase a excluir no puede ser la clase a incluir");
			if (!clases_excluidas.add(c)) throw new IllegalArgumentException("Clase a excluir repetida");
		}
		total = colección; 
		
		if (!constructor.isAccessible()) constructor.setAccessible(true);
		excluidos = constructor.newInstance();
		incluidos = constructor.newInstance();
		setOptimized(optimizada);
	}

	@SafeVarargs
	public ExcludingCollection(Collection<E> colección, boolean optimizada, Class<E> incluir, Class<? extends E>... excluir)
		throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this(colección, optimizada, colección.getClass().getDeclaredConstructor(), incluir, excluir);
	}

	@SafeVarargs
	public ExcludingCollection(Collection<E> colección, Class<E> incluir, Class<? extends E>... excluir) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this(colección, true, incluir, excluir);
	}
	
	public Collection<E> getIncludedReadOnly() {
		return Collections.unmodifiableCollection(incluidos);
	}
	
	public Collection<E> getExcludedReadOnly() {
		return Collections.unmodifiableCollection(excluidos);
	}
	
	public Collection<E> getTotalReadOnly() {
		return Collections.unmodifiableCollection(total);
	}
	
	public boolean isCollection(Collection<E> colección) {
		return total == colección;
	}
	
	public synchronized boolean isOptimized() {
		return optimizada;
	}

	public synchronized void setOptimized(boolean optimized) {
		boolean swOptimizada = optimizada;
		if (!swOptimizada && (optimizada = optimized)) {
			if (modificada) {
				clearIncludedExcluded();
				modificada = false;
			}
			separate(total);
		}
	}
	
	/**
	 * Separates the collection into excluded and included items
	 */
	protected void separate(Collection<? extends E> c) {
		for (E t : c) {
			separate(t);
		}
	}

	/**
	 * Separates the item into excluded and included collections
	 */
	protected void separate(E t) {
		if (clases_excluidas.contains(t.getClass())) {
			if (excluidos.add(t) && !optimizada) modificada = true;
		}
		else if (incluidos.add(t) && !optimizada) modificada = true;
	}

	@Override
	public boolean add(E e) {
		if (optimizada) separate(e);
		boolean add = total.add(e);
		if (add && !optimizada) modificada = true;
		return add;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (optimizada) separate(c);
		boolean addAll = total.addAll(c);
		if (addAll && !optimizada) modificada = true;
		return addAll; 
	}

	@Override
	public void clear() {
		clearIncludedExcluded();
		total.clear();
	}
	
	protected void clearIncludedExcluded() {
		incluidos.clear();
		excluidos.clear();
	}

	@Override
	public boolean contains(Object o) {
		return total.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return total.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return total.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return total.iterator();
	}

	@Override
	public boolean remove(Object o) {
		boolean remove = total.remove(o);
		if (optimizada) removeFromSeparated(o);
		else if (remove) modificada = true;
		return remove;
	}

	protected void removeFromSeparated(Object o) {
		if (clases_excluidas.contains(o.getClass())) excluidos.remove(o);
		else incluidos.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if (optimizada) {
			excluidos.removeAll(c);
			incluidos.removeAll(c);
		}
		boolean removeAll = total.removeAll(c);
		if (removeAll && !optimizada) modificada = true;
		return removeAll;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if (optimizada) {
			excluidos.retainAll(c);
			incluidos.retainAll(c);
		}
		boolean retainAll = total.retainAll(c);
		if (retainAll && !optimizada) modificada = true;
		return retainAll;
	}

	@Override
	public int size() {
		return total.size();
	}

	@Override
	public Object[] toArray() {
		return total.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return total.toArray(a);
	}
}