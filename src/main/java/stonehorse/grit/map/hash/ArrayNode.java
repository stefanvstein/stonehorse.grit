package stonehorse.grit.map.hash;


import static stonehorse.candy.Choices.cond;
import static stonehorse.candy.Choices.ifelse;
import static stonehorse.candy.Maybe.maybe;
import static stonehorse.grit.map.hash.NodeOp.cloneAndSet;
import static stonehorse.grit.map.hash.NodeOp.mask;
import static stonehorse.grit.map.hash.NodeOp.nextShift;

//NodeOp without values

//TODO All resizing should be dependent of size, rather than array size.

public final class ArrayNode implements Node {
    private static final long serialVersionUID = 1L;
    int count;
    final Node[] array;
    final Object owner;

    private ArrayNode(Object owner, int count, Node[] array) {
        this.array = array;
        this.owner = owner;
        this.count = count;
    }

    static ArrayNode createEphemerally(Object owner, int count, Node[] array){
        return new ArrayNode(owner, count, array);
    }

    private ArrayNode( int count, Node[] array) {
        this(null, count, array);
    }

     static ArrayNode create(int count, Node[] array){
        return new ArrayNode(count, array);
    }
boolean isNothingAt(int index){
    return null == array[index];
}
    @Override
    public Result with(int shift, int hash, Object key, Object val, boolean isAdding) {
        int idx = mask(hash, shift);

        return ifelse (isNothingAt(idx),
                ()-> withNewNode(shift, hash, key, val, isAdding, idx),
                ()-> withInNode(shift, hash, key, val, isAdding, array[idx], idx));
    }

    private Result withNewNode(int shift, int hash, Object key, Object val, boolean isAdding, int idx) {
        return Result.result( ArrayNode.create(count + 1, cloneAndSet(array, idx,
            BitmapIndexedNode.EMPTY.with(nextShift(shift), hash, key,
                    val, isAdding).node))).withResize(true);
    }

    private Result withInNode(int shift, int hash, Object key, Object val, boolean isAdding, Node node, int idx) {
        Result r = node.with(nextShift(shift), hash, key, val, isAdding);
        return Result.result(ifelse (r.node == node,
                ()->this,
                ()-> ArrayNode.create( count, cloneAndSet(array, idx, r.node))))
        .withResizeOf(r);
    }


    @Override
    public Node without(int shift, int hash, Object key) {
        int idx = mask(hash, shift);
        return ifelse (isNothingAt(idx),
                ()->this,
                ()->withoutInNode(shift, hash, key, array[idx], idx));
    }

    private Node withoutInNode(int shift, int hash, Object key, Node node, int idx) {
        return maybe(node.without(nextShift(shift), hash, key))
                .map(newNode ->
                        ifelse(newNode == node,
                                this::nothingFound,
                                () -> create(count, cloneAndSet(array, idx, newNode))))
                .orElseGet(() -> withoutNode(idx));
    }



    private Node withoutNode(int idx) {
        return ifelse (count <= 8, // shrink
        ()->pack(null, idx),
        ()->create( count - 1,  cloneAndSet(array, idx, null)));
    }

    Node nothingFound(){
        return this;
    }

    @Override
    public Object find(int shift, int hash, Object key, Object notFound) {
        int idx = mask(hash, shift);
        return ifelse(
                isNothingAt(idx),
                ()->notFound,
                ()->array[idx].find(nextShift(shift), hash, key, notFound));
    }


    private ArrayNode ensureEditable(Object owner) {
        return ifelse (this.owner == owner,
                ()->this,
                ()->new ArrayNode(owner, count, this.array.clone()));
    }

    private ArrayNode editAndSet(Object owner, int i,
            Node n) {
        ArrayNode editable = ensureEditable(owner);
        editable.array[i] = n;
        return editable;
    }

    private Node pack(Object owner, int idx) {

        Object[] newArray = new Object[2 * (count - 1)];
        int j = 1;
        int bitmap = 0;
        for (int i = 0; i < idx; i++)
            if (array[i] != null) {
                newArray[j] = array[i];
                bitmap |= 1 << i;
                j += 2;
            }
        for (int i = idx + 1; i < array.length; i++)
            if (array[i] != null) {
                newArray[j] = array[i];
                bitmap |= 1 << i;
                j += 2;
            }
        return BitmapIndexedNode.createEphemerally(owner, bitmap, newArray);
    }

    @Override
    public Result withEphemerally(Object owner, int shift, int hash,
                                  Object key, Object val, boolean isAdding) {
        int idx = mask(hash, shift);
        return ifelse (isNothingAt(idx),
                ()->withNewNodeEphemerally(owner, shift, hash, key, val, idx),
                ()->withInNodeEphemerally(owner, shift, hash, key, val, isAdding, array[idx], idx));
    }

    private Result withInNodeEphemerally(Object owner, int shift, int hash, Object key, Object val, boolean isAdding, Node node, int idx) {
        Result r = node.withEphemerally(owner, nextShift(shift), hash, key, val, isAdding);
        return Result.result(ifelse (r.node == node,
                ()->this,
                ()->editAndSet(owner, idx, r.node)))
                .withResizeOf(r);
    }

    private Result withNewNodeEphemerally(Object owner, int shift, int hash, Object key, Object val, int idx) {
        ArrayNode editable = editAndSet(owner, idx,
                BitmapIndexedNode.EMPTY.withEphemerally(owner, nextShift(shift), hash,
                        key, val, false).node);
        editable.count++;
        return Result.result(editable).withResize(true);
    }

    @Override
    public Result withoutEphemerally(Object owner, int shift, int hash,
                                   Object key, boolean isRemoving) {
        int idx = mask(hash, shift);

        return ifelse (isNothingAt(idx),
                ()->Result.result(this).withResize(isRemoving),
                ()->withoutInNodeEphemerally(owner, shift, hash, key, isRemoving, array[idx], idx));
    }

    private Result withoutInNodeEphemerally(Object owner, int shift, int hash, Object key, boolean isRemoving, Node node, int idx) {

        Result r = node.withoutEphemerally(owner, nextShift(shift), hash, key, isRemoving);
        return cond(
                () -> r.node == node,
                () -> Result.result(this).withResizeOf(r),
                () -> r.node == null,
                () -> withoutNodeEphemerally(owner, idx, r),
                () -> Result.result(editAndSet(owner, idx, r.node)).withResizeOf(r));
    }

    private Result withoutNodeEphemerally(Object owner, int idx, Result r) {
        return ifelse(count <= 8, // shrink
                () -> Result.result(pack(owner, idx)).withResizeOf(r),
                () -> {
                    ArrayNode editable = editAndSet(owner, idx, r.node);
                    editable.count--;
                    return Result.result(editable).withResizeOf(r);
                });
    }


    @Override
    public boolean isEmpty() {
        return count==0;
    }

    @Override
    public int size() {
        return count;
    }

//    @Override
//    public Map.Entry collapse() {
//        return when(count==1, ()-> new APMapEntry (array[0], array[1]));
//    }

    @Override public Object[] array() {
		return array;
	}
	
	@Override public String toString() {
		return toString(0);
	}

	@Override public String toString(int ident) {
		StringBuilder sb = new StringBuilder();
		for(int i =0;i<ident;i++)
			sb.append(" ");
		sb.append(count +" ("+array.length+") ArrayNode");
		for(Node node : array){
			sb.append("\n");
			if(node==null){
				for(int i=-1;i<ident;i++)
					sb.append(" ");
				sb.append("null");
			}else sb.append(node.toString(ident+1));
		}

		return sb.toString();
	}


}