package stonehorse.grit.vector;

import stonehorse.candy.Choices;
import stonehorse.candy.Iterables;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static stonehorse.candy.Choices.mapOr;
import static stonehorse.candy.Iterables.first;
import static stonehorse.candy.Iterables.flatMap;

public class Node implements Serializable {

    private static final long serialVersionUID = -7774724501115118700L;
    private final Object[] array;

    public Node child(int index) {
        return (Node) array[index];
    }

    public Object childObject(int index) {
        return array[index];
    }

    private static final Node emptyNode = new Node();

    private Node(Object[] array) {
        this.array = array;
    }

    private Node() {
        this.array = null;
    }

    public Object[] array(){
        return array;
    }

    public static Node node() {
        return new Node(new Object[VFuns.BUCKET_SIZE]);
    }

    public Node altered(int index, Object value) {
        if (this==emptyNode) {
            Node node = node();
            node.array[index] = value;
            return node;
        } else {
            array[index] = value;
            return this;
        }
    }

    public Node with(int index, Object value) {

            Node node = Choices.ifelse(this==emptyNode(),
                    Node::node,
                    ()->Node.nodeOf(this));

            node.array[index] = value;
            return node;

    }

    public static Node emptyNode() {
        return emptyNode;
    }

    public static Node nodeOf(Node node) {
        if (node.array == null)
            return node();
        return new Node(node.array.clone());
    }

    public static Node nodeOfArray(Object[] array) {
        return new Node(array);
    }


   static  <T> Iterable<T> interleave(Supplier<? extends T> t, Iterable<? extends T> d) {
        return Iterables.with(first(d), flatMap(v -> asList(t.get(), v), Iterables.next(d)));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Node)
        return Objects.deepEquals(this.array, ((Node)obj).array);
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(array);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("node[" + Integer.toHexString(System.identityHashCode(this)) + "]{");
        for (String a : interleave(() -> ", ", Iterables.map(Objects::toString, mapOr(array, Arrays::asList, () -> null))))
            sb.append(a);
        sb.append("}");
        return sb.toString();

    }
}