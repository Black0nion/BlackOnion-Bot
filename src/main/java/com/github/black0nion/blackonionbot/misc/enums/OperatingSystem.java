package com.github.black0nion.blackonionbot.misc.enums;

import java.util.Arrays;
import java.util.function.Supplier;

import static com.github.black0nion.blackonionbot.bot.BotInformation.OS_BEAN;

public enum OperatingSystem {
	LINUX,
	WINDOWS,
	MACOS(() -> OS_BEAN.getName().contains("mac")),
	UNKNOWN;

	private final Supplier<Boolean> isCurrentOS;

	OperatingSystem() {
		this.isCurrentOS = () -> {
			String os = OS_BEAN.getName().toLowerCase();
			return os.contains(name().toLowerCase());
		};
	}

	OperatingSystem(Supplier<Boolean> isCurrentOS) {
		this.isCurrentOS = isCurrentOS;
	}

	public static OperatingSystem get() {
		return Arrays.stream(values()).filter(o -> o.isCurrentOS.get()).findFirst().orElse(UNKNOWN);
	}
}
