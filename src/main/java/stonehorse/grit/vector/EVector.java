package stonehorse.grit.vector;

import stonehorse.grit.Indexed;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

import static stonehorse.candy.Choices.cond;
import static stonehorse.candy.Choices.ifelse;
import static stonehorse.candy.Choices.mapOr;
import static stonehorse.candy.Iterables.first;
import static stonehorse.candy.Iterables.next;
import static stonehorse.candy.Iterables.reduce;
import static stonehorse.grit.vector.Node.nodeOfArray;
import static stonehorse.grit.vector.VFuns.*;

public final class EVector<T> implements Indexed<T> {
	private int size;
private AtomicReference<Object> owner;
	private Node root;
	private Object[] tail;

	private EVector(int cnt, Node root, Object[] tail) {
		this.size = cnt;
		this.root = root;
		this.tail = tail;
		owner = new AtomicReference<>(new Object());
	}

	public static <T> EVector<T> of(PVector<T> v){
		return new EVector<T>(v.size, Node.nodeOf(v.root), Arrays.copyOf(v.tail, BUCKET_SIZE));
	}

	private void ensureEditable() {
		if(owner.get()==null)
			throw new IllegalStateException(
					"No longer Ephemeral");

	}
	public PVector<T> persistent() {
		if(null==owner.getAndUpdate(v->null))
			throw new IllegalStateException(
					"No longer Ephemeral");
		return new PVector<>(size, root, trimmedTail());
	}

	private Object[] trimmedTail() {
		return Arrays.copyOf(tail, size- VFuns.tailStartOffset(size));
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(int i) {
		return (T) arrayFor(i,size,root,levels(size),tail)[indexOfArrayAt(i)];
	}

	@Override
	public T get(int i, T notFound) {
		return ifelse(outOfBounds(i, size),
				() -> notFound,
				() -> get(i));
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	private Node rootNodeWith(int lev, Node node, int i, Object val) {
		int index = indexInLevel(i, lev);
		Object value =  ifelse(
				isLeaf(lev),
				()->val,
				()->rootNodeWith(lev - 1, node.child(index), i, val));
		return withIndexSet(node, index, value);
	}

	@Override
	public EVector<T> withAt(T val, int i) {
		ensureEditable();
		return cond(
				()->isAtEnd(i), ()->with(val),
				()->outOfBounds(i,size), EVector::throwIndexOutOfBounds,
				()->isInTail(i,size), ()->withInTail(i, val),
				()->withInTree(i,val));
	}

	private EVector<T> withInTail(int i, T val){
		tail[indexOfArrayAt(i)] = val;
		return this;
	}

	private EVector<T> withInTree(int i, T val) {
		root = rootNodeWith(levels(size()), root, i, val);
		return this;
	}

	private boolean isAtEnd(int i) {
		return size==i;
	}

	private static <T> EVector<T> throwIndexOutOfBounds(){
		throw new IndexOutOfBoundsException();
	}
	@Override
	public EVector<T> with(T val) {
		ensureEditable();
		return withImpl(val);
	}

	public EVector<T> withImpl(T val) {
		return ifelse(VFuns.isFullTail(size),
				() -> withPushedIntoTree(val),
				() -> tailWith(val));
	}

	private EVector<T> tailWith(T val) {
		tail[indexOfArrayAt(size)] = val;
		++size;
		return this;
	}

	private EVector<T> withPushedIntoTree(T val) {
		return ifelse(
				isFullRoot(size, levels(size())),
				()-> withPushedIntoDeeperTree(val),
				()-> withPushedIntoEquallySizedTree(val));
	}

	private EVector<T> withPushedIntoEquallySizedTree(T val) {
		root = withNodeIn(pathToNode(size, size - 1), root, nodeOfArray( tail));
		tail = new Object[BUCKET_SIZE];
		tail[0] = val;
		++size;
		return this;
	}

	private EVector<T> withPushedIntoDeeperTree(T val) {
		root = newLevelRoot(root, levels(size()), nodeOfArray( tail));
		tail = new Object[BUCKET_SIZE];
		tail[0] = val;
		++size;
		return this;
	}

	private static boolean has(Object o){
		return null!=o;
	}
	private static Node withNodeIn(Iterable<Integer> path, Node rootNode, Node node) {
		int index = first(path);
		Iterable<Integer> remaining = next(path);
		return ifelse(has(remaining),
				()-> mapOr(rootNode.child(index),
						child->withIndexSet(rootNode, index, withNodeIn(remaining, child, node)),
						()->withIndexSet(rootNode, index, newPath( size(path), node))),
				()->withIndexSet(rootNode, index, node));
	}

	private static int size(Iterable<?> i){
		return reduce((a,v)-> a+1, 0, i);
	}

	private static Node withIndexSet(Node node, int index, Object val) {
		return node.altered(index, val);
	}

	@Override
	public  EVector<T> without() {
		ensureEditable();
		return withoutImpl();
	}

	private EVector<T> withoutImpl() {
		if (isEmpty())
			throw new NoSuchElementException();
		return ifelse(isWithoutInTail(),
				this::withoutInTail,
				this::withoutInTree);
	}

	private EVector<T> withoutInTree() {
		Object[] newTail = arrayFor(size - 2, size, root, levels(size()), tail);
		Iterable<Integer> path = pathToNode(size, indexOfLastAfterWithout(size));
		root = rootWithoutLast(path, root);

		if (canShrink())
			shrink();

		size--;
		tail = newTail;
		return this;
	}

	private boolean canShrink() {
		return levels(size) > 1 && root.childObject(1) == Node.emptyNode();
	}

	private void shrink(){
		root = root.child(0);
	}

	private static Node rootWithoutLast(Iterable<Integer> path, Node node) {
		int index = first(path);
		Iterable<Integer> remaining = next(path);
		return ifelse(has(remaining),
				()->withIndexSet(node, index, rootWithoutLast(remaining, node.child(index))),
				()->ifelse(index==0,
						Node::emptyNode,
						()->withIndexSet(node, index, null)));
	}


	private static int indexOfLastAfterWithout(int size){
		return size-2;
	}
	private EVector<T> withoutInTail() {
		size--;
		return this;
	}

	private boolean isWithoutInTail() {
		return size == 1 || indexOfArrayAt(size-1) > 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj instanceof EVector) {
			EVector<?> t = (EVector<?>) obj;
			if (t.size() != t.size())
				return false;
			for (int i = 0; i < size(); i++) {
				T v = get(i);
				Object a = t.get(i);
				if (v == a)
					continue;
				if (v != null && v.equals(a))
					continue;
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = 0 ;i<size;i++) {
			Object e = get(i);
			hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
		}
		return hashCode;
	}

	@Override
	public T get() {
		if (isEmpty())
			return null;
		return get(size() - 1);
	}

	@Override
	public EVector<T> withAll(Iterable<T> elements) {
		ensureEditable();
		if (elements == null)
			return this;
		EVector<T> l = this;
		for (T t : elements)
			l = l.withImpl(t);
		return l;
	}

	@Override
	public EVector<T> drop(int num) {
		ensureEditable();
		if (num == size)
			return PVector.<T> empty().ephemeral();
		if(num>size)
			throw new IllegalArgumentException("Too few elements");
		EVector<T> l = this;
		for (int i = 0; i < num; i++)
			l = l.withoutImpl();
		return l;
	}

	@Override
	public T apply(int index) {
		return get(index);
	}
}