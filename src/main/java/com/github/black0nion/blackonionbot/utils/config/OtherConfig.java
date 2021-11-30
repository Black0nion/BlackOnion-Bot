package com.github.black0nion.blackonionbot.utils.config;

import com.github.black0nion.blackonionbot.misc.RunMode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OtherConfig {

	@Nonnull
	public RunMode RUN_MODE = RunMode.DEV;

	@Nullable
	public final String TOPGG_AUTH = null;

	@Nullable
	public final String CONTENT_MODERATOR_TOKEN = null;

	@Nullable
	public final String SPOTIFY_CLIENT_ID = null;

	@Nullable
	public final String SPOTIFY_CLIENT_SECRET = null;

	@Override
	public String toString() {
		return "OtherConfig{" +
				"RUN_MODE=" + RUN_MODE +
				'}';
	}
}