/**
 * Author: _SIM_
 * Date: 25.05.2021
 * Copyright 2021 BlackOnion
 * File: AntiSpoilerType.java
 */
package com.github.black0nion.blackonionbot.systems.antispoiler;

/**
 * @author _SIM_
 * Date: 25.05.2021
 * Copyright 2021 BlackOnion
 * Class Name: AntiSpoilerType
 */
public enum AntiSpoilerType {
	DELETE("Deletes the message, never to be seen again."),
	REPLACE("Removes the spoilers from the message and resends the message without them."),
	OFF;

	private String description;

	AntiSpoilerType(final String description) {
		this.description = description;
	}

	AntiSpoilerType() {}

	public String getDescription() {
		return description;
	}

	public static AntiSpoilerType parse(final String input) {
		if (input == null || input.isEmpty()) return null;
		try {
			return valueOf(input.toUpperCase());
		} catch (Exception ignored) {
			return null;
		}
	}
}