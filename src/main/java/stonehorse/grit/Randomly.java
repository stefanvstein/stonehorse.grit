package stonehorse.grit;


import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Composite where homogeneous members are randomly accessible by integer index
 */
public interface Randomly<T> extends IntFunction<T> {
    /**
     * Elememt at index, where i is absolut and less than size
     */
    T get(int index);

    /**
     * Element at i or notFound, where i is absolut
     */
    T getOr(int index, Supplier<T> notFound);

    /**
     * the maximum index allowed in getOr
     */
    int size();

    /**
     * when getOr always will fail
     */
    boolean isEmpty();
}
