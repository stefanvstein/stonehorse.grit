package stonehorse.grit.tools;

import stonehorse.grit.Randomly;

import java.util.NoSuchElementException;

public class RandomlyListIterator<T> extends ImmutableListIterator<T> {
	private int nexti;
	private final Randomly<T> ran;

	private RandomlyListIterator(Randomly<T> ran, int index){
		this.ran =ran;
		nexti=index;
	}
	
	public static <T> RandomlyListIterator<T> create(Randomly<T> ran, int index){
		return new RandomlyListIterator<>(ran, index);
	}

	public boolean hasNext() {
		return nexti < ran.size();
	}

	public T next() {
		if (hasNext())

			return ran.get(nexti++);
		throw new NoSuchElementException();
	}

	public boolean hasPrevious() {
		return nexti > 0;
	}

	public T previous() {
		if (hasPrevious())
			return ran.get(--nexti);
		throw new NoSuchElementException();
	}

	public int nextIndex() {
		return nexti;
	}

	public int previousIndex() {
		return nexti - 1;
	}
}