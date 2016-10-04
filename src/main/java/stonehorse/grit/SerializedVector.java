package stonehorse.grit;

import stonehorse.grit.PersistentVector;
import stonehorse.grit.Vectors;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
/**
 * Placeholder for serial vectors, implemented as a simple sequence of values
 */
public class SerializedVector implements Serializable {
	private static final long serialVersionUID = 1L;
	private transient PersistentVector vector;
	public SerializedVector(PersistentVector vector) {
		this.vector = vector;
	}
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeInt(vector.size());
		for (Object o : vector)
			out.writeObject(o);
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		int size = in.readInt();
		vector = Vectors.vector();
		for (int i = 0; i < size; i++) {
			vector = vector.with(in.readObject());
		}
	}
	
	private Object readResolve() throws ObjectStreamException {
		return vector;
	}
}
