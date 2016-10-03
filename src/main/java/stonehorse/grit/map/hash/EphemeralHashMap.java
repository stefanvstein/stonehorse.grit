package stonehorse.grit.map.hash;




import stonehorse.grit.Associative;
import stonehorse.grit.map.EphemeralMap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static stonehorse.candy.Atomic.atomic;
import static stonehorse.candy.Choices.cond;
import static stonehorse.candy.Choices.ifelse;
import static stonehorse.grit.tools.Util.changes;
import static stonehorse.grit.tools.Util.hash;
import static stonehorse.grit.tools.Util.not;

public final class EphemeralHashMap<T, V> implements EphemeralMap<T, V> {
    final AtomicReference<Object> owner= atomic(null);
    Node root;
    int count;
    boolean hasNull;
    V nullValue;
    final private static Object NOT_FOUND = new Object();


    public static <T,V> EphemeralHashMap<T,V> of(PHashMap<? extends T, ? extends V> p){
        EphemeralHashMap e= new EphemeralHashMap( p.root,
                p.count, p.hasNull, p.nullValue);
        e.owner.set(new Object());
        return e;
    }

    private EphemeralHashMap(Node root, int count,
                             boolean hasNull, V nullValue) {
        this.root = root;
        this.count = count;
        this.hasNull = hasNull;
        this.nullValue = nullValue;
    }



    public EphemeralHashMap<T, V> with(T key, V val) {
        ensureEditable();
        if (key == null)
            return withNull(val);

        Result result = assoc(rootOrEmpty(), key, val);
        if(isReplaced(result))
            root = result.node;
        else if(isResized(result)){
            root=result.node;
            count++;
        }
        return this;
    }

    private Node rootOrEmpty() {
        return root == null ? BitmapIndexedNode.EMPTY : root;
    }

    boolean isReplaced(Result result){
        return result.node!=root && !result.isResized;

    }
    @Override
    public Associative<T, V> whenMissing(T key, Supplier<V> valueSupplier) {
        requireNonNull(valueSupplier);
        return ifelse(!has(key),
                () -> with(key, valueSupplier.get()),
                () -> this);
    }
    boolean isResized(Result result){
        return result.isResized;
    }

    Result assoc(Node node, T key, V val){
        return node.withEphemerally(owner.get(), 0, hash(key), key, val, false);
    }

    private EphemeralHashMap<T, V> withNull(V val) {
        if (this.nullValue != val)
            this.nullValue = val;
        if (!hasNull) {
            this.count++;
            this.hasNull = true;
        }
        return this;
    }

    public EphemeralHashMap<T, V> without(Object key) {
        ensureEditable();
        return cond(
                ()-> key == null,
                ()-> withoutNull(),
                ()-> count==0,
                ()-> this,
                ()-> withoutFromTree(key));
    }


    @Override
    public EphemeralHashMap<T, V> withoutWhen(Object key, Predicate<V> predicate) {
        requireNonNull(predicate);
        return cond(()->!has(key),
                ()->this,
                ()->predicate.test(get(key)),
                ()->without(key),
                ()->without(key));
    }

    private EphemeralHashMap<T, V> withoutFromTree(Object key) {
        Result r = root.withoutEphemerally(owner.get(), 0, hash(key), key, false);
        if (isResized(r) || isReplaced(r))
            this.root = r.node;
        this.count--;
        return this;
    }


    private EphemeralHashMap<T, V> withoutNull() {
        if (not(hasNull))
            return this;
        hasNull = false;
        nullValue = null;
        this.count--;
        return this;
    }

    public PHashMap<T, V> persistent() {
        if(null==owner.getAndUpdate(v->null))
            throw new IllegalStateException(
                    "No longer Ephemeral");
        //Root är nödvändigtvis inte rensad
        return  PHashMap.create(count, root, hasNull, nullValue);
    }

    @Override
   public
    V get(Object key, V notFound) {
        return cond(
                ()->key == null,
                ()->ifelse(hasNull,
                    ()->nullValue,
                    ()-> notFound),
                ()->root == null,
                ()-> notFound,
                ()->(V) root.find(0, hash(key), key, notFound));
    }

    public int size() {
        return count;
    }

    private void ensureEditable() {
        if(owner.get()==null)
            throw new IllegalStateException(
                    "No longer Ephemeral");

    }




    @Override public
    boolean has(Object key) {
        return cond(
                ()->key == null,
                ()->hasNull,
                ()->root == null,
                ()->false,
                ()->changes(
                        NOT_FOUND,
                        o->root.find(0, hash(key), key, o))
                );
    }

    @Override public
    boolean isEmpty() {
        return count == 0;
    }

	@Override
    public EphemeralHashMap<T, V> withoutAll(Iterable<?> keys) {
        EphemeralHashMap<T, V> val = this;
        for (Object key : keys)
            val = val.without(key);
        return val;
    }

	@Override
	public EphemeralHashMap<T, V> withAll(Map<? extends T, ? extends V> map) {
        ensureEditable();
		if(map== null)
			return this;
		EphemeralHashMap<T, V> v = this;
		for(Entry<? extends T, ? extends V> e:map.entrySet())
			v=v.with(e.getKey(), e.getValue());
		return v;
	}

    public String dump(){
        return "count: "+size()+
                System.lineSeparator()+
                (hasNull?"null:"+nullValue+System.lineSeparator():"")+
                "root:" + Objects.toString(root);
    }
}