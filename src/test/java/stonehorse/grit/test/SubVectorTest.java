package stonehorse.grit.test;

import org.junit.Test;
import stonehorse.grit.PersistentVector;
import stonehorse.grit.vector.PVector;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SubVectorTest {
	@Test public void testCreate() {
		PersistentVector<Integer> v = PVector.<Integer>createOfAll(1, 2, 3, 4, 5, 6);
		assertEquals(Arrays.asList(1, 2, 3), v.subList(0, 3));
		assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), v.subList(0, 6));
		try {
			v.subList(6, 7);
			fail();
		} catch (IndexOutOfBoundsException e) {}
		assertTrue(v.subList(6, 6).isEmpty());
		try {
			v.subList(-1, 2);
			fail();
		} catch (IndexOutOfBoundsException e) {}
		assertEquals(Arrays.asList(4, 5), v.subList(1, 5).subList(2, 4));
		try {
			v.subList(1, 5).subList(2, 5);
			fail();
		} catch (IndexOutOfBoundsException e) {}
		try {
			v.subList(1, 5).subList(-1, 5);
			fail();
		} catch (IndexOutOfBoundsException e) {}
		assertEquals(Arrays.asList(2, 3), v.subList(1, 5).subList(0, 2));
	}
	
	@Test public void testIndex() {
		PersistentVector<Integer> v = PVector.<Integer>createOfAll(1, 2, 3, 4, 5, 6);
		PersistentVector<Integer> sv = v.subList(2,4);
		assertEquals(-1, sv.indexOf(1));
		assertEquals(-1, sv.indexOf(5));
		assertEquals(0, sv.indexOf(3));
		assertEquals(1, sv.indexOf(4));
		PersistentVector<Integer> sv2 = sv.with(3).with(5);
		assertEquals(Arrays.asList(3,4), sv);
		assertEquals(0, sv2.indexOf(3));
		assertEquals(0, sv2.indexOf(3));
		assertEquals(2, sv2.lastIndexOf(3));
		assertEquals(3, sv2.lastIndexOf(5));
		assertEquals(-1, sv2.lastIndexOf(6));
		assertEquals(-1, sv2.indexOf(null));
		assertEquals(-1, sv2.lastIndexOf(null));
	}
	
	@Test public void containsAll(){
		PersistentVector<Integer> v = PVector.<Integer>createOfAll(1, 2, 3, 4, 5, 6);
		PersistentVector<Integer> sv = v.subList(2,4);
		assertTrue(sv.containsAll(PVector.<Integer>createOfAll(3,4)));
		assertTrue(sv.containsAll(PVector.<Integer>createOfAll(3)));
		assertFalse(sv.containsAll(PVector.<Integer>createOfAll(3,4,5)));
		assertFalse(sv.containsAll(PVector.<Integer>createOfAll(3,4,null)));
	}
	
	@Test public void equalityAndHash(){
		PersistentVector<Integer> v = PVector.<Integer>createOfAll(1, 2, null, 4, 5, 6);
		PersistentVector<Integer> sv = v.subList(2,4);
		assertEquals(sv, Arrays.asList(null, 4));
		assertEquals(Arrays.asList(null, 4), sv);
		assertEquals(Arrays.asList(null, 4).hashCode(), sv.hashCode());
		assertEquals(Arrays.asList(null, 4).toString(), sv.toString());
		assertArrayEquals(new Integer[]{null, 4}, sv.toArray(new Integer[]{}));
		assertArrayEquals(new Integer[]{null, 4}, sv.toArray());
		assertEquals(Integer.valueOf(4), sv.get());
	}
	
	@Test public void get(){
		PersistentVector<Integer> v = PVector.<Integer>createOfAll(1, 2, null, 4, 5, 6);
		PersistentVector<Integer> sv = v.subList(2,4);
		assertEquals(null, sv.get(0));
		assertEquals(Integer.valueOf(4), sv.get(1));
		assertEquals(Integer.valueOf(99), sv.getOr(2, ()->99));
		
	}
	@Test public void withOrWithoutYou(){
		PersistentVector<Integer> v = PVector.<Integer>createOfAll(1, 2, null, 4, 5, 6);
		PersistentVector<Integer> sv = v.subList(2,4);
		assertEquals(Arrays.<Integer>asList(new Integer[]{null}), sv.drop(1));
		assertEquals(Arrays.<Integer>asList(new Integer[]{}), sv.drop(2));
		try{
		sv.drop(3);
		fail();
		}catch(IllegalArgumentException e){}
		assertEquals(v.subList(2,4), sv);
		assertEquals(Arrays.<Integer>asList(new Integer[]{null}), sv.without());
		assertEquals(v.subList(2,4), sv);
		assertEquals(Arrays.<Integer>asList(new Integer[]{3,4}), sv.withAt(3, 0));
		assertEquals(Arrays.<Integer>asList(new Integer[]{null,6}), sv.withAt(6, 1));
		assertEquals(Arrays.<Integer>asList(new Integer[]{null,4,5}), sv.withAt(5, 2));
		try{
		sv.withAt(6, 3);
		fail();}catch(IndexOutOfBoundsException e){}
		assertEquals(PVector.<Integer>createOfAll(null, 4, 5, 6), sv.withAll(Arrays.asList(5,6)));
	}
}
