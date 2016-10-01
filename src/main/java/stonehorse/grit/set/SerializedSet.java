package stonehorse.grit.set;

import stonehorse.grit.PersistentSet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

public class SerializedSet<V> implements Serializable {
	private static final long serialVersionUID = 1L;
	private transient PersistentSet<V> set;
	public SerializedSet(PersistentSet<V> set) {
		this.set = set;
	}
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeInt(set.size());
		for (V v : set)
			out.writeObject(v);
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		int size = in.readInt();
		set = PSet.empty();
		for (int i = 0; i < size; i++) {
			set = set.with((V)in.readObject());
		}
	}
	
	private Object readResolve() throws ObjectStreamException {
		return set;
	}
}
