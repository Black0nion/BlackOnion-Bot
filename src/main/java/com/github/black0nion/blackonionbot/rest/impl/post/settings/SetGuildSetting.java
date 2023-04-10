package com.github.black0nion.blackonionbot.rest.impl.post.settings;

import com.github.black0nion.blackonionbot.config.discord.SettingsUtils;
import com.github.black0nion.blackonionbot.config.discord.api.settings.Setting;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettingsRepo;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettingsRepo;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.rest.api.IPostRoute;
import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class SetGuildSetting implements IPostRoute {

	private final UserSettingsRepo userSettingsRepo;
	private final GuildSettingsRepo guildSettingsRepo;

	public SetGuildSetting(UserSettingsRepo userSettingsRepo, GuildSettingsRepo guildSettingsRepo) {
		this.userSettingsRepo = userSettingsRepo;
		this.guildSettingsRepo = guildSettingsRepo;
	}

	@Override
	public Object handle(Context ctx, JSONObject body, RestSession session, DiscordUser user) throws Exception {
		String name = body.getString("name");

		UserSettings userSettings = userSettingsRepo.getSettings(Long.parseLong(user.getUser().getId()));
		GuildSettings guildSettings = guildSettingsRepo.getSettings(Long.parseLong(ctx.pathParam("guildId")));

		Guild guild = guildSettings.retrieveEntity();
		Member member;
		try {
			// this method will be run in a seperate thread, therefore we can use synchronous methods
			member = guild.retrieveMemberById(user.getUser().getId()).complete();
		} catch (Exception e) {
			// we use a try catch here, because the user might not be in the guild
			// unfortunately, `complete` does not have a way to specify a `ErrorHandler`
			throw new ForbiddenResponse("You are not a member of this guild");
		}

		Setting<Object> setting = guildSettings
			.getSetting(name);

		if (setting == null) {
			throw new BadRequestResponse("No setting found with name: " + name);
		}

		if (!setting.getRequiredCustomPermissions().isEmpty() && !CustomPermission.hasRights(userSettings.getPermissions().getValue(), setting.getRequiredCustomPermissions())) {
			throw new ForbiddenResponse("You don't have the required custom permissions to change this setting");
		}

		if (!setting.getRequiredPermissions().isEmpty() && !member.hasPermission(setting.getRequiredPermissions())) {
			throw new ForbiddenResponse("You don't have the required permissions to change this setting");
		}

		return SettingsUtils.parsePassedValue(body, setting);
	}

	@NotNull
	@Override
	public String url() {
		return "settings/guild/{guildId}";
	}

	@Override
	public String[] requiredBodyParameters() {
		return new String[] { "name", "value" };
	}
}
