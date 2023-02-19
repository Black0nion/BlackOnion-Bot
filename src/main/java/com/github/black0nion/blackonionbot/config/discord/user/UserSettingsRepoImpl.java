package com.github.black0nion.blackonionbot.config.discord.user;

import com.github.black0nion.blackonionbot.config.discord.api.repo.AbstractSettingsRepo;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.ThrowableSupplier;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

import java.sql.ResultSet;
import java.util.function.LongFunction;

import static com.github.black0nion.blackonionbot.config.discord.user.UserSettings.TABLE_NAME;

public class UserSettingsRepoImpl extends AbstractSettingsRepo<UserSettings, RestAction<User>, User> implements UserSettingsRepo {

	private final LanguageSystem languageSystem;

	public UserSettingsRepoImpl(SQLHelperFactory sqlHelperFactory, LongFunction<RestAction<User>> entityGetter, LanguageSystem languageSystem) {
		super(TABLE_NAME, sqlHelperFactory, entityGetter);
		this.languageSystem = languageSystem;
	}

	@Override
	public String getReloadName() {
		return "UserSettingsRepo";
	}

	@Override
	protected UserSettings loadSettingsImpl(long id, SQLHelperFactory helper, ThrowableSupplier<ResultSet> resultSetSupplier, SQLHelperFactory factory) throws Exception {
		return new UserSettingsImpl(id, entityGetter, resultSetSupplier.get(), factory, languageSystem);
	}
}
