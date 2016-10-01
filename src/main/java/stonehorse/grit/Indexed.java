package stonehorse.grit;

/**
 * Sequentially indexed composition of homogeneous values where modification is expression.
  */


public interface Indexed<T> extends Randomly<T>, PersistentStack<T> {
    /**
     * This with value at index. This has to be at least sized equal to index.
     * The first element starts at 0.
     */
    Indexed<T> withAt(T value, int index);

    /**
     * This withAt value associated at the natural growing position
     */
    @Override
    Indexed<T> with(T value);

    /**
     * This withAt the most naturally removable element removed.
     */
    @Override
    Indexed<T> without();

    /**
     * This withAt the all elements at the naturally growing position
     */
    @Override
    Indexed<T> withAll(Iterable<T> elements);

    /**
     * This withAt the num most naturally removable elements removed.
     */
    @Override
    Indexed<T> drop(int num);
}
