package stonehorse.grit.vector;

import stonehorse.candy.Iterables;
import stonehorse.candy.Tuples;
import stonehorse.grit.PersistentFifo;
import stonehorse.grit.tools.RandomSubList;
import stonehorse.grit.tools.RandomlyListIterator;
import stonehorse.grit.tools.Util;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static stonehorse.candy.Choices.cond;
import static stonehorse.candy.Maybe.maybe;

/**
 * Created by stefan on 1/28/17.
 */
public class PFifo<T> implements PersistentFifo<T> {
    private final APVector<T> head;
    private final PVector<T> tail;
    private static PFifo empty=new PFifo(null, null);
    PFifo(APVector<T> head, PVector<T> tail){
        this.head=head;
        this.tail=tail;
    }


    @Override
    public PersistentFifo<T> with(T t) {
        return new PFifo<T>(head, maybe(tail)
                .map(c -> c.with(t))
                .orElseGet(() -> PVector.<T>empty().with(t)));

    }

    public static <T> PFifo<T> empty(){
        return empty;
    }

    @Override
    public PersistentFifo<T> without() {
        return cond(
                () -> isEmpty(),
                () -> this,
                () -> size() == 1,
                () -> empty(),
                () -> maybe(head).map(h -> h.isEmpty()).orElse(true),
                () -> new PFifo<>(tail.subList(1, tail.size()), null),
                () -> new PFifo<>(head.subList(1, head.size()), tail));
    }

    @Override
    public T get() {
        return get(0);
    }

    @Override
    public T getOr(Supplier<T> s) {
        return getOr(0, s);
    }

    @Override
    public String toString() {
        return Util.collectionToString(this);
    }

    @Override
    public Iterator<T> iterator() {
        if(Objects.nonNull(head) || Objects.nonNull(tail))
        return Iterables.concat(head, tail).iterator();
        return Collections.emptyIterator();
    }

    @Override
    public int size() {
        return maybe(head)
                .map(Collection::size)
                .orElse(0) +
               maybe(tail)
                       .map(Collection::size)
                       .orElse(0);
    }

    @Override
    public boolean isEmpty() {
        return size()==0;
    }

    @Override
    public T get(int index) {
        return getOr(index, ()->{throw new IndexOutOfBoundsException();});
    }


    @Override
    public int indexOf(Object o) {

       for(Tuples.T2<Integer, T> e:Iterables.map((i,e)-> Tuples.of(i,e), Iterables.range(), this)){
           if(Objects.equals(o,Tuples.second(e)))
               return Tuples.first(e);

       }return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int last=-1;

        for(Tuples.T2<Integer, T> e:Iterables.map((i,e)-> Tuples.of(i,e), Iterables.range(), this)) {
            if (Objects.equals(Tuples.second(e), o))
                last = Tuples.first(e);
        }
        return last;
    }

    @Override
    public ListIterator<T> listIterator() {
        return RandomlyListIterator.create(this,0);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return RandomlyListIterator.create(this,index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return RandomSubList.create(this, fromIndex, toIndex);
    }

    @Override
    public T getOr(int index, Supplier<T> notFound) {
        int headSize=maybe(head).map(Collection::size).orElse(0);

        if(index<headSize)
            return head.getOr(index, notFound);
        int tailSize=maybe(tail).map(Collection::size).orElse(0);
        return maybe(tail).map(t->t.getOr(index-headSize, notFound)).orElse(null);

    }

    @Override
    public boolean contains(Object o) {
        return Iterables.filter(e->Objects.equals(e,o),this).iterator().hasNext();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for(Object e:c){
            if(!contains(e))
                return false;
        }
        return true;
    }



    @Override
    public T apply(int value) {
        return get(value);
    }

    @Override
    public <V> V fold(BiFunction<? super V, ? super T, ? extends V> fn, V acc) {

        if(head!=null)
            acc=head.fold(fn,acc);
        if(tail!=null)
            acc=tail.fold(fn,acc);
        return acc;
    }

    @Override
    public T reduce(BiFunction<? super T, ? super T, ? extends T> fn) {
        if(head!=null && !head.isEmpty()) {
            T t = head.reduce(fn);
            if(tail!=null && !tail.isEmpty())
                return tail.fold(fn, t);
            return t;
        }
        if(tail!=null && !tail.isEmpty())
            return tail.reduce(fn);
        return null;
    }

    @Override
    public PersistentFifo<T> filter(Predicate<? super T> p) {
        Objects.requireNonNull(p);
        if(isEmpty())
            return this;
        return new PFifo<T>(null, PVector.<T>empty().ephemeral().withAll(Iterables.filter(p, this)).persistent());
    }

    @Override
    public <V> PersistentFifo<V> map(Function<? super T, ? extends V> f) {
        Objects.requireNonNull(f);
        if(isEmpty())
            return empty();
        return new PFifo<V>(null, PVector.<V>empty().ephemeral().withAll(Iterables.map(f, this)).persistent());
    }

    @Override
    public <V> PersistentFifo<V> flatMap(Function<? super T, Iterable<? extends V>> f) {
        Objects.requireNonNull(f);
        if(isEmpty())
            return empty();
        return new PFifo<V>(null, PVector.<V>empty().ephemeral().withAll(Iterables.flatMap(f, this)).persistent());
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
    public boolean equals(Object obj) {
        return Util.listEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return Util.listHash(this);
    }
}
