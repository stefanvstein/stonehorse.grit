package stonehorse.grit.test.generic;

import stonehorse.grit.test.A;
import stonehorse.grit.PersistentStack;
import stonehorse.grit.test.B;

import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static stonehorse.grit.test.A.a;
import static stonehorse.grit.test.B.b;

public class StackCheck {

	public void checkStack(PersistentStack<A> empty) {
		checkPeek( empty);
		checkPush( empty);
		checkPop(empty);
		checkDrop(empty);
		checkDropWhile(empty);
	}

	private void checkPop(PersistentStack<A> empty) {
		PersistentStack<A> abnulla = empty.with(A.a()).with(B.b()).with(null).with(A.a());
		assertEquals(3, size(abnulla.without()));
		assertEquals(abnulla, abnulla.without().with(a()));
		PersistentStack<A> a = abnulla.without().without().without().without();
		assertEquals(empty, a);
		assertNotNull(a);
		assertEquals(asList(b()), a.with(b()));

		try {
			assertEquals(empty, a.without());
			fail();
		} catch (NoSuchElementException e) {
		}
		try {
			empty.without();
			fail();
		} catch (NoSuchElementException e) {
		}
		assertNotNull(empty.with(a()).without());
		assertEquals(asList(a()), empty.with(a()).with(b()).without());

	}

	private boolean not(boolean b) {
		return !b;
	}

	private Object size(PersistentStack<A> stack) {
		int i = 0;
		while (not(stack.isEmpty())) {
			i++;
			stack = stack.without();
		}
		return i;
	}

	private void checkPush(PersistentStack<A> empty) {
		PersistentStack<A> abnulla = empty.with(A.a()).with(B.b()).with(null).with(A.a());
		assertEquals(b(), abnulla.with(b()).get());
		assertEquals(5, size(abnulla.with(null)));
		assertEquals(null, abnulla.with(null).get());

		assertEquals( empty.with(a()),asList(a()));
		assertEquals(1, size(empty.with(a())));
		assertEquals(1, size(empty.with(null)));
		assertEquals(b(), empty.with(null).with(b()).get());
	}

	private void checkPeek( PersistentStack<A> empty) {
		PersistentStack<A> abnulla = empty.with(A.a()).with(B.b()).with(null).with(A.a());
		assertEquals(a(), abnulla.get());
		try{
			empty.get();
			fail();

		}catch(NoSuchElementException e){}

	}
	private void checkDrop( PersistentStack<A> empty){
		PersistentStack<A> abnulla = empty.with(A.a()).with(B.b()).with(null).with(A.a());
		try{
			abnulla.drop(-1);
			fail();
		}catch(IllegalArgumentException e){}
		try{
			abnulla.drop(5);
			fail();
		}catch(IllegalArgumentException e){}
		assertEquals(abnulla,abnulla.drop(0));
		assertEquals(empty,abnulla.drop(4));
		try{
			empty.drop(1);
			fail();
		}catch(IllegalArgumentException e){}
		empty.drop(0);
		PersistentStack<A> abn = empty.with(A.a()).with(B.b()).with(null).with(A.a());
		assertEquals(empty.with(a()).with(b()),abn.drop(1).drop(1));
		assertEquals(empty.with(a()),abn.drop(1).drop(2));
		assertEquals(empty,abnulla.drop(1).drop(2).drop(1).drop(0));
	}

	private void checkDropWhile(PersistentStack<A> empty){
		PersistentStack<A> abnulla = empty.with(A.a()).with(B.b()).with(null).with(A.a());
		assertEquals(empty, empty.dropWhile(e->true));
		assertEquals(empty, empty.dropWhile(e->false));
		assertEquals(empty, abnulla.dropWhile(e->true));
		assertEquals(abnulla, abnulla.dropWhile(e->false));
		assertEquals(abnulla.drop(1), abnulla.dropWhile(e->e!=null));
	}
}
