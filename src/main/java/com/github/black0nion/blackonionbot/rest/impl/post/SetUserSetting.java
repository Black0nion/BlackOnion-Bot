package com.github.black0nion.blackonionbot.rest.impl.post;

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

		final String value = "value";
		Object toSet;
		if (setting.canParse(Long.class)) {
			toSet = body.getLong(value);
		} else if (setting.canParse(Boolean.class)) {
			toSet = body.getBoolean(value);
		} else if (setting.canParse(String.class)) {
			toSet = body.getString(value);
		} else if (setting.canParse(Integer.class)) {
			toSet = body.getInt(value);
		} else if (setting.canParse(Double.class)) {
			toSet = body.getDouble(value);
		} else if (setting.canParse(Float.class)) {
			toSet = body.getFloat(value);
		} else {
			throw new BadRequestResponse("No parser found for : " + setting.getType().getSimpleName());
		}

		setting.setParsedValue(toSet);

		return null;
	}

	@NotNull
	@Override
	public String url() {
		return "user_settings";
	}

	@Override
	public String[] requiredBodyParameters() {
		return new String[] { "name", "value" };
	}
}
