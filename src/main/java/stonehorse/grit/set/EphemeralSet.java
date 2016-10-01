package stonehorse.grit.set;


import stonehorse.grit.PersistentSet;

public interface EphemeralSet<T> {
	PersistentSet<T> persistent();

	EphemeralSet<T> ifMissing(T t);
	
	EphemeralSet<T> with(T t);
	
	EphemeralSet<T> without(T t);
	
	EphemeralSet<T> difference(Iterable<? super T> iterable);
	EphemeralSet<T> union(Iterable<? extends T> iterable);
	
	int size();
	boolean isEmpty();
}
