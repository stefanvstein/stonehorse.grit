package stonehorse.grit;

import java.util.function.Predicate;

/**
 * A stack of elements where mutation is expression
 */
public interface PersistentStack<T> {
  /**
   * The head of the stack or NoSuchElementException
   */
  T get();

  /**
   * This stack with an additional pushed element
   */
  PersistentStack<T> with(T element);

  /**
   * This stack without head or NoSuchElementException
   */
  PersistentStack<T> without();

  /**
   * This stack with elements added to head
   */
  PersistentStack<T> withAll(Iterable<T> elements);

  /**
   * This stack without num elements at head, or IllegalArgumentException
   */
  PersistentStack<T> drop(int num);

  /**
   * This without those first matching pred. Will not throw unless pred is null or throws.
   */
  PersistentStack<T> dropWhile(Predicate<? super T> pred);

  /**
   * When without get get would throw
   */
  boolean isEmpty();
}
