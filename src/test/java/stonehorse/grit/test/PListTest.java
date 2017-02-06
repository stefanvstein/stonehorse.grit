package stonehorse.grit.test;

import jdk.internal.dynalink.NoSuchDynamicMethodException;
import org.junit.Test;
import stonehorse.grit.Vectors;
import stonehorse.grit.vector.PList;
import stonehorse.grit.vector.PVector;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PListTest {
    PList<String> p = PList.of(Vectors.vector("a","b","c"));
    PList<String> o = PList.of(Vectors.vector("a"));
    PList<String> e = PList.of(Vectors.vector());
    @Test
    public void testGet() {


        assertEquals("c", p.get());
        assertEquals("a", o.get());
        assertTrue(e.isEmpty());

        assertEquals("c", p.get(0));
        assertEquals("a", o.get(0));
        //  new ArrayList<>().get(0);

        try {
            e.get(0);
            fail();
        } catch (IndexOutOfBoundsException ex) {
        }

        assertEquals("a",p.getOr(2, ()->"No"));
        assertEquals("No",p.getOr(3, ()->"No"));
        assertEquals("No",p.getOr(-1, ()->"No"));
        assertEquals("c",p.getOr(0, ()->"No"));
        assertEquals("No",e.getOr(0, ()->"No"));
    }
    @Test
    public void testWithout() {
        assertEquals(PList.of(Vectors.vector("a", "b")), p.without());
        assertEquals(o, p.without().without());
        assertEquals(e, p.without().without().without());
        try {
            p.without().without().without().without();
            fail();
        } catch (NoSuchElementException ex) {

        }
    }
    @Test
    public void testDrop() {
        assertEquals(PList.of(Vectors.vector("a", "b")), p.drop(1));
        assertEquals(o, p.drop(2));
        assertEquals(e, p.drop(3));
        try{
            p.drop(4);
            fail();
        }catch(IllegalArgumentException ex){}
        try{
            p.drop(-1);
            fail();
        }catch(IllegalArgumentException ex){}
        assertEquals(p, p.drop(0));
        assertEquals(e, e.drop(0));
        assertEquals(o, o.drop(0));
        assertEquals(e, o.drop(1));

        assertEquals(e, p.dropWhile(x->true));
        assertEquals(p, p.dropWhile(x->false));
        assertEquals(o, p.dropWhile(x-> !"a".equals(x)));
        assertEquals(e, e.dropWhile(x->true));
        assertEquals(e, e.dropWhile(x->false));

    }

    @Test
    public void testWith() {
        assertEquals(p, e.with("a").with("b").with("c"));
        assertEquals(e.with("e").with("b").with("d"), p.withAt("d",0).withAt("e",2));
        assertEquals(o, e.withAt("a",0));
    }

    @Test
    public void testIndexOf() {
        assertEquals(0, p.indexOf("c"));
        assertEquals(2, p.indexOf("a"));
        assertEquals(-1, p.indexOf("d"));
        assertEquals(-1, e.indexOf("a"));
        assertEquals(0, o.indexOf("a"));
        assertEquals(-1, e.indexOf("a"));

        assertEquals(1, e.with("a").with("a").with("b").indexOf("a"));

        assertEquals(0, p.lastIndexOf("c"));
        assertEquals(2, p.lastIndexOf("a"));
        assertEquals(-1, p.lastIndexOf("d"));
        assertEquals(-1, e.lastIndexOf("a"));
        assertEquals(0, o.lastIndexOf("a"));
        assertEquals(-1, e.lastIndexOf("a"));
        assertEquals(2, e.with("a").with("a").with("b").lastIndexOf("a"));
    }

    @Test public void testListIterator(){
        LinkedList<String> l = new LinkedList<String>();
        l.addFirst("a");
        l.addFirst("b");
        l.addFirst("c");
        System.out.println(l);
        System.out.println(p);

        ListIterator<String> ll = l.listIterator();
        ListIterator<String> i = p.listIterator();
        assertEquals(ll.hasNext(), i.hasNext());
        assertEquals(ll.hasPrevious(), i.hasPrevious());
        assertEquals(ll.nextIndex(), i.nextIndex());
        assertEquals(ll.previousIndex(), i.previousIndex());
        assertEquals(ll.next(), i.next());
        assertEquals(ll.hasPrevious(), i.hasPrevious());
        assertEquals(ll.hasNext(), i.hasNext());
        assertEquals(ll.next(), i.next());
        assertEquals(ll.hasNext(), i.hasNext());
        assertEquals(ll.hasPrevious(), i.hasPrevious());
        assertEquals(ll.nextIndex(), i.nextIndex());
        assertEquals(ll.previousIndex(), i.previousIndex());
        assertEquals(ll.next(), i.next());
        assertEquals(ll.hasNext(), i.hasNext());
        assertEquals(ll.hasPrevious(), i.hasPrevious());
        assertEquals(ll.nextIndex(), i.nextIndex());
        assertEquals(ll.previousIndex(), i.previousIndex());

        l = new LinkedList<String>();
        ll=l.listIterator();
        i=e.listIterator();
        assertEquals(ll.hasNext(), i.hasNext());
        assertEquals(ll.nextIndex(), i.nextIndex());
        try{
            i.next();
            fail();
        }catch(NoSuchElementException e){}

        i=p.listIterator(p.size());
        assertTrue(i.hasPrevious());
        assertFalse(i.hasNext());
        try{
            i.next();
            fail();
        }catch(NoSuchElementException ex){}
        try {
            p.listIterator(p.size() + 1);
            fail();
        }catch(IndexOutOfBoundsException e){}
        try {
            p.listIterator(-1);
            fail();
        }catch(IndexOutOfBoundsException e){}
        p.listIterator(0);

    }

    @Test public void testSub(){
        System.out.println("*");
        System.out.println(p);
        List<String> s = p.subList(0, 2);
        s=s.subList(0,1);
        System.out.println(s);
    }

}
