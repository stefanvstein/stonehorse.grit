package stonehorse.grit;


import stonehorse.candy.Iterables;

import java.io.Serializable;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static stonehorse.grit.tools.Util.defaultReduce;

public interface PersistentList<T> extends Indexed<T>, Traversable<T>,
        List<T>, Serializable {
    @Override
    PersistentList<T> withAt(T val, int i);

    /**
     * The value at index or value if this is smaller
     */
    default T getOrDefault(int i, T value){
        return getOr(i, ()->value);
    }
    /**
     * List with val at the tail
     */
    @Override
    PersistentList<T> with(T val);

    /**
     * List without the tail
     */
    @Override
    PersistentList<T> without();

    /**
     * Functional interface of the value at index
     */
    @Override
    default T apply(int index){
        return get(index);
    }

    /**
     * sublist delimited by indexes from and to.
     * Returns a delimited view on this, that will keep on referring to this
     * even when this otherwise is forgotten.
     */
    @Override
    List<T> subList(int from, int to);

    /**
     * The list of applying f to each of this
     */
    @Override
    <V> PersistentList<V> map(Function<? super T, ? extends V> f);

    /**
     * List of the concatenation of the results of applying f to each of this
     */
    @Override
    <V> PersistentList<V> flatMap(Function<? super T, Iterable<? extends V>> f);

    /**
     * List of all elements matching predicate
     */
    @Override
    PersistentList<T> filter(Predicate<? super T> predicate);

    /**
     * The vector with all elements added to beginning
     */
    @Override
    PersistentList<T> withAll(Iterable<T> elements);

    /**
     * Vector with num elements dropped from the beginning
     */
    @Override
    PersistentList<T> drop(int num);

    /**
     * The remaining value of repeatedly combining accumulation with each element in this, starting with an initial accumulator value.
     */

    @Override
    default <V> V fold(BiFunction<? super V, ? super T, ? extends V> fn, V acc) {
        return Iterables.fold(fn,acc, this);
    }

    /**
     * The remaining value of repeatedly combining accumulation with each element in this except the first, which is used as initial accumulator value
     */
    @Override
    default T reduce(BiFunction<? super T, ? super T, ? extends T> fn) {
        return defaultReduce(fn, this);
    }


}

