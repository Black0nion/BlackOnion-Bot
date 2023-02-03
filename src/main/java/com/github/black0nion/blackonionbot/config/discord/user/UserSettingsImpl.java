package com.github.black0nion.blackonionbot.config.discord.user;

import com.github.black0nion.blackonionbot.config.discord.api.AbstractSettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.StringSetting;
import net.dv8tion.jda.api.entities.User;

import java.util.function.Function;

public class UserSettingsImpl extends AbstractSettingsContainer implements UserSettings {

	private final StringSetting joinMessage;
	private final Function<Long, User> userGetter;

	public UserSettingsImpl(long id, Function<Long, User> userGetter) {
		super(id);

		this.userGetter = userGetter;

		this.joinMessage = addSetting(new StringSetting("joinMessage", "Welcome %user% to %guild%!"));
	}

	public User retrieveUser() {
		return userGetter.apply(getIdentifier());
	}

	@Override
	public StringSetting getJoinMessage() {
		return joinMessage;
	}
}
