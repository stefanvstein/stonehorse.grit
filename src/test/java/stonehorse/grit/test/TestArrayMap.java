package stonehorse.grit.test;

import org.junit.Test;

import stonehorse.grit.Maps;
import stonehorse.grit.PersistentMap;
import stonehorse.grit.Vectors;
import stonehorse.grit.map.EphemeralMap;
import stonehorse.grit.map.array.EphemeralArrayMap;
import stonehorse.grit.map.array.PArrayMap;
import stonehorse.grit.map.hash.PHashMap;
import stonehorse.grit.tools.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;


public class TestArrayMap extends MapBase {
	@Test
	public void testBecomingHash() {
		PersistentMap<String, String> a = PArrayMap.empty();
	
		for (int i = 0; i < PArrayMap.HASHTABLE_THRESHOLD; i++) {
			a = a.with(Integer.toString(i), Integer.toHexString(i));
		}
		assertTrue(a instanceof PArrayMap);
		a = a.with("a", "a");
		assertFalse(a instanceof PArrayMap);
	}
	@SuppressWarnings("unchecked") @Test
	public void testEphemeral() {
		PArrayMap<String, String> a =PArrayMap.empty();
		EphemeralMap<String, String> ta = a.ephemeral();
		for (int i = 0; i < PArrayMap.HASHTABLE_THRESHOLD; i++) {
			ta = ta.with(Integer.toString(i), Integer.toHexString(i));
		}
		ta = ta.with("2", "Nils");
		assertEquals("Nils", ta.get("2"));
		PersistentMap<String, String> r = ta.persistent();  
		assertTrue(r instanceof PArrayMap);
		ta=((PArrayMap)r).ephemeral();
		ta = ta.with("55", "55");
		ta=ta.ensureKey("55", "Oskar");
		assertEquals("55",ta.get("55"));
		ta=ta.ensureKey("56", "Oskar");
		assertEquals("Oskar",ta.get("56"));
		assertTrue(ta.persistent() instanceof PHashMap);
		ta=PArrayMap.<String,String>empty().ephemeral();
		for(int i =0; i<8;i++)
			ta=ta.with(Integer.toString(i), Integer.toBinaryString(i));
		EphemeralArrayMap o = ((EphemeralArrayMap) ta);
		ta=ta.with("Olle", "Nisse");
		for(int i =32; i<33+7;i++)
			ta=ta.with(Integer.toString(i), Integer.toBinaryString(i));
		ta=ta.with("Olle", "Nisse");
		assertEquals(17,ta.size());
		
	}
	
    @Override
    public <K, V> PersistentMap<K, V> create() {
        return PArrayMap.empty();
    }
    
    @Test
    public void testWithoutAll(){
    	PersistentMap<Object, Object> m = create().with(1, 1).with(2, 2).with(null, null);
    	m=m.withoutAll(Arrays.asList(null, 2));
    	assertEquals(create().with(1, 1), m);
    }
    @Test
    public void testWithAll(){
    	HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
    	hm.put(1, 1);
    	hm.put(2, 2);
    	hm.put(null, null);
    	hm.put(3, 3);
    	PersistentMap<Integer, Integer> m = create();
    	m=m.withAll(hm);
    	assertEquals(m, hm);
    	assertEquals(hm,m);
    	assertEquals(m.hashCode(), hm.hashCode());
    }

	@Test
	public void testSerial() throws IOException, ClassNotFoundException {
		PersistentMap<Integer, String> source = PArrayMap.empty();
		source=source.with(1, "Adam").with(2, "Bertil").with( null, "Doris").with(3, null );


		PersistentMap<Integer, String> copy = Util.deepCopy(source);
		assertEquals(copy, source);
		assertEquals(source, copy);
		assertNotSame(copy, source);

		source = PArrayMap.empty();
		copy = Util.deepCopy(source);
		assertEquals(copy, source);
		assertEquals(source, copy);
		assertSame(copy, source);

	}
	@Test public void stream(){
		Map<Integer, String> a=Vectors.vector(1,2,3)
				.stream()
				.collect(Maps.toMap(v->v,
						v->Integer.toHexString(v),
						(old, current)->current));
		assertEquals(Maps.map(1,"1", 2, "2", 3, "3"), a);
	}

	@Test public void fromEntries(){
		Map<Integer, String>m = Maps.map(1, "1").with(2,"3");
		Set<Map.Entry<Integer, String>> f = m.entrySet();

		assertEquals(m, Maps.fromEntries(f));
	}

}
