package stonehorse.grit.map;




import stonehorse.grit.Associative;
import stonehorse.grit.tools.ImmutableMap;
import stonehorse.grit.PersistentMap;
import stonehorse.grit.tools.Util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static stonehorse.candy.Choices.cond;
import static stonehorse.candy.Choices.ifelse;

public abstract class APMap<K, V> extends ImmutableMap<K, V> implements  PersistentMap<K,V> {



    private int hash = 0;

    @Override
    public int hashCode() {
        if (hash == 0 && size() != 0)
            hash = Util.mapHash(this);
        return hash;
    }

    @Override public  APMap<K, V> ensureKey(K key, V val) {
        if (!has(key))
            return with(key, val);
        return this;
    }

    @Override
    public abstract APMap<K, V> with(K key, V val);

    @Override
    public boolean equals(Object obj) {
        return Util.mapEquals(this, obj);
    }

    @Override
    public String toString() {
        return Util.mapToString(this);
    }
    
    public abstract Iterator<Map.Entry<K, V>> iterator();

    @Override
    public Collection<V> values() {
        return new ValueCollection<K, V>(this);
    }

    @Override
    public Set<K> keySet() {
        return new KeySet<K, V>(this);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new EntrySet<K, V>(this);
    }

    @Override
    public Associative<K, V> ifMissing(K key, Supplier<V> valueSupplier) {
        requireNonNull(valueSupplier);
        return ifelse(!has(key),
                () -> with(key, valueSupplier.get()),
                () -> this);
    }


}
