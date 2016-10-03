package stonehorse.grit.test;

import stonehorse.grit.Associative;

import stonehorse.grit.PersistentMap;

import java.util.HashMap;

import static org.junit.Assert.*;

public class AssociativeBase {
    public void testAssciative(PersistentMap<Number, String> empty) {
        assertTrue("Not empty", empty.entrySet().isEmpty());

        nullValue(empty.with(0, null));
    }

    private void nullValue(PersistentMap<Number, String> empty) {
        PersistentMap<Number, String> object = empty.with(0, null);
        HashMap<Integer, String> hashmap = new HashMap<Integer, String>();
        hashmap.put(0, null);
        testEqHash(object, hashmap);
        assertEquals(object.entrySet().size(), 1);
        testEqHash(object.entrySet(), hashmap.entrySet());
        assertNull(object.get(0));
        assertNull(object.getOrDefault(0, "null"));
        assertEquals("not null", object.getOrDefault(1, "not null"));
        assertTrue(object.has(0));
        assertFalse(object.has(1));
        PersistentMap<Number, String> another = empty.with(0, null);
        testEqHash(object, another);
        testEqHash(object.entrySet(), another.entrySet());
        another = object.with(0, null);
        testEqHash(object, another);
        another = another.with(1, null);
        assertTrue(another.has(1));
        assertFalse(object.has(1));
        assertFalse(another.equals(object));
    }

    private void testEqHash(Object o1, Object o2) {
        assertEquals(o1, o2);
        assertEquals(o2, o1);
        assertEquals(o2.hashCode(), o1.hashCode());
    }



}
