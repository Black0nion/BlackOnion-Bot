package com.github.black0nion.blackonionbot.api;

import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.github.black0nion.blackonionbot.Shared.HTTP_CLIENT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Order(20)
class ApiBaseTest {
	@BeforeAll
	public static void init() {
		API.init();
	}

	@Test
	void test_hello_world() {
		Response response = assertDoesNotThrow(HTTP_CLIENT.newCall(new Request.Builder().url("http://localhost:187/hello").build())::execute);
		assertNotNull(response);
		response.close();
	}
}