package stonehorse.grit.test;

import org.junit.Ignore;
import org.junit.Test;
import stonehorse.candy.Iterables;
import stonehorse.grit.Indexed;
import stonehorse.grit.PersistentVector;
import stonehorse.grit.test.generic.EmptyListCheck;
import stonehorse.grit.test.generic.ListCheck;
import stonehorse.grit.test.generic.StackCheck;
import stonehorse.grit.tools.Util;
import stonehorse.grit.vector.EVector;
import stonehorse.grit.vector.PVector;
import stonehorse.grit.Vectors;
import stonehorse.grit.vector.VFuns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static stonehorse.grit.test.A.a;
import static stonehorse.grit.test.B.b;


public class PersistentVectorTest {


    @Test
    public void testEmpty() {
        new EmptyListCheck().checkEmptyList(Vectors.vector());
        // new EmptyListCheck().checkEmptyPersistent(f.vectorOfAll());
    }

    @Test
    public void testStack() {
        new StackCheck().checkStack(abnulla(), Vectors.<A>vector());
    }

    PersistentVector<A> abnulla() {
        return Vectors.vector(a(), b(), null, a());
    }

    @Test
    public void testDrop() {
        PersistentVector<A> a = abnulla();
        assertEquals(Vectors.vector(), a.drop(4));
        try {
            a.drop(5);
            fail();
        } catch (IllegalArgumentException e) {
        }
        assertEquals(Vectors.vectorOfAll(a()), a.drop(3));
        assertEquals(a, a.drop(0));
    }

    @Test
    public void testList() {
        new ListCheck().testList(abnulla());
    }

    @Test
    public void testSerial() throws IOException, ClassNotFoundException {
        PersistentVector<A> source = abnulla();
        PersistentVector<A> copy = Util.deepCopy(source);
        assertEquals(copy, source);
        assertEquals(source, copy);
        assertNotSame(copy, source);

    }


    @Test
    public void testEmptySerialSame() throws IOException, ClassNotFoundException {
        PersistentVector<A> source = Vectors.vector();
        PersistentVector<A> copy = Util.deepCopy(source);
        assertSame(copy, source);
    }

    @Test @Ignore
    public void testWithMany() {
        withAndGet(17 * 64, Vectors.<Integer>vector());
        testIndexed(17 * 64, Vectors.<Integer>vector());
    }

    @Test
    public void testHash() {
        ArrayList<A> a = new ArrayList<A>(abnulla());
        assertEquals(abnulla(), a);
        assertEquals(abnulla().hashCode(), a.hashCode());
    }

    @Test
    public void testToString() {
        assertEquals(new ArrayList<A>(abnulla()).toString(), abnulla().toString());
    }

    @Test
    public void testWithAll() {
        PersistentVector<A> a = abnulla();
        assertEquals(a, Vectors.vectorOf(a));
        PersistentVector<A> v = Vectors.vector();
        assertEquals(a, v.withAll(a));
    }

    private void testIndexed(int n, PersistentVector<Integer> v) {
        assertEquals(Integer.valueOf(99), v.getOr(0, ()->99));
        Indexed<Integer> u = v;
        for (int i = 0; i < n; i++) {
            u = u.with(i + 1);
            for (int j = 0; j < i; j++)
                assertEquals(Integer.valueOf(j + 1), u.getOr(j, ()->n + 99));
            assertEquals(Integer.valueOf(99), v.getOr(0, ()->99));
            assertEquals(Integer.valueOf(-1), u.getOr(n + 1, ()->-1));
            Indexed<Integer> w = u;
            for (int j = 0; j < i; j++) {
                w = w.withAt(j + 1, i - 1 - j);
            }
            for (int j = 0; j < i; j++)
                assertEquals(Integer.valueOf(j + 1), w.getOr(i - 1 - j, ()->n + 99));
        }
        assertEquals(Integer.valueOf(99), v.getOr(0,()-> 99));
        assertEquals(Integer.valueOf(99), v.getOr(1, ()->99));
        assertEquals(Integer.valueOf(-1), u.getOr(n + 1, ()->-1));
        try {
            u.withAt(-1, n + 1);
            fail();
        } catch (IndexOutOfBoundsException e) {
        }
    }


    @Test
    public void nullTest() {

        PersistentVector<Integer> v = Vectors.vector();
try {

    int m = 1024 + 32;
    v = v.with(1);
    for (int i = 0; i < m - 1; i++) {

        v = v.with(null);
        assertEquals(1, VFuns.levels(v.size()));
    }
    v = v.with(0);
    assertEquals(2, VFuns.levels(v.size()));

    assertEquals(m + 1, v.size());
    while (!v.isEmpty()) {
        v = v.without();
        if (v.size() == 1)
            assertEquals(Integer.valueOf(1), v.get());

    }
}catch(Exception e){
    throw e;
}
    }

    @Test
    public void EphemeralConsistencyTest() {
        PersistentVector<Integer> v = Vectors.vector();
        int m = 1024 + 31;
        v = v.withAll(Iterables.range(0, m));
        PersistentVector<Integer> v2 = Vectors.vector();
        for (int i = 0; i < m; i++) {
            v2 = v2.with(i);
        }
        assertEquals(((PVector) v2).data(), ((PVector) v).data());
        PersistentVector<Integer> v3 = Vectors.vector();
        v3=v3.with(12);
        for (int i = 1; i < m; i++) {
            v3 = v3.with(i);
        }
        assertNotEquals(((PVector)v3).data(), ((PVector)v2).data());
        for (int i = 0; i < m - 1; i++) {
            v = v.drop(1);
            v2 = v2.without();
        }
        v = v.drop(1);
        v2 = v2.without();
        assertTrue(v.isEmpty());
        assertTrue(v2.isEmpty());

    }

    void withAndGet(int n, PersistentVector<Integer> v) {

        for (int i = 0; i < n; i++) {
            v = v.with(i);

            Indexed<Integer> u = v.with(i + 1);
            assertNotEquals(v, u);
            for (int j = 0; j < i; j++)
                assertEquals(Integer.valueOf(j), v.get(j));
            for (int j = 0; j < i + 1; j++)
                assertEquals(Integer.valueOf(j), u.get(j));
            for (int j = 0; j < i + 1; j++)
                assertEquals(Integer.valueOf(j), u.get(j));
            Indexed<Integer> z = u;
            for (int j = i; j > 0; j--) {
                u = u.without();
                assertNotEquals(u, z);
                assertEquals(Integer.valueOf(j), u.get());
            }
        }
    }

    @Test
    public void testEphemeral() {
        EVector<Integer> v = PVector.createEphemeral(Arrays.asList(1, 2, 3));

        try {
            v.drop(4);
            fail();
        } catch (IllegalArgumentException e) {
        }
        assertEquals(3, v.size());
        assertTrue(v.drop(3).isEmpty());
        v = PVector.createEphemeral(Arrays.asList(1, 2, 3));
        v = v.without();
        assertEquals(2, v.size());
        assertEquals(Integer.valueOf(99), v.getOr(2, ()->99));
        assertEquals(Integer.valueOf(2), v.getOr(1, ()->99));
        v = v.with(3);
        assertEquals(Arrays.asList(1, 2, 3), v.persistent());

        v = PVector.createEphemeral(Arrays.asList(1, 2, 3));
        v = v.withAt(4, 3);
        try {
            v.withAt(6, 5);
            fail();
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            v.withAt(0, -1);
            fail();
        } catch (IndexOutOfBoundsException e) {
        }
        v = v.withAll(Arrays.asList(5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32));
        v = v.withAt(6, 5);
        v = v.withAt(33, 32);
        v = v.withAt(6, 5);
        v = v.without();
        assertEquals(Integer.valueOf(32), v.get());


    }

    @Test public void vectorOfAll(){
        PersistentVector<String> v = Vectors.vectorOfAll(new String[]{"A"});
        assertEquals("A", v.get());
        assertEquals("A", v.apply(0));
        v = Vectors.vectorOfAll(new String[]{});
        assertTrue(v.isEmpty());
        assertEquals(Vectors.vector(1,2), Vectors.vectorOfAll(1,2));
    }

    @Test
    public void testStream() {
        {
            PersistentVector<Object> v = Vectors.vector();
            assertEquals(0, v.stream().map(Object::toString).count());
        }
        {
            PersistentVector<Object> v = Vectors.vectorOfAll(1, 2, 3);
            assertEquals(3, v.stream().map(Object::toString).count());
            assertEquals(Arrays.asList("1", "2", "3"), v.stream().map(Object::toString).collect(Collectors.toList()));
            assertEquals(Arrays.asList("1", "2"), Vectors.vectorOfAll(1, 2, 3).subList(0, 2).stream().map(Object::toString).collect(Collectors.toList()));
            PersistentVector<String> r = Vectors.vector(1, 2, 3)
                    .stream()
                    .map(Objects::toString)
                    .collect(Vectors.toVector());
            assertEquals(Vectors.vector("1", "2", "3"), r);


        }
    }
}
