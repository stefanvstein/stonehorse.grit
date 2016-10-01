package stonehorse.grit.tools;


import java.util.Collection;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Set that throws UnsupportedOperationException up on mutation finally
		 * <p>
		 * Currently holds for Java 1.8
 */
public abstract class ImmutableSet<T> implements Set<T> {

	/**
	 * Throws UnsupportedOperationException
	 */
	@Override
	public final  boolean add(T e) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws UnsupportedOperationException
	 */
	@Override
	public final  boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws UnsupportedOperationException
	 */
	@Override
	public final  boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws UnsupportedOperationException
	 */
	@Override
	public final  boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws UnsupportedOperationException
	 */
	@Override
	public final  boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws UnsupportedOperationException
	 */
	@Override
	public final  void clear() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Throws UnsupportedOperationException
	 */
	@Override
	public final  boolean removeIf(Predicate<? super T> predicate) {
		throw new UnsupportedOperationException();
	}


	@Override
	public final  Spliterator<T> spliterator() {
		return Spliterators.spliterator(this, Spliterator.IMMUTABLE & Spliterator.CONCURRENT & Spliterator.SIZED);
	}


	@Override
	public Stream<T> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}


	@Override
	public Stream<T> parallelStream() {
		return StreamSupport.stream(this.spliterator(), true);
	}


}
