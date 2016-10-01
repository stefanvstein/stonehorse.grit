package stonehorse.grit.map;


import stonehorse.grit.Associative;
import stonehorse.grit.PersistentMap;

import java.util.Map;

public interface EphemeralMap<K, V>
        extends Associative<K, V> {

    public boolean isEmpty();

	public EphemeralMap<K, V> withoutAll(Iterable<?> iterable) ;

	public EphemeralMap<K, V> withAll(Map<? extends K, ? extends V> map) ;

    public int size() ;

    public V get(Object key, V otherwise);

    @Override default public V get(Object key) {
        return get(key, null);
    }


    public PersistentMap<K, V> persistent() ;

    @Override public default EphemeralMap<K, V> ensureKey(K key, V val) {
        if (!has(key))
            return with(key, val);
        return this;
    }

    @Override public EphemeralMap<K, V> with(K key, V val) ;

    @Override public EphemeralMap<K, V> without(Object key);
    
    

}
