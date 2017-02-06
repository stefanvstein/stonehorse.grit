package stonehorse.grit.vector;

import stonehorse.candy.Choices;
import stonehorse.candy.Iterables;
import stonehorse.grit.PersistentList;
import stonehorse.grit.PersistentVector;
import stonehorse.grit.Vectors;
import stonehorse.grit.tools.ImmutableListIterator;
import stonehorse.grit.tools.RandomSubList;
import stonehorse.grit.tools.Util;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static stonehorse.candy.Choices.ifelse;


public class PList <T> extends  APList<T>{
    final PersistentVector<T> vec;
    private int hash=0;


    private PList(PersistentVector<T> vec){
        this.vec=vec;
    }

    public static <T> PList<T> of(PersistentVector<T> v){
        return new PList<T>(v);
    }

    @Override
    public PersistentList<T> withAt(T val, int i) {
        return ifelse(isEmpty() && i==0,
                ()->of(Vectors.vector(val)),
                ()-> of(vec.withAt(val, vec.size()-1-i)));
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
        ListIterator<T> i = vec.listIterator(size());
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return i.hasPrevious();
            }

            @Override
            public T next() {
                return i.previous();
            }
        };
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
        int idx = vec.lastIndexOf(o);
        if(idx<0)
            return -1;

        return vec.size()-1-idx;
    }

    @Override
    public int lastIndexOf(Object o) {
       int idx =vec.indexOf(o);
       if(idx<0)
           return -1;
       return vec.size()-1-idx;
    }

    @Override
    public ListIterator<T> listIterator() {
      return listIterator(0);

    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return Choices.either(size(),
                s->s==0,
                s->Collections.emptyListIterator(),
                s->ReversedListIterator.reversedListIterator(vec.listIterator(s-index),s));
    }

    @Override
    public List<T> subList(int from, int to) {
        return RandomSubList.create(this, from , to);
    }

    @Override
    public <V> PersistentList<V> map(Function<? super T, ? extends V> f) {
        return null;
    }

    @Override
    public <V> PersistentList<V> flatMap(Function<? super T, Iterable<? extends V>> f) {
        return null;
    }

    @Override
    public PersistentList<T> filter(Predicate<? super T> predicate) {
        return null;
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

    private <TT> Supplier<TT> throwIndexOutOfBounds(){
        return ()->{throw new IndexOutOfBoundsException();};
    }
    @Override
    public T get(int index) {

        return ifelse(isEmpty(),throwIndexOutOfBounds(), ()->vec.get(size()-1-index));
    }

    @Override
    public T getOr(int index, Supplier<T> notFound) {
        if(isEmpty())
            return notFound.get();
        return vec.getOr(size()-1-index, notFound);
    }

    @Override
    public int size() {
        return vec.size();
    }

    @Override
    public boolean isEmpty() {
        return size()==0;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PList)
            return Objects.equals(((PList) obj).vec, vec);
        return Objects.equals(obj, this);
    }

    @Override
    public String toString() {

        return Objects.toString(Iterables.forceList(this));
    }

    @Override
    public int hashCode() {
        if(hash==0)
        hash= Util.listHash(this);
        return hash;
    }

    private static class ReversedListIterator<T> extends ImmutableListIterator<T> {

        private final ListIterator<T> i;
        private final int size;
public static <T> ReversedListIterator<T> reversedListIterator(ListIterator<T> i, int size){
    return new ReversedListIterator<T>(i,size);
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

            return size -1- i.previousIndex();
        }

        @Override
        public int previousIndex() {
            return size -1- i.nextIndex();
        }
    }
}
