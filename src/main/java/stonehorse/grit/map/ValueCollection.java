package stonehorse.grit.map;


import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class ValueCollection<K, V> extends AbstractCollection<V> {

	private final Map<K, V> map;

	public ValueCollection(Map<K, V> map) {
		this.map = map;
	}
	@Override
	public Iterator<V> iterator() {
	    final Iterator<Entry<K, V>> mi = map.entrySet().iterator();

	    return new Iterator<V>() {
	        @Override
	        public boolean hasNext() {
	            return mi.hasNext();
	        }
	        @Override
	        public V next() {
	            Entry<K, V> e = (Entry<K, V>) mi.next();
	            return e.getValue();
	        }
	    };
	}
	@Override
	public int size() {
	    return map.size();
	}
}