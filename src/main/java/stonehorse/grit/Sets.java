package stonehorse.grit;

import stonehorse.grit.set.PSet;

import java.util.stream.Collector;

import static stonehorse.grit.set.SetCollector.collector;

/**
 * Creation of persistent Set structures that accept null as ordinary valid value.
 * These sets are read only, while mutation is an expression and hence a new value.
 * These sets may differ in implementation depending on content and size.
 * Larger sets share structure withAt previous sets.
 * All element are expected to be effectively immutable.
 */
public class Sets {
    /**
     * The set of nothing
     */
    public static <T> PersistentSet<T> set(){
        return PSet.empty();
    }

    /**
     * A single value set
     */
    public static <T> PersistentSet<T> set(T a){
        return PSet.<T>empty().with(a);
    }

    /**
     * A set of supplied values
     */
    public static <T> PersistentSet<T> set(T a, T b){
        return PSet.<T>empty().with(a).with(b);
    }
    /**
     * A set of supplied values
     */
    public static <T> PersistentSet<T> set(T a, T b, T c){
        return PSet.<T>empty().with(a).with(b).with(c);
    }
    /**
     * A set of supplied values
     */
    public static <T> PersistentSet<T> set(T a, T b, T c, T d){
        return PSet.<T>empty().with(a).with(b).with(c).with(d);
    }
    /**
     * A set of supplied values
     */
    public static <T> PersistentSet<T> set(T a, T b, T c, T d, T e){
        return PSet.<T>empty().with(a).with(b).with(c).with(d).with(e);
    }
    /**
     * A set of supplied values
     */
    public static <T> PersistentSet<T> set(T a, T b, T c, T d, T e, T f){
        return PSet.<T>empty().with(a).with(b).with(c).with(d).with(e).with(f);
    }
    /**
     * A set of supplied values
     */
    public static <T> PersistentSet<T> set(T a, T b, T c, T d, T e, T f, T g){
        return PSet.<T>empty().with(a).with(b).with(c).with(d).with(e).with(f).with(g);
    }
    /**
     * A set of supplied values
     */
    public static <T> PersistentSet<T> set(T a, T b, T c, T d, T e, T f, T g, T h){
        return PSet.<T>empty().with(a).with(b).with(c).with(d).with(e).with(f).with(g).with(h);
    }
    /**
     * A set of supplied values
     */
    public static <T> PersistentSet<T> set(T a, T b, T c, T d, T e, T f, T g, T h, T i){
        return PSet.<T>empty().with(a).with(b).with(c).with(d).with(e).with(f).with(g).with(h).with(i);
    }
    /**
     * A set of supplied values
     */
    public static <T> PersistentSet<T> set(T a, T b, T c, T d, T e, T f, T g, T h, T i, T j){
        return PSet.<T>empty().with(a).with(b).with(c).with(d).with(e).with(f).with(g).with(h).with(i).with(j);
    }
    /**
     * A set of supplied values
     */
    public static <T> PersistentSet<T> setOfAll(T... values){
        PersistentSet<T> s=Sets.<T>set();
        for(int i = 0;i<values.length; i++)
            s=s.with(values[i]);
        return s;
    }
    /**
     * A set of all values
     */
    public static <T> PersistentSet<T> setOf(Iterable<? extends T> values){
        return PSet.<T>empty().union(values);
    }

    /**
     * A terminal persistent set of a Stream
     */
    public static <T> Collector<T, ?, PersistentSet<T>> toSet() {
        return collector(set());
    }
}
