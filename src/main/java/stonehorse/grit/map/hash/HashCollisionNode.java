package stonehorse.grit.map.hash;


import stonehorse.grit.tools.Util;

import static stonehorse.candy.Choices.cond;
import static stonehorse.candy.Choices.ifelse;
import static stonehorse.grit.map.hash.NodeOp.cloneAndSet;
import static stonehorse.grit.map.hash.Result.result;
import static stonehorse.grit.tools.Util.not;

/**
 * A node where all have same hash
 */

//TODO All resizing should be dependent of size, rather than array size.

final public class HashCollisionNode implements Node {

    final int hash;
    int count;
    Object[] array;
    final Object owner;

    private HashCollisionNode(Object owner, int hash, int count,
            Object... array) {
        this.owner = owner;
        this.hash = hash;
        this.count = count;
        this.array = array;
    }


    static HashCollisionNode createEphemerally(Object owner, int hash, int count,
                                        Object... array){
        return new HashCollisionNode(owner, hash, count, array);
    }
    private HashCollisionNode(int hash, int count, Object... array) {
      this(null,hash, count, array);
    }

    static HashCollisionNode create(int hash, int count, Object... array){
        return new HashCollisionNode(hash, count, array);
    }

    @Override
    public Result with(int shift, int hash, Object key, Object val, boolean isAdding) {
        return ifelse (hash == this.hash,
                ()-> withCollision(hash, key, val, isAdding),
        ()->nestInBitmap(shift, hash, key, val, isAdding));

    }

    private Result nestInBitmap(int shift, int hash, Object key, Object val, boolean isAdding) {
        return BitmapIndexedNode.create(
                NodeOp.bitpos(this.hash, shift),
                new Object[] { null, this })
                .with(shift, hash, key, val, isAdding);
    }

    private Result withCollision(int hash, Object key, Object val, boolean isAdding) {
        int idx = findIndex(key);
        return ifelse(
                isFound(idx),
                () -> result(
                        ifelse(array[idx + 1] == val,
                                () -> this,
                                () -> create(hash, count, cloneAndSet(array, idx + 1, val))))
                        .withResize(isAdding),
                () -> result(create(hash, count + 1, copyWithKeyValLast(key, val)))
                        .withResize(true));
        // return createEphemerally(edit, hash, count + 1, newArray);
    }

    private Object[] copyWithKeyValLast(Object key, Object val) {
        Object[] newArray = new Object[array.length + 2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = key;
        newArray[array.length + 1] = val;
        return newArray;
    }


    private boolean isFound(int idx) {
        return idx != -1;
    }

    @Override
    public Node without(int shift, int hash, Object key) {
        int idx = findIndex(key);
        return ifelse(not(isFound(idx)),
                () -> this,
                () -> ifelse(
                        count == 1,
                        () -> null,
                        () -> create(hash, count - 1,
                                NodeOp.removePair(array, idx / 2))));

    }


    @Override
    public Object find(int shift, int hash, Object key, Object notFound) {
        int idx = findIndex(key);
        return cond(
                ()->(idx < 0),
                ()->notFound,
                ()->Util.equal(key, array[idx]),
                ()->array[idx + 1],
                ()->notFound);
    }

 //   public Sequence nodeSeq() {
 //       return NodeSeq.createNodeEphemerally(array);
 //   }

    public int findIndex(Object key) {
        for (int i = 0; i < 2 * count; i += 2) {
            if (Util.equal(key, array[i]))
                return i;
        }
        return -1;
    }

    private HashCollisionNode ensureEditable(Object owner) {
        return ifelse (this.owner == owner,
                ()->this,
                ()->createEphemerally(owner, hash, count, makeRoom()));
    }

    private Object[] makeRoom() {
        Object[] newArray = new Object[2 * (count + 1)];
        System.arraycopy(array, 0, newArray, 0, 2 * count);
        return newArray;
    }

    private HashCollisionNode ensureEditable(Object owner,
            int count, Object[] array) {
        if (this.owner == owner) {
            this.array = array;
            this.count = count;
            return this;
        }
        return createEphemerally(owner, hash, count, array);
    }

    private HashCollisionNode editAndSet(Object owner,
            int i, Object a) {
        HashCollisionNode editable = ensureEditable(owner);
        editable.array[i] = a;
        return editable;
    }

    private HashCollisionNode editAndSet(Object owner,
            int i, Object a, int j, Object b) {
        HashCollisionNode editable = ensureEditable(owner);
        editable.array[i] = a;
        editable.array[j] = b;
        return editable;
    }
    @Override
    public Result withEphemerally(Object owner, int shift, int hash,
                                  Object key, Object val, boolean isAdding) {
        return ifelse(hash == this.hash,
                ()->withCollisionEphemerally(owner, key, val, isAdding),
                ()-> nestInBitmapEphemerally(owner, shift, hash, key, val, isAdding));
    }

    private Result nestInBitmapEphemerally(Object owner, int shift, int hash, Object key, Object val, boolean isAdding) {
        return BitmapIndexedNode.createEphemerally(
                  owner,
                  NodeOp.bitpos(this.hash, shift),
                  new Object[]{null, this, null, null})
                .withEphemerally(owner, shift, hash, key, val, isAdding);
    }

    private Result withCollisionEphemerally(Object owner, Object key, Object val, boolean isAdding) {
        int idx = findIndex(key);
        return ifelse(
                isFound(idx),
                () -> result(
                        ifelse(array[idx + 1] == val,
                                () -> this,
                                () -> editAndSet(owner, idx + 1, val)))
                        .withResize(isAdding),
                () -> result(
                        ifelse(isAdditionalRoom(),
                                () -> withKeyValLast(owner, key, val),
                                () -> ensureEditable(owner, count + 1, copyWithKeyValLast(key, val))))
                        .withResize(true));
    }

    private HashCollisionNode withKeyValLast(Object owner, Object key, Object val) {
        HashCollisionNode editable = editAndSet(owner, 2 * count,
                key, 2 * count + 1, val);
        editable.count++;
        return editable;
    }

    private boolean isAdditionalRoom() {
        return array.length > 2 * count;
    }

    @Override
    public Result withoutEphemerally(Object owner, int shift, int hash,
                                     Object key, boolean isRemoving) {
        int idx = findIndex(key);
        return cond(
                () -> not(isFound(idx)),
                () -> result(this).withResize(isRemoving),
                () -> count == 1,
                () -> result(null).withResize(true),
                () -> result(thrinkEphemerably(owner, idx)).withResize(true));
    }

    private HashCollisionNode thrinkEphemerably(Object owner, int idx) {
        HashCollisionNode editable = ensureEditable(owner);
        if(2*(count-1)<editable.array.length/2)
            return thrinkInHalf(idx, editable);

        editable.array[idx] = editable.array[2 * count - 2];
        editable.array[idx + 1] = editable.array[2 * count - 1];
        editable.array[2 * count - 2] = null;
        editable.array[2 * count - 1] = null;
        editable.count--;
        return editable;
    }

    private HashCollisionNode thrinkInHalf(int idx, HashCollisionNode node) {
        Object newArray[] = new Object[2*(count-1)];
        System.arraycopy(node.array, 0, newArray, 0, idx);
        System.arraycopy(node.array, idx+2, newArray, idx, newArray.length-idx );
        node.array=newArray;
        node.count--;
        return node;

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
//        if(array.length==2 && array[0]!=null)
//            return new APMapEntry(array[0], array[1]);
//        return null;
//    }

    @Override
	public Object[] array() {
		return array;
	}

		@Override public String toString(int ident) {
			StringBuilder sb = new StringBuilder();
			for(int i =0;i<ident;i++)
				sb.append(" ");
			sb.append("Collision("+ count+":"+(array==null?"null":array.length/2)+")"+System.lineSeparator());
            for(int n=0; n<array.length;n=n+2) {
                for (int i = 0; i < ident+1; i++)
                    sb.append(" ");
                sb.append(array[n]+"="+array[n+1]+System.lineSeparator());
            }

            return sb.toString();
	}


}