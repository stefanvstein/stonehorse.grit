package stonehorse.grit.tools;


import java.util.Iterator;

public abstract class ImmutableIterator<T> implements Iterator<T> {
    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }
}
