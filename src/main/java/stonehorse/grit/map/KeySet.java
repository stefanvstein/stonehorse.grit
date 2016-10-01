package stonehorse.grit.map;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

final class KeySet<K, V> extends AbstractSet<K> {

	private final Map<K, V> map;

	public KeySet(Map<K, V> map) {
		this.map = map;
	}
	@Override public Iterator<K> iterator() {
		final Iterator<Entry<K, V>> mi = map.entrySet().iterator();

		return new Iterator<K>() {
			@Override public boolean hasNext() {
				return mi.hasNext();
			}
			@Override public K next() {
				Entry<K, V> e = (Entry<K, V>) mi.next();
				return e.getKey();
			}
		};
	}
	@Override public int size() {
		return map.size();
	}
	@Override public boolean contains(Object o) {
		return map.containsKey(o);
	}
}