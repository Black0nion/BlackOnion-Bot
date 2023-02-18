package com.github.black0nion.blackonionbot.config.discord.user;

import com.github.black0nion.blackonionbot.config.discord.api.container.AbstractSettingsContainer;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.LanguageSetting;
import com.github.black0nion.blackonionbot.config.discord.impl.settings.LanguageSettingImpl;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.util.function.LongFunction;

import static com.github.black0nion.blackonionbot.config.discord.user.UserSettingsRepoImpl.TABLE_NAME;

public class UserSettingsImpl extends AbstractSettingsContainer<RestAction<User>> implements UserSettings {

	private final LanguageSetting language;

	public UserSettingsImpl(long id, LongFunction<RestAction<User>> userGetter, ResultSet resultSet, SQLHelperFactory helperFactory, LanguageSystem languageSystem) throws Exception {
		super(TABLE_NAME, id, userGetter, helperFactory);

		language = addSetting(new LanguageSettingImpl.Builder(settingsSaver, "language", languageSystem));

		this.loadSettings(resultSet);
	}

	@NotNull
	@Override
	public LanguageSetting getLanguage() {
		return language;
	}
}
