package stonehorse.grit;


import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The most basic strict higher order application on sequential types
 */
public interface Traversable<T> {
    /**
     * The remaining value of repeatedly combining accumulation with each element, starting with an initial accumulator value.
     */
    <V> V fold(BiFunction<? super V, ? super T, ? extends V> fn, V acc);

    /**
     * The remaining value of repeatedly combining accumulation with each element except the first, which is used as initial accumulator value
     */
    T reduce(BiFunction<? super T, ? super T, ? extends T> fn);

    /**
     * The elements matching the predicate
     */
    Traversable<T> filter(Predicate<? super T> p);

    /**
     * The result of applying f to every element
     */
    <V> Traversable<V> map(Function<? super T,? extends V> f);

    /**
     * The concatenation of the results of applying f to every element
     */
    <V> Traversable<V> flatMap(Function<? super T,Iterable<? extends V>> f);
}
