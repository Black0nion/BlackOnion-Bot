package com.github.black0nion.blackonionbot.rest.impl.post;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
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

import static com.github.black0nion.blackonionbot.utils.AuthUtils.JWT_VALID_FOR;

public class Login implements IPostRoute {

	private final Algorithm algorithm;
	private final DiscordOAuth discordOAuth;
	private final SQLHelperFactory sqlHelperFactory;

	public Login(Algorithm algorithm, DiscordOAuth discordOAuth, SQLHelperFactory sqlHelperFactory) {
		this.algorithm = algorithm;
		this.discordOAuth = discordOAuth;
		this.sqlHelperFactory = sqlHelperFactory;
	}

	@Override
	public Object handle(Context ctx, JSONObject body, @Nullable RestSession session, OAuthUser user) throws Exception {
		String code = ctx.queryParam("code");
		if (code == null) {
			throw new BadRequestResponse("Missing code parameter");
		}

		long requestTime = System.currentTimeMillis();
		TokensResponse tokens = discordOAuth.getTokens(code);
		String accessToken = tokens.getAccessToken();
		String refreshToken = tokens.getRefreshToken();
		long expiresAt = requestTime + tokens.getExpiresIn();

		user = new OAuthUser(accessToken, refreshToken, expiresAt, new DiscordAPI(accessToken));

		sqlHelperFactory.run("INSERT INTO sessions (user_id, access_token, refresh_token, expires_at) VALUES (?, ?, ?, ?) ON CONFLICT (user_id) DO UPDATE SET access_token = ?, refresh_token = ?, expires_at = ?",
			user.getId(),
			accessToken,
			refreshToken,
			expiresAt,
			accessToken,
			refreshToken,
			expiresAt
		);

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