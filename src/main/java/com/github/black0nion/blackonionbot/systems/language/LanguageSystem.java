package com.github.black0nion.blackonionbot.systems.language;

import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nullable;
import java.util.Map;

public interface LanguageSystem {
	Map<String, Language> getLanguages();

	Language getLanguage(User user, Guild guild);

	Language getLanguage(@Nullable BlackUser user, @Nullable BlackGuild guild);

	Language getDefaultLanguage();

	@Nullable
	Language getLanguageFromCode(String name);

	String getTranslation(@Nullable String key, @Nullable BlackUser author, @Nullable BlackGuild guild);

	String getLanguageString();
}
