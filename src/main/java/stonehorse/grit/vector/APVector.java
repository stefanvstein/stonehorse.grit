package stonehorse.grit.vector;

import stonehorse.grit.tools.ImmutableList;
import stonehorse.grit.PersistentVector;
import stonehorse.grit.tools.Util;

import java.util.Collection;
import java.util.ListIterator;

import static stonehorse.candy.Choices.mapOr;
import static stonehorse.candy.Choices.unless;
import static stonehorse.candy.Choices.when;

/**
 * Warning, should only store immutables
 *
 * @param <T>
 * @author stefan
 */
public abstract class APVector<T> extends ImmutableList<T> implements PersistentVector<T> {


    @Override
    public abstract APVector<T> with(T val);

    @Override
    public abstract APVector<T> withAt(T val, int i);

    @Override
    public abstract APVector<T> without();


    public abstract EVector<T> ephemeral();

    private static final long serialVersionUID = 1L;
    private int _hash = -1; 

    @Override

    public int hashCode() {
        if(_hash==-1)
            _hash= Util.listHash(this);
        return _hash;
    }

    public String toString() {
        return Util.collectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
        return Util.listEquals(this, obj);
    }


    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return Util.iteratorContains(iterator(), o);
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
        for (Object o : c) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    private int indexOfNull(){
        int s=size();
        for (int i = 0; i < s; i++)
            if (get(i) == null)
                return i;
        return -1;
    }

    @Override
    public int indexOf(Object o) {
        return mapOr(o,
                v -> {
                    int s = size();
                    for (int i = 0; i < s; i++)
                        if (v.equals(get(i)))
                            return i;
                    return -1;
                },
                this::indexOfNull);
    }

    private int lastIIndexOfNull(){
        int s = size();
        for (int i = s - 1; i >= 0; i--)
            if (get(i) == null)
                return i;
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        return mapOr(o,
                v -> {
                    int s = size();
                    for (int i = s - 1; i >= 0; i--)
                        if (o.equals(get(i)))
                            return i;
                    return -1;
                },
                this::lastIIndexOfNull);
    }

    @Override
    public ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        return unless(
                () -> VectorListIterator.create(this, index),
                index < 0 || index > size(),
                () -> {
                    throw new IndexOutOfBoundsException();
                });
    }

    @Override
    public APVector<T> subList(int fromIndex, int toIndex) {
        return unless(() -> SubVector.create(this, fromIndex, toIndex),
                toIndex < fromIndex || fromIndex < 0 || toIndex > size(),
                () -> {
                    throw new IndexOutOfBoundsException();
                });
    }


    @Override
    public T get() {
        return when(
                size()>0,
                ()->get(size() - 1));
    }

}
