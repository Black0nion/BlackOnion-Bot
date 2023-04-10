package com.github.black0nion.blackonionbot.config.discord.user;

import com.github.black0nion.blackonionbot.config.discord.api.repo.SettingsRepo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public interface UserSettingsRepo extends SettingsRepo<UserSettings, User> {
	default UserSettings getSettings(Member member) {
		return getSettings(member.getUser());
	}
}
