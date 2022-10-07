package com.github.black0nion.blackonionbot.rest;

import io.javalin.testtools.JavalinTest;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BasicRouteTest extends ApiBaseTest {

	@BeforeEach
	void setup() {
		app.get("/api/hello", ctx -> ctx.result("hello"));
	}

	@Test
	void test_hello_world() {
		JavalinTest.test(app, (server, client) -> {
			Response response = client.get("/api/hello");
			assertNotNull(response);
			assertEquals(200, response.code());
			response.close();
		});
	}

	@Test
	void test_unknown_route() {
		JavalinTest.test(app, ((server, client) -> {
			Response response = client.get("/api/unknown");
			assertNotNull(response);
			assertEquals(404, response.code());
			response.close();
		}));
	}
}
