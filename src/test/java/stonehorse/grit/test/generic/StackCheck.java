package stonehorse.grit.test.generic;

import stonehorse.grit.test.A;
import stonehorse.grit.PersistentStack;

import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static stonehorse.grit.test.A.a;
import static stonehorse.grit.test.B.b;

public class StackCheck {

	public void checkStack(PersistentStack<A> abnulla, PersistentStack<A> empty) {
		checkPeek(abnulla, empty);
		checkPush(abnulla, empty);
		checkPop(abnulla, empty);

	}

	private void checkPop(PersistentStack<A> abnulla, PersistentStack<A> empty) {
		assertEquals(3, size(abnulla.without()));
		assertEquals(abnulla, abnulla.without().with(a()));
		PersistentStack<A> a = abnulla.without().without().without().without();
		assertEquals(empty, a);
		assertNotNull(a);
		assertEquals(asList(b()), a.with(b()));
		assertEquals(null,a.get());
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

	private void checkPush(PersistentStack<A> abnulla, PersistentStack<A> empty) {
		assertEquals(b(), abnulla.with(b()).get());
		assertEquals(5, size(abnulla.with(null)));
		assertEquals(null, abnulla.with(null).get());

		assertEquals( empty.with(a()),asList(a()));
		assertEquals(1, size(empty.with(a())));
		assertEquals(1, size(empty.with(null)));
		assertEquals(b(), empty.with(null).with(b()).get());
	}

	private void checkPeek(PersistentStack<A> abnulla, PersistentStack<A> empty) {
		assertEquals(a(), abnulla.get());
		assertEquals(null,empty.get());

	}
}
