package com.github.black0nion.blackonionbot.systems.guildmanager;

import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.CustomManager;

@SuppressWarnings("unused")
public class GuildSettings {
	String guildId;
	String welcomeChannel;
	String welcomeMessage;
	String leaveMessage;
	String testCommand;
	Language language;
	CustomManager guildManager;
	
	public GuildSettings(String guildId) {
		this.guildManager = new CustomManager("guildoptions/" + guildId);
		
		this.welcomeChannel = setIfNotPresent("welcomeChannel", "none");
		this.welcomeMessage = setIfNotPresent("welcomeMessage", "Welcome, %user%");
		this.leaveMessage = setIfNotPresent("goodbyeMessage", "Bye, %user%");
		this.testCommand = setIfNotPresent("testCommand", "test");
		this.language = LanguageSystem.getLanguageFromName(setIfNotPresent("language", LanguageSystem.defaultLocale.getLanguageCode()));
		this.guildId = guildId;
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
	
	public Language getLanguage() {
		return language;
	}
	
	public void setLanguage(Language language) {
		guildManager.save("language", language.getLanguageCode());
		this.language = language;
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
