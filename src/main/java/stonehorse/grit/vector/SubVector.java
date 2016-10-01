package stonehorse.grit.vector;

import stonehorse.grit.PersistentVector;


import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static stonehorse.candy.Choices.*;

public class SubVector<T> extends APVector<T> {

    private final PersistentVector<T> v;
    private final int start;
    private final int end;

    public static <T> SubVector<T> create(PersistentVector<T> vec, int start, int end) {
        if (vec instanceof SubVector) {
            SubVector<T> sv = (SubVector<T>) vec;
            return new SubVector<>(sv.v, start + sv.start, end + sv.start);
        }
        return new SubVector<>(vec, start, end);
    }

    private SubVector(PersistentVector<T> vec, int start, int end) {

        this.v = vec;
        this.start = start;
        this.end = end;
    }

    private boolean isOutOfBounds(int index) {
        return start + index > end || index < 0;
    }

    @Override
    public SubVector<T> withAt(T val, int i) {
        return cond(
                () -> isOutOfBounds(i), SubVector::throwIndexOutOfBounds,
                () -> isAtEnd(i), () -> with(val),
                () -> create(v.withAt(val, start + i), start, end));
    }

    private static <T> T throwIndexOutOfBounds(){
        throw new IndexOutOfBoundsException();
    }

    private boolean isAtEnd(int i) {
        return start + i == end;
    }

    @Override
    public SubVector<T> with(T val) {
        return create(v.withAt(val, end), start, end + 1);
    }

    @Override
    public APVector<T> without() {
        if (isEmpty())
            throw new NoSuchElementException();
        return create(v, start, end - 1);
    }

    @Override
    public <V> PVector<V> map(Function<? super T, ? extends V> f) {
        EVector<V> destination = PVector.<V>empty().ephemeral();
        for(T t:this)
            destination=destination.with(f.apply(t));
        return destination.persistent();
    }

    @Override
    public <V> PVector<V> flatMap(Function<? super T, Iterable<? extends V>> f) {
        EVector<V> destination = PVector.<V>empty().ephemeral();
        for(T t:this) {
            Iterable<? extends V> vs = f.apply(t);
            if (vs != null) {
                Iterator<? extends V> vsi = vs.iterator();
                if(vsi!=null)
                    while (vsi.hasNext())
                        destination = destination.with(vsi.next());
            }
        }
        return destination.persistent();
    }

    @Override
    public PVector<T> filter(Predicate<? super T> p) {
        EVector<T> destination = PVector.<T>empty().ephemeral();
        for (T t : this)
            if (p.test(t))
                destination = destination.with(t);
        return destination.persistent();
    }

    @Override
    public <V> V fold(BiFunction<? super V, ? super T, ? extends V> fn, V acc) {
        for(int i = start; i< end; i++)
            acc = fn.apply(acc , v.get(i));
        return acc;
    }

    @Override
    public int size() {
        return end - start;
    }


    @Override
    public T get(int i) {
        return unless(() -> v.get(start + i),
                isAtEnd(i) || isOutOfBounds(i),
                SubVector::throwIndexOutOfBounds);
    }

    @Override
    public T get(int i, T notFound) {
        return unless(
                () -> v.get(start + i),
                isAtEnd(i) || isOutOfBounds(i),
                () -> notFound);
    }

    @Override
    public Iterator<T> iterator() {
        return ((PVector<T>) v).rangedIterator(start, end);
    }

    @Override
    public EVector<T> ephemeral() {
        return PVector.createEphemeral(this);
    }

    private SubVector<T> withAllEphemerable(Iterable<T> elements) {
        int newEnd = end;
        EVector<T> e = ((APVector<T>)v).ephemeral();
        for (T t : elements) {
            e = e.withAt(t, newEnd++);
        }
        return create(e.persistent(), start, newEnd);
    }

    private SubVector<T> withAllNonEphemerable(Iterable<T> elements) {
        int newEnd = end;
        PersistentVector<T> e = v;
        for (T t : elements) {
            e = e.withAt(t, newEnd++);
        }
        return create(v, start, newEnd);
    }

    @Override
    public SubVector<T> withAll(Iterable<T> elements) {
        return mapOr(
                elements,
                v -> ifelse(
                        isEphemeral(v),
                        () -> withAllEphemerable(elements),
                        () -> withAllNonEphemerable(elements)),
                () -> this);
    }

    private boolean isEphemeral(Iterable<T> v) {
        return v instanceof APVector;
    }

    @Override
    public SubVector<T> drop(int num) {
        return unless(
                () -> create(v, start, end - num),
                num > size(),
                () -> {
                    throw new IllegalArgumentException("Too few elements");
                }
        );
    }

}