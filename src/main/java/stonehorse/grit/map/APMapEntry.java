package stonehorse.grit.map;

import java.util.Map;

public class APMapEntry<K, V> implements Map.Entry<K, V> {

    private final V v;
    private final K k;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (k == null)
            sb.append("null");
        else sb.append(k.toString());
        sb.append("=");
        if (v == null)
            sb.append("null");
        else sb.append(v.toString());
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Map.Entry))
            return false;
        Map.Entry<?, ?> e = (Map.Entry<?, ?>) obj;
        Object k1 = getKey();
        Object k2 = e.getKey();
        if (k1 == k2 || (k1 != null && k1.equals(k2))) {
            Object v1 = getValue();
            Object v2 = e.getValue();
            if (v1 == v2 || (v1 != null && v1.equals(v2)))
                return true;
        }
        return false;
    }

    public APMapEntry(K k, V v) {
        this.k = k;
        this.v = v;
    }

    @Override
    public K getKey() {
        return k;
    }

    @Override
    public V getValue() {
        return v;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }
}