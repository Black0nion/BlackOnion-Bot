package com.github.black0nion.blackonionbot.commands.fun;

import java.time.Instant;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AvatarCommand implements Command {
	
	@Override
	public String[] getCommand() {
		return new String[] { "avatar" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		BlackUser mentionedUser = BlackUser.from(author);
		if (args.length > 1) {			
			String user = String.join(" ", Utils.removeFirstArg((args)));
			final List<BlackUser> mentionedBlackUsers = message.getMentionedBlackUsers();
			if (!mentionedBlackUsers.isEmpty()) {
				mentionedUser = mentionedBlackUsers.get(0);
			} else {
				if (!Utils.isLong(user)) {
					message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
					return;
				}
				
				e.getJDA().retrieveUserById(user.trim()).queue(uzer -> {
					print(author, BlackUser.from(uzer), guild, channel, message);
				}, failure -> {
					message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "someerrorhappened", false).build()).queue();
				});
				return;
			}
		}
		
		print(author, mentionedUser, guild, channel, message);
	}
	
	private static void print(BlackUser author, BlackUser mentionedUser, BlackGuild guild, MessageChannel channel, BlackMessage message) {
		EmbedBuilder builder = new EmbedBuilder()
			.setTitle(LanguageSystem.getTranslation("pfpof", author, guild) + " " + Utils.removeMarkdown(mentionedUser.getName()) + "#" + mentionedUser.getDiscriminator(), mentionedUser.getEffectiveAvatarUrl())
				.setImage(mentionedUser.getEffectiveAvatarUrl() + "?size=2048")
				.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
				.setTimestamp(Instant.now());
		message.reply(builder.build()).queue();	
	}
	
	@Override
	public Category getCategory() {
		return Category.FUN;
	}

	@Override
	public String getSyntax() {
		return "<@User / UserID>";
	}
}