package stonehorse.grit.tools;


import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * List that throws UnsupportedOperationException up on mutation finally
 * <p>
 * Currently holds for Java 1.8
 */

public abstract class ImmutableList<T>  implements List<T> {

    @Override
    public final boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void replaceAll(UnaryOperator<T> operator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final  T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final  void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final  T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void sort(Comparator<? super T> c) {
        throw new UnsupportedOperationException();
    }
    /**
     * Throws UnsupportedOperationException
     */
    @Override
    public final  boolean add(T e) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws UnsupportedOperationException
     */
    @Override
    public final  boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws UnsupportedOperationException
     */
    @Override
    public final  boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws UnsupportedOperationException
     */
    @Override
    public final  boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws UnsupportedOperationException
     */
    @Override
    public final  boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws UnsupportedOperationException
     */
    @Override
    public final  void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws UnsupportedOperationException
     */
    @Override
    public final  boolean removeIf(Predicate<? super T> predicate) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Spliterator<T> spliterator() {
        return Spliterators.spliterator(this, Spliterator.IMMUTABLE & Spliterator.CONCURRENT & Spliterator.SIZED & Spliterator.SUBSIZED & Spliterator.ORDERED);
    }


    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }


    @Override
    public Stream<T> parallelStream() {
        return StreamSupport.stream(this.spliterator(), true);
    }

}
