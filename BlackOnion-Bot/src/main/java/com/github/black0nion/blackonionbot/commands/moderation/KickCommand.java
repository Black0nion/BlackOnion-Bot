package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.Arrays;
import java.util.List;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class KickCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "kick", "yeet" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		final List<Member> mentionedMembers = message.getMentionedMembers();
		if (mentionedMembers.size() == 0) {
			message.reply(EmbedUtils.getDefaultErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("wrongargument", author, guild), LanguageSystem.getTranslation("tagornameuser", author, guild), false).build()).queue();
			return;
		} else {
			guild.kick(mentionedMembers.get(0)).queue();
			final String kickMessage = args.length >= 3 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : LanguageSystem.getTranslation("yougotkicked", author);
			message.reply(EmbedUtils.getDefaultSuccessEmbed(author, guild).setTitle("Kick").addField("usergotkicked", LanguageSystem.getTranslation("message", author, guild) + ": " + kickMessage, false).build()).queue();
			mentionedMembers.get(0).getUser().openPrivateChannel().queue(c -> { 
				c.sendMessage(EmbedUtils.getErrorEmbed(author, guild).setTitle("Kick").addField("yougotkicked", LanguageSystem.getTranslation("message", author, guild) + ": " + kickMessage, false).build()).queue();
			});
		}
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] {Permission.KICK_MEMBERS};
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 1;
	}
	
	@Override
	public String getSyntax() {
		return "<@User> [reason]";
	}
	
	@Override
	public Category getCategory() {
		return Category.MODERATION;
	}

	@Override
	public Permission[] getRequiredBotPermissions() {
		return new Permission[] { Permission.KICK_MEMBERS };
	}
}