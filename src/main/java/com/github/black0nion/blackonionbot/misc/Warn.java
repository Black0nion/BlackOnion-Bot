package com.github.black0nion.blackonionbot.misc;

public record Warn(long issuer, long user, long guild, long date, String reason) {
	public Warn(long issuer, long user, long guild, long date) {
		this(issuer, user, guild, date, null);
	}
}
