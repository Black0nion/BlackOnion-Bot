package com.github.black0nion.blackonionbot.utils;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings("unchecked")
class AwaitDoneTest {

	@Test
	void test() {
		Consumer<String> listener = mock(Consumer.class);
		AwaitDone<String> awaitDone = new AwaitDone<>(listener);
		assertDoesNotThrow(() -> awaitDone.done("test"));

		verify(listener).accept("test");
	}

	@Test
	void test_setOnDone() {
		Consumer<String> listener = mock(Consumer.class);
		AwaitDone<String> awaitDone = new AwaitDone<>();
		awaitDone.setOnDone(listener);
		assertDoesNotThrow(() -> awaitDone.done("test"));

		verify(listener).accept("test");
	}

	@Test
	void test_noListener() {
		AwaitDone<String> awaitDone = new AwaitDone<>();
		assertDoesNotThrow(() -> awaitDone.done("test"));
	}

	@Test
	void test_nullListener() {
		AwaitDone<String> awaitDone = new AwaitDone<>(null);
		assertDoesNotThrow(() -> awaitDone.done("test"));
	}

	@Test
	void test_nullResult() {
		Consumer<String> listener = mock(Consumer.class);
		AwaitDone<String> awaitDone = new AwaitDone<>(listener);
		assertDoesNotThrow(() -> awaitDone.done(null));

		verify(listener).accept(null);
	}

	@Test
	void test_nullResult_noListener() {
		AwaitDone<String> awaitDone = new AwaitDone<>();
		assertDoesNotThrow(() -> awaitDone.done(null));
	}

	@Test
	void test_nullResult_nullListener() {
		AwaitDone<String> awaitDone = new AwaitDone<>(null);
		assertDoesNotThrow(() -> awaitDone.done(null));
	}

	@Test
	void test_setOnDone_null() {
		AwaitDone<String> awaitDone = new AwaitDone<>();
		awaitDone.setOnDone(null);
		assertDoesNotThrow(() -> awaitDone.done("test"));
	}

	@Test
	void test_setOnDone_nullResult() {
		Consumer<String> listener = mock(Consumer.class);
		AwaitDone<String> awaitDone = new AwaitDone<>();
		awaitDone.setOnDone(listener);
		assertDoesNotThrow(() -> awaitDone.done(null));

		verify(listener).accept(null);
	}

	@Test
	void test_setOnDone_nullResult_nullListener() {
		AwaitDone<String> awaitDone = new AwaitDone<>();
		awaitDone.setOnDone(null);
		assertDoesNotThrow(() -> awaitDone.done(null));
	}

	@Test
	void test_setOnDone_noListener() {
		AwaitDone<String> awaitDone = new AwaitDone<>();
		awaitDone.setOnDone(null);
		assertDoesNotThrow(() -> awaitDone.done("test"));
	}
}
