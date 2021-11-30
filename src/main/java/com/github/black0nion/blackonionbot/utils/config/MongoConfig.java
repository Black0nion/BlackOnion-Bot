package com.github.black0nion.blackonionbot.utils.config;

import javax.annotation.Nonnull;

public class MongoConfig {
	@Nonnull
	public final String CONNECTION_STRING = "";
	public final int TIMEOUT = -1;

	@Override
	public String toString() {
		return "MongoConfig{" +
				"CONNECTION_STRING='" + CONNECTION_STRING.replace("/\\/\\/([^:]+):(.*)@/,\"//***:***@\"","//***:***@") + '\'' +
				", TIMEOUT=" + TIMEOUT +
				'}';
	}
}