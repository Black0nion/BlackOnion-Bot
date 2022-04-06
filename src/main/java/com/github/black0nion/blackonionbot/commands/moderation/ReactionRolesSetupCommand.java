package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.commands.TextCommand;
import com.github.black0nion.blackonionbot.systems.ReactionRoleSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReactionRolesSetupCommand extends TextCommand {

	public ReactionRolesSetupCommand() {
		this.setCommand("reactionrole", "rr")
				.setSyntax("<create | remove / delete> <#channel> <message id> <emote> <role to give>")
				.setRequiredArgumentCount(5).setRequiredPermissions(Permission.MANAGE_ROLES)
				.setRequiredBotPermissions(Permission.MANAGE_ROLES, Permission.MESSAGE_ADD_REACTION);
	}

	@Override
	public void execute(String[] args, final @NotNull CommandEvent cmde, final @NotNull MessageReceivedEvent e,
			final @NotNull Message message, final BlackMember member, final BlackUser author,
			final @NotNull BlackGuild guild, final TextChannel channel) {
		args = message.getContentDisplay().split(" ");
		final List<TextChannel> channels = message.getMentionedChannels();
		final List<Role> roles = message.getMentionedRoles();

		final List<String> argz = Arrays.asList(args);

		if (argz.contains("@everyone") || argz.contains("@here")) {
			cmde.error("invalidrole", "iseveryone");
			return;
		}

		if (!channels.isEmpty() && !roles.isEmpty()) {
			final TextChannel tc = channels.get(0);
			final Role role = roles.get(0);

			final String[] finalArgs = args;
			final String messageIDString = args[3];

			try {
				final long messageID = Long.parseLong(messageIDString);
				tc.retrieveMessageById(messageID).queue(success -> {
					final String emoteName = finalArgs[4];
					guild.retrieveEmotes().queue(emoteList -> {
						String emote;

						emoteList = emoteList.stream()
								.filter(entry -> entry.getName().equals(emoteName.replace(":", "")))
								.collect(Collectors.toList());

						if (emoteList.size() != 0) {
							tc.addReactionById(messageID, emoteList.get(0)).queue();
							emote = emoteList.get(0).getAsMention();
						} else {
							emote = emoteName;
							tc.addReactionById(messageID, emote).queue(null,
									fail -> cmde.error("wrongargument", "emotenotfound"));
						}

						final Document doc = new Document().append("guildid", e.getGuild().getIdLong())
								.append("channelid", tc.getIdLong()).append("messageid", messageID)
								.append("emote", emote).append("roleid", role.getIdLong());

						if (finalArgs[1].equalsIgnoreCase("create")) {
							if (ReactionRoleSystem.collection.find(doc).first() != null) {
								message.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild)
										.addField("alreadyexisting", "thisalreadyexisting", false).build()).queue();
								return;
							}

							ReactionRoleSystem.collection.insertOne(doc);

							cmde.success("reactionrolecreated", "reactionrolecreatedinfo",
									new Placeholder("emote", emote), new Placeholder("role", role.getAsMention()));
						} else if (finalArgs[1].equalsIgnoreCase("remove") || finalArgs[1].equalsIgnoreCase("delete")) {
							if (ReactionRoleSystem.collection.find(doc).first() != null) {
								ReactionRoleSystem.collection.deleteOne(doc);

								final String finalEmote = emote;
								tc.retrieveMessageById(messageID)
										.queue(msg -> guild.retrieveEmoteById(finalEmote.split(":")[2].replace(">", ""))
												.queue(customEmote -> {
													if (customEmote != null) {
														msg.clearReactions(customEmote).queue();
													} else {
														msg.clearReactions(finalEmote).queue();
													}
													cmde.success("entrydeleted", "reactionroledeleted");
												}));
							} else {
								cmde.error("errorhappened", "thisnotfound");
							}
						} else {
							cmde.sendPleaseUse();
						}
					}, fail -> cmde.exception());
				}, fail -> cmde.error("messagenotfound", "messagecouldntbefound"));
			} catch (final NumberFormatException ex) {
				cmde.sendPleaseUse();
			}
		} else {
			cmde.sendPleaseUse();
		}
	}
}
