package stonehorse.grit.test;

import org.junit.Test;
import stonehorse.grit.PersistentSet;
import stonehorse.grit.Sets;
import stonehorse.grit.map.hash.PHashMap;
import stonehorse.grit.set.PSet;
import stonehorse.grit.tools.Util;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.junit.Assert.*;
import static stonehorse.candy.SupplierLogic.or;


public class SetTest {
	@Test
    public void create() {
		PSet<String> s =  PSet.empty();
		assertEquals(s, PSet.empty());
		assertEquals(s, PSet.of(PHashMap.empty()));
	}
	@Test
    public void with() {
		PersistentSet<String> s = PSet.empty();
		PersistentSet<String> t = s.with("1");
		assertNotEquals(s, t);
		s = t.with("1");
		assertEquals(s, t);
	}
	@Test
    public void stringEqualsAndHash() {
		PersistentSet<String> s = PSet.empty();
		PersistentSet<String> t = s.with("1").with("2");
		assertTrue("[1, 2]".equals(t.toString()) || "[2, 1]".equals(t.toString()));
		Set<String> ts = new TreeSet<String>();
		ts.add("1");
		ts.add("2");
		assertTrue("[1, 2]".equals(ts.toString()) || "[2, 1]".equals(ts.toString()));
		assertEquals(ts.hashCode(), t.hashCode());
		assertEquals(ts, t);
		assertEquals(t, ts);
	}
	@Test
    public void without() {
		PersistentSet<String> s = PSet.<String>empty().with("1").with("2").with("3");
		PersistentSet<String> t = s.without("1");
		assertEquals(PSet.<String>empty().with("2").with("3"), t);
		assertNotEquals(s, t);
		assertEquals(t, t.without("1"));
		assertSame(t, t.without("1"));
	}
	@Test
    public void contains() {
		PersistentSet<String> s =  PSet.<String>empty().with("1").with("2").with("3");
		assertTrue(s.contains("1"));
		assertTrue(s.contains("3"));
		assertFalse(s.contains("4"));
		assertTrue(s.containsAll(Arrays.asList("1")));
		assertTrue(s.containsAll(Arrays.asList("1", "3")));
		assertTrue(s.containsAll(Arrays.asList("1", "2", "3")));
		assertFalse(s.containsAll(Arrays.asList("1", "2", "3", "4")));
		assertTrue(s.containsAll(s));
		assertFalse(s.containsAll(s.with("4")));
	}
	@Test
    public void get() {
		PersistentSet<String> s = PSet.empty();
		assertNull(s.get(null));
		assertFalse(s.contains(null));
		s = PSet.<String>empty().with("1").with("2").with("3");
		assertNull(s.get(null));
		assertFalse(s.contains(null));
		assertEquals("2", s.get("2"));
		assertTrue(s.contains("2"));

		HashSet<A> set = new HashSet<A>();
		set.add(A.a());
		assertTrue(set.contains(new LikeA()));

		PersistentSet<Object> t = PSet.<Object>empty().with(A.a());
		assertTrue(t.contains(new LikeA()));
		assertEquals(A.a(), t.get(new LikeA()));
	}
	@Test
    public void union(){
		PersistentSet<Integer> s = PSet.<Integer>empty().with(1).with(2).with(3);
		assertEquals(s, PSet.<Integer>empty().with(1).with(2).union( PSet.<Integer>empty().with(2).with(3)));
	}
	
	@Test
    public void intersection(){
		PersistentSet<Integer> s = PSet.<Integer>empty().with(2);
		assertEquals(s, PSet.<Integer>empty().with(1).with(2).intersection( PSet.<Integer>empty().with(2).with(3)));
	}
	@Test
    public void difference(){
		PersistentSet<Integer> s = PSet.<Integer>empty().with(1);
		assertEquals(s, PSet.<Integer>empty().with(1).with(2).difference(PSet.<Integer>empty().with(2).with(3)));
	}
	@Test
    public void array(){
		PersistentSet<Integer> s = PSet.empty();
		assertArrayEquals(new Object[]{}, s.toArray());
		assertArrayEquals(new Integer[]{}, s.toArray(new Integer[]{}));
		PersistentSet<Integer> t = s.with(1).with(2);
		assertTrue(or(()->Arrays.equals(new Object[]{Integer.valueOf(1),Integer.valueOf(2)}, t.toArray()),
				()->Arrays.equals(new Object[]{Integer.valueOf(2),Integer.valueOf(1)}, t.toArray())));
		assertTrue(or(()->Arrays.equals(new Integer[]{Integer.valueOf(1),Integer.valueOf(2)}, t.toArray()),
				()->Arrays.equals(new Integer[]{Integer.valueOf(2),Integer.valueOf(1)}, t.toArray(new Integer[]{}))));
		
	}
	
	 @Test
     public void testSerial() throws ClassNotFoundException, IOException{
	    	PersistentSet<Integer> m = PSet.empty();
	    	m=m.with(1).with(3).with(null).with(5);
	    	assertEquals(m, Util.deepCopy(m));
	    	assertNotSame(m, Util.deepCopy(m));
	    	PersistentSet<Integer> m2 = Util.deepCopy(m);
	    	PersistentSet<Integer> m3 = Util.deepCopy(m2);
	    	m2=m2.with(2);
	    	assertNotEquals(m,m2);
	    	assertEquals(m3, m);
	    }
	    @Test
	    public void setsTests(){
	    	assertEquals(Sets.set(1,2,3), Sets.set().with(1).with(2).with(3).with(3));
			assertEquals(Sets.set(1,2,3), Sets.setOfAll(1,2,3,2));
			assertEquals(Sets.set(1,2,3), Sets.setOf(Sets.set(1,1,2,3)));
			assertEquals(Sets.set(1,2,3),Sets.set(1,2,3,2).stream().parallel().collect(Sets.toSet()));
		}

		@Test public void traversable(){
			assertEquals(Sets.set(4,2,3), Sets.set(1,2,3,3).map(v->v+1));
			assertEquals(Sets.set(), Sets.<Integer>set().map(v->v+1));
			try{
				Sets.<Integer>set().map((Function)null);
				fail();
			}catch(NullPointerException e){
			}
			assertEquals(Sets.set(null), Sets.set(1,2,3,3).map(v->null));
			assertEquals(Sets.set(1),Sets.set(1,2,3,3).map(v->1));
			assertEquals(Sets.set(2),Sets.set(1,2,3,3).filter(v->v%2==0));
			try {
				assertEquals(Sets.set(), Sets.set(1, 2, 3, 3).filter(null));
			}catch(NullPointerException e){}
			assertEquals(Integer.valueOf(6), Sets.set(1,2,3,3).reduce((a,v)-> a+v));
			AtomicInteger cnt=new AtomicInteger(0);
			assertNull(Sets.set(1,2,3,3).reduce((a,v)-> {cnt.incrementAndGet();return null;}));
			assertEquals(2, cnt.get());
			cnt.set(0);
			assertEquals(Sets.set(1,1,2,3), Sets.set(1,2,3,3).fold((a,v)-> {cnt.incrementAndGet(); return a.with(v);}, Sets.set()));
			assertEquals(3, cnt.get());
			cnt.set(0);
			assertNull( Sets.set(1,2,3,3).fold((a,v)-> {cnt.incrementAndGet();return a;}, (PersistentSet)null));
			assertEquals(3, cnt.get());
			assertEquals(Sets.set(1,"1",2,"2",3,"3"),Sets.set(1,2,3).flatMap(v->Sets.set(v.toString(),v)));
			assertEquals(Sets.set(),Sets.set(1,2,3).flatMap(v->null));
		}

		@Test public void emptyAndHas(){
			PersistentSet<Integer> s = PSet.empty();
			assertTrue(s.isEmpty());
			assertFalse(s.has(null));
			assertFalse(s.has(1));
			s=s.with(1);
			assertFalse(s.isEmpty());
			assertFalse(s.has(null));
			assertTrue(s.has(1));
			s=s.with(null);
			assertFalse(s.isEmpty());
			assertTrue(s.has(null));
			assertTrue(s.has(1));
			assertFalse(s.has(2));

	}
}
