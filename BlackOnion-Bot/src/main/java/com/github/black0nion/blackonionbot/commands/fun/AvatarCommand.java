package com.github.black0nion.blackonionbot.commands.fun;

import java.time.Instant;

import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AvatarCommand implements Command {

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		User mentionedUser = author;
		if (args.length > 1) {			
			String user = String.join(" ", Utils.removeFirstArg((args)));
			if (!message.getMentionedUsers().isEmpty()) {
				mentionedUser = message.getMentionedUsers().get(0);
			} else {
				if (!Utils.isLong(user)) {
					channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
					return;
				}
				
				e.getJDA().retrieveUserById(user.trim()).queue(uzer -> {
					print(author, uzer, guild, channel);
				}, failure -> {
					channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "someerrorhappened", false).build()).queue();
				});
				return;
			}
		}
		
		print(author, mentionedUser, guild, channel);
	}
	
	private static void print(User author, User mentionedUser, Guild guild, MessageChannel channel) {
		EmbedBuilder builder = new EmbedBuilder()
			.setTitle(LanguageSystem.getTranslatedString("pfpof", author, guild) + " " + Utils.removeMarkdown(mentionedUser.getName()) + "#" + mentionedUser.getDiscriminator(), mentionedUser.getEffectiveAvatarUrl())
				.setImage(mentionedUser.getEffectiveAvatarUrl() + "?size=2048")
				.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
				.setTimestamp(Instant.now());
		channel.sendMessage(builder.build()).queue();	
	}
	
	@Override
	public Category getCategory() {
		return Category.FUN;
	}

	@Override
	public String getSyntax() {
		return "<@User / UserID>";
	}
	
	@Override
	public String[] getCommand() {
		return new String[] { "avatar" };
	}
}
