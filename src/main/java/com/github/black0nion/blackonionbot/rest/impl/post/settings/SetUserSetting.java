package com.github.black0nion.blackonionbot.rest.impl.post.settings;

import com.github.black0nion.blackonionbot.config.discord.SettingsUtils;
import com.github.black0nion.blackonionbot.config.discord.api.settings.Setting;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettingsRepo;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.oauth.DiscordUser;
import com.github.black0nion.blackonionbot.rest.api.IPostRoute;
import com.github.black0nion.blackonionbot.rest.sessions.RestSession;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class SetUserSetting implements IPostRoute {

	private final UserSettingsRepo userSettingsRepo;

	public SetUserSetting(UserSettingsRepo userSettingsRepo) {
		this.userSettingsRepo = userSettingsRepo;
	}

	@Override
	public Object handle(Context ctx, JSONObject body, RestSession session, DiscordUser user) throws Exception {
		String name = body.getString("name");

		UserSettings userSettings = userSettingsRepo.getSettings(Long.parseLong(user.getUser().getId()));

		Setting<Object> setting = userSettings
			.getSetting(name);

		if (setting == null) {
			throw new BadRequestResponse("No setting found with name: " + name);
		}

		if (!setting.getRequiredCustomPermissions().isEmpty() && !CustomPermission.hasRights(userSettings.getPermissions().getValue(), setting.getRequiredCustomPermissions())) {
			throw new ForbiddenResponse("You don't have the required custom permissions to change this setting");
		}

		return SettingsUtils.parsePassedValue(body, setting);
	}

	@NotNull
	@Override
	public String url() {
		return "settings/user";
	}

	@Override
	public String[] requiredBodyParameters() {
		return new String[] { "name", "value" };
	}
}
