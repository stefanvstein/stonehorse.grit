package stonehorse.grit;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Placeholder for serial queues, implemented as a simple sequence of values
 */
public class SerializedQueue implements Serializable {
	private static final long serialVersionUID = 1L;
	private transient PersistentQueue queue;
	public SerializedQueue(PersistentQueue queue) {
		this.queue = queue;
	}
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeInt(queue.size());
		for (Object o : queue)
			out.writeObject(o);
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		int size = in.readInt();
		PersistentVector vector = Vectors.vector();
		for (int i = 0; i < size; i++) {
			vector = vector.with(in.readObject());
		}
		queue=Vectors.queued(vector);
	}
	
	private Object readResolve() throws ObjectStreamException {
		return queue;
	}
}
