package com.github.black0nion.blackonionbot.commands.fun;

import java.time.Instant;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AvatarCommand extends Command {
	
	public AvatarCommand() {
		this.setCommand("avatar", "profilepicture", "pb")
			.setSyntax("<@User / UserID>");
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		BlackUser mentionedUser = author;
		if (args.length > 1) {			
			final String user = String.join(" ", Utils.removeFirstArg((args)));
			final List<BlackUser> mentionedBlackUsers = message.getMentionedBlackUsers();
			if (!mentionedBlackUsers.isEmpty())
				mentionedUser = mentionedBlackUsers.get(0);
			else {
				if (!Utils.isLong(user)) {
					cmde.sendPleaseUse();
					return;
				}
				
				e.getJDA().retrieveUserById(user.trim()).queue(uzer -> {
					print(cmde, author, BlackUser.from(uzer), guild, channel, message);
				}, failure -> {
					cmde.exception();
					return;
				});
				return;
			}
		}
		
		print(cmde, author, mentionedUser, guild, channel, message);
	}
	
	private static void print(final CommandEvent cmde, final BlackUser author, final BlackUser mentionedUser, final BlackGuild guild, final MessageChannel channel, final BlackMessage message) {
		cmde.reply(cmde.success().setTitle(LanguageSystem.getTranslation("pfpof", author, guild) + " " + Utils.removeMarkdown(mentionedUser.getName()) + "#" + mentionedUser.getDiscriminator(), mentionedUser.getEffectiveAvatarUrl())
				.setImage(mentionedUser.getEffectiveAvatarUrl() + "?size=2048")
				.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
				.setTimestamp(Instant.now()));	
	}
}