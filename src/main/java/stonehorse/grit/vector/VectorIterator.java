package stonehorse.grit.vector;



import stonehorse.grit.tools.ImmutableIterator;

import java.util.NoSuchElementException;

final class VectorIterator<T> extends ImmutableIterator<T> {
    
    private final int end;
    private final PVector<T> vector;
    private int i;
    private int base;
    private Object[] array;

    public VectorIterator(int start, int end, PVector<T> vector) {
        this.vector=vector;
        
        this.end = end;
        i = start;
        base = i - (i % VFuns.BUCKET_SIZE);
        if(start<vector.size())
            array= VFuns.arrayFor(i, vector.size, vector.root, vector.tail);
        else array=null;
    }

    public boolean hasNext() {
        return i < end;
    }

    @SuppressWarnings("unchecked") public T next() {
        if (hasNext()) {
            if (i - base == VFuns.BUCKET_SIZE) {
                array = VFuns.arrayFor(i,vector.size, vector.root, vector.tail);
                base += VFuns.BUCKET_SIZE;
            }
            i++;
            return (T) array[VFuns.indexOfArrayAt(i - 1)];
        } else
            throw new NoSuchElementException();
    }
}