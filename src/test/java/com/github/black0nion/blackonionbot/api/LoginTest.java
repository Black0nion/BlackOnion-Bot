package com.github.black0nion.blackonionbot.api;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.github.black0nion.blackonionbot.Shared.HTTP_CLIENT;
import static org.junit.jupiter.api.Assertions.*;

@Order(22)
class LoginTest {
	private static final Logger log = LoggerFactory.getLogger(LoginTest.class);

	@Test
	void test_create_session_id_no_code() {
		Response response = assertDoesNotThrow(HTTP_CLIENT.newCall(getBaseBuilder().build())::execute);
		assertNotNull(response);
		assertEquals(400, response.code());
		String bodyString = assertDoesNotThrow(Objects.requireNonNull(response.body())::string);
		JSONObject body = assertDoesNotThrow(() -> new JSONObject(bodyString));
		assertTrue(body.has("message"));
		assertTrue(body.has("status"));
		assertEquals("Missing headers: [code]", body.getString("message"));
		assertEquals(400, assertDoesNotThrow(() -> body.getInt("status")));
	}

	@Test
	void test_create_session_id_invalid_code() {
		Response response = assertDoesNotThrow(HTTP_CLIENT.newCall(getBaseBuilder()
			.addHeader("code", "invalid")
			.build())::execute);
		assertNotNull(response);
		assertEquals(400, response.code());
		String bodyString = assertDoesNotThrow(Objects.requireNonNull(response.body())::string);
		JSONObject body = assertDoesNotThrow(() -> new JSONObject(bodyString));
		assertTrue(body.has("message"));
		assertTrue(body.has("status"));
		assertEquals("Invalid code", body.getString("message"));
		assertEquals(400, assertDoesNotThrow(() -> body.getInt("status")));
	}

	private Request.Builder getBaseBuilder() {
		return new Request.Builder()
			.post(RequestBody.create(new byte[0], null))
			.url("http://localhost:187/api/login");
	}

	@Test
	void test_create_session_id() {
		Response response = assertDoesNotThrow(HTTP_CLIENT.newCall(getBaseBuilder()
			.addHeader("code", GenericSessionTest.EXAMPLE_CODE)
			.build())::execute);

		assertNotNull(response);
		String responseBody = assertDoesNotThrow(Objects.requireNonNull(response.body())::string);
		assertEquals(200, response.code(), responseBody);
		log.info("Response Body: " + responseBody);
		assertNotNull(responseBody);
		assertTrue(responseBody.equalsIgnoreCase(GenericSessionTest.EXAMPLE_SESSION_ID));
	}
}