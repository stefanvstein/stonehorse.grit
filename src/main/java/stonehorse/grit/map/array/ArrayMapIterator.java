package stonehorse.grit.map.array;



import stonehorse.grit.tools.ImmutableIterator;
import stonehorse.grit.map.APMapEntry;

import java.util.Map;
import java.util.NoSuchElementException;

class ArrayMapIterator<K, V> extends ImmutableIterator<Map.Entry<K, V>> {

	private final Object[] array;

	private int i = -2;

	public ArrayMapIterator(Object[] array) {
		this.array = array;
	}

	public boolean hasNext() {
		return i < array.length - 2;
	}

	@SuppressWarnings("unchecked")
	public Map.Entry<K, V> next() {
		i += 2;
		if(i>=array.length)
			throw new NoSuchElementException();
		return new APMapEntry<K, V>((K) array[i], (V) array[i + 1]);
	}

}