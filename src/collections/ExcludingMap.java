package collections;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ExcludingMap<K,V> implements Map<K,V> { // En este caso los valores se actualizan dependiendo de la implementación del mapa
	private Map<K,V> mapa;
	private ExcludingCollection<V> valores;
	private boolean optimizada;
		
	@SafeVarargs
	public ExcludingMap(Map<K,V> mapa, boolean optimizada, Class<V> incluir, Class<? extends V>... excluir) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this.mapa = mapa;
		valores = new ExcludingCollection<>(new HashSet<>(mapa.values()), optimizada, incluir, excluir); // Le paso un HashSet por tener un constructor utilizable
		this.optimizada = optimizada;
	}

	@SafeVarargs
	public ExcludingMap(Map<K,V> mapa, Class<V> incluir, Class<? extends V>... excluir) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this(mapa, true, incluir, excluir);
	}
	
	public boolean isMap(Map<K,V> colección) {
		return mapa == colección;
	}
	
	public synchronized boolean isOptimized() {
		return optimizada;
	}

	public synchronized void setOptimized(boolean optimized) {
		optimizada = optimized;
		valores.setOptimized(optimized);
	}

	@Override
	public void clear() {
		mapa.clear();
		if (optimizada) valores.clear();
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return mapa.compute(key, remappingFunction);
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return mapa.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return mapa.computeIfPresent(key, remappingFunction);
	}

	@Override
	public boolean containsKey(Object key) {
		return mapa.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return mapa.containsValue(value);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return mapa.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return mapa.equals(o);
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		mapa.forEach(action);
	}

	@Override
	public V get(Object key) {
		return mapa.get(key);
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return mapa.getOrDefault(key, defaultValue);
	}

	@Override
	public int hashCode() {
		return mapa.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return mapa.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return mapa.keySet();
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return mapa.merge(key, value, remappingFunction);
	}

	@Override
	public V put(K key, V value) {
		return mapa.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		mapa.putAll(m);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return mapa.putIfAbsent(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return mapa.remove(key, value);
	}

	@Override
	public V remove(Object key) {
		return mapa.remove(key);
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return mapa.replace(key, oldValue, newValue);
	}

	@Override
	public V replace(K key, V value) {
		return mapa.replace(key, value);
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		mapa.replaceAll(function);
	}

	@Override
	public int size() {
		return mapa.size();
	}

	@Override
	public Collection<V> values() {
		return valores;
	}

}
