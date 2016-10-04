package stonehorse.grit.set;


import stonehorse.grit.PersistentSet;
import stonehorse.grit.map.EphemeralMap;

import static stonehorse.grit.tools.Util.not;

public class EpSet<T> implements EphemeralSet<T> {
	EphemeralMap<T, T> impl;

	public EpSet(EphemeralMap<T, T> impl) {
		this.impl = impl;
	}

	@Override public int size() {
		return impl.size();
	}

	@Override public boolean isEmpty() {
		return impl.isEmpty();
	}

	@Override public PersistentSet<T> persistent() {
		return PSet.of(
				impl.persistent());
	}

	@Override public EphemeralSet<T> ifMissing(T t) {
		if (not(impl.has(t)))
			impl = impl.with(t, t);
		return this;
	}

	@Override public EphemeralSet<T> with(T t) {
		impl.with(t, t);
		return this;
	}

	@Override public EphemeralSet<T> without(T t) {
		impl=impl.without(t);
		return this;
	}

	@Override public EphemeralSet<T> difference(Iterable<? super T> iterable) {
		for (Object i : iterable)
			impl = impl.without(i);
		return this;
	}

	@Override public EphemeralSet<T> union(Iterable<? extends T> iterable) {
		for (T i : iterable)
			impl = impl.with(i, i);
		return this;
	}
}
