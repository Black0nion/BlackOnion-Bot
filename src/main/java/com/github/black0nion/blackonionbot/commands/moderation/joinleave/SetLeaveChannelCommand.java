package com.github.black0nion.blackonionbot.commands.moderation.joinleave;

import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class SetLeaveChannelCommand extends TextCommand {

	public SetLeaveChannelCommand() {
		this.setCommand("setleavechannel", "setleavechat").setSyntax("[clear / off]")
				.setRequiredBotPermissions(Permission.MESSAGE_MANAGE).setRequiredPermissions(Permission.MANAGE_SERVER);
	}

	@Override
	public String @NotNull [] getCommand() {
		return new String[]{"setleavechannel", "setleavechat"};
	}

	@Override
	public void execute(final String @NotNull [] args, final @NotNull CommandEvent cmde, final MessageReceivedEvent e,
			final @NotNull Message message, final BlackMember member, final BlackUser author,
			final @NotNull BlackGuild guild, final @NotNull TextChannel channel) {
		message.delete().queue();
		if (args.length >= 2 && (args[1].equalsIgnoreCase("clear") || args[1].equalsIgnoreCase("off"))) {
			guild.setLeaveChannel(-1);
			message.replyEmbeds(
					cmde.success().addField("leavechannelcleared", "leavechannelclearedinfo", false).build())
					.delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		} else {
			guild.setLeaveChannel(channel.getIdLong());
			message.replyEmbeds(cmde.success().addField("leavechannelset", "leavechannelsetinfo", false).build())
					.delay(Duration.ofSeconds(5)).flatMap(Message::delete).queue();
		}
	}
}
