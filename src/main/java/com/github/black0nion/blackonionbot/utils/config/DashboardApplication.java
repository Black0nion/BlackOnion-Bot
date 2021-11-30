package com.github.black0nion.blackonionbot.utils.config;

import javax.annotation.Nullable;

@AllOrNone
public class DashboardApplication {
	@Nullable
	public final String CLIENT_SECRET = null;
	@Nullable
	public final String CLIENT_ID = null;
	@Nullable
	public final String REDIRECT_URI = null;

	@Override
	public String toString() {
		return "DashboardApplication{" +
				"CLIENT_SECRET={REDACTED, " + (CLIENT_SECRET != null ? CLIENT_SECRET.length() : "null") + "}" +
				", CLIENT_ID={REDACTED, " + (CLIENT_ID != null ? CLIENT_ID.length() : "null") + "}" +
				", REDIRECT_URI={REDACTED, " + (REDIRECT_URI != null ? REDIRECT_URI.length() : "null") + "}" +
				'}';
	}
}