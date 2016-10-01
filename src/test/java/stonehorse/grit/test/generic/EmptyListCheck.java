package stonehorse.grit.test.generic;


import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class EmptyListCheck {
   
    public void checkEmptyList(List<?> empty) {
        sublistCheck(empty);
        listIteratorCheck(empty);
        indexCheck(empty);
        getCheck(empty);
        arrayCheck(empty);
        iteratorCheck(empty);
        containsCheck(empty);
        sizeCheck(empty);
        hashAndEquals(empty);
        get(empty);
    }

   

	private void get(List<?> empty) {
		try{
			empty.get(0);
		}catch(IndexOutOfBoundsException e){}
		try{
			empty.get(1);
		}catch(IndexOutOfBoundsException e){}
	}

	private void hashAndEquals(List<?> empty) {
        assertEquals(emptyList().hashCode(), empty.hashCode());
        assertEquals(emptyList(), empty);
        assertEquals(empty, emptyList());

    }

    private void sizeCheck(List<?> empty) {
        assertEquals(0, empty.size());
    }

    private void containsCheck(List<?> empty) {
        assertEquals(false, empty.contains(null));
        assertEquals(true, empty.containsAll(emptyList()));
        assertEquals(false, empty.containsAll(asList(new Object[]{null})));
      
        try{
            assertEquals(true, empty.containsAll(null));
            fail();
            }catch(NullPointerException e){}

    }

    private void iteratorCheck(List<?> empty) {
        assertEquals(false, empty.iterator().hasNext());
        try {
            empty.iterator().next();
            fail();
        } catch (NoSuchElementException e) {}
        try {
            Iterator<?> i = empty.iterator();
            i.hasNext();
            i.next();
            fail();
        } catch (NoSuchElementException e) {}

    }

    private void arrayCheck(List<?> empty) {
        assertEquals(0, empty.toArray().length);
        assertEquals(0, empty.toArray(new Object[empty.size()]).length);

    }

    private void getCheck(List<?> empty) {
        try {
            empty.get(0);
        } catch (IndexOutOfBoundsException e) {}
        try {
            empty.get(1);
        } catch (IndexOutOfBoundsException e) {}

    }

    private void indexCheck(List<?> empty) {
        assertEquals(-1, empty.indexOf(null));
        assertEquals(-1, empty.indexOf(new Object()));
        assertEquals(-1, empty.lastIndexOf(null));
        assertEquals(-1, empty.lastIndexOf(new Object()));
    }

    private void listIteratorCheck(List<?> empty) {

        checkListIterator(empty.listIterator());
        checkListIterator(empty.listIterator(0));
        try {
            empty.listIterator(1);
            fail();
        } catch (IndexOutOfBoundsException e) {}
    }

    private void checkListIterator(ListIterator<?> i) {
        assertEquals(false, i.hasNext());
        assertEquals(false, i.hasPrevious());
        assertEquals(-1, i.previousIndex());
        assertEquals(0, i.nextIndex());
        try {
            i.next();
            fail();
        } catch (NoSuchElementException e) {}
        try {
            i.previous();
            fail();
        } catch (NoSuchElementException e) {}
    }

    private void sublistCheck(List<?> empty) {
        assertEquals(empty, empty.subList(0, 0));
        try {
            empty.subList(1, 0);
            fail();
        } catch (IllegalArgumentException e) {}
        catch (IndexOutOfBoundsException e) {}
        try {
            empty.subList(0, 1);
        } catch (IndexOutOfBoundsException e) {}
    }
}
