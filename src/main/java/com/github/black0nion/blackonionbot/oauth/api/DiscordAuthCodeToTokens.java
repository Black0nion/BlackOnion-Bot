package com.github.black0nion.blackonionbot.oauth.api;

import java.io.IOException;
import java.util.InputMismatchException;

public interface DiscordAuthCodeToTokens {
	String loginWithDiscord(String code) throws IOException, InputMismatchException;
}
