package stonehorse.grit.tools;


import java.util.ListIterator;

/**
 * ListIterator that throws UnsupportedOperationException up on mutation
 */
public abstract class ImmutableListIterator<E> implements ListIterator<E> {

	@Override
	public final void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void set(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void add(E e) {
		throw new UnsupportedOperationException();
	}

}
