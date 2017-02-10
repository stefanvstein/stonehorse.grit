package stonehorse.grit.test;

import org.junit.Test;
import stonehorse.candy.Lists;
import stonehorse.grit.PersistentList;
import stonehorse.grit.PersistentQueue;
import stonehorse.grit.Vectors;
import stonehorse.grit.test.generic.EmptyListCheck;
import stonehorse.grit.test.generic.ListCheck;
import stonehorse.grit.tools.Util;
import stonehorse.grit.vector.PFifo;
import stonehorse.grit.vector.PList;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static junit.framework.TestCase.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static stonehorse.candy.Choices.when;
import static stonehorse.candy.Lists.asList;

public class FifoTest {

    PersistentQueue<String> left(){
        PersistentQueue<String> fifo=PFifo.empty();
      return fifo.with("a").with("b").with(null).with("a");
    }
    PersistentQueue<String> mid(){
        PersistentQueue<String> fifo=PFifo.<String>empty().with("crap");
        return fifo.with("a").with("b").without().with(null).with("a");
    }
    PersistentQueue<String> right(){
        PersistentQueue<String> fifo=PFifo.<String>empty().with("crap");
        return fifo.with("a").with("b").with(null).with("a").without();
    }
private List<A> leftA(){
       return PFifo.<A>empty().with(A.a()).with(B.b()).with(null).with(A.a());
}
    private List<A> rightA(){
        return PFifo.<A>empty().with(A.a()).with(A.a()).with(B.b()).with(null).with(A.a()).without();
    }

    private List<A> midA() {
        return PFifo.<A>empty().with(A.a()).with(A.a()).with(B.b()).without().with(null).with(A.a());
    }
    PersistentQueue<String> empty(){
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
        PersistentQueue<String> l = left();
        assertEquals("a", l.get(0));
        assertEquals("b", l.get(1));
        assertNull(l.get(2));
        assertEquals("a", l.get(3));
        try {
            assertEquals("a", l.get(4));
            fail();
        }catch(IndexOutOfBoundsException e){}
        l = mid();
        assertEquals("a", l.get(0));
        assertEquals("b", l.get(1));
        assertNull(l.get(2));
        assertEquals("a", l.get(3));
        try {
            assertEquals("a", l.get(4));
            fail();
        }catch(IndexOutOfBoundsException e){}
        l = right();
        assertEquals("a", l.get(0));
        assertEquals("b", l.get(1));
        assertNull(l.get(2));
        assertEquals("a", l.get(3));
        try {
            assertEquals("a", l.get(4));
            fail();
        }catch(IndexOutOfBoundsException e){}
    }

    @Test public void testGetOr(){
        PersistentQueue<String> l = left();
        assertEquals("a", l.getOr(0, ()->"ELSE"));
        assertEquals("b", l.getOr(1, ()->"ELSE"));
        assertNull(l.getOr(2, ()->"ELSE"));
        assertEquals("a", l.getOr(3, ()->"ELSE"));
        assertEquals("ELSE", l.getOr(4, ()->"ELSE"));
        assertEquals("ELSE", l.getOr(-1, ()->"ELSE"));

        l = mid();
        assertEquals("a", l.getOr(0, ()->"ELSE"));
        assertEquals("b", l.getOr(1, ()->"ELSE"));
        assertNull(l.getOr(2, ()->"ELSE"));
        assertEquals("a", l.getOr(3, ()->"ELSE"));
        assertEquals("ELSE", l.getOr(4, ()->"ELSE"));
        assertEquals("ELSE", l.getOr(-1, ()->"ELSE"));

        l = right();
        assertEquals("a", l.getOr(0, ()->"ELSE"));
        assertEquals("b", l.getOr(1, ()->"ELSE"));
        assertNull(l.getOr(2, ()->"ELSE"));
        assertEquals("a", l.getOr(3, ()->"ELSE"));
        assertEquals("ELSE", l.getOr(4, ()->"ELSE"));
        assertEquals("ELSE", l.getOr(-1, ()->"ELSE"));
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
    PersistentQueue<String> m = mid();
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
        PersistentQueue<Integer> s = PFifo.empty();

        s=s.with(1).with(2);
        assertTrue(s.containsAll(asList(1,2)));
        s=s.without();
        assertFalse(s.contains(1));
        assertTrue(s.contains(2));
        s=s.with(3);

        assertFalse(s.contains(1));
        assertTrue(s.containsAll(Lists.arrayList(2,3)));
        assertFalse(s.containsAll(Lists.arrayList(1,3)));

        assertEquals(Integer.valueOf(2), s.getOr(0, ()->-1));
        assertEquals(Integer.valueOf(-1), s.getOr(2, ()->-1));
        assertEquals(Integer.valueOf(3), s.getOr(1, ()->-1));
        s=s.without();
        assertFalse(s.contains(2));

        s=s.without();
        s=s.with(4);
        s=s.without();
        assertFalse(s.containsAll(Lists.arrayList(4)));
    }

    @Test public void testreduce(){

        PersistentQueue<String> m = mid();
        assertEquals("abnulla", m.reduce((e, d)->e+d));
        m=left();
        assertEquals("abnulla", m.reduce((e, d)->e+d));
        m=right();
        assertEquals("abnulla", m.reduce((e, d)->e+d));
        m=empty();
        assertNull( m.reduce((e, d)->e+d));
    }
    @Test public void testfold(){

        PersistentQueue<String> m = mid();
        assertEquals("kabnulla", m.fold((e, d)->e+d,"k"));
        m=left();
        assertEquals("kabnulla", m.fold((e, d)->e+d, "k"));
        m=right();
        assertEquals("kabnulla", m.fold((e, d)->e+d, "k"));
        m=empty();
        assertEquals("k", m.fold((e, d)->e+d, "k"));
    }


    @Test public void testmap(){
        PersistentQueue<String> m = mid();
        assertEquals(asList("A","B",null,"A"), m.map(e->when(nonNull(e), ()->e.toUpperCase())));
        m=left();
        assertEquals(asList("A","B",null,"A"), m.map(e->when(nonNull(e), ()->e.toUpperCase())));
        m=right();
        assertEquals(asList("A","B",null,"A"), m.map(e->when(nonNull(e), ()->e.toUpperCase())));
        m=empty();
        assertEquals( asList(), m.map(e->when(nonNull(e), ()->e.toUpperCase())));
    }

    @Test public void testflatmap(){
        PersistentQueue<String> m = mid();
        assertEquals(asList("A","U", "B", "U","A", "U"), m.flatMap(e->when(nonNull(e), ()->asList(e.toUpperCase(), "U"))));
        m=left();
        assertEquals(asList("A","U", "B", "U","A", "U"), m.flatMap(e->when(nonNull(e), ()->asList(e.toUpperCase(), "U"))));
        m=right();
        assertEquals(asList("A","U", "B", "U","A", "U"), m.flatMap(e->when(nonNull(e), ()->asList(e.toUpperCase(), "U"))));
        m=empty();
        assertEquals(asList(), m.flatMap(e->when(nonNull(e), ()->asList(e.toUpperCase(), "U"))));
    }
    @Test public void testfilter(){
        PersistentQueue<String> m = mid();
        assertEquals(asList("a","b","a"), m.filter(Objects::nonNull));
        m=left();
        assertEquals(asList("a","b","a"), m.filter(Objects::nonNull));
        m=right();
        assertEquals(asList("a","b","a"), m.filter(Objects::nonNull));
        m=empty();
        assertEquals( asList(),m.filter(Objects::nonNull));
    }

    @Test public void testListIterator(){
        PersistentQueue<String> m = mid();
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


    @Test public void listCheck() {
       new ListCheck().testList(midA());
        new ListCheck().testList(leftA());
        new ListCheck().testList(rightA());
        new EmptyListCheck().checkEmptyList(empty());
    }

    @Test public void testEqualsHash(){
        assertEquals(mid(), left());
        assertEquals(right(), left());
        assertEquals(asList("a","b", null, "a"), left());
        assertEquals(asList("a","b", null, "a"), right());
        assertEquals(asList("a","b", null, "a"), mid());
        assertEquals(left(),asList("a","b", null, "a") );
        assertEquals(right(), asList("a","b", null, "a"));
        assertEquals(mid(), asList("a","b", null, "a"));
        assertEquals(midA(),rightA());
        assertEquals(asList("a","b", null, "a").hashCode(), mid().hashCode());
        assertEquals(right().hashCode(), mid().hashCode());
        assertEquals(right().hashCode(), left().hashCode());
    }

    @Test public void defaults(){
        PersistentQueue<String> l = mid().with("b");
        assertEquals("a", l.get());
        assertEquals("a", l.getOr(()->"O"));
        assertEquals("1",PFifo.empty().getOr(()->"1"));

        assertEquals("b", l.apply(1));
    }

    @Test
    public void testSerial() throws IOException, ClassNotFoundException {
        PersistentQueue<A> source = Vectors.queued(Vectors.vector(A.a(), null, B.b(), A.a()));
        PersistentQueue<A> copy = Util.deepCopy(source);
        assertEquals(copy, source);
        assertEquals(source, copy);
        assertNotSame(copy, source);
    }
}
