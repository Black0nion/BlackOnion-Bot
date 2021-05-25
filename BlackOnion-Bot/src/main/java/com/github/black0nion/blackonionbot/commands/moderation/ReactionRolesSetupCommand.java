package com.github.black0nion.blackonionbot.commands.moderation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.ReactionRoleSystem;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.mongodb.BasicDBObject;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ReactionRolesSetupCommand implements Command {
	
	@Override
	public String[] getCommand() {
		return new String[] {"reactionrole", "rr"};
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		// TODO: switch to new system
		args = message.getContentDisplay().split(" ");
		List<TextChannel> channels = message.getMentionedChannels();
		List<Role> roles = message.getMentionedRoles();
		
		List<String> argz = Arrays.asList(args); 
		
		if (argz.contains("@everyone") || argz.contains("@here")) {
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("invalidrole", author, guild), LanguageSystem.getTranslation("iseveryone", author, guild), false).build()).queue();
			return;
		}
		
		if (!channels.isEmpty() && !roles.isEmpty()) {
			TextChannel tc = channels.get(0);
			Role role = roles.get(0);
			
			final String[] finalArgs = args; 
			String messageIDString = args[3];
			
			try {
				long messageID = Long.parseLong(messageIDString);
				tc.retrieveMessageById(messageID).queue(success -> {
					final String emoteName = finalArgs[4];
					guild.retrieveEmotes().queue(emoteList -> {
						String emote = null;
						
						emoteList = emoteList.stream().filter(entry -> {return entry.getName().equals(emoteName.replace(":", ""));}).collect(Collectors.toList());
						
						if (emoteList.size() != 0) {
							tc.addReactionById(messageID, emoteList.get(0)).queue();
							emote = emoteList.get(0).getAsMention();
						} else {
							emote = emoteName;
							tc.addReactionById(messageID, emote).queue(null, fail -> {
								message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("wrongargument", author, guild), LanguageSystem.getTranslation("emotenotfound", author, guild), false).build()).queue();
								return;
							});
						}
						if (finalArgs[1].equalsIgnoreCase("create")) {
						
							if (ReactionRoleSystem.collection.find(new BasicDBObject().append("guildid", e.getGuild().getIdLong())
									.append("channelid", tc.getIdLong())
									.append("messageid", messageID)
									.append("roleid", role.getIdLong())).first() != null) {
								message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("alreadyexisting", "thisalreadyexisting", false).build()).queue();
								return;
							}
							
							ReactionRoleSystem.collection.insertOne(new Document()
									.append("guildid", e.getGuild().getIdLong())
									.append("channelid", tc.getIdLong())
									.append("messageid", messageID)
									.append("emote", emote)
									.append("roleid", role.getIdLong()));
							
							message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("reactionrolecreated", LanguageSystem.getTranslation("reactionrolecreatedinfo", author, guild).replace("%emote%", emote).replace("%role%", role.getAsMention()), false).build()).queue();
							return;
						} else if (finalArgs[1].equalsIgnoreCase("remove") || finalArgs[1].equalsIgnoreCase("delete")) {
							if (ReactionRoleSystem.collection.find(new BasicDBObject().append("guildid", e.getGuild().getIdLong())
									.append("channelid", tc.getIdLong())
									.append("messageid", messageID)
									.append("roleid", role.getIdLong())).first() != null) {
								ReactionRoleSystem.collection.deleteOne(new BasicDBObject().append("guildid", e.getGuild().getIdLong())
										.append("channelid", tc.getIdLong())
										.append("messageid", messageID)
										.append("roleid", role.getIdLong()));
								final String finalEmote = emote;
								tc.retrieveMessageById(messageID).queue(msg -> {
									guild.retrieveEmoteById(finalEmote.split(":")[2].replace(">", "")).queue(customEmote -> {										
										if (customEmote != null) {
											msg.clearReactions(customEmote).queue();
										} else {
											msg.clearReactions(finalEmote).queue();
										}
										message.reply(EmbedUtils.getSuccessEmbed(author, guild).addField("entrydeleted", "reactionroledeleted", false).build()).queue();
									});
								});
								return;
							} else {
								message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("errorhappened", "thisnotfound", false).build()).queue();
								return;
							}
						} else {
							message.reply(EmbedUtils.getErrorEmbed(author, guild).addField("wrongargument", Utils.getPleaseUse(guild, author, this), false).build()).queue();
							return;
						}
					}, fail -> {
						message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("errorhappened", author, guild), LanguageSystem.getTranslation("somethingwentwrong", author, guild), false).build()).queue();
						return;
					});
				}, fail -> {
					message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("messagenotfound", author, guild), LanguageSystem.getTranslation("messagecouldntbefound", author, guild), false).build()).queue();
					return;
				});
			} catch (NumberFormatException ex) {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("wrongargument", author, guild), Utils.getPleaseUse(guild, author, this), false).build()).queue();
			}
		} else {
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(LanguageSystem.getTranslation("wrongargument", author, guild), Utils.getPleaseUse(guild, author, this), false).build()).queue();
		}
	}
	
	@Override
	public String getSyntax() {
		return "<create | remove / delete> <#channel> <message id> <emote> <role to give>";
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 5;
	}
	
	@Override
	public Category getCategory() {
		return Category.MODERATION;
	}
	
	@Override
	public Permission[] getRequiredPermissions() {
		return new Permission[] { Permission.MANAGE_ROLES };
	}
	
	@Override
	public Permission[] getRequiredBotPermissions() {
		return new Permission[] { Permission.MANAGE_ROLES, Permission.MESSAGE_ADD_REACTION };
	}
}