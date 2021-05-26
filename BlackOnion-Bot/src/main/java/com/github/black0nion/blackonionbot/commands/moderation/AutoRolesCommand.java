package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.Arrays;
import java.util.List;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AutoRolesCommand implements Command {

	@Override
	public String[] getCommand() {
		return new String[] { "autoroles", "autorole" };
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		List<String> argz = Arrays.asList(args); 
		
		if (argz.contains("@everyone") || argz.contains("@here")) {
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("invalidrole", author, guild), LanguageSystem.getTranslation("iseveryone", author, guild), false).build()).queue();
			return;
		}
		
		if (args[1].equalsIgnoreCase("create")) {
			List<Role> roles = message.getMentionedRoles();
			Long roleID = null;
			Role role = null;
			if (roles.size() != 0) {
				if (roles.get(0).getAsMention().equals(args[2])) {
					role = roles.get(0);
					roleID = roles.get(0).getIdLong();
				} else {
					message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
					return;
				}
			} else {
				try {role = guild.getRoleById(args[2]); if (role != null) roleID = Long.parseLong(args[2]);} catch (NumberFormatException ignored) {}
				if (roleID == null) {
					message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
					return;
				}
			}
			
			List<Long> tempList = guild.getList("autoroles", Long.class);
			if (tempList.contains(roleID)) {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("alreadyexisting", "thisalreadyexisting", false).build()).queue();
				return;
			} else tempList.add(roleID);
			guild.saveList("autoroles", tempList);
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("autorolecreated", LanguageSystem.getTranslation("autorolecreatedinfo", author, guild).replace("%role%", role.getAsMention()), false).build()).queue();
		} else if (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("delete")) {
			List<Role> roles = message.getMentionedRoles();
			Long roleID = null;
			Role role = null;
			if (roles.size() != 0) {
				if (roles.get(0).getAsMention().equals(args[2])) {
					role = roles.get(0);
					roleID = roles.get(0).getIdLong();
				} else {
					message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
					return;
				}
			} else {
				try {role = guild.getRoleById(args[2]); if (role != null) roleID = Long.parseLong(args[2]);} catch (NumberFormatException ignored) {}
				if (roleID == null) {
					message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
					return;
				}
			}
			
			List<Long> tempList = guild.getList("autoroles", Long.class);
			if (!tempList.contains(roleID)) {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("notfound", "thisnotfound", false).build()).queue();
				return;
			} else tempList.remove(roleID);
			guild.saveList("autoroles", tempList);
			message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("autoroledeleted", LanguageSystem.getTranslation("autoroledeletedinfo", author, guild).replace("%role%", role.getAsMention()), false).build()).queue();
		} else {
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
		}
	}
	
	@Override
	public String getSyntax() {
		return "<create | remove / delete> <@role | roleid>";
	}
	
	@Override
	public Category getCategory() {
		return Category.MODERATION;
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 2;
	}

	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.MANAGE_ROLES };
	}
	
	@Override
	public Permission[] getRequiredBotPermissions() {
		return new Permission[] { Permission.MANAGE_ROLES };
	}
}