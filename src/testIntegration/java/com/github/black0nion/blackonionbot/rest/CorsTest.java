package com.github.black0nion.blackonionbot.rest;

import io.javalin.testtools.JavalinTest;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CorsTest extends ApiBaseTest {

	@Test
	void test_google_origin() {
		JavalinTest.test(app, (server, client) -> {
			Response response = client.request("/api/hello", req -> req
				.header("Origin", "https://google.com")
				.method("OPTIONS", null));
			assertNotNull(response);
			assertEquals(200, response.code());
			assertEquals("*", response.header("Access-Control-Allow-Origin"));
			response.close();
		});
	}

	@Test
	void test_unknown_origin() {
		JavalinTest.test(app, (server, client) -> {
			Response response = client.request("/api/hello", req -> req
				.header("Origin", "https://unknown.com")
				.method("OPTIONS", null));
			assertNotNull(response);
			assertEquals(200, response.code());
			assertEquals("*", response.header("Access-Control-Allow-Origin"));
			response.close();
		});
	}

	@Test
	void test_ip_origin() {
		JavalinTest.test(app, (server, client) -> {
			Response response = client.request("/api/hello", req -> req
				.header("Origin", "192.69.69.69")
				.method("OPTIONS", null));
			assertNotNull(response);
			assertEquals(200, response.code());
			assertNull(response.header("Access-Control-Allow-Origin"));
			response.close();
		});
	}

	@Test
	void test_no_origin() {
		JavalinTest.test(app, (server, client) -> {
			Response response = client.request("/api/hello", req -> req
				.method("OPTIONS", null));
			assertNotNull(response);
			assertEquals(200, response.code());
			assertNull(response.header("Access-Control-Allow-Origin"));
			response.close();
		});
	}
}
