package stonehorse.grit;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Associative composition of homogeneous values where modification is expression.
 */
public interface Associative<K, V> extends Lookup<V>{
    /**
     * This associated with new key and value binding, if key is not yet used as association
     */
    Associative<K, V> ensureKey(K key, V value);

    /**
     * This associated with new key and lazy value binding, when key is not yet used as association
     */
    Associative<K, V> whenMissing(K key, Supplier<V> valueSupplier);

    /**
     * This associated with the key and value binding
     */
    Associative<K, V> with(K key, V value);

    /**
     * This without binding of key
     */
    Associative<K, V> without(Object key);
    /**
     * This without binding of key when value match predicate
     */
    Associative<K, V> withoutWhen(Object key, Predicate<V> predicate);
    }
