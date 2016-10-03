package stonehorse.grit.test;

import org.junit.Test;
import stonehorse.candy.Atomic;
import stonehorse.grit.PersistentMap;


import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static stonehorse.candy.Atomic.atomic;
import static stonehorse.candy.Atomic.value;
import static stonehorse.candy.Iterables.range;
import static stonehorse.candy.Iterables.stream;

public abstract class MapBase {
  public abstract <K,V> PersistentMap<K,V> create();
  
  @Test
  public void string(){
      PersistentMap<Object, Object> m = create();
	  HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
	assertEquals(hashMap.toString(), m.toString());
	hashMap.put("String", 0);
	assertEquals(hashMap.toString(), m.with("String", 0).toString());
  }
  
  @Test
  public void mapEntry(){
      PersistentMap<String, String> m = create();
	  m=m.with(Integer.toBinaryString(1), "One");
	  Entry<String, String> e = m.entrySet().iterator().next();
	  Map<String, String> o = new HashMap<>();
	  o.put(Integer.toBinaryString(1), "One");
	  Entry<String, String> oe = o.entrySet().iterator().next();
	  assertEquals(oe, e);
	  assertEquals(e, oe);
	  assertEquals(e.hashCode(), oe.hashCode());
	  assertEquals(oe.toString(), e.toString());
	  m=create();
	  m=m.with(null, null);
	  e=m.entrySet().iterator().next();
	  o.clear();
	  o.put(null, null);
	  oe = o.entrySet().iterator().next();
	  assertEquals(oe, e);
	  assertEquals(e, oe);
	  assertEquals(e.hashCode(), oe.hashCode());
	  assertEquals(oe.toString(), e.toString());
	  
	  
	  
  }
  @Test
  public void larger() {
      PersistentMap<String, String> m =create();
      for (int i = 0; i < 10000; i++) {
          m = m.with(Integer.toBinaryString(i % 100), Integer.toHexString(i));
      }
      assertEquals(100, m.size());
      for (int i = 0; i < 100; i++)
          assertEquals(m.get(Integer.toBinaryString(i)), Integer.toHexString(10000 - 100 + i));

  }
  
  @Test
  public void has() {
      PersistentMap<Object, Object> m = create().with("1", "One").with("2", "Two");
      assertTrue(m.has("1"));
      assertTrue(m.has("2"));
      assertFalse(m.has("3"));
      assertFalse(m.has(null));
      assertTrue(m.with(null, "Foo").has(null));

  }
  
  @Test
  public void testAssociative(){
      new AssociativeBase().testAssciative( this.<Number, String>create());
  }
  
  @Test
  public void testWithNull(){
      PersistentMap<Integer, Integer> a = create();
      a=a.with(null, null);
      assertSame(a, a.with(null, null));
      assertEquals(a, a.with(1, 1).without(1).without(2));
      Map<Integer, Integer> b = new HashMap<Integer, Integer>();
      b.put(null, null);
      assertEquals(a,b);
      assertEquals(b,a);
      assertEquals(a.hashCode(),b.hashCode());
      PersistentMap<Integer, Integer> c=a.with(null, 1);
      assertFalse(a.equals(c));
      assertFalse(c.equals(a));
      assertFalse(a.hashCode()==c.hashCode());
      assertEquals(null, a.get(null));
      assertEquals(Integer.valueOf(1), c.get(null));
      assertTrue(c.containsKey(null));
      assertTrue(c.containsValue(1));
      assertFalse(c.containsValue(null));
      assertTrue(a.containsValue(null));
      //assertEquals(a.entryAt(null), new AbstractMap.SimpleEntry(null, null));
      assertEquals(Integer.valueOf(2),a.getOrDefault(1, 2));
      assertEquals(null, create().with(null, null).getOrDefault(null, null));
      assertTrue(a.has(null));
      assertTrue(c.has(null));
      assertFalse(c.has(3));
      assertNull(a.ensureKey(null, 3).get(null));
      assertEquals(null, a.ensureKey(1,null).get(1));
      assertFalse(a.isEmpty());
      assertTrue(a.without(null).isEmpty());
      assertTrue(a.values().contains(null));

  }
  
  @Test
  public void testEqualityHash(){
      PersistentMap<String, Integer> a = create();
      Map<String, Integer> b = new HashMap<String, Integer>();
      assertEquals(a,b);
      assertEquals(b,a);
      assertEquals(a.hashCode(),b.hashCode());
      a=a.with("a", Integer.MAX_VALUE);
      b.put("a", Integer.MAX_VALUE);
      assertEquals(a,b);
      assertEquals(b,a);
      assertEquals(a.hashCode(),b.hashCode());
      for(int i=1;i<8;i++){
          a=a.with(Integer.toString(i), i);
          b.put(Integer.toString(i), i);
      }
      assertEquals(a,b);
      assertEquals(b,a);
      assertEquals(a.hashCode(),b.hashCode());
      a=a.with("b", Integer.MAX_VALUE);
      b.put("b", Integer.MAX_VALUE);
      assertEquals(a,b);
      assertEquals(b,a);
      assertEquals(a.hashCode(),b.hashCode());
      
  }

    @Test public void withoutWhen(){
        PersistentMap<String   , Integer> m = create();
        AtomicBoolean b= atomic(false);
        m.withoutWhen(null, v->{b.set(true);return v==null;});
        assertFalse(value(b));
        assertEquals(m.with("1", 1), m.with("1", 1).withoutWhen("1", v->v==2));
        assertEquals(m, m.with("1", 1).withoutWhen("1", v->v==1));
        assertEquals(m.with("1", 1), m.with("1", 1).with(null, 0).withoutWhen(null, v->v==0));

        PersistentMap<String, Integer> mm = m.withAll(stream(range(20)).collect(Collectors.toMap(v -> v.toString(), v -> v, (ol, ne) -> ne)));
        assertEquals(mm, mm.withoutWhen("1", v->v==2));
        assertEquals(mm.without("1"), mm.withoutWhen("1", v->v==1));
        assertEquals(mm, mm.with(null, 0).withoutWhen(null, v->v==0));

    }
  
 
  
  @Test
  public void testKeySet(){
      PersistentMap<String, Integer> m = create();
	  Set<String> ks1 = m.keySet();
	  assertEquals(new HashSet<String>(), ks1);
	  m=m.with("One", 1).with("Two", 2);
	  Set<String> ks2 = m.keySet();
	  assertEquals(true, ks2.contains("One"));
	  assertEquals(true, ks2.contains("Two"));
	  assertEquals(false, ks1.contains("One"));
	  assertEquals(true, ks2.containsAll(Arrays.asList("Two", "One")));
      PersistentMap<String, Integer> m2 = m.with("Three", 3);
	  assertEquals(false, ks2.contains("Three"));
	  assertEquals(true, m2.keySet().containsAll(Arrays.asList("Three")));
	  
	  List<String> target = new ArrayList<String>();
	  for(String s:ks2)
		  target.add(s);
	  assertEquals(2, target.size());
	  assertEquals(true, target.containsAll(Arrays.asList("One", "Two")));
	  //Next on iterator
  }

  @Test public void testValues(){
      PersistentMap<String, Integer> m = create();
      assertTrue(m.values().isEmpty());

      m=m.with("a",0).with("b",1);
      Collection<Integer> v = m.values();
      assertTrue(v.contains(0));
      assertTrue(v.contains(1));
      assertFalse(v.contains(2));

  }

  @Test public void ifMissing(){
      PersistentMap<String, Integer> m = create();
      assertEquals(create().with("1", 1), m.whenMissing("1", ()->1));

      AtomicInteger ctr =  atomic(0);
      PersistentMap<String, Integer> m1 = m.with("1", 1);
      assertEquals(create().with("1", 1), m1.whenMissing("1", ()->{ctr.incrementAndGet();return 1;}));
      assertEquals(0, value(ctr));

      ctr.set(0);
      assertEquals(m.with(null, null), m.with(null,null).whenMissing(null, ()->{ctr.incrementAndGet();return 22;}));
      assertEquals(0, value(ctr));

      assertEquals(m.with("1", 1),m.ensureKey("1", 1));
      assertEquals(m.with("1", 1),m.with("1", 1).ensureKey("1", 2));

      ctr.set(0);
      PersistentMap<String, Integer> mm = m.withAll(stream(range(20)).collect(Collectors.toMap(v -> v.toString(), v -> v, (ol, ne) -> ne)));
      assertEquals(mm.with(null, 0), mm.whenMissing(null, ()->0));
      mm.whenMissing(null, ()->0).whenMissing(null, ()->{ctr.incrementAndGet();return 0;});
      assertEquals(0, value(ctr));
      assertEquals(mm.with("L", 1), mm.whenMissing("L", ()->1));

  }
}
