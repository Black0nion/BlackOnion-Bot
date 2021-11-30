package com.github.black0nion.blackonionbot.utils.config;

import javax.annotation.Nullable;

@AllOrNone
public class InfluxConfig {
	@Nullable
	public final String DATABASE_URL = null;
	@Nullable
	public final String TOKEN = null;
	@Nullable
	public final String ORG = null;

	@Override
	public String toString() {
		return "InfluxConfig{" +
				"DATABASE_URL='" + DATABASE_URL + '\'' +
				", TOKEN={REDACTED, " + (TOKEN != null ? TOKEN.length() : "null") + "}" +
				", ORG='" + ORG + '\'' +
				'}';
	}
}