package stonehorse.grit.map.hash;

import stonehorse.candy.Iterables;
import stonehorse.candy.SupplierLogic;
import stonehorse.candy.Trampoline;
import stonehorse.grit.PersistentVector;
import stonehorse.grit.map.APMapEntry;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Collections.emptyIterator;
import static java.util.Collections.emptyList;
import static stonehorse.candy.Choices.cond;
import static stonehorse.candy.Choices.ifelse;
import static stonehorse.candy.Iterables.range;
import static stonehorse.candy.Trampoline.*;
import static stonehorse.grit.Vectors.vector;
import static stonehorse.grit.tools.Util.not;


public class Iteration {

    public static <T, V> Iterable<Map.Entry<T, V>> iterable(Node node, boolean hasNull, V nullValue) {
        if (isEmpty(node, hasNull))
            return emptyList();
        return () ->
                withNull(hasNull, nullValue,lazy(iterable(
                        vector(node),
                        Collections.<Map.Entry<T, V>>emptyIterator())))
                        .iterator();
    }

    private static <T, V> Iterable withNull(boolean hasNull, V nullValue, Iterable<Map.Entry<T, V>> iterable) {
        return ifelse(hasNull,
                () -> Iterables.with(mapEntry(null, nullValue), iterable),
                () -> iterable);
    }


    private static boolean isEmpty(Node node, boolean hasNull) {
        return
                SupplierLogic.and(()->not(hasNull),

                        ()->SupplierLogic.or( ()->node == null,
                                ()->node.isEmpty()));
    }

    private static <T,V> Supplier<Trampoline.Continuation<Map.Entry<T,V>>> iterable(PersistentVector<Node> stack, Iterator<Map.Entry<T,V>> elements) {
        return () ->
                cond(
                    ()->elements.hasNext(),
                    ()-> returnAndContinueWithRemaining(stack, elements),
                    ()->stack.isEmpty(),
                    ()-> stop(),
                    ()-> continueWithNextNode(stack));
    }

    private static <T, V> Continuation<Map.Entry<T, V>> continueWithNextNode(PersistentVector<Node> stack) {
        Node node = stack.get();
        return ifelse(node==null,
                ()->recur(iterable(stack.without(), emptyIterator())),
                ()-> ifelse(
                        hasKeys(node),
                        ()->recur(extractNodeAndElements(node, stack.without())),
                        ()->recur(extractNodes((ArrayNode) node, stack.without()))));
    }

    private static <T, V> Continuation<Map.Entry<T, V>> returnAndContinueWithRemaining(PersistentVector<Node> stack, Iterator<Map.Entry<T, V>> elements) {
        Map.Entry<T, V> t = elements.next();
        return seq(iterable(stack, elements), t);
    }

    private static boolean hasKeys(Node node) {
        return not(node instanceof ArrayNode);
    }

    static int keyPos(int i){
        return 2*i;
    }

    static int valPos(int i){
        return 2*i+1;
    }

    private static <T,V> Supplier<Continuation<Map.Entry<T,V>>> extractNodeAndElements(Node node, PersistentVector<Node> stack) {
        return ()->{
            PersistentVector<Node> nodes= vector();
            PersistentVector<Map.Entry<T,V>> elements = vector();
            Object[] array = node.array();
            for(int i: range(node.size())){
                if(isNodeAt(array, i))
                  nodes=nodes.with(nodeAt(array,i));
                else
                    elements=elements.with(mapEntryOf(array, i));
            }
            return recur(iterable(stack.withAll(nodes), elements.iterator()));
        };
    }

    private static boolean isNodeAt(Object[] array, int i) {
        return array[keyPos(i)]==null;
    }

    private static Node nodeAt(Object[] array, int i) {
        return (Node) array[valPos(i)];
    }

    private static <T, V>  Map.Entry<T, V> mapEntryOf(Object[] array, int i) {
        return mapEntry( (T) array[keyPos(i)], (V) array[valPos(i)]);
    }

    private static <K,V> Map.Entry<K,V> mapEntry(K k, V v){
        return new APMapEntry<>(k,v);
    }

    private static <T,V> Supplier<Continuation<Map.Entry<T,V>>> extractNodes(Node node, PersistentVector<Node> stack) {
        return ()->{
            PersistentVector nodes = vector();
            Object[] array = node.array();
            for (Object anArray : array)
                if (anArray != null)
                    nodes = nodes.with(anArray);
            return recur( iterable( stack.withAll(nodes), emptyIterator()));
        };
    }
}
