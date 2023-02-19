package com.github.black0nion.blackonionbot.config.discord.guild;

import com.github.black0nion.blackonionbot.bot.CommandRegistry;
import com.github.black0nion.blackonionbot.config.discord.api.repo.AbstractSettingsRepo;
import com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.ThrowableSupplier;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.ResultSet;
import java.util.function.LongFunction;

import static com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings.TABLE_NAME;

public class GuildSettingsRepoImpl extends AbstractSettingsRepo<GuildSettings, Guild, Guild> implements GuildSettingsRepo {

	private final LanguageSystem languageSystem;
	private final CommandRegistry commandRegistry;

	public GuildSettingsRepoImpl(SQLHelperFactory sqlHelperFactory, LongFunction<Guild> entityGetter, LanguageSystem languageSystem, CommandRegistry commandRegistry) {
		super(TABLE_NAME, sqlHelperFactory, entityGetter);
		this.languageSystem = languageSystem;
		this.commandRegistry = commandRegistry;
	}

	@Override
	public String getReloadName() {
		return "GuildSettingsRepo";
	}

	@Override
	protected GuildSettings loadSettingsImpl(long id, SQLHelperFactory helper, ThrowableSupplier<ResultSet> resultSetSupplier, SQLHelperFactory factory) throws Exception {
		return new GuildSettingsImpl(id, entityGetter, resultSetSupplier.get(), factory, languageSystem, commandRegistry);
	}
}
