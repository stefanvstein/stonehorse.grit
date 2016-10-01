package stonehorse.grit.map.hash;


import stonehorse.grit.tools.ImmutableIterator;
import stonehorse.grit.PersistentMap;
import stonehorse.grit.map.*;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static stonehorse.candy.Choices.cond;
import static stonehorse.candy.Choices.ifelse;
import static stonehorse.candy.Choices.mapOr;
import static stonehorse.grit.tools.Util.hash;

/*
A persistent rendition of Phil Bagwell's Hash Array Mapped Trie

Uses path copying for persistence
HashCollision leaves vs. extended hashing
NodeOp polymorphism vs. conditionals
No sub-tree pools or root-resizing

*/
// https://idea.popcount.org/2012-07-25-introduction-to-hamt/

public class PHashMap<T, V> extends APMap<T, V> implements EphemerableMap<T,V>, PersistentMap<T,V>, Serializable{

	final private static Object NOT_FOUND = new Object();
	final int count;
	final Node root;
	final boolean hasNull;
	final V nullValue;

	final public static PHashMap<?, ?> EMPTY = new PHashMap<Object, Object>(0,
			null, false, null);

	@SuppressWarnings("unchecked")
	public static <K, V> PHashMap<K, V> empty() {
		return (PHashMap<K, V>) EMPTY;
	}

	private PHashMap(int count, Node root, boolean hasNull, V nullValue) {
		this.count = count;
		this.root = root;
		this.hasNull = hasNull;
		this.nullValue = nullValue;
	}

	static <K,V> PHashMap<K,V> create(int count, Node root, boolean hasNull, V nullValue){
		//TODO return empty if count is 0. but first make sure all nodes are cleaned up when no longer used

		return new PHashMap<K, V>(count, root, hasNull, nullValue);
	}

	 private Object writeReplace() throws ObjectStreamException {
			return new SerializedMap(this);
		}

	@Override
	public PHashMap<T, V> with(T key, V val) {
		return mapOr(key,
				k -> withKey(val, k),
				() -> withNull(val));
	}

	private PHashMap<T, V> withKey(V val, T k) {
		Result r = assoc(rootOrEmpty(), k, val);
		return cond(
                () -> isReplaced(r), () -> create(count, r.node, hasNull, nullValue),
                () -> isResized(r), () -> create(count + 1, r.node, hasNull, nullValue),
                () -> this);
	}

	private Node rootOrEmpty() {
		return root == null ? BitmapIndexedNode.EMPTY : root;
	}


	private boolean isReplaced(Result r){
	return (r.node!=root && !r.isResized);
	}

	private boolean isResized(Result r){
		return r.isResized;
	}

	private PHashMap<T, V> withNull(V val) {
		return ifelse (
				nullValueIs(val),
				()-> this,
				()-> create(
						ifelse(hasNull,
								()-> count,
								()-> count + 1),
						root,
						true,
						val));
	}

	private boolean nullValueIs(V val) {
		return hasNull && val == nullValue;
	}

	private Result assoc(Node node, Object key, Object val ){
		return node.with(0, hash(key), key, val, false);
	}

	@Override
	public PHashMap<T, V> ensureKey(T key, V val) {
		if (!has(key))
			return with(key, val);
		return this;
	}

	@Override
	public PHashMap<T, V> without(Object key) {
		return cond(
				() -> key == null,
				this::withoutNull,

				() -> root == null || root.isEmpty(),
				() -> ifelse(hasNull(),
						() -> this,
						()->empty()),
				() -> withoutInTree(key));
	}

	private PHashMap<T, V> withoutInTree(Object key) {
		Node newroot = root.without(0, hash(key), key);
		return cond(
				() -> newroot == root,
				() -> this,
				() -> (!hasNull() && (newroot == null || root.isEmpty())),
				() -> empty(),
				() -> create(count - 1, newroot, hasNull, nullValue));
	}

	private PHashMap<T, V> withoutNull() {
		return ifelse(
				hasNull ,
				()-> create(count - 1, root, false, null),
                ()-> this);
	}

	@Override
	public boolean containsKey(Object key) {
		return has(key);
	}

	@Override
	public boolean has(Object key) {
		return cond(
				()->key == null,
				()->hasNull,
				()->root!=null,
				()->root.find(0, hash(key), key, NOT_FOUND) != NOT_FOUND,
				()->false);
	}

	@Override
	public int size() {
		return count;
	}

	@Override
	public boolean isEmpty() {
		return count == 0;
	}



	@Override
	public V get(Object key) {
		return ifelse(key == null,
				() -> ifelse(hasNull,
						() -> (V) nullValue,
						() -> null),
				() -> ifelse(root != null,
						() -> (V) root.find(0, hash(key), key, null),
						() -> null));
	}

	
	public EphemeralHashMap<T, V> ephemeral() {
		return  EphemeralHashMap.of(this);
	}



	@Override
	public Iterator<Map.Entry<T, V>> iterator() {

		final Iterator<Map.Entry<T,V>> i= Iteration.<T,V>iterable(root, hasNull, nullValue).iterator();
		return new ImmutableIterator<Entry<T, V>>() {
			@Override
			public boolean hasNext() {
				return i.hasNext();
			}

			@Override
			public Entry<T, V> next() {
				return i.next();
			}
		};
	}

	@Override
	public PersistentMap<T, V> withAll(Map<? extends T, ? extends V> map) {
		EphemeralMap<T, V> m= ephemeral();
			m=m.withAll(map);
		return m.persistent();
	}

	
	@Override
	public PersistentMap<T, V> withoutAll(Iterable<?> keys) {
		EphemeralMap<T, V> m= ephemeral();
		return m.withoutAll(keys).persistent();
	}

	public Node root() {
		return root;
	}

	public boolean hasNull() {
		return hasNull;
	}

	public V nullValue() {
		return nullValue;
	}

	@Override
	public boolean containsValue(Object value) {
		return new ValueCollection<>(this).contains(value);
	}

	public String dump(){
		return "count: "+size()+
				System.lineSeparator()+
				(hasNull()?"null:"+nullValue+System.lineSeparator():"")+
				"root:" +Objects.toString(root());
	}

	@Override
	public APMap<T, V> withoutWhen(Object key, Predicate<V> predicate) {
		requireNonNull(predicate);
		return cond(()->!has(key),
				()->this,
				()->predicate.test(get(key)),
				()->without(key),
				()->without(key));
	}
}
