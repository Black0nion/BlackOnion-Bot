package com.github.black0nion.blackonionbot.config.discord.user;

import com.github.black0nion.blackonionbot.config.discord.api.repo.AbstractSettingsRepo;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.ThrowableSupplier;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.LongFunction;

public class UserSettingsRepo extends AbstractSettingsRepo<UserSettings, RestAction<User>> {

	public static final String TABLE_NAME = "user_settings";
	private final LanguageSystem languageSystem;

	@SQLSetup
	public static void setup(SQLHelperFactory sqlHelperFactory) throws SQLException {
		try (SQLHelper helper = sqlHelperFactory.create("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (identifier BIGINT PRIMARY KEY, language VARCHAR(5), FOREIGN KEY (language) REFERENCES language (code))");
				PreparedStatement ps = helper.create()) {
			ps.executeUpdate();
		}
	}

	public UserSettingsRepo(SQLHelperFactory sqlHelperFactory, LongFunction<RestAction<User>> entityGetter, LanguageSystem languageSystem) {
		super(TABLE_NAME, sqlHelperFactory, entityGetter);
		this.languageSystem = languageSystem;
	}

	@Override
	protected UserSettings loadSettingsImpl(long id, SQLHelperFactory helper, ThrowableSupplier<ResultSet> resultSetSupplier, SQLHelperFactory factory) throws Exception {
		return new UserSettingsImpl(id, entityGetter, resultSetSupplier.get(), factory, languageSystem);
	}
}
