package com.github.black0nion.blackonionbot.oauth.impl;

import com.github.black0nion.blackonionbot.oauth.OAuthAPI;
import com.github.black0nion.blackonionbot.oauth.api.SessionHandler;
import com.github.black0nion.blackonionbot.oauth.api.DiscordAuthCodeToTokens;
import io.mokulu.discord.oauth.model.TokensResponse;

import java.io.IOException;
import java.util.InputMismatchException;

public class DiscordAuthCodeToTokensImpl implements DiscordAuthCodeToTokens {

	private final SessionHandler login;

	public DiscordAuthCodeToTokensImpl(SessionHandler login) {
		this.login = login;
	}

	@Override
	public String loginWithDiscord(String code) throws IOException, InputMismatchException {
		final TokensResponse response = OAuthAPI.getInstance().getOAuthApi().getTokens(code);
		if (response == null) return null;
		else return login.createSession(response.getAccessToken(), response.getRefreshToken(), response.getExpiresIn());
	}
}
