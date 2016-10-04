package stonehorse.grit.vector;


import stonehorse.candy.Iterables;
import stonehorse.candy.Tuples;

import stonehorse.grit.PersistentVector;
import stonehorse.grit.SerializedVector;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static stonehorse.candy.Choices.cond;
import static stonehorse.candy.Choices.ifelse;
import static stonehorse.candy.Choices.mapOr;
import static stonehorse.candy.Iterables.*;
import static stonehorse.grit.vector.VFuns.*;


/**
 *                                 root shift=5 
 * 00 00000 00000 00000 00000 00000 00000 = 0
 * 00 00000 00000 00000 00000 00000 00001 = 1
 * 00 00000 00000 00000 00000 00000 11111 = 31
 *                            root shift=10
 * 00 00000 00000 00000 00000 00001 00000 = 1 0 = 32
 * 00 00000 00000 00000 00000 00001 00010 = 1 2 = 34
 *
 * https://infoscience.epfl.ch/record/169879/files/RMTrees.pdf
 */

public class PVector<T> extends APVector<T> implements Serializable{



    final int size;
    final Node root;
    final Object[] tail;

    PVector(int cnt, Node root, Object[] tail) {
        this.size = cnt;
        this.root = root;
        this.tail = tail;
    }

    public Tuples.T3<Integer, Node, Node> data(){
        return Tuples.of(size, Node.nodeOfArray(tail), root);
    }
    public String dump(){
        return ("Size:" + size) +
                " Tail:" + Arrays.asList(tail) +
                " Tree:" + root;
    }

    private static <T> EVector<T> ephemeral(PVector<T> v){
        return v.ephemeral();
    }

    static public <T> PersistentVector<T> create(Iterable<? extends T> items) {
        EVector<T> ret = ephemeral(empty());
        for (T item : items)
            ret = ret.with(item);
        return ret.persistent();
    }

    static public <T> EVector<T> createEphemeral(Iterable<? extends T> items) {
        EVector<T> ret = ephemeral(empty());
        for (T item : items)
            ret = ret.with(item);
        return ret;
    }

    static public <T> PersistentVector<T> createOfAll(T... items) {
        if(items.length<=VFuns.BUCKET_SIZE)
            return withTail(items);
        EVector<T> ret = ephemeral(empty());
        for (T item : items)
            ret = ret.with(item);
        return ret.persistent();
    }

    static public <T> PersistentVector<T> withTail(Object[] tail){
        if(tail.length>VFuns.BUCKET_SIZE)
            throw new IllegalArgumentException();
        return new PVector<T>(tail.length, Node.emptyNode(), tail);
    }
    @SuppressWarnings("unchecked") public static <T> PVector<T> empty() {
        return (PVector<T>) EMPTY;
    }

    private Object writeReplace() throws ObjectStreamException {
		return new SerializedVector(this);
	}
    
    @SuppressWarnings("rawtypes") private final static PVector<?> EMPTY =
            new PVector(0,  Node.emptyNode(), new Object[] {});

    @Override public int size() {
        return size;
    }

    Iterator<T> rangedIterator(final int start, final int end) {
        return new VectorIterator<>(start, end, this);
    }

    @Override public Iterator<T> iterator() {
        return rangedIterator(0, size());
    }


    @Override
	public PersistentVector<T> withAll(Iterable<T> elements) {
        return mapOr(elements,
                (e) -> ephemeral().withAll(e).persistent(),
                ()->this);
    }

    // **************** getOr

    @SuppressWarnings("unchecked") @Override public T get(int i) {
        return (T) arrayFor(i,size,root, levels(size), tail)[indexOfArrayAt(i)];
    }

    @Override public T getOr(int i, Supplier<T> notFound) {
        requireNonNull(notFound);
        return ifelse (outOfBounds(i, size),
                ()->notFound.get(),
                ()->get(i));
    }

    // *** drop
	@Override
    public PersistentVector<T> drop(int num) {
        return cond(
                () -> num > size(),
                PVector::throwToFewElements,
                () -> num > 0,
                () -> ephemeral().drop(num).persistent(),
                () -> this);
    }

    private static <T> PersistentVector<T> throwToFewElements() {
        throw new IllegalArgumentException("To few elements");
    }

    // ************with
    @Override public PVector<T> with(T val) {
        return ifelse (isFullTail(size),
                ()->withPushedIntoTree(val),
                ()->withPushedIntoTail(val));
    }

    private PVector<T> withPushedIntoTail(T val) {
        return new PVector<>(size + 1,  root, tailWith(val));
    }

    private Object[] tailWith(T val) {
        Object[] newTail=Arrays.copyOf(tail,tail.length+1);
        newTail[tail.length] = val;
        return newTail;
    }

    private PVector<T> withPushedIntoTree(T val) {
        return ifelse(
                isFullRoot(size, levels(size)),
                ()-> withPushedIntoDeeperTree(val),
                ()-> withPushedIntoEquallySizedTree(val));
    }

    private PVector<T> withPushedIntoEquallySizedTree(T val) {
        final Iterable<Integer> path = pathToNode(size, size - 1);
        Node newRoot = withNodeIn(path, root, Node.nodeOfArray(tail));
        return new PVector<>(size + 1,  newRoot, new Object[]{val});
    }


    private static Node withNodeIn(Iterable<Integer> path, Node mother, Node value){
        int index = first(path);
        Iterable<Integer> remaining= next(path);
        return ifelse(has(remaining),
                ()->mapOr(mother.child(index),
                        child->cloneNodeWithValue(mother, withNodeIn(remaining, child, value), index),
                        ()->cloneNodeWithValue(mother, newPath(size(path)-1, value), index)),
                ()->cloneNodeWithValue(mother, value, index));

    }

    private static int size(Iterable<?> i){
        return Iterables.reduce((a, v)-> a+1, 0, i);
    }

    private static Node cloneNodeWithValue(Node node, Object v, int index) {
        return node.with(index, v);
    }

    private PVector<T> withPushedIntoDeeperTree(T val) {
        final Node newRoot = newLevelRoot(this.root, levels(size), Node.nodeOfArray(tail));
        return new PVector<>(size + 1, newRoot, new Object[]{val});
    }

    @Override
    public PVector<T> withAt(T val, int i) {
        return cond(
                () -> isAtEnd(i), () -> with(val),
                () -> outOfBounds(i, size), PVector::throwIndexOutOfBounds,
                () -> isInTail(i, size), () -> withInTail(i, val),
                () -> withInTree(i, val));
    }

    private static <T> PVector<T> throwIndexOutOfBounds(){
        throw new IndexOutOfBoundsException();
    }

    private PVector<T> withInTree(int i, T val) {
        final Iterable<Integer> path = pathToValue(size, i);
        return new PVector<>(size, nodeWith(path, root, val), tail);
    }

    private PVector<T> withInTail(int i, T val) {
        return new PVector<>(size,  root, tailWith(i, val));
    }


    private boolean isAtEnd(int i) {
        return size==i;
    }


    private Object[] tailWith(int i, T val) {
        return withIndexSet(tail.clone(), indexOfArrayAt(i), val);
    }

    private static Object[] withIndexSet(Object[] arr, int index, Object val){
        arr[index]=val;
        return arr;
    }

    private static Node nodeWith(Iterable<Integer> path, Node node, Object val) {
        int index= first(path);
        Iterable<Integer> remaining= next(path);
        Object value= ifelse(
                has(remaining),
                ()->nodeWith(remaining, node.child(index), val),
                ()->val);
        return cloneNodeWithValue(node, value, index);
    }

    private static boolean has(Object o){
        return null!=o;
    }

 // *************** withoutEphemerally

    @Override
    public PVector<T> without() {
        return cond(
                () -> size == 0,
                PVector::throwNoSuchElement,
                () -> size == 1,
                PVector::empty,
                () -> tailSize(size) > 1,
                this::withoutInTail,
                this::withoutInTree);
    }

    @Override
    public <V> V fold(BiFunction<? super V, ? super T, ? extends V> fn, V acc) {
        requireNonNull(fn);
        int levels = levels(size);
        for(int i = 0; i<size();i++)
          acc=fn.apply(acc,(T)arrayFor(i,size,root, levels, tail)[indexOfArrayAt(i)]);
        return acc;
    }

    @Override
    public <V> PVector<V> map(Function<? super T, ? extends V> f) {
        requireNonNull(f);
        EVector<V> destination = PVector.<V>empty().ephemeral();
        for(T t:this)
            destination=destination.with(f.apply(t));
        return destination.persistent();
    }
//Gosh...rewrite this shit
    @Override
    public <V> PVector<V> flatMap(Function<? super T, Iterable<? extends V>> f) {
        requireNonNull(f);
        EVector<V> destination = PVector.<V>empty().ephemeral();
        for(T t:this) {
            Iterable<? extends V> vs = f.apply(t);
            if (vs != null) {
                Iterator<? extends V> vsi = vs.iterator();
                if(vsi!=null)
                    while (vsi.hasNext())
                        destination = destination.with(vsi.next());
            }
        }
        return destination.persistent();
    }

    @Override
    public PVector<T> filter(Predicate<? super T> p) {
        requireNonNull(p);
        EVector<T> destination = PVector.<T>empty().ephemeral();
        for (T t : this)
            if (p.test(t))
                destination = destination.with(t);
        return destination.persistent();
    }

    private static <T> PVector<T> throwNoSuchElement(){
        throw new NoSuchElementException();
    }
    private PVector<T> withoutInTail() {
        return new PVector<>(size - 1,  root, tailWithout());
    }

    private PVector<T> withoutInTree() {
        Iterable<Integer> path = pathToNode(size, indexOfLastAfterWithout(size));
        Node newRoot = rootWithoutLast(path, root);
        Object[] newTail =arrayFor(size - 2,size,root, levels(size), tail);
        return ifelse(
                canShrink(newRoot),
                () -> shrink(newRoot, newTail),
                () -> new PVector<T>(size - 1, newRoot, newTail));
    }

    private PVector<T> shrink(Node newRoot, Object[] newTail) {
        return new PVector<>(
                size - 1,
                newRoot.child(0),
                newTail);
    }

    private boolean canShrink(Node newRoot) {
        return levels(size) > 1 && newRoot.childObject(1) == Node.emptyNode();
    }

    private Object[] tailWithout() {
        return Arrays.copyOf(tail, tail.length-1);
    }


    private static Node rootWithoutLast(Iterable<Integer> path, Node node) {
        int index = first(path);
        Iterable<Integer> remaining = next(path);
        return ifelse(has(remaining),
                () -> cloneNodeWithValue(node, rootWithoutLast(remaining, node.child(index)), index),
                () -> ifelse(index == 0,
                        Node::emptyNode,
                        () -> cloneNodeWithValue(node, null, index)));
    }


    private static int indexOfLastAfterWithout(int size){
        return size-2;
    }
    @Override
     public EVector<T> ephemeral() {
        return  EVector.of(this);
    }
}
