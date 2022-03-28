package es.doncomedia.collections;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class ExcludingCollection<E> implements Collection<E>, Serializable {
	private static final long serialVersionUID = 1L;
	protected Collection<E> included, excluded, total;
	protected LinkedHashSet<Class<? extends E>> excludedClasses;
	protected boolean optimized, modified;
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@SafeVarargs
	public ExcludingCollection(Collection<E> collection, boolean optimized, Constructor<? extends Collection> constructor, Class<E> include,  Class<? extends E>... exclude)
		throws InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException {
		if (include == null) throw new IllegalArgumentException("Included class is null");
		
		int capacity = 16;
		if (exclude.length > 11) capacity = (int) (exclude.length * 1.3);
		excludedClasses = new LinkedHashSet<>(capacity);
		for (Class<? extends E> c : exclude) {
			if (c == null) throw new IllegalArgumentException("Excluded class is null");
			if (c == include) throw new IllegalArgumentException("Excluded class can't be included class");
			if (!excludedClasses.add(c)) throw new IllegalArgumentException("Excluded class repeated: " + c.getName());
		}
		total = collection; 
		
		if (!constructor.isAccessible()) constructor.setAccessible(true);
		excluded = constructor.newInstance();
		included = constructor.newInstance();
		setOptimized(optimized);
	}

	@SafeVarargs
	public ExcludingCollection(Collection<E> collection, boolean optimized, Class<E> include, Class<? extends E>... exclude)
		throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this(collection, optimized, collection.getClass().getDeclaredConstructor(), include, exclude);
	}

	@SafeVarargs
	public ExcludingCollection(Collection<E> collection, Class<E> include, Class<? extends E>... exclude) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this(collection, true, include, exclude);
	}
	
	public Collection<E> getIncludedReadOnly() {
		return Collections.unmodifiableCollection(included);
	}
	
	public Collection<E> getExcludedReadOnly() {
		return Collections.unmodifiableCollection(excluded);
	}
	
	public Collection<E> getTotalReadOnly() {
		return Collections.unmodifiableCollection(total);
	}
	
	public boolean isCollection(Collection<E> collection) {
		return total == collection;
	}
	
	public synchronized boolean isOptimized() {
		return optimized;
	}

	public synchronized void setOptimized(boolean optimized) {
		boolean swOptimizada = this.optimized;
		if (!swOptimizada && (this.optimized = optimized)) {
			if (modified) {
				clearIncludedExcluded();
				modified = false;
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
		if (excludedClasses.contains(t.getClass())) {
			if (excluded.add(t) && !optimized) modified = true;
		}
		else if (included.add(t) && !optimized) modified = true;
	}

	@Override
	public boolean add(E e) {
		if (optimized) separate(e);
		boolean add = total.add(e);
		if (add && !optimized) modified = true;
		return add;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (optimized) separate(c);
		boolean addAll = total.addAll(c);
		if (addAll && !optimized) modified = true;
		return addAll; 
	}

	@Override
	public void clear() {
		clearIncludedExcluded();
		total.clear();
	}
	
	protected void clearIncludedExcluded() {
		included.clear();
		excluded.clear();
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
		if (optimized) removeFromSeparated(o);
		else if (remove) modified = true;
		return remove;
	}

	protected void removeFromSeparated(Object o) {
		if (excludedClasses.contains(o.getClass())) excluded.remove(o);
		else included.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if (optimized) {
			excluded.removeAll(c);
			included.removeAll(c);
		}
		boolean removeAll = total.removeAll(c);
		if (removeAll && !optimized) modified = true;
		return removeAll;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if (optimized) {
			excluded.retainAll(c);
			included.retainAll(c);
		}
		boolean retainAll = total.retainAll(c);
		if (retainAll && !optimized) modified = true;
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