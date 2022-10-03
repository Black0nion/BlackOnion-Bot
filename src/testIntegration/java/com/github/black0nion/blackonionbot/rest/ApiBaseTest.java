package com.github.black0nion.blackonionbot.rest;

import com.github.black0nion.blackonionbot.config.api.Config;
import com.github.black0nion.blackonionbot.inject.NullInjector;
import com.github.black0nion.blackonionbot.stats.StatsCollectorFactory;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.github.black0nion.blackonionbot.tests.junit.Shared.HTTP_CLIENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiBaseTest {
	private static final int PORT = 8080;
	public static final String API_BASE_URL = "http://localhost:" + PORT + "/api";
	public static final Config CONFIG = mock(Config.class);

	@BeforeAll
	public static void init() {
		when(CONFIG.getApiPort()).thenReturn(PORT);
		API api = new API(CONFIG, new NullInjector(), new StatsCollectorFactory(ignored -> {}));
		verify(CONFIG, atLeastOnce()).getApiPort();
		api.getApp().get("/api/hello", ctx -> ctx.result("hello"));
	}

	@Test
	void test_hello_world() {
		Response response = assertDoesNotThrow(HTTP_CLIENT.newCall(new Request.Builder().url(API_BASE_URL + "/hello").build())::execute);
		assertNotNull(response);
		assertEquals(200, response.code());
		response.close();
	}

	@Test
	void test_unknown_route() {
		Response response = assertDoesNotThrow(HTTP_CLIENT.newCall(new Request.Builder().url(API_BASE_URL + "/unknown").build())::execute);
		assertNotNull(response);
		assertEquals(404, response.code());
		response.close();
	}
}
