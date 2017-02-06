package stonehorse.grit;


import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Composite where homogeneous members are randomly accessible by integer index
 */
public interface Randomly<T> extends IntFunction<T> {
    /**
     * Elememt at index or IndexOutOfBoundsException
     */
    T get(int index);

    /**
     * Element at i or value of notFound. Will not throw unless notFound is null.
     */
    T getOr(int index, Supplier<T> notFound);

    /**
     * One more than where get succeeds
     */
    int size();

    /**
     * when get always fails
     */
    boolean isEmpty();
}
