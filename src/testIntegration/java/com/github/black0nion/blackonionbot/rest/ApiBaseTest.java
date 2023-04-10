package com.github.black0nion.blackonionbot.rest;

import com.auth0.jwt.interfaces.JWTVerifier;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.inject.NullInjector;
import com.github.black0nion.blackonionbot.stats.StatsCollectorFactory;
import io.javalin.Javalin;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiBaseTest {
	protected static final Config CONFIG = mock(Config.class);

	protected Javalin app;

	@BeforeEach
	public void init() {
		when(CONFIG.getApiPort()).thenReturn(8080);
		API api = new API(CONFIG, new NullInjector(), new StatsCollectorFactory(ignored -> {}), mock(JWTVerifier.class), l -> null) {
			@Override
			protected void startServer() {
				// overriden to prevent starting the server (required for JavalinTest)
			}
		};
		app = api.getApp();
	}
}