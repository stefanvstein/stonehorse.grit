package stonehorse.grit;

import stonehorse.grit.PersistentMap;
import stonehorse.grit.map.array.PArrayMap;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Map.Entry;

/**
 * Placeholder for serial maps, implemented as a simple sequence of key value pairs
 */
public class SerializedMap<K,V> implements Serializable{
	private static final long serialVersionUID = 1L;
	private transient PersistentMap<K,V> map;
	public SerializedMap(PersistentMap<K,V> map) {
		this.map = map;
	}
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeInt(map.size());
		for (Object o : map.entrySet()){
			Entry<?,?> e = (Entry<?,?>) o;
			out.writeObject(e.getKey());
			out.writeObject(e.getValue());
		}
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		int size = in.readInt();
		map = PArrayMap.empty();
		for (int i = 0; i < size; i++) {
			map = map.with((K)in.readObject(),(V)in.readObject());
		}
	}
	
	private Object readResolve() throws ObjectStreamException {
		return map;
	}
}
