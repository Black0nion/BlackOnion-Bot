package com.github.black0nion.blackonionbot.rest.impl.post;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.black0nion.blackonionbot.oauth.OAuthUser;
import com.github.black0nion.blackonionbot.rest.api.IPostRoute;
import com.github.black0nion.blackonionbot.rest.exception.TooEarlyResponse;
import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.time.Instant;

import static com.github.black0nion.blackonionbot.utils.AuthUtils.JWT_VALID_FOR;

public class RefreshToken implements IPostRoute {

	private final Algorithm algorithm;

	public RefreshToken(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public Object handle(Context ctx, JSONObject body, RestSession session, OAuthUser user) throws Exception {
		if (session.getJwt().getExpiresAtAsInstant().isAfter(Instant.now().plusSeconds(JWT_VALID_FOR / 2))) {
			throw new TooEarlyResponse("You can only refresh your token after half of the validity time has passed");
		}

		return JWT.create()
			.withExpiresAt(Instant.now().plusSeconds(JWT_VALID_FOR))
			.withIssuer("BlackOnion-Bot")
			.withSubject(user.getIdString())
			.sign(algorithm);
	}

	@NotNull
	@Override
	public String url() {
		return "refresh_token";
	}
}