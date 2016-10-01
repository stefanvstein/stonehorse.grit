package stonehorse.grit.test.generic;

import stonehorse.grit.test.A;
import stonehorse.grit.test.B;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static stonehorse.grit.test.A.a;
import static stonehorse.grit.test.B.b;
import static stonehorse.grit.test.EqualsA.likeA;

public class ListCheck {

    public void testList(List<A> abnulla) {
        testSize(abnulla);
        testIndexOf(abnulla);
        testGet(abnulla);
        testSubList(abnulla);
        testLastIndexOf(abnulla);
        testContainsAll(abnulla);
        testContains(abnulla);
        testListIterator(abnulla);
        testArray(abnulla);
    }

    private void testSize(List<A> abnulla) {
        assertEquals(4, abnulla.size());
    }

    private void testSubList(List<A> abnulla) {
        assertEquals(asList(a(), b()), abnulla.subList(0, 2));
        assertEquals(asList(b()), abnulla.subList(1, 2));
        assertEquals(asList(), abnulla.subList(2, 2));
        assertEquals(asList(null, a()), abnulla.subList(2, 4));
        try {
            abnulla.subList(2, 5);
            fail();
        } catch (IndexOutOfBoundsException e) {}
        try {
            abnulla.subList(-1, 1);
            fail();
        } catch (IndexOutOfBoundsException e) {}
        try {
            abnulla.subList(5, 5);
            fail();
        } catch (IndexOutOfBoundsException e) {}
        try {
            abnulla.subList(2, 1);
            fail();
        } catch (IndexOutOfBoundsException e) {}  // JDK-7097404
        catch (IllegalArgumentException e) {}
    }

    private void testListIterator(List<A> abnulla) {
        ListIterator<A> i = abnulla.listIterator();
        assertFalse(i.hasPrevious());
        assertTrue(i.hasNext());
        assertEquals(a(), i.next());
        assertTrue(i.hasNext());
        assertTrue(i.hasPrevious());
        assertEquals(0, i.previousIndex());
        assertEquals(a(), i.previous());
        assertEquals(-1, i.previousIndex());
        assertEquals(0, i.nextIndex());
        try {
            i.previous();
            fail();
        } catch (NoSuchElementException e) {}

        assertEquals(a(), i.next());
        assertEquals(b(), i.next());
        assertEquals(null, i.next());
        assertEquals(a(), i.next());
        try{
        assertEquals(a(), i.next());
        fail();
        }catch(NoSuchElementException e){}
        assertEquals(4,i.nextIndex());
        assertEquals(likeA(), i.previous());
        assertEquals(2,i.previousIndex());
        
        i=abnulla.listIterator(4);
        assertEquals(4,i.nextIndex());
        assertFalse(i.hasNext());
        assertTrue(i.hasPrevious());
        assertEquals(a(),i.previous());
        try{
        abnulla.listIterator(5);
        fail();
        }catch(IndexOutOfBoundsException e){}
    }

    private void testArray(List<A> abnulla){
        assertTrue(Arrays.equals(asList(a(),b(),null,a()).toArray(), abnulla.toArray()));
        try{
            abnulla.toArray(new B[]{});
        }catch(ArrayStoreException e){}
        assertTrue(Arrays.equals(asList(a(),b(),null,a()).toArray(new A[]{}), abnulla.toArray(new A[]{})));
        assertEquals(null, abnulla.toArray()[2]);
        assertEquals(a(), abnulla.toArray()[3]);
        assertEquals(4, abnulla.toArray().length);
    }

    private void testLastIndexOf(List<A> abnulla) {
        assertEquals(3, abnulla.lastIndexOf(a()));
        assertEquals(1, abnulla.lastIndexOf(b()));
        assertEquals(2, abnulla.lastIndexOf(null));
        assertEquals(-1, abnulla.lastIndexOf(b("NO WAY")));
        assertEquals(3, abnulla.lastIndexOf(likeA()));

    }

    private void testIndexOf(List<A> abnulla) {
        assertEquals(2, abnulla.indexOf(null));
        assertEquals(0, abnulla.indexOf(a()));
        assertEquals(1, abnulla.indexOf(b()));
        assertEquals(-1, abnulla.indexOf(b("NO WAY")));
        assertEquals(0, abnulla.indexOf(likeA()));
    }

    private void testGet(List<A> abnulla) {
        assertEquals(a(), abnulla.get(0));
        assertNull(abnulla.get(2));
        assertEquals(a(), abnulla.get(3));
        try {
            abnulla.get(4);
            fail();
        } catch (IndexOutOfBoundsException e) {}
    }

    private void testContainsAll(List<A> abnulla) {
        assertTrue(abnulla.containsAll(asList(a())));
        assertTrue(abnulla.containsAll(nullist()));
        assertTrue(abnulla.containsAll(asList(a(), null)));
        assertTrue(abnulla.containsAll(asList(a(), null, a(), b())));
        assertTrue(abnulla.containsAll(abnulla));
        assertTrue(abnulla.containsAll(Collections.emptyList()));
        assertTrue(asList(a(), a(), b(), null).containsAll(abnulla));

        assertTrue(asList(a(), b(), null, a()).containsAll(asList(a(), b(), null, a(), a())));
        assertTrue(abnulla.containsAll(asList(a(), b(), null, a(), a())));
        assertFalse(abnulla.containsAll(asList(a("c"))));
    }

    private List<Object> nullist() {
        List<Object> l = new ArrayList<Object>();
        l.add(null);
        return l;
    }

    private void testContains(List<A> abnulla) {
        assertTrue(abnulla.contains(null));
        assertTrue(abnulla.contains(a()));
        assertTrue(abnulla.contains(b()));
        assertFalse(abnulla.contains(a("c")));
        assertTrue(abnulla.contains(likeA()));
    }

}
