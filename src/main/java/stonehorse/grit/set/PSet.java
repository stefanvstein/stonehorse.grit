package stonehorse.grit.set;


import stonehorse.grit.tools.ImmutableIterator;
import stonehorse.grit.tools.ImmutableSet;
import stonehorse.grit.PersistentMap;
import stonehorse.grit.PersistentSet;
import stonehorse.grit.map.EphemerableMap;
import stonehorse.grit.map.array.PArrayMap;

import stonehorse.grit.tools.Util;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class PSet<T> extends ImmutableSet<T> implements PersistentSet<T>, Serializable{

	private final PersistentMap<T, T> impl ;
	private int hash = -1;
	private PSet() {
		impl= PArrayMap.empty();
	}
	private PSet(PersistentMap<T, T> impl) {
		this.impl = impl;
	}
	private static final PSet<?> EMPTY = new PSet<Object>();
	
	public static <T> PSet<T> of(PersistentMap<T,T> impl){
		return new PSet<T>(impl);
	}
	
	@SuppressWarnings("unchecked") 
	public static <T> PSet<T> empty(){
		return (PSet<T>) EMPTY;
	}
	
	 private Object writeReplace() throws ObjectStreamException {
			return new SerializedSet<T>(this);
		}
	@Override public PersistentSet<T> with(T t) {
		PersistentMap<T, T> val = impl.with(t, t);
		if (val == impl)
			return this;
		return new PSet<T>(val);
	}
	@Override public PersistentSet<T> without(Object key) {
		PersistentMap<T, T> val = impl.without(key);
		if (val == impl)
			return this;
		return new PSet<T>(val);
	}

	public String toString() {
		return Util.collectionToString(this);
	}

	public boolean contains(Object key) {
		return impl.containsKey(key);
	}

	public boolean has(Object key) {
		return impl.has(key);
	}
	public T get(Object key) {
		return impl.get(key);
	}

	@Override public int size() {
		return impl.size();
	}
	@Override public boolean isEmpty() {
		return impl.isEmpty();
	}
	@Override public Iterator<T> iterator() {
		final Iterator<Entry<T, T>> i = impl.entrySet().iterator();
		return new ImmutableIterator<T>() {

			@Override public boolean hasNext() {
				return i.hasNext();
			}

			@Override public T next() {
				return i.next().getValue();
			}
		};
	}
	@Override public Object[] toArray() {
		Object[] array = new Object[size()];
		int i = 0;
		for (T t : this) {
			array[i] = t;
			i++;
		}
		return array;
	}
	@Override public <T> T[] toArray(T[] a) {
		return Util.setToArray(a, this);
	}
	@Override public boolean containsAll(Collection<?> c) {
		return impl.keySet().containsAll(c);
	}
	// AddAll
	@Override public PersistentSet<T> union(Iterable<? extends T> iterable) {
		return ephemeral().union(iterable).persistent();
	}
	@Override public boolean equals(Object obj) {
		return Util.setEquals(this, obj);
	}
	@Override public int hashCode() {
		if (hash == -1) {
			hash = Util.setHash(this);
		}
		return hash;
	}
	// RemoveAll
	@Override public PersistentSet<T> difference(Iterable<? super T> iterable) {
		return ephemeral().difference(iterable).persistent();
	}
	
	// Contains in both
	@Override public PersistentSet<T> intersection(Iterable<? extends T> set) {
		EphemeralSet<T> result = new PSet<T>().ephemeral();
		for (T t : set) {
			if (contains(t))
				result.with(t);

		}

		return result.persistent();
	}

	@Override
	public <V> PersistentSet<V> map(Function<? super T, ? extends V> f) {
		requireNonNull(f);
		EphemeralSet<V> destination = PSet.<V>empty().ephemeral();
		for(T t:this)
			destination=destination.with(f.apply(t));
		return destination.persistent();
	}


	//Rewrite!
	@Override
	public <V> PersistentSet<V> flatMap(Function<? super T, Iterable<? extends V>> f) {
		EphemeralSet<V> destination = PSet.<V>empty().ephemeral();
		for(T t:this) {
			Iterable<? extends V> vs = f.apply(t);
			if (vs != null) {
				Iterator<? extends V> vsi = vs.iterator();
				if(vsi!=null)
					while (vsi.hasNext())
						destination = destination.with(vsi.next());
			}
		}
		return destination.persistent();
	}

	@Override
	public PersistentSet<T> filter(Predicate<? super T> p) {
		requireNonNull(p);
		EphemeralSet<T> destination = PSet.<T>empty().ephemeral();
		for (T t : this)
			if (p.test(t))
				destination = destination.with(t);
		return destination.persistent();
	}

	public EphemeralSet<T> ephemeral() {
		if (impl instanceof EphemerableMap)
			return new EpSet<T>(((EphemerableMap<T, T>) impl).ephemeral());
		throw new UnsupportedOperationException();
	}

}
