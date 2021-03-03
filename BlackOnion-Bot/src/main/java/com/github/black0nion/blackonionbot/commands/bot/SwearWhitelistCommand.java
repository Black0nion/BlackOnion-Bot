package com.github.black0nion.blackonionbot.commands.bot;

import java.util.ArrayList;
import java.util.List;

import com.github.black0nion.blackonionbot.bot.BotInformation;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.systems.guildmanager.GuildManager;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SwearWhitelistCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "swearwhitelist", "antiswearwhitelist", "asw" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")) {
			List<String> mentionedStuff = new ArrayList<>();
			List<Role> roles = message.getMentionedRoles();
			List<TextChannel> channels = message.getMentionedChannels();
			for (int i = 2; i < args.length; i++) {
				String input = args[i];
				Role rl = roles.stream().filter(r -> r.getAsMention().equals(input)).findFirst().orElse(null);
				TextChannel ch = channels.stream().filter(c -> c.getAsMention().equals(input)).findFirst().orElse(null);
				Permission perm = null;
				try { perm = Permission.valueOf(input.toUpperCase()); } catch (Exception ignored) {}
				
				if (rl != null) mentionedStuff.add(rl.getAsMention());
				if (ch != null) mentionedStuff.add(ch.getAsMention());
				if (perm != null) mentionedStuff.add(perm.name());
			}
			
			boolean add = args[1].equalsIgnoreCase("add");
			
			if (mentionedStuff.size() != 0) {
				List<String> newWhitelist = GuildManager.getList(guild, "whitelist", String.class);
				List<String> temp = newWhitelist;
				if (add) { 
					temp.retainAll(mentionedStuff);
					newWhitelist.removeAll(temp);
				} else newWhitelist.removeAll(mentionedStuff);
				GuildManager.saveList(guild, "whitelist", newWhitelist);
				// TODO: test
			}
		} else {
			channel.sendMessage(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", LanguageSystem.getTranslatedString("pleaseuse", author, guild) + BotInformation.getPrefix(guild) + getCommand()[0] + " " + getSyntax(), false).build()).queue();
		}
	}

	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.ADMINISTRATOR };
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 2;
	}
	
	@Override
	public String getSyntax() {
		return "<add | remove> <@role | #channel | Permission Name>";
	}
}
