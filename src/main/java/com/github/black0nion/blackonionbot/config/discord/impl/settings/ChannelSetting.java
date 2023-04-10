package com.github.black0nion.blackonionbot.config.discord.impl.settings;

import com.github.black0nion.blackonionbot.config.discord.api.settings.Setting;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public interface ChannelSetting<C extends GuildChannel> extends Setting<C> {}
