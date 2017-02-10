package stonehorse.grit.vector;


import stonehorse.candy.Iterables;
import stonehorse.grit.PersistentList;
import stonehorse.grit.PersistentVector;
import stonehorse.grit.SerializedList;
import stonehorse.grit.Vectors;
import stonehorse.grit.tools.*;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static stonehorse.candy.Choices.*;
import static stonehorse.candy.Choices.ifelse;


public class PList<T>  extends ImmutableList<T> implements PersistentList<T>, RandomAccess, Serializable {
    final PersistentVector<T> vec;
    private int hash = 0;
    private static final PList empty=PList.of(Vectors.vector());

    private PList(PersistentVector<T> vec) {
        this.vec = vec;
    }

    public static <T> PList<T> of(PersistentVector<T> v) {
        return new PList<T>(v);
    }

    @Override
    public PersistentList<T> withAt(T val, int i) {
        return ifelse(isEmpty() && i == 0,
                () -> of(Vectors.vector(val)),
                () -> of(vec.withAt(val, vec.size() - 1 - i)));
    }

    @Override
    public T get() {
        return vec.get();
    }

    @Override
    public PersistentList<T> with(T val) {
        return new PList<T>(vec.with(val));
    }

    @Override
    public PersistentList<T> without() {
        return new PList<T>(vec.without());
    }

    @Override
    public boolean contains(Object o) {
        return vec.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return ReversedIterator.of(vec.listIterator(size()));

    }
    private Object writeReplace() throws ObjectStreamException {
        return new SerializedList(this.vec);
    }
    @Override
    public Object[] toArray() {
        return Util.indexedToArray(this);
    }

    @Override
    public <V> V[] toArray(V[] a) {
        return Util.indexedToArray(a, this);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return vec.containsAll(c);
    }

    @Override
    public int indexOf(Object o) {
        return either (vec.lastIndexOf(o),
                idx-> idx < 0,
                idx-> -1,
                idx-> vec.size() - 1 - idx);
    }

    @Override
    public int lastIndexOf(Object o) {
        return either(vec.indexOf(o),
                idx->idx<0,
                idx-> -1,
                idx->vec.size() - 1 - idx);
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);

    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return either(size(),
                s -> s == 0,
                s -> Collections.emptyListIterator(),
                s -> ReversedListIterator.reversedListIterator(vec.listIterator(s - index), s));
    }

    @Override
    public List<T> subList(int from, int to) {
        return RandomSubList.create(this, from, to);
    }

    @Override
    public <V> PersistentList<V> map(Function<? super T, ? extends V> f) {
        return PList.<V>empty().withAll(Iterables.map(requireNonNull(f),this));
    }

    private static <V> PList<V> empty() {
        return empty;
    }

    @Override
    public <V> PersistentList<V> flatMap(Function<? super T, Iterable<? extends V>> f) {
        return PList.<V>empty().withAll(Iterables.flatMap(requireNonNull(f),this));
    }

    @Override
    public <V> V fold(BiFunction<? super V, ? super T, ? extends V> fn, V acc) {
        return Iterables.fold(fn, acc, this);
    }

    @Override
    public T reduce(BiFunction<? super T, ? super T, ? extends T> fn) {
        return Iterables.reduce(fn, this);
    }

    @Override
    public PersistentList<T> filter(Predicate<? super T> predicate) {
        return PList.<T>empty().withAll(Iterables.filter( requireNonNull(predicate), this));
    }

    @Override
    public PersistentList<T> withAll(Iterable<T> elements) {
        return new PList<T>(vec.withAll(elements));
    }

    @Override
    public PersistentList<T> drop(int num) {
        return PList.of(vec.drop(num));
    }

    @Override
    public PList<T> dropWhile(Predicate<? super T> pred) {
        return PList.of(vec.dropWhile(pred));
    }

    private <TT> Supplier<TT> throwIndexOutOfBounds() {
        return () -> {
            throw new IndexOutOfBoundsException();
        };
    }

    @Override
    public T get(int index) {

        return ifelse(isEmpty(), throwIndexOutOfBounds(), () -> vec.get(size() - 1 - index));
    }

    @Override
    public T getOr(int index, Supplier<T> notFound) {
        if (isEmpty())
            return notFound.get();
        return vec.getOr(size() - 1 - index, notFound);
    }

    @Override
    public int size() {
        return vec.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PList)
            return Objects.equals(((PList) obj).vec, vec);
        return Objects.equals(obj, this);
    }

    @Override
    public String toString() {
        return Util.collectionToString(this);
    }

    @Override
    public int hashCode() {
        if (hash == 0)
            hash = Util.listHash(this);
        return hash;
    }

    private static class ReversedListIterator<T> extends ImmutableListIterator<T> {

        private final ListIterator<T> i;
        private final int size;

        public static <T> ReversedListIterator<T> reversedListIterator(ListIterator<T> i, int size) {
            return new ReversedListIterator<T>(i, size);
        }

        public ReversedListIterator(ListIterator<T> i, int size) {
            this.i = i;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return i.hasPrevious();
        }

        @Override
        public T next() {
            return i.previous();
        }

        @Override
        public boolean hasPrevious() {
            return i.hasNext();
        }

        @Override
        public T previous() {
            return i.next();
        }

        @Override
        public int nextIndex() {

            return size - 1 - i.previousIndex();
        }

        @Override
        public int previousIndex() {
            return size - 1 - i.nextIndex();
        }
    }

    private static class ReversedIterator<T> extends ImmutableIterator<T> {
        public static <T> ReversedIterator<T> of(ListIterator<T> i){
            return new ReversedIterator<T>(i);
        }
        private final ListIterator<T> i;

        public ReversedIterator(ListIterator<T> i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return i.hasPrevious();
        }

        @Override
        public T next() {
            return i.previous();
        }
    }
}
