package stonehorse.grit.map.array;



import stonehorse.grit.Associative;
import stonehorse.grit.map.EphemeralMap;
import stonehorse.grit.map.hash.PHashMap;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static stonehorse.candy.Choices.cond;
import static stonehorse.candy.Choices.ifelse;

public class EphemeralArrayMap<K, V> implements EphemeralMap<K, V> {
    int len;
    Object array[];
    public static final int GROWTH = 8;

    EphemeralArrayMap(Object[] array) {
        if(array.length>=2) {
            this.array = Arrays.copyOf(array, array.length);
            this.len = array.length;
        }else{
            this.array=new Object[GROWTH*2];
            len=0;
    }}

    @Override
    public Associative<K, V> whenMissing(K key, Supplier<V> valueSupplier) {
        requireNonNull(valueSupplier);
        return ifelse(!has(key),
                () -> with(key, valueSupplier.get()),
                () -> this);
    }

    @Override public EphemeralMap<K, V> with(K key, V val) {
        int i = PArrayMap.indexOf(key, array, len);
        if (i >= 0)
            return replace(val, i);
        if (shouldTransform())
            return PHashMap.<K,V>empty().ephemeral().withAll(persistent().with(key, val));
        return grow(key, val);
    }

    private EphemeralArrayMap<K, V> grow(K key, V val) {
        if(len+2>array.length)
            array=Arrays.copyOf(array, (array.length+(GROWTH*2))- (array.length+ GROWTH*2) % (GROWTH*2));

        array[len++] = key;
        array[len++] = val;
        return this;
    }
    private EphemeralArrayMap<K, V> purge(int i) {
        if (len >= 2) {
            array[i] = array[len - 2];
            array[i + 1] = array[len - 1];
        }
        len -= 2;
        return this;
    }

    private boolean shouldTransform() {
        return len >= array.length &&
                len >= PArrayMap.HASHTABLE_THRESHOLD * 2;
    }

    private EphemeralArrayMap<K, V> replace(V val, int i) {
        if (array[i + 1] != val) 
            array[i + 1] = val;
        return this;
    }

    @Override public EphemeralArrayMap<K, V> without(Object key) {
        int i = PArrayMap.indexOf(key, array, len);
        if (i >= 0) 
            return purge(i);
        return this;
    }

    @Override
    public EphemeralArrayMap<K, V> withoutWhen(Object key, Predicate<V> predicate) {
        requireNonNull(predicate);
        return cond(()->!has(key),
                ()->this,
                ()->predicate.test(get(key)),
                ()->without(key),
                ()->without(key));
    }


    @SuppressWarnings("unchecked") @Override public V get(Object key, V notFound) {
        int i = PArrayMap.indexOf(key, array, len);
        if (i >= 0)
            return (V) array[i + 1];
        return notFound;
    }

    @Override
    public boolean has(Object key) {
        return  PArrayMap.indexOf(key, array, len) >=0;
    }

    @Override public int size() {
        return len / 2;
    }

    @Override public PArrayMap<K, V> persistent() {
             return PArrayMap.<K,V>create(Arrays.copyOf(array,len));
    }



    @Override public boolean isEmpty() {
        return len == 0;
    }

	@Override
	public EphemeralArrayMap<K, V> withoutAll(Iterable<?> keys) {
	EphemeralArrayMap<K, V> val = this;
	for(Object key:keys){
		val=val.without(key);
	}
	return val;
	}

	@Override
	public EphemeralMap<K, V> withAll(Map<? extends K, ? extends V> map) {
		if(map== null)
			return this;
		EphemeralMap<K, V> v = this;
		for(Entry<? extends K, ? extends V> e:map.entrySet())
			v=v.with(e.getKey(), e.getValue());
		return v;
	}

}