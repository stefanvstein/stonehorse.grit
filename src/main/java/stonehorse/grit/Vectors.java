package stonehorse.grit;

import stonehorse.grit.vector.PFifo;
import stonehorse.grit.vector.PVector;

import java.util.stream.Collector;

import static stonehorse.grit.vector.VectorCollector.collector;

/**
 * Creation of persistent sequential vector structures.
 * These vectors are immutable, while mutation is an expression and hence a new value.
 * These vectors share structure so all elements are expected to be effectively immutable.
 * <p>
 * These vectors are based on very shallow and highly branching trees, with an algorithmic
 * complexity of O log32 n. The same kind of vectors are used as default in languages like Clojure and Scala.
 */
public class Vectors {
    /**
     * empty
     */
     static public <T> PersistentVector<T> vector() {
        return PVector.empty();
        }

    /**
     * single value
     */
    static public <T> PersistentVector<T> vector(T t1) {
        return PVector.withTail(new Object[]{t1});
    }

    /**
     * Persistent Vector of supplied values in order
     */
    static public <T> PersistentVector<T> vector(T t1, T t2) {
        return PVector.withTail(new Object[]{t1,t2});
    }
    /**
     * Persistent Vector of supplied values in order
     */
    static public <T> PersistentVector<T> vector(T t1, T t2, T t3) {
        return PVector.withTail(new Object[]{t1, t2, t3});
    }
    /**
     * Persistent Vector of supplied values in order
     */
    static public <T> PersistentVector<T> vector(T t1, T t2, T t3, T t4) {
        return PVector.withTail(new Object[]{t1, t2, t3, t4});
    }
    /**
     * Persistent Vector of supplied values in order
     */
    static public <T> PersistentVector<T> vector(T t1, T t2, T t3, T t4, T t5) {
        return PVector.withTail(new Object[]{t1, t2, t3, t4, t5});
    }
    /**
     * Persistent Vector of supplied values in order
     */
    static public <T> PersistentVector<T> vector(T t1, T t2, T t3, T t4, T t5, T t6) {
        return PVector.withTail(new Object[]{t1, t2, t3, t4, t5, t6});
    }
    /**
     * Persistent Vector of supplied values in order
     */
    static public <T> PersistentVector<T> vector(T t1, T t2, T t3, T t4, T t5, T t6, T t7) {
        return PVector.withTail(new Object[]{t1, t2, t3, t4, t5, t6, t7});
    }
    /**
     * Persistent Vector of supplied values in order
     */
    static public <T> PersistentVector<T> vector(T t1, T t2, T t3, T t4, T t5, T t6, T t7, T t8) {
        return PVector.withTail(new Object[]{t1, t2, t3, t4, t5, t6, t7, t8});
    }
    /**
     * Persistent Vector of supplied values in order
     */
    static public <T> PersistentVector<T> vector(T t1, T t2, T t3, T t4, T t5, T t6, T t7, T t8, T t9) {
        return PVector.withTail(new Object[]{t1, t2, t3, t4, t5, t6, t7, t8, t9});
    }
    /**
     * Persistent Vector of supplied values in order
     */
    static public <T> PersistentVector<T> vector(T t1, T t2, T t3, T t4, T t5, T t6, T t7, T t8, T t9, T t10) {
        return PVector.withTail(new Object[]{t1, t2, t3, t4, t5, t6, t7, t8, t9, t10});
    }
    /**
     * Persistent Vector of all values in retrieved order
     */
     static public <T, V extends T> PersistentVector<T> vectorOf(
            Iterable<? extends V> elements) {
        return PVector.create(elements);
    }
    /**
     * Persistent Vector of supplied all values in order
     */
    static public <T, V extends T> PersistentVector<T> vectorOfAll(V... elements) {
        return PVector.createOfAll(elements);
    }

    static <T> PersistentFifo<T> fifo(PersistentVector v){
        return PFifo.of(v);
    }

    /**
     * Terminates Stream in a persistent vector
     */
    public static <T> Collector<T,?, PersistentVector<T>> toVector(){
        return  collector(vector());
    }

}
