package com.github.black0nion.blackonionbot.config.discord.user;

import com.github.black0nion.blackonionbot.config.discord.api.repo.SettingsRepo;
import net.dv8tion.jda.api.entities.User;

public interface UserSettingsRepo extends SettingsRepo<UserSettings, User> {}
