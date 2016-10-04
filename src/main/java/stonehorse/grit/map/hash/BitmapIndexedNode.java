package stonehorse.grit.map.hash;



import static java.lang.System.arraycopy;
import static stonehorse.candy.Choices.cond;
import static stonehorse.candy.Choices.ifelse;
import static stonehorse.candy.SupplierLogic.or;
import static stonehorse.grit.map.hash.NodeOp.nextShift;
import static stonehorse.grit.map.hash.Result.result;
import static stonehorse.grit.tools.Util.equal;
import static stonehorse.grit.tools.Util.hash;


//TODO All resizing should be dependent of size, rather than array size.

public final class BitmapIndexedNode implements Node {
	int bitmap;
	Object[] array;
	final Object owner;

	public final int index(int bit) {
		return Integer.bitCount(bitmap & (bit - 1));
	}

	static final BitmapIndexedNode EMPTY = create(0,new Object[0]);


	private BitmapIndexedNode(Object owner, int bitmap, Object[] array) {
		this.bitmap = bitmap;
		this.array = array;
		this.owner = owner;
	}

	private BitmapIndexedNode( int bitmap, Object[] array) {
		this(null, bitmap, array);
	}

	static BitmapIndexedNode create(int bitmap, Object[] array){
		return new BitmapIndexedNode(bitmap, array);
	}

	static BitmapIndexedNode createEphemerally(Object owner, int bitmap, Object[] array){
		return new BitmapIndexedNode(owner,bitmap, array);
	}

	@Override
	public Result with(int shift, int hash, Object key, Object val, boolean isAdding) {
		int bit = NodeOp.bitpos(hash, shift);
		int idx = index(bit);
		return cond(
				() -> availableInBitmap(bit),
				() -> ifelse(
						isFull(population()),
						() -> fillIntoNewArrayNode(shift, hash, key, val),
						() -> fillIntoNewBitmapNode(key, val, bit, idx)),
				() -> isNodeAtIndex(idx),
				() -> withAtNode(shift, hash, key, val, isAdding, idx),
				() -> equal(key, keyAtIndex(idx)),
				() -> result(
						ifelse(val == valAtIndex(idx),
								() -> noChange(),
								() -> create(
										bitmap,
										NodeOp.cloneAndSet(array, valuePosition(idx), val))))
						.withResize(isAdding),
				() -> result(putInNewNode(shift, hash, key, val, idx))
						.withResize(true));

	}

	private Result withAtNode(int shift, int hash, Object key, Object val, boolean isAdding, int idx) {
		Node nodeAtIndex = nodeAtIndex(idx);
		Result res = nodeAtIndex.with(nextShift(shift), hash, key, val, isAdding);
		return result(
                ifelse(res.node == nodeAtIndex,
                        () -> noChange(),
                        () -> create(
                                bitmap,
                                NodeOp.cloneAndSet(array, valuePosition(idx), res.node))))
                .withResizeOf(res);
	}

	private BitmapIndexedNode putInNewNode(int shift, int hash, Object key, Object val, int idx) {
		return create(bitmap,
                NodeOp.cloneAndSet(
                        array,
                        keyPosition(idx),
                        null,
                        valuePosition(idx),
                        createNode(nextShift(shift), keyAtIndex(idx), valAtIndex(idx), hash, key,
                                val)));
	}

	private static int valuePosition(int idx) {
		return 2 * idx + 1;
	}

	private int population() {
		return Integer.bitCount(bitmap);
	}

	private boolean isNodeAtIndex(int idx) {
		return !hasValue(keyAtIndex(idx));
	}

	public Node nodeAtIndex(int idx) {
		if(isNodeAtIndex(idx))
		return (Node) valAtIndex(idx);
		throw new IllegalArgumentException("trying to fetch a node from index that do have a key and hence should be a concrete value");
	}
	public Object valAtIndex(int idx) {
		return array[valuePosition(idx)];
	}

	private boolean hasValue(Object o) {
		return null != o;
	}
	public Object keyAtIndex(int idx) {
		return array[keyPosition(idx)];
	}


	private Result fillIntoNewBitmapNode(Object key, Object val, int bit, int idx) {
		int population=population();
		Object[] newArray = new Object[2 * (population + 1)];
		copyArrayUntil(array, newArray, keyPosition(idx));

		newArray[keyPosition(idx)] = key;
		newArray[valuePosition(idx)] = val;

		arraycopy(array, keyPosition(idx), newArray, keyPosition(idx+1),
				keyPosition(population)-keyPosition(idx));
		return result(create( bitmap | bit, newArray)).withResize(true);
	}

	private static void copyArrayUntil(Object[] array, Object[] newArray, int idx) {
		arraycopy(array, 0, newArray, 0, idx);
	}


	private Result fillIntoNewArrayNode(int shift, int hash, Object key, Object val) {
		Node[] nodes = asNodesForArrayNodeAt(shift);
		nodes[NodeOp.mask(hash, shift)] = EMPTY.with(nextShift(shift), hash, key, val, false).node;
		return result(ArrayNode.create( population()+1, nodes)).withResize(true);
	}

	private Node[] asNodesForArrayNodeAt(int shift) {
		Node[] nodes = new Node[32];
		int index = 0;
		for (int i = 0; i < 32; i++)
			if (((bitmap >>> i) & 1) != 0) {
				Object value = array[valuePosition(index)];
				Object key = array[keyPosition(index)];
				nodes[i]= ifelse(
						isNodeAtIndex(index),
						()->(Node) value,
						()-> EMPTY.with(
							nextShift(shift),
							hash(key),
							key,
							value,
							true).node);
				index++;
			}
		return nodes;
	}



	private boolean isFull(int n) {
		return n >= 16;
	}

	private boolean availableInBitmap(int bit) {
		return (bitmap & bit) == 0;
	}
	@Override

	public Node without(int shift, int hash, Object key) {
		int bit = NodeOp.bitpos(hash, shift);
		Node n= ifelse(
				availableInBitmap(bit),
				this::noChange,
				() ->  remove(shift, hash, key, bit));
		if(n==null || n.isEmpty())
			return null;
			return n;
	}

	//Should return value if size ==1 and element = value
	private Node remove(int shift, int hash, Object key, int bit) {
		int idx = index(bit);
		Object keyAtIndex = keyAtIndex(idx);
		return cond(
                () -> isNodeKey(keyAtIndex),
                () -> removeInNode(shift, hash, key, bit, idx, nodeAtIndex(idx)),

                () -> equal(key, keyAtIndex),
                () -> removeAtIndex(bit, idx),

                () -> noChange());
	}

	private Node removeAtIndex(int bit, int idx) {
		// TODO: collapse
		Object[] remaining = NodeOp.removePair(array, idx);

		return ifelse(or(()->remaining==null, ()->remaining.length==0),
				()->EMPTY,
				()->create(bitmap ^ bit, remaining));
	}
//Node som arg ska förkortas
	private Node removeInNode(int shift, int hash, Object key, int bit, int idx, Node node) {
		Node without = node.without(nextShift(shift), hash, key);

		if (node == without)
            return noChange();
		if (isRemove(without)){
            return ifelse (bitmap == bit,
					()-> removeNode(),
					()-> create( bitmap ^ bit,
					NodeOp.removePair(array, idx)));
        }

       return create(
				bitmap,
				NodeOp.cloneAndSet( array, valuePosition(idx), without));
	}

	private BitmapIndexedNode noChange() {
		return this;
	}

	@Override
	public Object find(int shift, int hash, Object key, Object notFound) {
		int bit = NodeOp.bitpos(hash, shift);
		if (availableInBitmap(bit))
			return notFound;
		int idx = index(bit);
		Object keyOrNull = keyAtIndex(idx);
		
		if (isNodeKey(keyOrNull))
			return nodeAtIndex(idx).find(nextShift(shift), hash, key, notFound);
		if (equal(key, keyOrNull))
			return valAtIndex(idx);
		return notFound;
	}

	private BitmapIndexedNode ensureEditable(Object owner) {
		if (this.owner == owner)
			return this;
		int n = population();
		Object[] newArray = new Object[n >= 0 ? 2 * (n + 1) : 4]; // make
																	// room
																	// for
																	// next
																	// withEphemerally
		arraycopy(array, 0, newArray, 0, 2 * n);
		return createEphemerally(owner, bitmap, newArray);
	}

	private BitmapIndexedNode editAndSet(Object owner, int i, Object a) {
		BitmapIndexedNode editable = ensureEditable(owner);
		editable.array[i] = a;
		return editable;
	}

	private BitmapIndexedNode editAndSetNode(Object owner,  int j, Object b) {
		BitmapIndexedNode editable = ensureEditable(owner);
		editable.array[keyPosition(j)] = null;
		editable.array[valuePosition(j)] = b;
		return editable;
	}

	//TODO BETYDER Null att noden kan tas bort???

	private BitmapIndexedNode editAndRemovePair(Object owner, int bit, int i) {
		if (bitmap == bit)
			return null;
		BitmapIndexedNode editable = ensureEditable(owner);
		editable.bitmap ^= bit;
		thrinkAndPadBehind(i, editable.array);
		return editable;
	}

	private void thrinkAndPadBehind(int i, Object[] array) {
		arraycopy(array, 2 * (i + 1),
				  array, 2 * i,
				array.length - 2 * (i + 1));
		array[array.length - 2] = null;
		array[array.length - 1] = null;
	}

	@Override
	public Result withEphemerally(Object owner, int shift, int hash,
								  Object key, Object val, boolean isAdding) {
		int bit = NodeOp.bitpos(hash, shift);
		int idx = index(bit);
		return cond(
				() -> availableInBitmap(bit),
				() -> assocEphemerallyWhenAvailable(owner, shift, hash, key, val, isAdding, bit, idx),
				() -> isNodeAtIndex(idx),
				() -> withInNodeEphemerally(owner, shift, hash, key, val, isAdding, idx),
				() -> equal(key, keyAtIndex(idx)),
				() -> result(ifelse(val == valAtIndex(idx),
								() -> noChange(),
								() -> editAndSet(owner, valuePosition(idx), val)))
						.withResize(isAdding),
				() -> result(putInNewNodeEphemerally(owner, shift, hash, key, val, idx)).withResize(true));
	}

	private Result withInNodeEphemerally(Object owner, int shift, int hash, Object key, Object val, boolean isAdding, int idx) {
		Node node = nodeAtIndex(idx);
		Result res = node.withEphemerally(owner, nextShift(shift), hash,
                key, val, isAdding);
		return result(
                ifelse(	res.node == node,
                        () -> noChange(),
                        () -> editAndSet(owner, valuePosition(idx), res.node)))
                .withResizeOf(res);
	}

	private BitmapIndexedNode putInNewNodeEphemerally(Object owner, int shift, int hash, Object key, Object val, int idx) {
		return editAndSetNode(
                owner,
                idx,
                createNodeEphemerally(owner, nextShift(shift), keyAtIndex(idx), valAtIndex(idx), hash,
                        key, val));
	}


	private Result assocEphemerallyWhenAvailable(Object owner, int shift, int hash, Object key, Object val, boolean addedLeaf, int bit, int idx) {
		int population = population();
		return result(cond(
				() -> isFull(population),
				() -> intoArrayNode(owner, shift, hash, key, val, addedLeaf, population),
				() -> isRoomInArray(population),
				() -> intoCurrentArray(owner, key, val, bit, idx, population),
				() -> intoBlankNode(owner, key, val, bit, idx, population)))
				.withResize(true);
	}

	private Node intoBlankNode(Object owner, Object key, Object val, int bit, int idx, int population) {
		BitmapIndexedNode editable = ensureEditable(owner);
		editable.array = ephemerallyIntoNewArray(array, key, val, idx, population);
		editable.bitmap |= bit;
		return editable;
	}

	private Node intoCurrentArray(Object owner, Object key, Object val, int bit, int idx, int population) {
		BitmapIndexedNode editable = ensureEditable(owner);
		putIntoExistingArray(editable.array, key, val, idx, population);
		editable.bitmap |= bit;
		return editable;
	}

	private Node intoArrayNode(Object owner, int shift, int hash, Object key, Object val, boolean addedLeaf, int population) {
		Node[] nodes = asNodesForArrayNodeAtEphemerally(owner, shift);
		nodes[NodeOp.mask(hash, shift)] = EMPTY.withEphemerally(owner, nextShift(shift), hash, key, val,
                addedLeaf).node;
		return ArrayNode.createEphemerally(owner, population + 1, nodes);
	}

	private static Object[] ephemerallyIntoNewArray(Object[] oldArray, Object key, Object val, int idx, int population) {
		Object[] newArray = new Object[2 * (population + 4)];
		copyArrayUntil(oldArray, newArray, keyPosition(idx));
		newArray[keyPosition(idx)] = key;

		newArray[valuePosition(idx)] = val;
		arraycopy(oldArray, keyPosition(idx), newArray, 2 * (idx + 1),
                2 * (population - idx));
		return newArray;
	}

	private void putIntoExistingArray(Object[] array, Object key, Object val, int idx, int population) {
		arraycopy(array, keyPosition(idx),
                array, keyPosition(idx + 1),
                keyPosition(population) - keyPosition(idx));
		array[keyPosition(idx)] = key;
		array[valuePosition(idx)] = val;
	}

	private boolean isRoomInArray(int population) {
		return population * 2 < array.length;
	}

	private Node[] asNodesForArrayNodeAtEphemerally(Object owner, int shift) {
		Node[] nodes = new Node[32];
		int index = 0;
		for (int i = 0; i < 32; i++)
			if (((bitmap >>> i) & 1) != 0) {
				Object value = array[valuePosition(index)];
				Object key = array[keyPosition(index)];
				nodes[i]= ifelse(
						isNodeAtIndex(index),
						()->(Node) value,
						()-> EMPTY.withEphemerally(owner,
								nextShift(shift),
								hash(key),
								key,
								value,
								true).node);
				index++;
			}
		return nodes;
	}
	private static int keyPosition(int idx) {
		return 2 * idx;
	}

	static boolean  isRemove(Node node){
		return node ==null;
	}

	static Node removeNode(){
		return null;
	}

	@Override
	public Result withoutEphemerally(Object owner, int shift, int hash,
								   Object key, boolean isRemoving) {
		int bit = NodeOp.bitpos(hash, shift);
		if (availableInBitmap(bit))
			return result(noChange()).withResize(isRemoving);
		return removeEphemerally(owner, shift, hash, key, isRemoving, bit);
	}

	private Result removeEphemerally(Object owner, int shift, int hash, Object key, boolean isRemoved, int bit) {
		int idx = index(bit);
		Object keyAtIndex = keyAtIndex(idx);

		return cond(()->isNodeKey(keyAtIndex),
				()-> removeNodeEphemerally(owner, shift, hash, key, isRemoved, bit, idx),
				()-> equal(key, keyAtIndex),
				()-> removeAtIndexEphemerally(owner, bit, idx),
				()-> result(noChange()).withResize(isRemoved));
	}

	private Result removeAtIndexEphemerally(Object owner, int bit, int idx) {
		// TODO: collapse
		//System.out.println("Collapse!!");
		return result(editAndRemovePair(owner, bit, idx)).withResize(true);
	}

	private Result removeNodeEphemerally(Object owner, int shift, int hash, Object key, boolean isRemoved, int bit, int idx) {
		Node node = (Node) valAtIndex(idx);
		Result res = node.withoutEphemerally(owner, nextShift(shift), hash, key, isRemoved);
		return cond(
				() -> res.node == node,
				() -> result(this).withResizeOf(res),
				() -> isRemove(res.node),
				() -> result(editAndRemovePair(owner, bit, idx)).withResize(isRemoved),
				() -> result(editAndSet(owner, valuePosition(idx), res.node)).withResizeOf(res));
	}

	private boolean isNodeKey(Object key) {
		return key == null;
	}

	@Override
	public boolean isEmpty() {
		return bitmap==0;

	}

//	@Override
//	public Map.Entry collapse() {
//		if(array.length==2 && array[0]!=null)
//		return new APMapEntry(array[0], array[1]);
//		return null;
//	}

	static Node createNodeEphemerally(Object owner, int shift,
									  Object key1, Object val1,
									  int key2hash,
									  Object key2, Object val2) {
		int key1hash = hash(key1);
		return ifelse (key1hash == key2hash,
				()-> HashCollisionNode.create(
						key1hash,
						2,
						key1, val1, key2, val2),

				()->BitmapIndexedNode.EMPTY
						.withEphemerally(owner, shift, key1hash, key1, val1, false).node
						.withEphemerally(owner, shift, key2hash, key2, val2, true).node);
	}

	static Node createNode(int shift, Object key1, Object val1,
						   int key2hash, Object key2, Object val2) {
		int key1hash = hash(key1);
		return ifelse (
				key1hash == key2hash,
				()->HashCollisionNode.create(
					key1hash,
					2,
						key1, val1, key2, val2),
				()->BitmapIndexedNode.EMPTY
						.with(shift, key1hash, key1, val1,	false).node
						.with(shift, key2hash, key2, val2, true).node);
	}

	@Override
	public int size(){
	return Integer.bitCount(bitmap);
}

	@Override public Object[] array() {
		return array;
	}

	@Override
	public String toString() {
		return toString(0);
	}

	@Override public String toString(int ident) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ident; i++)
			sb.append(" ");

		sb.append("Bitmap[" + String.format("%32s",
				Integer.toBinaryString(bitmap)).replace(' ', '0') +
				"]("+ Integer.bitCount(bitmap)+":"+(array==null?"null":array.length/2)+")");
		if (array != null)
			for (int idx =0;idx < Integer.bitCount(bitmap);idx+=1) {
				sb.append(System.lineSeparator());
				if (isNodeAtIndex(idx)) {
					sb.append(nodeAtIndex(idx).toString(ident + 1));
				} else{

					for (int i = -1; i < ident; i++)
						sb.append(" ");
					sb.append(keyAtIndex(idx).toString() +"="+valAtIndex(idx) );
				}
			}
		return sb.toString();
	}

}