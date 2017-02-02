package stonehorse.grit;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Adds in the tail and removes in head
 */
public interface PersistentFifo<T> extends Randomly<T>, Traversable<T>, List<T> {
    PersistentFifo<T> with(T t);
    PersistentFifo<T> without();
    T get();
    T getOr(Supplier<T> s);
    /**
     * The elements matching the predicate
     */
    @Override
    PersistentFifo<T> filter(Predicate<? super T> p);

    /**
     * The result of applying f to every element
     */
    @Override
    <V> PersistentFifo<V> map(Function<? super T,? extends V> f);

    /**
     * The concatenation of the results of applying f to every element
     */
    @Override
    <V> PersistentFifo<V> flatMap(Function<? super T,Iterable<? extends V>> f);

    @Override
    default boolean add(T t){
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean remove(Object o){
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean addAll(Collection<? extends T> c){
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean removeAll(Collection<?> c){
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean removeIf(Predicate<? super T> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean retainAll(Collection<?> c){
        throw new UnsupportedOperationException();
    }

    @Override
    default void clear(){
        throw new UnsupportedOperationException();
    }
    @Override
    default boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }
    @Override
    default T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    default T remove(int index) {
        throw new UnsupportedOperationException();
    }

}
