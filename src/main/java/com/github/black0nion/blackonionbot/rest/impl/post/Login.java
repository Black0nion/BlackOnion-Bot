package com.github.black0nion.blackonionbot.rest.impl.post;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.black0nion.blackonionbot.oauth.OAuthUser;
import com.github.black0nion.blackonionbot.rest.api.IPostRoute;
import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.mokulu.discord.oauth.DiscordAPI;
import io.mokulu.discord.oauth.DiscordOAuth;
import io.mokulu.discord.oauth.model.TokensResponse;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.time.Instant;

public class Login implements IPostRoute {

	// 7 days
	private static final int JWT_VALID_FOR = 60 * 60 * 24 * 7;
	private final Algorithm algorithm;
	private final DiscordOAuth discordOAuth;

	public Login(Algorithm algorithm, DiscordOAuth discordOAuth) {
		this.algorithm = algorithm;
		this.discordOAuth = discordOAuth;
	}

	@Override
	public Object handle(Context ctx, JSONObject body, @Nullable RestSession session, OAuthUser user) throws Exception {
		String code = ctx.queryParam("code");
		if (code == null) {
			throw new BadRequestResponse("Missing code parameter");
		}

		TokensResponse tokens = discordOAuth.getTokens(code);
		user = new OAuthUser(tokens.getAccessToken(), tokens.getRefreshToken(), new DiscordAPI(tokens.getAccessToken()));

		return JWT.create()
			.withExpiresAt(Instant.now().plusSeconds(JWT_VALID_FOR))
			.withIssuer("BlackOnion-Bot")
			.withSubject(user.getIdString())
			.sign(algorithm);
	}

	@Override
	public @Nonnull String url() {
		return "login";
	}

	@Override
	public boolean requiresLogin() {
		return false;
	}
}