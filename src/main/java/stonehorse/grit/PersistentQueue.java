package stonehorse.grit;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Adds in the tail and removes in head
 */
public interface PersistentQueue<T> extends Randomly<T>, Traversable<T>, List<T> , java.util.RandomAccess{
    PersistentQueue<T> with(T t);
    PersistentQueue<T> without();
    T get();
    T getOr(Supplier<T> s);
    /**
     * The elements matching the predicate
     */
    @Override
    PersistentQueue<T> filter(Predicate<? super T> p);

    /**
     * The result of applying f to every element
     */
    @Override
    <V> PersistentQueue<V> map(Function<? super T,? extends V> f);

    /**
     * The concatenation of the results of applying f to every element
     */
    @Override
    <V> PersistentQueue<V> flatMap(Function<? super T,Iterable<? extends V>> f);


}
