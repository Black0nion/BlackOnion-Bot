package com.github.black0nion.blackonionbot.commands.admin;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.config.Config;
import com.github.black0nion.blackonionbot.utils.config.ConfigManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.Duration;

public class StatusCommand extends Command {

	public StatusCommand() {
		this.setCommand("status").setSyntax("[online | invisible, offline | idle, afk | dnd, donotdisturb]").setHidden().setRequiredArgumentCount(1);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		message.delete().queue();
		OnlineStatus status;
		switch (args[1].toLowerCase()) {
			case "online" -> status = OnlineStatus.ONLINE;
			case "invisible", "offline" -> status = OnlineStatus.OFFLINE;
			case "idle", "afk" -> status = OnlineStatus.IDLE;
			case "dnd", "donotdisturb" -> status = OnlineStatus.DO_NOT_DISTURB;
			default -> status = null;
		};
		if (status == null) {
			channel.sendMessageEmbeds(cmde.success().addField("statussetfail", CommandEvent.getPleaseUse(guild, author, this), false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
			return;
		}
		Config.online_status = status;
		ConfigManager.saveConfig();
		channel.sendMessageEmbeds(cmde.success().addField("statussetsuccess", LanguageSystem.getTranslation("newstatus", author, guild) + ": **" + status.name().toUpperCase() + "**", false).build()).delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();

		e.getJDA().getPresence().setStatus(status);
	}

	public static OnlineStatus getStatusFromConfig() {
		return Config.online_status != null ? Config.online_status : OnlineStatus.ONLINE;
	}
}