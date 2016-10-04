package stonehorse.grit.tools;

import stonehorse.grit.Randomly;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

import static stonehorse.candy.Choices.when;

public class Util {

	public static <T>  boolean changes(T t,UnaryOperator<T> s){
		return s.apply(t)!=t;
	}
	
	public static boolean not(boolean b){
		return !b;
	}
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
//	public static String bytesToHex(byte[] bytes) {
//		char[] hexChars = new char[bytes.length * 2];
//		for ( int j = 0; j < bytes.length; j++ ) {
//			int v = bytes[j] & 0xFF;
//			hexChars[j * 2] = hexArray[v >>> 4];
//			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
//		}
//		return new String(hexChars);
//	}


	public static <T> T deepCopy(T t) throws IOException, ClassNotFoundException{
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(bos);
	    oos.writeObject(t);
	    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
	    ObjectInputStream ois = new ObjectInputStream(bis);
	    return (T) ois.readObject();
	    
	}
	
	public static int mapHash(Map<?, ?> m) {
		if (m == null)
			return 0;
		int hash = 0;
		for (Entry<?, ?> e : m.entrySet())
			hash += (e.getKey() == null ? 0 : e.getKey().hashCode())
					^ (e.getValue() == null ? 0 : e.getValue().hashCode());
		return hash;
	}

	public static boolean setEquals(Set<?> s1, Object o) {
		if (s1 == o)
			return true;
		if (s1 == null)
			return false;
		if (o instanceof Collection) {
			Collection<?> c2 = (Collection<?>) o;
			return s1.size()==c2.size() && s1.containsAll(c2);
		}
		return false;

	}

	public static boolean exist(Object o) {
		return null != o;
	}


	public static <T> T defaultReduce(BiFunction<? super T, ? super T, ? extends T> fn, Iterable<T> iterable) {
		Iterator<T> i = iterable.iterator();
		return when(i.hasNext(),
				() -> {
					T acc = i.next();
					while (i.hasNext())
						acc = fn.apply(acc, i.next());
					return acc;
				});
	}



	public static String collectionToString(Collection<?> c) {
		StringBuilder sb = new StringBuilder("[");
		Iterator<?> i = c.iterator();
		while (i.hasNext()) {
			Object o = i.next();
			if (o == null)
				sb.append("null");
			else if (o == c)
				sb.append("(this Collection)");
			else
				sb.append(o.toString());
			if (i.hasNext())
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	public static <K, V> boolean mapEquals(Map<K, V> a, Object b) {
		if (a == b)
			return true;
		if (b == null || a == null)
			return false;
		if (!(b instanceof Map))
			return false;
		Map<?, ?> t = (Map<?, ?>) b;
		if (a.size() != t.size())
			return false;
		try {
			for (Entry<K, V> e : a.entrySet()) {
				K key = e.getKey();
				V value = e.getValue();
				if (value == null) {
					if (!(t.get(key) == null && t.containsKey(key)))
						return false;
				} else {
					if (!value.equals(t.get(key)))
						return false;
				}
			}
		} catch (ClassCastException unused) {
			return false;
		} catch (NullPointerException unused) {
			return false;
		}
		return true;
	}
public static boolean iteratorContains(Iterator<?> it, Object o){
	if (o == null) {
		while (it.hasNext())
			if (it.next() == null)
				return true;
	} else {
		while (it.hasNext())
			if (o.equals(it.next()))
				return true;
	}
	return false;
}
	public static <K, V> String mapToString(Map<K, V> m) {
		if (m == null)
			return "null";
		Set<Entry<K, V>> set = m.entrySet();
		int max = set.size() - 1;
		if (max == -1)
			return "{}";

		StringBuilder sb = new StringBuilder();
		Iterator<Entry<K, V>> it = set.iterator();

		sb.append('{');
		for (int i = 0;; i++) {
			Entry<K, V> e = it.next();
			K key = e.getKey();
			V value = e.getValue();
			sb.append(key == m ? "(this Map)" : (key == null ? "null" : key
					.toString()));
			sb.append('=');
			sb.append(value == m ? "(this Map)" : (value == null ? "null"
					: value.toString()));

			if (i == max)
				return sb.append('}').toString();
			sb.append(", ");
		}

	}

	// TODO Hm this may be more advanced, Hasheq
	public static int hash(Object o) {
		if (o == null)
			return 0;
		return o.hashCode();

	}

	public static int setHash(Set<?> set) {
		int hash = 0;
		for (Object o : set)
			if (o != null)
				hash = hash + o.hashCode();
		return hash;
	}

	public static int listHash(List<?> list) {
		if (list == null)
			return 0;
		int hashCode = 1;
		for (Object e : list)
			hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
		return hashCode;
	}

	public static boolean listEquals(List<?> a, Object b) {
		if (a == b)
			return true;
		if (b == null || a == null)
			return false;
		if (!(b instanceof List))
			return false;
		List<?> bl = (List<?>) b;
		if (effectiveListSizeNotEqual(a, bl))
			return false;
		if (isRandomList(a) && isRandomList(bl)) {
			int size = a.size();
			for (int i = 0; i < size; i++) {
				Object o1 = a.get(i);
				Object o2 = bl.get(i);
				if (o1 == o2)
					continue;
				if (o1 == null)
					return false;
				if (!o1.equals(o2))
					return false;
			}
			return true;
		} else if (isRandomList(a) && !isRandomList((List<?>) b)) {
			return equalsRandomIterable(a, bl);
		} else if ((!isRandomList(a)) && isRandomList((List<?>) b)) {
			return equalsRandomIterable(bl, a);
		} else {
			Iterator<?> e1 = a.iterator();
			Iterator<?> e2 = bl.iterator();
			while (e1.hasNext() && e2.hasNext()) {
				Object o1 = e1.next();
				Object o2 = e2.next();
				if (!(o1 == null ? o2 == null : o1.equals(o2)))
					return false;
			}
			return !(e1.hasNext() || e2.hasNext());
		}

	}

	private static boolean equalsRandomIterable(List<?> a, List<?> bl) {
		Iterator<?> it = bl.iterator();
		for (Object anA : a) {
			if (it.hasNext()) {
				Object o2 = it.next();
				if (anA == o2)
					continue;
				if (anA == null)
					return false;
				if (!anA.equals(o2))
					return false;
			} else
				return false;
		}
		return !it.hasNext();

	}

	private static boolean isRandomList(List<?> list) {
		return (list instanceof RandomAccess);
	}

	private static boolean effectiveListSizeNotEqual(List<?> a, List<?> b) {
				return a.size() != b.size();
	}

	public static <T> Comparator<T> reverse(final Comparator<T> c){
		return (o1, o2) -> -(c.compare(o1, o2));
	}  
	@SuppressWarnings("rawtypes")
	private static final Comparator DEFAULT_COMPARATOR = new DefaultComparator();

	public static Object[] indexedToArray(Randomly<?> c) {
        int len = c.size();
        Object[] a = new Object[len];
        for (int i = 0; i < len; i++)
            a[i] = c.get(i);
        return a;
    }


	@SuppressWarnings("serial")
	private static final class DefaultComparator implements Comparator,
			Serializable {
		public int compare(Object o1, Object o2) {
			return Util.compare(o1, o2);
		}

		private Object readResolve() throws ObjectStreamException {
			return DEFAULT_COMPARATOR;
		}
	}

	private static int compare(Object k1, Object k2) {
		if (k1 == k2)
			return 0;
		if (k1 == null)
			return -1;
		if (k2 == null)
			return 1;
		return ((Comparable) k1).compareTo(k2);
	}

	static public boolean isInteger(Object x) {
		if (x == null)
			return false;
		if (x instanceof Integer || x instanceof Short || x instanceof Byte)
			return true;

		if (x instanceof Long || x instanceof BigInteger) {
			return ((Number) x).longValue() <= (long) Integer.MAX_VALUE;
		}
		if (x instanceof Float || x instanceof Double) {
			return isBigDecimalInteger(BigDecimal.valueOf(((Number) x)
					.doubleValue()));
		}
		return x instanceof BigDecimal && isBigDecimalInteger((BigDecimal) x);

	}

	private static boolean isBigDecimalInteger(BigDecimal b) {
		try {
			b.toBigIntegerExact();
			return true;
		} catch (ArithmeticException ignored) {
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <T, V extends Randomly<?>> T[] indexedToArray(
			T[] a, V col) {
		int size = col.size();
		if (a.length < size)
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass()
					.getComponentType(), size);

		Object[] result = a;
		for (int i = 0; i < size; i++)
			result[i] = col.get(i);

		if (a.length > size)
			a[size] = null;

		return a;
	}

	@SuppressWarnings("unchecked")
	public static <T, V extends Set<?>> T[] setToArray(T[] a, V col) {
		int size = col.size();
		if (a.length < size)
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass()
					.getComponentType(), size);

		Object[] result = a;
		int i = 0;

		for (Object t : col) {
			result[i] = t;
			i++;
		}

		if (a.length > size)
			a[size] = null;

		return a;
	}

	public static boolean equal(Object a, Object b) {
		return (a == b) || (a != null && a.equals(b));
	}

	public static int hashCode(Object o) {
		return o != null ? o.hashCode() : 0;
	}

	public static int hash(Object... values) {
		return Arrays.hashCode(values);
	}

	public static String toString(Object o) {
		return String.valueOf(o);
	}

	public static <T> int compare(T a, T b, Comparator<? super T> c) {
		return (a == b) ? 0 : c.compare(a, b);
	}

	public static <T> T requireNonNull(T obj) {
		if (obj == null)
			throw new NullPointerException();
		return obj;
	}

	public static <T> T requireNonNull(T obj, String message) {
		if (obj == null)
			throw new NullPointerException(message);
		return obj;
	}

	public static int iteratorCount(Iterable<?> it) {
		int n = 0;
		for (Iterator<?> i = it.iterator(); i.hasNext(); i.next(), n++)
			;
		return n;
	
	}

	

}
