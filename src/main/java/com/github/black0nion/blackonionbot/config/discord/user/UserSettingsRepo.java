package com.github.black0nion.blackonionbot.config.discord.user;

import com.github.black0nion.blackonionbot.config.discord.api.repo.AbstractSettingsRepo;
import com.github.black0nion.blackonionbot.database.SQLHelper;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.misc.SQLSetup;
import com.github.black0nion.blackonionbot.utils.ThrowableSupplier;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.LongFunction;

public class UserSettingsRepo extends AbstractSettingsRepo<UserSettings, User> {

	@SQLSetup
	public static void setup(SQLHelperFactory sqlHelperFactory) {
		try (SQLHelper helper = sqlHelperFactory.create("CREATE TABLE IF NOT EXISTS user_settings (identifier BIGINT PRIMARY KEY, joinMessage VARCHAR(255) NOT NULL)");
				PreparedStatement ps = helper.create()) {
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UserSettingsRepo(SQLHelperFactory sqlHelperFactory, LongFunction<RestAction<User>> entityGetter) {
		super("user_settings", sqlHelperFactory, entityGetter);
	}

	@Override
	protected UserSettings loadSettingsImpl(long id, SQLHelper helper, ThrowableSupplier<ResultSet> resultSetSupplier) throws Exception {
		return new UserSettingsImpl(id, entityGetter, resultSetSupplier.get());
	}
}
