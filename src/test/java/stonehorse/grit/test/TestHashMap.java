package stonehorse.grit.test;

import org.junit.Test;
import stonehorse.candy.Iterables;
import stonehorse.grit.PersistentMap;
import stonehorse.grit.map.APMapEntry;
import stonehorse.grit.map.EphemeralMap;
import stonehorse.grit.map.hash.*;
import stonehorse.grit.tools.Util;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TestHashMap extends MapBase {



    @Test
    public void hashCollisionAmongOthers() {
        PersistentMap<Object, String> a = PHashMap.empty();
        a = a.with(new SameHash(1), "1");
        a = a.with(new SameHash(2), "2");
        for (int i = 0; i < 17; i++)
            a = a.with(Integer.toBinaryString(i), Integer.toString(i));
        PHashMap hm = (PHashMap) a;
        Node node = hm.root();
        BitmapIndexedNode bin = (BitmapIndexedNode) node;
        bin = (BitmapIndexedNode) bin.nodeAtIndex(bin.index(NodeOp.bitpos(1, 0)));
        HashCollisionNode hcn = (HashCollisionNode) bin.nodeAtIndex(bin.index(NodeOp.bitpos(1, NodeOp.nextShift(0))));
        assertEquals(2, hcn.size());
    }

    public static <K, V> Map.Entry<K, V> entry(K k, V v) {
        return new APMapEntry<K, V>(k, v);
    }

    public static Map.Entry<Object, String> mapentry(int i) {
        return entry(Integer.toHexString(i), Integer.toString(i));
    }

    @Test
    public void hashMapIt() {
        PersistentMap<Object, String> a = PHashMap.empty();
        a = a.with(new SameHash(1), "1");
        a = a.with(new SameHash(2), "2");
        a = a.withAll((Map<Object, String>) arrayNode());
        for (int i = 0; i < 17; i++)
            a = a.with(Integer.toBinaryString(i), Integer.toString(i));

        PersistentMap<Object, String> source = a;

        for (Map.Entry<Object, String> e :
                Iteration.iterable(((PHashMap<Object, String>) a).root()
                        , true, "NULLVALUE")) {
            if (e.getKey() == null)
                assertEquals("NULLVALUE", e.getValue());
            else {
                assertEquals(source.get(e.getKey()), e.getValue());
                PersistentMap<Object, String> s2 = source.without(e.getKey());
                assertTrue(s2.size() < source.size());
                source = s2;
            }
        }
        assertEquals(Integer.valueOf(0),
                Iterables.reduce(
                        (Integer ac, Object v) -> ac + 1, 0,
                        Iteration.iterable(((PHashMap<Object, String>) source).root(), false, null))
        );

        assertEquals(Integer.valueOf(39),
                Iterables.reduce(
                        (Integer ac, Object v) -> ac + 1, 0,
                        Iteration.iterable(((PHashMap<Object, String>) a).root(), false, null))
        );

        EphemeralMap<String, String> y = PHashMap.<String, String>empty().ephemeral();
        HashMap hm = new HashMap();
        for (int i = 0; i < 1000; i++) {
            y = y.ensureKey(Integer.toBinaryString(i), Integer.toString(i));
            hm.put(Integer.toBinaryString(i), Integer.toString(i));
        }

        for (Map.Entry<?, ?> e : Iteration.iterable(((PHashMap<?, ?>) y.persistent()).root(), false, null)) {
            assertTrue(hm.containsKey(e.getKey()));
            assertEquals(hm.get(e.getKey()), e.getValue());
            hm.remove(e.getKey());
        }
        assertTrue(hm.isEmpty());
    }


    @Test
    public void withSameHash() {
        PersistentMap<SameHash, String> a = PHashMap.empty();
        a = a.with(new SameHash(1), "1");
        a = a.with(new SameHash(2), "2");
        assertEquals(a.get(new SameHash(1)), "1");
        assertEquals(a.get(new SameHash(2)), "2");
        assertNull(a.get(new SameHash(3)));

    }

    @Test
    public void manyWithSameHash() {
        PersistentMap<SameHash, String> a = PHashMap.empty();
        for (int i = 0; i < 1000; i++)
            a = a.with(new SameHash(i), Integer.toString(i));

        for (int i = 0; i < 1000; i++)
            a = a.without(new SameHash(i));
        assertEquals(true, a.isEmpty());
    }

    @Test
    public void manyWithSameHashEphemeral() {
        EphemeralHashMap<SameHash, String> a = PHashMap.<SameHash, String>empty().ephemeral();
        for (int i = 0; i < 1000; i++)
            a = a.with(new SameHash(i), Integer.toString(i));
        for (int i = 0; i < 1000; i++) {
            a = a.without(new SameHash(i));
        }
        assertEquals(true, a.isEmpty());
        assertEquals(true, a.persistent().isEmpty());
    }

    @Test
    public void repeatedPersist() {
        EphemeralHashMap a = PHashMap.empty().ephemeral();
        a.persistent();
        try {
            a.persistent();
            fail();
        } catch (IllegalStateException e) {
        }
        a = PHashMap.empty().ephemeral();
        a.persistent();
        try {
            a.with(1, 1);
        } catch (IllegalStateException e) {
        }
        a = PHashMap.empty().ephemeral();
        a.persistent();
        try {
            a.without(1);
        } catch (IllegalStateException e) {
        }
        PHashMap p = PHashMap.empty();
        a = p.ephemeral();
        EphemeralHashMap b = p.ephemeral();
        a = a.with(1, 1);
        b = b.with(2, 2);
        assertFalse(a.has(2));
        assertFalse(b.has(1));

        p = PHashMap.empty();
        p = p.with(1, 1);
        a = p.ephemeral();
        b = p.ephemeral();
        a = a.with(1, 10);
        b = b.with(1, 20);
        assertEquals(Integer.valueOf(1), p.get(1));
        assertEquals(Integer.valueOf(10), a.get(1));
        assertEquals(Integer.valueOf(20), b.get(1));
        PHashMap p2 = a.persistent();
        b = b.with(1, 30);
        assertEquals(Integer.valueOf(10), p2.get(1));
        assertEquals(Integer.valueOf(30), b.get(1));

        p = arrayNode();
        a = p.ephemeral();
        a.with(Integer.toBinaryString(62), 10);
        b = p.ephemeral();
        a.with(Integer.toBinaryString(62), 10);
        b.with(Integer.toBinaryString(62), 20);
        p.ephemeral().with(Integer.toBinaryString(62), 30);
        assertEquals(Integer.valueOf(10), a.get(Integer.toBinaryString(62)));
        assertEquals(Integer.valueOf(20), b.get(Integer.toBinaryString(62)));
        assertEquals(Integer.valueOf(62), p.get(Integer.toBinaryString(62)));
        assertEquals(Integer.valueOf(10), a.persistent().get(Integer.toBinaryString(62)));
        assertEquals(Integer.valueOf(20), b.persistent().get(Integer.toBinaryString(62)));
        assertEquals(Integer.valueOf(62), p.get(Integer.toBinaryString(62)));

        p = PHashMap.empty();
        p = p.with(new SameHash(1), 1);
        p = p.with(new SameHash(2), 2);
        a = p.ephemeral();
        a = a.with(new SameHash(2), 22);
        b = p.ephemeral();
        a = a.with(new SameHash(2), 23);
        assertEquals(Integer.valueOf(2), b.get(new SameHash(2)));
        b = b.with(new SameHash(2), 43);
        assertEquals(Integer.valueOf(2), p.get(new SameHash(2)));
        assertEquals(Integer.valueOf(23), a.get(new SameHash(2)));
        a = a.with(new SameHash(3), 3);
        assertNull(b.get(new SameHash(3)));
    }

    @Test
    public void many() {
        PersistentMap<String, String> d = PHashMap.empty();
        assertEquals(true, d.isEmpty());
        d = d.with("1", "1").without("1");
        assertEquals(true, d.isEmpty());
        PersistentMap<String, String> a = PHashMap.empty();
        for (int i = 0; i < 1000; i++)
            a = a.ensureKey(Integer.toBinaryString(i), Integer.toString(i));
        for (int i = 0; i < 999; i++)
            a = a.without(Integer.toBinaryString(i));
        a = a.without(Integer.toBinaryString(999));
        assertEquals(true, a.isEmpty());
    }

    @Test
    public void manyEphemeral() {
        EphemeralMap<String, String> a = PHashMap.<String, String>empty().ephemeral();
        for (int i = 0; i < 1000; i++)
            a = a.ensureKey(Integer.toBinaryString(i), Integer.toString(i));
        assertEquals(1000, a.size());
        for (int i = 0; i < 998; i++)
            a = a.without(Integer.toBinaryString(i));
        assertEquals(2, a.size());
        assertFalse(a.isEmpty());
        a = a.without(Integer.toBinaryString(998));
        a = a.without(Integer.toBinaryString(999));
        assertEquals(0, a.size());
        assertTrue(a.isEmpty());
    }

    private static PHashMap binaryKey(PHashMap m, int i) {
        return m.with(Integer.toBinaryString(i), i);
    }


    public PHashMap arrayNode() {
        PHashMap m = PHashMap.empty();
        m = binaryKey(m, 41);
        m = binaryKey(m, 47);
        m = binaryKey(m, 44);
        m = binaryKey(m, 35);
        m = binaryKey(m, 32);
        m = binaryKey(m, 59);
        m = binaryKey(m, 38);
        m = binaryKey(m, 56);
        m = binaryKey(m, 62);
        m = binaryKey(m, 50);
        m = binaryKey(m, 2);
        m = binaryKey(m, 137);
        m = binaryKey(m, 143);
        m = binaryKey(m, 128);
        m = binaryKey(m, 158);
        m = binaryKey(m, 14);
        m = binaryKey(m, 8);
        m = binaryKey(m, 140);
        m = binaryKey(m, 131);
        m = binaryKey(m, 11);
        m = binaryKey(m, 146);
        m = binaryKey(m, 134);
        m = binaryKey(m, 152);
        m = binaryKey(m, 155);
        return m;
    }

    @Test
    public void withAndWithoutMany() {
        HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
        hm.put(1, 1);
        hm.put(2, 2);
        hm.put(null, null);
        hm.put(3, 3);
        PersistentMap<Integer, Integer> m = PHashMap.empty();
        m = m.withAll(hm);
        assertEquals(hm, m);
        assertEquals(m, hm);
        hm.put(4, 4);
        hm.put(5, 5);
        m = m.withAll(hm);
        assertEquals(hm, m);
        assertEquals(m, hm);
        m = m.withoutAll(Arrays.asList(5, 3, null, 2, 4));
        hm = new HashMap<Integer, Integer>();
        hm.put(1, 1);
        assertEquals(hm, m);

    }

    @Test
    public void testSerial() throws ClassNotFoundException, IOException {
        PersistentMap<Integer, String> m = (PersistentMap<Integer, String>) PHashMap.EMPTY;
        m = m.with(1, "I").with(3, "III").with(null, "nil").with(5, "V");
        //System.out.println(((PHashMap)m).dump());
        assertEquals(m, Util.deepCopy(m));
        assertNotSame(m, Util.deepCopy(m));
        PersistentMap<Integer, String> m2 = Util.deepCopy(m);
        PersistentMap<Integer, String> m3 = Util.deepCopy(m2);
        m2 = m2.with(2, "II");
        assertNotEquals(m, m2);
        assertEquals(m3, m);
    }

    @Test
    public void ephemeral() {
        PHashMap<String, String> m = PHashMap.empty();
        EphemeralHashMap<String, String> t = m.ephemeral();
        assertTrue(t.isEmpty());

        t = t.with(Integer.toBinaryString(1), "One");
        assertFalse(t.isEmpty());

        assertTrue(t.without(Integer.toBinaryString(1)).isEmpty());
        assertTrue(t.isEmpty()); // Small maps are modified

        m = t.persistent();
        try {
            t.with("3", "no");
            fail();
        } catch (IllegalStateException e) {
        }

        t = m.ephemeral().with(Integer.toBinaryString(1), "One")
                .with(Integer.toBinaryString(2), "Two")
                .with(Integer.toBinaryString(3), "Three");
        assertEquals(true, t.has(Integer.toBinaryString(2)));
        t = t.with(null, "Hoo");
        assertEquals("Hoo", t.get(null));
        t = t.without(null);

        assertEquals("Foo", t.get(null, "Foo"));
        assertEquals("Two", t.get(Integer.toBinaryString(2)));


        assertNull(t.get("Exceptions"));
        assertEquals(3, t.size());

        PHashMap<String, String> source = Iterables.reduce(
                (acc, v) -> acc.with(Integer.toBinaryString(v), Integer.toHexString(v)),
                PHashMap.empty(),
                Iterables.range(5000));
        t = PHashMap.<String, String>empty().ephemeral().withAll(source);
        t = t.withoutAll(source.keySet());
        assertEquals(0, t.size());
        assertTrue(t.isEmpty());
        t = t.without("3");
        assertEquals(0, t.size());
    }

    @Test
    public void digIntoEp() {
        EphemeralHashMap<String, String> t = PHashMap.<String, String>empty().ephemeral();
        assertFalse(t.has("1"));
        t = t.with("1", "1");
        assertTrue(t.has("1"));

        PHashMap<String, String> source = Iterables.reduce(
                (acc, v) -> acc.with(Integer.toBinaryString(v), Integer.toHexString(v)),
                PHashMap.empty(),
                Iterables.range(5000));
        final PHashMap<String, String> source1 = source;
        for (String k : source.keySet()) {
            t = t.with(k, source.get(k));
            assertTrue(t.has(k));
        }
        assertTrue(t.has("1"));
    }

    @Test
    public void collapse() {
        PHashMap<String, String> m = PHashMap.empty();
        for (int i = 0; i < 10; i++)
            m = m.with(Integer.toBinaryString(i), Integer.toString(i));
        m = m.without(Integer.toBinaryString(2));
        m = m.without(Integer.toBinaryString(8));
        m = PHashMap.empty();
        for (int i = 0; i < 159; i++)
            m = m.with(Integer.toBinaryString(i), Integer.toString(i));

        m = m.without(Integer.toBinaryString(41));
        m = m.without(Integer.toBinaryString(47));
        m = m.without(Integer.toBinaryString(44));
        m = m.without(Integer.toBinaryString(35));
        m = m.without(Integer.toBinaryString(32));
        m = m.without(Integer.toBinaryString(59));

        m = m.without(Integer.toBinaryString(38));
        m = m.without(Integer.toBinaryString(56));
        m = m.without(Integer.toBinaryString(62));
        m = m.without(Integer.toBinaryString(50));
        m = m.without(Integer.toBinaryString(2));
        m = m.without(Integer.toBinaryString(137));
        m = m.without(Integer.toBinaryString(143));
        m = m.without(Integer.toBinaryString(128));
        m = m.without(Integer.toBinaryString(158));
        m = m.without(Integer.toBinaryString(14));
        m = m.without(Integer.toBinaryString(8));
        m = m.without(Integer.toBinaryString(140));
        m = m.without(Integer.toBinaryString(131));
        m = m.without(Integer.toBinaryString(11));
        m = m.without(Integer.toBinaryString(146));
        m = m.without(Integer.toBinaryString(134));
        m = m.without(Integer.toBinaryString(152));
        m = m.without(Integer.toBinaryString(155));
    }

    @Override
    public <K, V> PersistentMap<K, V> create() {
        return PHashMap.<K, V>empty();
    }

}
