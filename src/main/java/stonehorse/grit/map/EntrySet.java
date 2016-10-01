package stonehorse.grit.map;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map.Entry;

final class EntrySet<K, V> extends AbstractSet<Entry<K, V>> {
	private final APMap<K, V> apMap;
	EntrySet(APMap<K, V> apMap) {
		this.apMap = apMap;
	}
	public Iterator<Entry<K, V>> iterator() {
		final Iterator<Entry<K, V>> i = apMap.iterator();
		return new Iterator<Entry<K, V>>() {
			@Override public boolean hasNext() {
				return i.hasNext();
			}
			@Override public Entry<K, V> next() {
				return i.next();
			}
		};
	}
	@Override public int size() {
		return apMap.size();
	}
	@Override public int hashCode() {
		return apMap.hashCode();
	}
	@Override public boolean contains(Object o) {
		if (o instanceof Entry) {
			Entry<?, ?> e = (Entry<?, ?>) o;
			if (this.apMap.has(e.getKey())) {
				Object value = this.apMap.get(e.getKey());
				if (value == e.getValue())
					return true;
				if (value == null)
					return false;
				return value.equals(e.getValue());
			}
		}
		return false;
	}
}