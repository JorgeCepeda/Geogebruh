package collections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class ExcludingList<E> extends ExcludingCollection<E> implements List<E> {
	private static final long serialVersionUID = 1L;
	private List<E> lista;
	
	@SafeVarargs
	public ExcludingList(List<E> lista, boolean optimizada, @SuppressWarnings("rawtypes") Constructor<? extends Collection> constructor, Class<E> incluir, Class<? extends E>... excluir)
		throws InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException {
		super(lista, optimizada, constructor, incluir, excluir);
		this.lista = lista;
	}
	
	@SafeVarargs
	public ExcludingList(List<E> lista, boolean optimizada, Class<E> incluir, Class<? extends E>... excluir)
		throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		super(lista, optimizada, incluir, excluir);
		this.lista = lista;
	}

	@SafeVarargs
	public ExcludingList(List<E> lista, Class<E> incluir, Class<? extends E>... excluir)
		throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this(lista, true, incluir, excluir);
	}

	@Override
	public void add(int index, E element) {
		if (optimizada) separate(element);
		else {
			lista.add(index, element);
			modificada = true;
		}
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		boolean addAll = lista.addAll(index, c);
		if (optimizada) separate(c);
		else if (addAll) modificada = true;
		return addAll;
	}

	@Override
	public E get(int index) {
		return lista.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return lista.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return lista.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return lista.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return lista.listIterator(index);
	}

	@Override
	public E remove(int index) {
		E e = lista.get(index);
		remove(e);
		return e;
	}

	@Override
	public E set(int index, E element) {
		E e = lista.set(index, element);
		if (e != element) {
			if (optimizada) {
				removeFromSeparated(e);
				separate(element);
			}
			else modificada = true;
		}
		return e;
	}
	
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return lista.subList(fromIndex, toIndex);
	}
}
