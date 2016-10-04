package stonehorse.grit.map;


import stonehorse.grit.Associative;
import stonehorse.grit.PersistentMap;

import java.util.Map;

public interface EphemeralMap<K, V>
        extends Associative<K, V> {

     boolean isEmpty();

	 EphemeralMap<K, V> withoutAll(Iterable<?> iterable) ;

	 EphemeralMap<K, V> withAll(Map<? extends K, ? extends V> map) ;

     int size() ;

     V get(Object key, V otherwise);

    @Override default V get(Object key) {
        return get(key, null);
    }


     PersistentMap<K, V> persistent() ;

    @Override  default EphemeralMap<K, V> ensureKey(K key, V val) {
        if (!has(key))
            return with(key, val);
        return this;
    }

    @Override  EphemeralMap<K, V> with(K key, V val) ;

    @Override  EphemeralMap<K, V> without(Object key);
    
    

}
