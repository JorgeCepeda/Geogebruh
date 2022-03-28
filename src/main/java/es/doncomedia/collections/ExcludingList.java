package es.doncomedia.collections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class ExcludingList<E> extends ExcludingCollection<E> implements List<E> {
	private static final long serialVersionUID = 1L;
	private List<E> list;
	
	@SafeVarargs
	public ExcludingList(List<E> list, boolean optimized, @SuppressWarnings("rawtypes") Constructor<? extends Collection> constructor, Class<E> include, Class<? extends E>... exclude)
		throws InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException {
		super(list, optimized, constructor, include, exclude);
		this.list = list;
	}
	
	@SafeVarargs
	public ExcludingList(List<E> list, boolean optimized, Class<E> include, Class<? extends E>... exclude)
		throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		super(list, optimized, include, exclude);
		this.list = list;
	}

	@SafeVarargs
	public ExcludingList(List<E> list, Class<E> include, Class<? extends E>... exclude)
		throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this(list, true, include, exclude);
	}

	@Override
	public void add(int index, E element) {
		if (optimized) separate(element);
		else {
			list.add(index, element);
			modified = true;
		}
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		boolean addAll = list.addAll(index, c);
		if (optimized) separate(c);
		else if (addAll) modified = true;
		return addAll;
	}

	@Override
	public E get(int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public E remove(int index) {
		E e = list.get(index);
		remove(e);
		return e;
	}

	@Override
	public E set(int index, E element) {
		E e = list.set(index, element);
		if (e != element) {
			if (optimized) {
				removeFromSeparated(e);
				separate(element);
			}
			else modified = true;
		}
		return e;
	}
	
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}
}
