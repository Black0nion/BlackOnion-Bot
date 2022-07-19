package com.github.black0nion.blackonionbot.commands.moderation;

import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.systems.ReactionRoleSystem;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReactionRolesSetupCommand extends SlashCommand {
	private static final String CREATE_REACTION = "create";
	private static final String CREATE_REACTION_CHANNEL = "channel";
	private static final String CREATE_REACTION_MESSAGE_ID = "message_id";
	private static final String CREATE_REACTION_EMOTE = "emoji";
	private static final String CREATE_REACTION_ROLE = "role";
	private static final String REMOVE_REACTION = "remove";
	private static final String REMOVE_REACTION_CHANNEL = "channel";
	private static final String REMOVE_REACTION_MESSAGE_ID = "message_id";
	private static final String REMOVE_REACTION_EMOTE = "emoji";
	private static final String REMOVE_REACTION_ROLE = "role";

	public ReactionRolesSetupCommand() {
		super(builder(
			Commands.slash("reactionroles", "Used to set up a reaction role.").addSubcommands(
				new SubcommandData(CREATE_REACTION, "Used to create a reaction role.")
					.addOption(OptionType.CHANNEL, CREATE_REACTION_CHANNEL,
						"The channel to create the reaction role in.")
					.addOption(OptionType.NUMBER, CREATE_REACTION_MESSAGE_ID,
						"The message to create the reaction role in.")
					.addOption(OptionType.NUMBER, CREATE_REACTION_EMOTE,
						"The emoji id to create the reaction role with.")
					.addOption(OptionType.ROLE, CREATE_REACTION_ROLE,
						"The role to give to the userid when they react."),
				new SubcommandData(REMOVE_REACTION, "Used to remove a reaction role.")
					.addOption(OptionType.CHANNEL, REMOVE_REACTION_CHANNEL,
						"The channel to remove the reaction role from.")
					.addOption(OptionType.NUMBER, REMOVE_REACTION_MESSAGE_ID,
						"The message to remove the reaction role from.")
					.addOption(OptionType.NUMBER, REMOVE_REACTION_EMOTE,
						"The emoji id to remove the reaction role with.")
					.addOption(OptionType.ROLE, REMOVE_REACTION_ROLE,
						"The role to remove from the userid when they react.")))
			.setRequiredPermissions(Permission.MANAGE_ROLES)
			.setRequiredBotPermissions(Permission.MANAGE_ROLES,
				Permission.MESSAGE_ADD_REACTION));
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, @NotNull BlackGuild guild, TextChannel channel) {
		switch (Objects.requireNonNull(e.getSubcommandName())) {
			case CREATE_REACTION -> setCreateReaction(cmde, e, guild);
			case REMOVE_REACTION -> setRemoveReaction(cmde, e, guild);
			default -> cmde.send("invalidsubcommand");
		}
	}

	public void setCreateReaction(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, @NotNull BlackGuild guild) {
		var createReactionChannel = e.getOption(CREATE_REACTION_CHANNEL, OptionMapping::getChannelType);
		var messageId = e.getOption(CREATE_REACTION_MESSAGE_ID, OptionMapping::getAsLong);
		var emoteId = e.getOption(CREATE_REACTION_EMOTE, OptionMapping::getAsLong);

		if (guild.getEmojiById(emoteId) == null) {
			cmde.send("emotenotfound");
		}

		var role = e.getOption(CREATE_REACTION_ROLE, OptionMapping::getAsRole);

		if (Objects.requireNonNull(createReactionChannel).isMessage()) {
			var textChannel = e.getOption(CREATE_REACTION_CHANNEL).getAsChannel().asTextChannel();
			textChannel.retrieveMessageById(messageId)
				.queue(success -> guild.retrieveEmojiById(emoteId).queue(
					successEmote -> textChannel.addReactionById(messageId, successEmote).queue(
						null,
						fail -> cmde.error("wrongargument", "emotenotfound")
					),
					fail -> cmde.error("wrongargument", "emotenotfound"))
				);

			var doc = appendDoc(e, textChannel, messageId, emoteId, role.getIdLong());

			if (ReactionRoleSystem.collection.find(doc).first() != null) {
				cmde.error("alreadyexisting", "thisalreadyexisting");
				return;
			}

			ReactionRoleSystem.collection.insertOne(doc);

			cmde.success("reactionrolecreated", "reactionrolecreatedinfo",
				new Placeholder("emote", e.getGuild().getEmojiById(emoteId).getAsMention()),
				new Placeholder("role", role.getAsMention()));
		} else {
			cmde.send("nottextchannel");
		}
	}

	public void setRemoveReaction(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e,
								  @NotNull BlackGuild guild) {
		ChannelType removeReactionChannel = e.getOption(REMOVE_REACTION_CHANNEL, OptionMapping::getChannelType);
		var messageId = e.getOption(REMOVE_REACTION_MESSAGE_ID, OptionMapping::getAsLong);
		var emoteId = e.getOption(REMOVE_REACTION_EMOTE, OptionMapping::getAsLong);

		if (guild.getEmojiById(emoteId) == null) {
			cmde.send("emotenotfound");
		}

		var emote = guild.getEmojiById(emoteId);
		var role = e.getOption(REMOVE_REACTION_ROLE, OptionMapping::getAsRole);

		if (Objects.requireNonNull(removeReactionChannel).isMessage()) {
			var textChannel = e.getOption(CREATE_REACTION_CHANNEL).getAsChannel().asTextChannel();
			Objects.requireNonNull(textChannel).retrieveMessageById(messageId)
				.queue(success -> guild.retrieveEmojiById(emoteId).queue(successEmote -> {
					textChannel.addReactionById(messageId, successEmote).queue(null,
						fail -> cmde.error("wrongargument", "emotenotfound"));
				}, fail -> cmde.error("wrongargument", "emotenotfound")));

			var doc = appendDoc(e, textChannel, messageId, emoteId, role.getIdLong());

			if (ReactionRoleSystem.collection.find(doc).first() != null) {
				cmde.error("alreadyexisting", "thisalreadyexisting");
				return;
			}

			if (ReactionRoleSystem.collection.find(doc).first() != null) {
				ReactionRoleSystem.collection.deleteOne(doc);

				textChannel.retrieveMessageById(messageId).queue(
					msg -> guild.retrieveEmojiById(emote.getId()).queue(customEmote -> {
						msg.clearReactions(Objects.requireNonNullElse(customEmote, emote)).queue();
						cmde.success("entrydeleted", "reactionroledeleted");
					}));
			} else {
				cmde.error("errorhappened", "thisnotfound");
			}

		} else {
			cmde.send("nottextchannel");
		}
	}

	private Document appendDoc(@NotNull SlashCommandInteraction e, @NotNull TextChannel channel, long messageId, long emoteId, long roleId) {
		return new Document().append("guildid", e.getGuild().getIdLong()).append("channelid", channel.getIdLong())
			.append("messageid", messageId).append("emote", e.getGuild().getEmojiById(emoteId))
			.append("roleid", roleId);
	}
}