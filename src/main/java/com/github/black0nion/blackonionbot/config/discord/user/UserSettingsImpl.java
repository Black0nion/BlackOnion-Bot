package com.github.black0nion.blackonionbot.config.discord.user;

import com.github.black0nion.blackonionbot.config.discord.api.container.AbstractSettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.StringSetting;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

import java.sql.ResultSet;
import java.util.function.LongFunction;

public class UserSettingsImpl extends AbstractSettingsContainer<User> implements UserSettings {

	private final StringSetting joinMessage = addSetting(new StringSetting("joinMessage", "Welcome %user% to %guild%!"));

	public UserSettingsImpl(long id, LongFunction<RestAction<User>> userGetter, ResultSet resultSet) throws Exception {
		super(id, userGetter);

		this.loadSettings(resultSet);
	}

	@Override
	public StringSetting getJoinMessage() {
		return joinMessage;
	}
}
