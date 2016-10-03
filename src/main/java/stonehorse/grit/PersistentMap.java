package stonehorse.grit;

import java.util.Map;

/**
 * A read only map where mutation is expression.
 * All mutating methods found in super types should throw UnsupportedOperationException
 */
public interface PersistentMap<K, V> extends Map<K,V>, Associative<K, V> {
	/**
	 * This map with additional key value association if such key was not already present
	 */
	@Override
	PersistentMap<K, V> ensureKey(K key, V value);

	/**
	 * This map with key value association
	 */
	@Override
	PersistentMap<K, V> with(K key, V value);

	/**
	 * This map without key association identified by key
	 */
	@Override
	PersistentMap<K, V> without(Object key);

	/**
	 * This map with all associations found in map
	 */
	PersistentMap<K, V> withAll(Map<? extends K, ? extends V> map);

	/**
	 * This map without all associations identified by keys
	 */
	PersistentMap<K, V> withoutAll(Iterable<?> keys);

}
