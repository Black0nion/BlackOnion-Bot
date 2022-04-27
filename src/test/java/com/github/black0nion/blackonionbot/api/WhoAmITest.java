package com.github.black0nion.blackonionbot.api;

import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
@Order(25)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WhoAmITest {
	private static final OkHttpClient client = new OkHttpClient();
	private static final Logger log = LoggerFactory.getLogger(WhoAmITest.class);

	@Test
	public void test_no_sessionid() {
		Response response = assertDoesNotThrow(client.newCall(new Request.Builder().url("http://localhost:187/api/whoami").build())::execute);
		assertDoesNotThrow(() -> new JSONObject(assertDoesNotThrow(response.body()::string)));
		assertEquals(400, response.code());
	}

	@Test
	public void test_invalid_session_id() {
		Response response = assertDoesNotThrow(client.newCall(new Request.Builder()
			.url("http://localhost:187/api/whoami")
			.addHeader("sessionid", "test")
			.build())::execute);
		assertEquals(400, response.code());
		JSONObject responseBody = assertDoesNotThrow(() -> new JSONObject(assertDoesNotThrow(response.body()::string)));
		log.info("Response Body: " + responseBody.toString());
		assertEquals("Invalid SessionID", responseBody.getString("message"));
	}

	@Test
	public void test_create_session_id_no_code() {
		Response response = assertDoesNotThrow(client.newCall(new Request.Builder()
			.post(RequestBody.create(new byte[0], null))
			.url("http://localhost:187/api/login")
			.addHeader("sessionid", GenericSessionTest.EXAMPLE_SESSION_ID)
			.build())::execute);
		JSONObject responseBody = assertDoesNotThrow(() -> new JSONObject(assertDoesNotThrow(response.body()::string)));
		assertEquals(400, response.code(), responseBody.toString());
		log.info("Response Body: " + responseBody);
		assertTrue(responseBody.getString("message").startsWith("Missing headers"));
	}

	@Test
	public void test_valid_session_id_not_found() {
		Response response = assertDoesNotThrow(client.newCall(new Request.Builder()
			.url("http://localhost:187/api/whoami")
			.addHeader("sessionid", GenericSessionTest.VALID_UNKNOWN_SESSION_ID)
			.build())::execute);
		JSONObject responseBody = assertDoesNotThrow(() -> new JSONObject(assertDoesNotThrow(response.body()::string)));
		assertEquals(401, response.code(), responseBody.toString());
		log.info("Response Body: " + responseBody);
		assertEquals("Unknown Session", responseBody.getString("message"));
	}

	@Test
	public void test_user_created() {
		Response response = assertDoesNotThrow(client.newCall(new Request.Builder()
			.url("http://localhost:187/api/whoami")
			.addHeader("sessionid", GenericSessionTest.EXAMPLE_SESSION_ID)
			.build())::execute);
		JSONObject responseBody = assertDoesNotThrow(() -> new JSONObject(assertDoesNotThrow(response.body()::string)));
		assertEquals(200, response.code(), responseBody.toString());
		log.info("Response Body: " + responseBody);
		// unfortunately, org.json.JSONObject doesn't override the equals method, so we have to do this
		assertEquals(DiscordUser.userToJson(GenericSessionTest.EXAMPLE_USER).toMap(), responseBody.toMap());
	}
}