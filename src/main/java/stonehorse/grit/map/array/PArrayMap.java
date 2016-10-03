package stonehorse.grit.map.array;



import stonehorse.grit.PersistentMap;
import stonehorse.grit.map.*;
import stonehorse.grit.map.hash.EphemeralHashMap;
import stonehorse.grit.map.hash.PHashMap;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static stonehorse.candy.Choices.cond;

public class PArrayMap<K, V> extends APMap<K, V> implements EphemerableMap<K,V>, Serializable{
	public static final int HASHTABLE_THRESHOLD = 8;
	private static final PArrayMap<?, ?> EMPTY = new PArrayMap<Object, Object>();

	private final Object[] array;

	static <K, V> PArrayMap<K, V> create(Object array[]) {
		return new PArrayMap<K, V>(array);
	}

	private PArrayMap() {
		array = new Object[] {};
	}

	private PArrayMap(Object array[]) {
		this.array = array;

	}

	private Object writeReplace() throws ObjectStreamException {
		return new SerializedMap<K, V>(this);
	}

	static int indexOf(Object key, Object array[], int len) {
		for (int i = 0; i < len; i += 2)
			if (Objects.equals(key, array[i]))
				return i;
		return -1;
	}

	private  APMap<K,V> transform(K key, V val){
		EphemeralHashMap<K, V> e = PHashMap.<K,V>empty().ephemeral();
		Iterator<Map.Entry<K,V>> i = iterator();
		while(i.hasNext()) {
			Map.Entry<K,V> entry = i.next();
			e = e.with(entry.getKey(),entry.getValue());
		}
		e=e.with(key, val);
		return e.persistent();

		//return grow(key,val);
	}

	@Override public APMap<K, V> with(K key, V val) {
		int i = indexOf(key, array, array.length);
		if (i >= 0)
			return replace(val, i);
		if (shouldTransform())
			return transform(key, val);

		return grow(key, val);
	}

	private PArrayMap<K, V> grow(K key, V val) {
		Object[] newArray = new Object[array.length + 2];
		if (array.length > 0)
			System.arraycopy(array, 0, newArray, 2, array.length);
		newArray[0] = key;
		newArray[1] = val;
		return create(newArray);
	}

	private boolean shouldTransform() {
		return  array.length >= HASHTABLE_THRESHOLD * 2;
	}

	private PArrayMap<K, V> replace(V val, int i) {
		if (array[i + 1] == val)
			return this;
		Object[] newArray = array.clone();
		newArray[i + 1] = val;
		return create(newArray);
	}

	@SuppressWarnings("unchecked") public static  <K,V> PArrayMap<K, V> empty() {
			return (PArrayMap<K, V>) EMPTY;
	}

	@Override public PArrayMap<K, V> without(Object key) {
		int i = indexOf(key, array, array.length);
		if (i >= 0)
			return purge(key);
		return this;
	}

	private PArrayMap<K, V> purge(Object key) {
		int newlen = array.length - 2;
		if (newlen == 0)
			return empty();
		Object[] newArray = new Object[newlen];
		for (int source = 0, destination = 0; source < array.length;) {
			if (!Objects.equals(array[source], key))
			{
				newArray[destination] = array[source];
				newArray[destination + 1] = array[source + 1];
				destination += 2;
			}
			source += 2;
		}
		return create(newArray);
	}

	public EphemeralArrayMap<K, V> ephemeral() {
		return new EphemeralArrayMap<K, V>(array);
	}

	@Override public int size() {
		return array.length / 2;
	}

	@Override public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return has(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return new ValueCollection<>(this).contains(value);
	}

	@Override public boolean has(Object key) {
		return indexOf(key, array, array.length) >= 0;
	}

    @Override public V get(Object key) {
		int i = indexOf(key, array, array.length);
		if (i >= 0)
			return (V) array[i + 1];
		return null;
	}

	@Override public Iterator<Map.Entry<K, V>> iterator() {
		return new ArrayMapIterator<K, V>(array);
	}

	@Override public PersistentMap<K, V> withoutAll(Iterable<?> iterable) {
		EphemeralMap<K, V> m = ephemeral();
		for (Object e : iterable)
			m = m.without(e);
		return m.persistent();
	}

	@Override public PersistentMap<K, V> withAll(Map<? extends K, ? extends V> map) {
		EphemeralMap<K, V> m = ephemeral();
		for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
			if (e != null)
				m = m.with(e.getKey(), e.getValue());
		}
		return m.persistent();
	}
	@Override
	public PArrayMap<K, V> withoutWhen(Object key, Predicate<V> predicate) {
		requireNonNull(predicate);
		return cond(()->!has(key),
				()->this,
				()->predicate.test(get(key)),
				()->without(key),
				()->this);
	}
}
