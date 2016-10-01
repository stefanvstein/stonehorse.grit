package stonehorse.grit;

/**
 * A stack of elements where mutation is expression
 */
public interface PersistentStack<T> {
  /**
   * The head of the stack
   */
  T get();

  /**
   * This stack withAt an additional pushed element
   */
  PersistentStack<T> with(T element);

  /**
   * This stack without head
   */
  PersistentStack<T> without();

  /**
   * This stack withAt elements added to head
   */
  PersistentStack<T> withAll(Iterable<T> elements);

  /**
   * This stack without num elements at head
   */
  PersistentStack<T> drop(int num);

  /**
   * when this stack is empty
   */
  boolean isEmpty();
}
