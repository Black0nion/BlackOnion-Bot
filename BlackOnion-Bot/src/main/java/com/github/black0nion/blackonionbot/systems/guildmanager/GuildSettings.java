package com.github.black0nion.blackonionbot.systems.guildmanager;

import com.github.black0nion.blackonionbot.utils.CustomManager;

@SuppressWarnings("unused")
public class GuildSettings {
	String guildId;
	String welcomeChannel;
	String welcomeMessage;
	String leaveMessage;
	String testCommand;
	CustomManager guildManager;
	
	public GuildSettings(String guildId) {
		this.guildManager = new CustomManager(guildId);
		
		this.welcomeChannel = setIfNotPresent("welcomeChannel", "none");
		this.welcomeMessage = setIfNotPresent("welcomeMessage", "Welcome, %user%");
		this.leaveMessage = setIfNotPresent("goodbyeMessage", "Bye, %user%");
		this.testCommand = setIfNotPresent("testCommand", "test");
		this.guildId = guildId;
		System.out.println(guildId);
	}
	
	public String getGuildId() {
		return guildId;
	}

	public String getTestCommand() {
		return testCommand;
	}
	
	public String getWelcomeChannel() {
		return welcomeChannel;
	}

	public void setWelcomeChannel(String welcomeChannel) {
		guildManager.save("welcomeChannel", welcomeChannel);
		this.welcomeChannel = welcomeChannel;
	}

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public void setWelcomeMessage(String welcomeMessage) {
		guildManager.save("welcomeMessage", welcomeMessage);
		this.welcomeMessage = welcomeMessage;
	}

	public String getLeaveMessage() {
		return leaveMessage;
	}

	public void setLeaveMessage(String leaveMessage) {
		guildManager.save("leaveMessage", leaveMessage);
		this.leaveMessage = leaveMessage;
	}

	private String setIfNotPresent(String key, String value) {
		if (this.guildManager.get(key) == null)
			guildManager.save(key, value);
		return guildManager.getString(key);
	}
	
	private boolean setIfNotPresent(String key, boolean value) {
		if (this.guildManager.get(key) == null)
			guildManager.save(key, value);
		return guildManager.getBoolean(key);
	}
	
	private Object setIfNotPresent(String key, Object value) {
		if (this.guildManager.get(key) == null)
			guildManager.save(key, value);
		return value;
	}
}
