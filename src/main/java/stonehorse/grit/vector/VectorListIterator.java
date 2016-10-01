package stonehorse.grit.vector;

import stonehorse.grit.tools.ImmutableListIterator;

import java.util.NoSuchElementException;

class VectorListIterator<T> extends ImmutableListIterator<T> {
	private int nexti;
	private final APVector<T> v;

	private VectorListIterator(APVector<T> v, int index){
		this.v=v;
		nexti=index;
	}
	
	public static <T> VectorListIterator<T> create(APVector<T> v, int index){
		return new VectorListIterator<>(v, index);
	}

	public boolean hasNext() {
		return nexti < v.size();
	}

	public T next() {
		if (hasNext())

			return v.get(nexti++);
		throw new NoSuchElementException();
	}

	public boolean hasPrevious() {
		return nexti > 0;
	}

	public T previous() {
		if (hasPrevious())
			return v.get(--nexti);
		throw new NoSuchElementException();
	}

	public int nextIndex() {
		return nexti;
	}

	public int previousIndex() {
		return nexti - 1;
	}
}