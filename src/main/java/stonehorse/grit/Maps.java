package stonehorse.grit;

import stonehorse.grit.map.ToMapCollector;
import stonehorse.grit.map.array.PArrayMap;

import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

import static stonehorse.candy.Iterables.fold;

/**
 * Creation of persistent Associative Map structures that accept null as ordinary valid key value.
 * The maps are read only, while mutation is an expression and hence a new value.
 * The maps may differ in implementation depending on content and size.
 * Larger maps share structure with previous maps.
 * All element, keys and values, are expected to be effectively immutable.
 */
public class Maps {
    static public <K,V> PersistentMap<K,V>  mapOf(
            Map<? extends K,? extends V> elements) {
        return PArrayMap.<K,V>empty().withAll(elements);
    }

    /**
     * The empty persistent map
     */
    static public <K,V> PersistentMap<K,V>  map() {
        return PArrayMap.empty();
    }

    /**
     * A single value map
     */
    static public <K,V> PersistentMap<K,V>  map(K key, V value) {
        return PArrayMap.<K,V>empty()
                .with(key, value);
    }

    /**
     * A persistent map of the successive pairs of key and values
     */
    static public <K,V> PersistentMap<K,V>  map(K k1, V v1, K k2, V v2) {
        return PArrayMap.<K,V>empty()
                .with(k1, v1)
                .with(k2, v2);
    }
    /**
     * A persistent map of the successive pairs of key and values
     */
    static public <K,V> PersistentMap<K,V>  map(K k1, V v1, K k2, V v2, K k3, V v3) {
        return PArrayMap.<K,V>empty()
                .with(k1, v1)
                .with(k2, v2)
                .with(k3, v3);
    }
    /**
     * A persistent map of the successive pairs of key and values
     */
    static public <K,V> PersistentMap<K,V>  map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return PArrayMap.<K,V>empty()
                .with(k1, v1)
                .with(k2, v2)
                .with(k3, v3)
                .with(k4, v4);
    }
    /**
     * A persistent map of the successive pairs of key and values
     */
    static public <K,V> PersistentMap<K,V>  map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return PArrayMap.<K,V>empty()
                .with(k1, v1)
                .with(k2, v2)
                .with(k3, v3)
                .with(k4, v4)
                .with(k5, v5);
    }
    /**
     * A persistent map of the successive pairs of key and values
     */
    static public <K,V> PersistentMap<K,V>  map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return PArrayMap.<K,V>empty()
                .with(k1, v1)
                .with(k2, v2)
                .with(k3, v3)
                .with(k4, v4)
                .with(k5, v5)
                .with(k6, v6);
    }
    /**
     * A persistent map of the successive pairs of key and values
     */
    static public <K,V> PersistentMap<K,V>  map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6,
    K k7, V v7) {
        return PArrayMap.<K,V>empty()
                .with(k1, v1)
                .with(k2, v2)
                .with(k3, v3)
                .with(k4, v4)
                .with(k5, v5)
                .with(k6, v6)
                .with(k7, v7);
    }
    /**
     * A persistent map of the successive pairs of key and values
     */
    static public <K,V> PersistentMap<K,V>  map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6,
                                                K k7, V v7, K k8, V v8) {
        return PArrayMap.<K,V>empty()
                .with(k1, v1)
                .with(k2, v2)
                .with(k3, v3)
                .with(k4, v4)
                .with(k5, v5)
                .with(k6, v6)
                .with(k7, v7)
                .with(k8, v8);
    }

    /**
     * A persistent map of the successive pairs of key and values
     */
    static public <K,V> PersistentMap<K,V>  map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6,
                                                K k7, V v7, K k8, V v8, K k9, V v9) {
        return PArrayMap.<K,V>empty()
                .with(k1, v1)
                .with(k2, v2)
                .with(k3, v3)
                .with(k4, v4)
                .with(k5, v5)
                .with(k6, v6)
                .with(k7, v7)
                .with(k8, v8)
                .with(k9, v9);
    }
    /**
     * A persistent map of the successive pairs of key and values
     */
    static public <K,V> PersistentMap<K,V>  map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6,
                                                K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
        return PArrayMap.<K,V>empty()
                .with(k1, v1)
                .with(k2, v2)
                .with(k3, v3)
                .with(k4, v4)
                .with(k5, v5)
                .with(k6, v6)
                .with(k7, v7)
                .with(k8, v8)
                .with(k9, v9)
                .with(k10, v10);
    }

    /**
     * Termination of stream of elements into Persistent Map where mapper functions are used to for key and value,
     * and value confilicts are resolved using a merge function.
     */
    public static <T,K,U> Collector<T,?,PersistentMap<K,U>>
    toMap(Function<? super T,? extends K> keyMapper,
          Function<? super T,? extends U> valueMapper,
          BinaryOperator<U> mergeFunction){
        return ToMapCollector.collector(keyMapper, valueMapper, mergeFunction, map());
    }

    /**
     * A persistent map made of map entries
     */
    public static <K,V> PersistentMap<K,V> fromEntries(Iterable<Map.Entry<K, V>> entries){
        return fold((acc, entry)->
                        acc.with(entry.getKey(), entry.getValue()),
                map(),
                entries);
    }
}

