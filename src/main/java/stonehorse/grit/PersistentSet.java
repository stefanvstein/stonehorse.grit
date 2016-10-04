package stonehorse.grit;

import stonehorse.candy.Iterables;
import stonehorse.grit.tools.Util;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A read only set structure where mutation is expression. Side effecting mutation throws UnsupportedOperationException
 */
public interface PersistentSet<T> extends Set<T>, Traversable<T> {
    /**
     * This set with value
     */
    PersistentSet<T> with(T value);

    /**
     * This set without value
     */
    PersistentSet<T> without(Object value);

    /**
     * The value equal to object if present
     */
    T get(T object);

    /**
     * is element equal to value present
     */
    boolean has(T value);

    /**
     * This with all values
     */
    PersistentSet<T> union(Iterable<? extends T> values);

    /**
     * This without all values
     */
    PersistentSet<T> difference(Iterable<? super T> values);

    /**
     * Elements found in this and values
     */
    PersistentSet<T> intersection(Iterable<? extends T> values);

    /**
     * The set of applying f to each of this, in arbitrary order
     */
    @Override
    <V> PersistentSet<V> map(Function<? super T, ? extends V> f);

    /**
     * Set of the concatenation of the results of applying f to each of this, in arbitrary order
     */
    @Override
    <V> PersistentSet<V> flatMap(Function<? super T, Iterable<? extends V>> f);

    /**
     * The set of all of this matching p
     */
    @Override
    PersistentSet<T> filter(Predicate<? super T> p);

    /**
     * The remaining value of repeatedly combining accumulation with each element in arbitrary order, starting with an initial accumulator value.
     */
    @Override
    default <V> V fold(BiFunction<? super V, ? super T, ? extends V> fn, V acc) {
        return Iterables.fold(fn, acc, this);
    }

    /**
     * The remaining value of repeatedly combining accumulation with each element in arbitrary order except one, which is used as initial accumulator value
     */
    @Override
    default T reduce(BiFunction<? super T, ? super T, ? extends T> fn) {
        return Util.defaultReduce(fn, this);
    }
}