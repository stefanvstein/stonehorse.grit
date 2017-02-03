package stonehorse.grit.test;

import org.junit.Test;
import stonehorse.candy.Lists;
import stonehorse.grit.PersistentFifo;
import stonehorse.grit.test.generic.ListCheck;
import stonehorse.grit.vector.PFifo;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertTrue;
import static stonehorse.candy.Choices.when;
import static stonehorse.candy.Lists.asList;

/**
 * Created by stefan on 1/28/17.
 */
public class FifoTest {

    PersistentFifo<String> left(){
        PersistentFifo<String> fifo=PFifo.empty();
      return fifo.with("a").with("b").with(null).with("a");
    }
    PersistentFifo<String> mid(){
        PersistentFifo<String> fifo=PFifo.<String>empty().with("crap");
        return fifo.with("a").with("b").without().with(null).with("a");
    }
    PersistentFifo<String> right(){
        PersistentFifo<String> fifo=PFifo.<String>empty().with("crap");
        return fifo.with("a").with("b").with(null).with("a").without();
    }
    PersistentFifo<String> empty(){
        return PFifo.<String>empty().without();
    }

    @Test public void asString(){
        assertEquals("[a, b, null, a]", left().toString());
        assertEquals("[a, b, null, a]", mid().toString());
        assertEquals("[a, b, null, a]", right().toString());
        assertEquals("[]", empty().toString());

    }
    @Test public void testIterator(){
        Iterator e = left().iterator();
        assertEquals("a",e.next());
        assertEquals("b",e.next());
        assertNull(e.next());
        assertEquals("a",e.next());
        e= mid().iterator();
        assertEquals("a",e.next());
        assertEquals("b",e.next());
        assertNull(e.next());
        assertEquals("a",e.next());
        e=right().iterator();
        assertEquals("a",e.next());
        assertEquals("b",e.next());
        assertNull(e.next());
        assertEquals("a",e.next());
        assertFalse(empty().iterator().hasNext());
    }

    @Test public void testGet(){
        PersistentFifo<String> l = left();
        assertEquals("a", l.get(0));
        assertEquals("b", l.get(1));
        assertNull(l.get(2));
        assertEquals("a", l.get(3));
    }

@Test public void without(){
        assertEquals("b", mid().without().get());
    assertNull( mid().without().without().get());
    assertEquals("b", left().without().get());
    assertNull( left().without().without().get());
    assertEquals("b", right().without().get());
    assertNull( right().without().without().get());
}

@Test public void testIndexOf(){
    PersistentFifo<String> m = mid();
    assertEquals(0,m.indexOf("a"));
    assertEquals(3,m.lastIndexOf("a"));
    assertEquals(1,m.indexOf("b"));
    assertEquals(1,m.lastIndexOf("b"));
    assertEquals(2,m.indexOf(null));
    assertEquals(2,m.lastIndexOf(null));
    assertEquals(-1, m.indexOf("c"));
    assertEquals(-1, m.lastIndexOf("c"));

    m = left();
    assertEquals(0,m.indexOf("a"));
    assertEquals(3,m.lastIndexOf("a"));
    assertEquals(1,m.indexOf("b"));
    assertEquals(1,m.lastIndexOf("b"));
    assertEquals(2,m.indexOf(null));
    assertEquals(2,m.lastIndexOf(null));
    assertEquals(-1, m.indexOf("c"));
    assertEquals(-1, m.lastIndexOf("c"));


    m = right();
    assertEquals(0,m.indexOf("a"));
    assertEquals(3,m.lastIndexOf("a"));
    assertEquals(1,m.indexOf("b"));
    assertEquals(1,m.lastIndexOf("b"));
    assertEquals(2,m.indexOf(null));
    assertEquals(2,m.lastIndexOf(null));
    assertEquals(-1, m.indexOf("c"));
    assertEquals(-1, m.lastIndexOf("c"));

    m=empty();
    assertEquals(-1,m.indexOf("a"));
    assertEquals(-1,m.lastIndexOf("a"));
    assertEquals(-1,m.indexOf("b"));
    assertEquals(-1,m.lastIndexOf("b"));
    assertEquals(-1,m.indexOf(null));
    assertEquals(-1,m.lastIndexOf(null));
}

    @Test public void addAndRemove(){
        PersistentFifo<Integer> s = PFifo.empty();
        System.out.println(s);
        s=s.with(1).with(2);
        System.out.println(s);
        s=s.without();
        System.out.println(s);
        s=s.with(3);
        assertTrue(s.contains(2));
        assertFalse(s.contains(1));
        assertTrue(s.containsAll(Lists.arrayList(2,3)));
        assertFalse(s.containsAll(Lists.arrayList(1,3)));
        System.out.println(s);
        assertEquals(Integer.valueOf(2), s.getOr(0, ()->-1));
        assertEquals(Integer.valueOf(-1), s.getOr(2, ()->-1));
        assertEquals(Integer.valueOf(3), s.getOr(1, ()->-1));
        s=s.without();
        assertFalse(s.contains(2));
        System.out.println(s);
        s=s.without();
        System.out.println(s);
        s=s.with(4);
        System.out.println(s);
        s=s.without();
        System.out.println(s);
        assertFalse(s.containsAll(Lists.arrayList(4)));

    }

    @Test public void testreduce(){

        PersistentFifo<String> m = mid();
        assertEquals("abnulla", m.reduce((e, d)->e+d));
        m=left();
        assertEquals("abnulla", m.reduce((e, d)->e+d));
        m=right();
        assertEquals("abnulla", m.reduce((e, d)->e+d));
        m=empty();
        assertNull( m.reduce((e, d)->e+d));
    }
    @Test public void testfold(){

        PersistentFifo<String> m = mid();
        assertEquals("kabnulla", m.fold((e, d)->e+d,"k"));
        m=left();
        assertEquals("kabnulla", m.fold((e, d)->e+d, "k"));
        m=right();
        assertEquals("kabnulla", m.fold((e, d)->e+d, "k"));
        m=empty();
        assertEquals("k", m.fold((e, d)->e+d, "k"));
    }


    @Test public void testmap(){
        PersistentFifo<String> m = mid();
        assertEquals(asList("A","B",null,"A"), m.map(e->when(nonNull(e), ()->e.toUpperCase())));
        m=left();
        assertEquals(asList("A","B",null,"A"), m.map(e->when(nonNull(e), ()->e.toUpperCase())));
        m=right();
        assertEquals(asList("A","B",null,"A"), m.map(e->when(nonNull(e), ()->e.toUpperCase())));
        m=empty();
        assertEquals( asList(), m.map(e->when(nonNull(e), ()->e.toUpperCase())));
    }

    @Test public void testflatmap(){
        PersistentFifo<String> m = mid();
        assertEquals(asList("A","U", "B", "U","A", "U"), m.flatMap(e->when(nonNull(e), ()->asList(e.toUpperCase(), "U"))));
        m=left();
        assertEquals(asList("A","U", "B", "U","A", "U"), m.flatMap(e->when(nonNull(e), ()->asList(e.toUpperCase(), "U"))));
        m=right();
        assertEquals(asList("A","U", "B", "U","A", "U"), m.flatMap(e->when(nonNull(e), ()->asList(e.toUpperCase(), "U"))));
        m=empty();
        assertEquals(asList(), m.flatMap(e->when(nonNull(e), ()->asList(e.toUpperCase(), "U"))));
    }
    @Test public void testfilter(){
        PersistentFifo<String> m = mid();
        assertEquals(asList("a","b","a"), m.filter(Objects::nonNull));
        m=left();
        assertEquals(asList("a","b","a"), m.filter(Objects::nonNull));
        m=right();
        assertEquals(asList("a","b","a"), m.filter(Objects::nonNull));
        m=empty();
        assertEquals( asList(),m.filter(Objects::nonNull));
    }

    @Test public void testListIterator(){
        PersistentFifo<String> m = mid();
        ListIterator<String> l = m.listIterator(1);
        assertEquals("b",l.next());
        assertTrue(l.hasPrevious());
        assertEquals("b",l.previous());
        assertTrue(l.hasPrevious());
        assertEquals("a",l.previous());
        assertFalse(l.hasPrevious());
        assertEquals("a", l.next());
        assertEquals("b", l.next());
        assertNull( l.next());
        assertEquals("a", l.next());
        m = right();
       l = m.listIterator(1);
        assertEquals("b",l.next());
        assertTrue(l.hasPrevious());
        assertEquals("b",l.previous());
        assertTrue(l.hasPrevious());
        assertEquals("a",l.previous());
        assertFalse(l.hasPrevious());
        assertEquals("a", l.next());
        assertEquals("b", l.next());
        assertNull( l.next());
        assertEquals("a", l.next());
        m = left();
        l = m.listIterator(1);
        assertEquals("b",l.next());
        assertTrue(l.hasPrevious());
        assertEquals("b",l.previous());
        assertTrue(l.hasPrevious());
        assertEquals("a",l.previous());
        assertFalse(l.hasPrevious());
        assertEquals("a", l.next());
        assertEquals("b", l.next());
        assertNull( l.next());
        assertEquals("a", l.next());
  }


    @Test public void testSubList() {
       new ListCheck().testList(midA());
    }

    private List<A> midA() {
       return PFifo.<A>empty().with(A.a()).with(B.b()).with(A.a()).with(B.b()).with(null).with(A.a()).without().without();


    }
}
