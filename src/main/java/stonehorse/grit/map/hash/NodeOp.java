package stonehorse.grit.map.hash;


public class NodeOp {


    static Node[] cloneAndSet(Node[] array, int i, Node a) {
        Node[] clone = array.clone();
        clone[i] = a;
        return clone;
    }

    static Object[] cloneAndSet(Object[] array, int i, Object a, int j,
                                Object b) {
        Object[] clone = array.clone();
        clone[i] = a;
        clone[j] = b;
        return clone;
    }

    static Object[] cloneAndSet(Object[] array, int i, Object a) {
        Object[] clone = array.clone();
        clone[i] = a;
        return clone;
    }

    static Object[] removePair(Object[] array, int i) {
        Object[] newArray = new Object[array.length - 2];
        System.arraycopy(array, 0, newArray, 0, 2 * i);
        System.arraycopy(array, 2 * (i + 1), newArray, 2 * i, newArray.length
                - 2 * i);
        return newArray;
    }

    public static int mask(int hash, int shift) {
        return (hash >>> shift) & 0x01f;
    }

    public static int bitpos(int hash, int shift) {
        return 1 << mask(hash, shift);
    }

    public static int nextShift(int shift) {
        return shift + 5;
    }
}
