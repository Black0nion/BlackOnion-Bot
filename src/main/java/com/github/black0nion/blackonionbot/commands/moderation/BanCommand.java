package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.Arrays;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BanCommand extends Command {

	public BanCommand() {
		this.setCommand("ban", "permayeet").setSyntax("<@User> [reason]").setRequiredArgumentCount(1).setRequiredPermissions(Permission.BAN_MEMBERS).setRequiredBotPermissions(Permission.BAN_MEMBERS);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		final List<Member> mentionedMembers = message.getMentionedMembers();
		if (mentionedMembers.size() == 0) {
			message.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField(cmde.getTranslation("wrongargument"), cmde.getTranslation("tagornameuser"), false).build()).queue();
		} else {
			String banMessage = author.getLanguage().getTranslationNonNull("yougotbanned");
			final BlackMember userToBan = BlackMember.from(mentionedMembers.get(0));

			assert userToBan != null;
			if (member.canInteract(userToBan)) {
				if (args.length >= 3) {
					banMessage = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
					guild.ban(userToBan, 0, banMessage).queue();
				} else {
					guild.ban(userToBan, 0).queue();
				}
				final String finalBanMessage = banMessage;
				cmde.success("Ban", "usergotbanned", "message", new Placeholder("msg", banMessage));
				userToBan.getBlackUser().openPrivateChannel().queue(c ->
					c.sendMessageEmbeds(EmbedUtils.getErrorEmbed(author, guild)
						.setTitle("Ban")
						.addField(author.getLanguage().getTranslation("yougotbanned"),
								  author.getLanguage().getTranslation("message", new Placeholder("msg", finalBanMessage)), false)
						.build())
					.queue());
			} else {
				cmde.error("usertoopowerful", "loweruserthanu");
			}
		}
	}
}