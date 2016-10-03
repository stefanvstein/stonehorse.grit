package stonehorse.grit;

import stonehorse.candy.Iterables;

import java.io.Serializable;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static stonehorse.grit.tools.Util.defaultReduce;

/**
 * A random access sequential collection with mutation as expression. Side effecting mutation throws UnsupportedOperationException
 */
public interface PersistentVector<T> extends Indexed<T>, Traversable<T>,
		 List<T>, Serializable, RandomAccess {
	/**
	 * Vector with val at index, where index is at most size
	 * <pre>{@code
	 * vector(1, 2).withAt(30, 0) => [30, 2]
	 * }</pre>
	 */
	@Override
	PersistentVector<T> withAt(T val, int i);

	/**
	 * The value at index or value if this is smaller
	 */
	public default T getOrDefault(int i, T value){
		return getOr(i, ()->value);
	}
	/**
	 * Vector with val at the tail
	 */
	@Override
	PersistentVector<T> with(T val);

	/**
	 * Vector without the tail
	 */
	@Override	
	PersistentVector<T> without();

	/**
	 * Functional interface of the value at index
	 */
	@Override
	default T apply(int index){
		return get(index);
	}

	/**
	 * subvector delimited by indexes from and to.
	 * Returns a delimited view on this, that will keep on referring to this
	 * even when this otherwise is forgotten.  
	 */
	@Override
	PersistentVector<T> subList(int from, int to);

	/**
	 * The vector of applying f to each of this
	 */
	@Override
	 <V> PersistentVector<V> map(Function<? super T, ? extends V> f);

	/**
	 * Vector of the concatenation of the results of applying f to each of this
	 */
	@Override
	<V> PersistentVector<V> flatMap(Function<? super T, Iterable<? extends V>> f);

	/**
	 * Vector of all elements matching predicate
	 */
	@Override
	PersistentVector<T> filter(Predicate<? super T> predicate);

	/**
	 * The vector with all elements added to end
	 */
	@Override
	PersistentVector<T> withAll(Iterable<T> elements);

	/**
	 * Vector with num elements dropped from the end
	 */
	@Override
	PersistentVector<T> drop(int num);

	/**
	 * The remaining value of repeatedly combining accumulation with each element in this, starting with an initial accumulator value.
	 */

	@Override
	default <V> V fold(BiFunction<? super V, ? super T, ? extends V> fn, V acc) {
		return Iterables.reduce(fn,acc, this);
	}

	/**
	 * The remaining value of repeatedly combining accumulation with each element in this except the first, which is used as initial accumulator value
	 */
	@Override
	default T reduce(BiFunction<? super T, ? super T, ? extends T> fn) {
		return defaultReduce(fn, this);
	}


}
