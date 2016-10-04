package stonehorse.grit.vector;

import static stonehorse.candy.Choices.ifelse;
import static stonehorse.candy.Iterables.*;

public class VFuns {

    static final int BUCKET_BITS = 5;
    static final int BUCKET_SIZE = 1 << BUCKET_BITS;

    private static int shift(int levels) {
        return levels * BUCKET_BITS;
    }

    static int indexOfArrayAt(int offset) {
        return offset & (BUCKET_SIZE - 1);
    }

    public static int levels(int size){
        size=size-1;
        if(size<BUCKET_SIZE*BUCKET_SIZE)
            return 1;
        size=size-BUCKET_SIZE;
        size = size >>> BUCKET_BITS;
        int levels = 0;
        while(size!=0){
            levels++;
            size=size >>> BUCKET_BITS;
        }
        return levels;
    }

    static int indexInLevel(int index, int level) {
        return indexOfArrayAt(index >>> shift(level));
    }

    public static int tailStartOffset(int size) {
        if (size < BUCKET_SIZE)
            return 0;
        return ((size - 1) >>> BUCKET_BITS) << BUCKET_BITS;
    }

    public static boolean outOfBounds(int index, int size) {
        return index < 0 || index >= size;
    }

    public static boolean isInTail(int index, int size) {
        return index >= tailStartOffset(size);
    }

    public static int tailSize(int size) {
        return size - tailStartOffset(size);
    }

    public static boolean isFullRoot(int size, int levels) {
        return (size >>> BUCKET_BITS) > (1 << shift(levels));
    }

    public static Node newPath(int level, Node node) {
        if (level == 0)
            return node;
        return Node.node().altered(0, newPath(level - 1, node));
    }

    public static Node newLevelRoot(Node root, int levels, Node newNode) {
        Node newRoot = Node.node();
        newRoot.altered(0, root);
        newRoot.altered(1, newPath(levels, newNode));
        return newRoot;
    }


    private static boolean has(Object o){
        return null!=o;
    }
    private static Node nodeFor(Iterable<Integer> path, Node node) {
        int index = first(path);
        Iterable<Integer> remaining=next(path);
        return ifelse(has(remaining),
                ()->nodeFor(remaining, node.child(index)),
                ()->node);
    }

    public static Object[] arrayFor(int index, int size, Node root, Object[] tail) {
        if (outOfBounds(index, size))
            throw new IndexOutOfBoundsException();
        if (isInTail(index, size))
            return tail;
        Iterable<Integer> path = pathToValue(size, index);
        return nodeFor(path, root).array();
    }

    public static boolean isFullTail(int size) {
        return tailSize(size) >= BUCKET_SIZE;
    }

    public static boolean isLeaf(int lev) {
        return lev == 0;
    }

    public static Iterable<Integer> pathToValue(int size, int i){
        return map(v->indexInLevel(i,v), range(levels(size), -1, -1));

    }
    public static Iterable<Integer> pathToNode(int size, int i){
        return map(v->indexInLevel(i,v), range(levels(size), 0, -1));

    }
}
