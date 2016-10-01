package stonehorse.grit;

/**
 * Composite where homogeneous values can be looked up by key
 */
public interface Lookup<V> {
    /**
     * value associated withAt key or null when missing
     */
    V get(Object key);

    /**
     * True if a value is associated withAt key
     */
    boolean has(Object key);
}
