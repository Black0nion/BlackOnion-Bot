package com.github.black0nion.blackonionbot.utils;

public class DiscordUser {
	
	boolean isAdmin;
	long userId;
	
	public DiscordUser(long userId, boolean isAdmin) {
		this.userId = userId;
		this.isAdmin = isAdmin;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}
	
	public long getUserId() {
		return userId;
	}
}